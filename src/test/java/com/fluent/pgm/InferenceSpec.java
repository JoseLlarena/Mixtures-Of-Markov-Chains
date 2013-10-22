package com.fluent.pgm;

import com.fluent.collections.FMap;
import com.fluent.collections.Sets;
import com.fluent.math.*;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;

import static com.fluent.collections.Sets.newFSet;
import static com.fluent.collections.Sets.newFSet;
import static com.fluent.math.P.*;
import static com.fluent.pgm.CPDBuilder.CPD_with_OOV;
import static com.fluent.pgm.ConditionalsBuilder.conditionals;
import static com.fluent.pgm.MPDBuilder.MPD;
import static com.fluent.pgm.Sequence.END;
import static org.hamcrest.Matchers.closeTo;

public class InferenceSpec extends AbstractSpec
{
    String A = "A", B = "B";
    String C1 = "1", C2 = "2";
    P J1, J2;
    Sequence sequence = new Sequence(A + B);
    MPD prior;
    FMap<String, CPD> conditionals;
    @Spy Inference inference = Inference.of;

    @Before
    public void BACKGROUND()
    {
        prior = MPD().p(C1, .4).and(C2, .6);

        final CPD CPD1 = CPD_with_OOV().p(A, .1).p(A, A, .4).p(B, A, .3).p(END, A, .3).p(B, .9).p(A, B, .4).p(B, B, .3).and(END, B, .3);
        final CPD CPD2 = CPD_with_OOV().p(B, .1).p(A, B, .1).p(B, B, .2).p(END, B, .7).p(A, .9).p(A, A, .4).p(B, A, .3).and(END, A, .3);

        conditionals = conditionals().add(C1, CPD1).plus(C2, CPD2);

        J1 = P(.4 * .1 * .2 * .3);
        J2 = P(.6 * .9 * .2 * .7);
    }

    @Test
    public void computes_marginal() throws Exception
    {
        WHEN(Inference.of.posteriorAndMarginal(sequence, prior, conditionals).$2.toDouble());

        THEN(theOutcome).shouldBe(closeTo(J1.add(J2).toDouble(), .0000001));
    }

    @Test
    public void computes_posterior() throws Exception
    {
        WHEN(Inference.of.posteriorAndMarginal(sequence, prior, conditionals).$1);

        THEN(theOutcome).shouldBe(MPD().p(C1, J1.div(J1.add(J2))).and(C2, J2.div(J1.add(J2))));
    }

    @Test
    public void computes_joints() throws Exception
    {
        GIVEN_SPY(inference).RETURNS(P(.01)).ON().likelihood(sequence, conditionals.of(C1));
        GIVEN_SPY(inference).RETURNS(P(.02)).ON().likelihood(sequence, conditionals.of(C2));

        WHEN(newFSet(inference.jointsAndMarginal(sequence, prior, conditionals).$1));

        THEN(theOutcome).shouldBe(Sets.asFSet(P(.4 * .01), P(.6 * .02)));
    }

    @Test
    public void computes_likelihood() throws Exception
    {
        WHEN(inference.likelihood(sequence, conditionals.of(C1)));

        THEN(theOutcome).shouldBe(P(.1 * .3 * .3));
    }
}
