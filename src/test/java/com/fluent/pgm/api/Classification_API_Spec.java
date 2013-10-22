package com.fluent.pgm.api;

import com.fluent.collections.FMap;
import com.fluent.math.*;
import com.fluent.pgm.*;
import com.fluent.pgm.momc.Mixture;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;
import org.mockito.Mock;

import static com.fluent.core.oo.oo;
import static com.fluent.math.P.*;
import static com.fluent.pgm.MPDBuilder.MPD;

public class Classification_API_Spec extends AbstractSpec
{
    @Mock Inference inference;
    @Mock FMap<String, CPD> conditionals;
    @Mock Sequence sequence;
    MPD prior = MPD(oo("A", (.6)), oo("B", (.4)));
    P[] joints = {ONE, ONE};
    @Mock P marginal;

    @Test
    public void computes_maximum_a_posteriori() throws Exception
    {
        GIVEN(inference.jointsAndMarginal(sequence, prior, conditionals)).RETURNS(oo(joints, marginal));
        GIVEN(inference.posterior(prior, joints, marginal)).RETURNS(prior);

        WHEN(new Classification_API(inference).maxPosterior(sequence, new Mixture(prior, conditionals)));

        THEN(theOutput).shouldBe(oo("A", P(.6)));
    }

    @Test
    public void classifies_sequence() throws Exception
    {
        GIVEN(inference.jointsAndMarginal(sequence, prior, conditionals)).RETURNS(oo(joints, marginal));
        GIVEN(inference.posterior(prior, joints, marginal)).RETURNS(prior);

        WHEN(new Classification_API(inference).maxPosterior(sequence, new Mixture(prior, conditionals)));

        THEN(theOutput).shouldBe(oo("A", P(.6)));
    }
}
