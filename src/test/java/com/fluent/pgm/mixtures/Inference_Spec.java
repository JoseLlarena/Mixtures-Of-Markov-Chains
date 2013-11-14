package com.fluent.pgm.mixtures;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.math.*;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.collections.Maps.EMPTY_FMAP;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.mixtures.Common.*;
import static com.fluent.pgm.mixtures.Inference.Inference;
import static org.hamcrest.Matchers.*;

public class Inference_Spec extends AbstractSpec
{
    @Spy Inference api = Inference;
    //
    Sequence sequence = Sequence.from_chars("ab");
    MoMC model;
    FMap<String, P> expected_joint = newFMap(oo(SWITCHING, P(.06)), oo(REPEATING, P(.28)));
    double expected_likelihood = P(.3 * .69 * .3).asLog();

    @Before
    public void CONTEXT()
    {
        model = example_model();
    }

    @Test
    public void calculates_posterior() throws Exception
    {
        THEN(api.posterior_density(sequence, model)).shouldBe(EMPTY_FMAP);
    }

    @Test
    public void calculates_marginal() throws Exception
    {
        THEN(api.marginal_from(api.joint(sequence, model))).shouldBe(ONE);
    }


    @Test
    public void calculates_joint() throws Exception
    {
        GIVEN_SPY(api).RETURNS(P(.2)).ON().conditional(sequence, model.transitions_for(SWITCHING));
        GIVEN_SPY(api).RETURNS(P(.4)).ON().conditional(sequence, model.transitions_for(REPEATING));

        THEN(api.joint(sequence, model)).shouldBe(expected_joint);
    }

    @Test
    public void finds_most_likely_sequences() throws Exception
    {
        FList<Sequence> data = asFList(Sequence.from_chars("xxxx"), Sequence.from_chars("xy"), Sequence.from_chars("yx"));

        THEN(api.most_likely(2, data, model)).shouldBe(asFList(Sequence.from_chars("xxxx"), Sequence.from_chars("xy")));
    }

    @Test
    public void finds_least_likely_sequences() throws Exception
    {
        FList<Sequence> data = asFList(Sequence.from_chars("xxxx"), Sequence.from_chars("xy"), Sequence.from_chars("yx"));

        THEN(api.least_likely(2, data, model)).shouldBe(asFList(Sequence.from_chars("yx"), Sequence.from_chars("xy")));
    }

    @Test
    public void calculates_likelihood() throws Exception
    {
        THEN(api.conditional(sequence, model.transitions_for(SWITCHING)).asLog()).shouldBe(closeTo(expected_likelihood,
                .00001));
    }
}
