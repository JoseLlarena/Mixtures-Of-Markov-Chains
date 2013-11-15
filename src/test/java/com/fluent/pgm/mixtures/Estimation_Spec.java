package com.fluent.pgm.mixtures;

import com.fluent.collections.FMap;
import org.junit.Before;
import org.junit.Test;

import static com.fluent.collections.Maps.newOrderedFMap;
import static com.fluent.pgm.mixtures.CPD.p;
import static com.fluent.pgm.mixtures.Estimation.Estimation;
import static com.fluent.pgm.mixtures.Token.END;
import static com.fluent.pgm.mixtures.Token.START;

public class Estimation_Spec   extends Base_Spec
{
    MoMC model = example_model(), the_smoothed_model;

    @Before
    public void CONTEXT()
    {
        double weight = Estimation.SMOOTHING, normal_2 = 1 + weight * 2, normal_3 = 1 + weight * 3;

        CPD C1_transitions = CPD.from(
                p(A, START, (.3 + weight) / normal_2),
                p(B, START, (.7 + weight) / normal_2),
                p(A, A, (.01 + weight) / normal_3),
                p(B, A, (.79 + weight) / normal_3),
                p(END, A, (.2 + weight) / normal_3),
                p(A, B, (.79 + weight) / normal_3),
                p(B, B, (.01 + weight) / normal_3),
                p(END, B, (.2 + weight) / normal_3));

        CPD C2_transitions = CPD.from(
                p(A, START, (.6 + weight) / normal_2),
                p(B, START, (.4 + weight) / normal_3),
                p(A, A, (.69 + weight) / normal_3),
                p(B, A, (.01 + weight) / normal_3),
                p(END, A, (.3 + weight) / normal_3),
                p(A, B, (.01 + weight) / normal_3),
                p(B, B, (.69 + weight) / normal_3),
                p(END, B, (.3 + weight) / normal_3));

        FMap conditionals = newOrderedFMap().plus(SWITCHING, C1_transitions).plus(REPEATING, C2_transitions);

        the_smoothed_model = new MoMC(model.prior(), conditionals);
    }

    @Test
    public void smooths_conditional_probabilities() throws Exception
    {
        So(Estimation.smooth(model)).shouldBe(the_smoothed_model);
    }
}
