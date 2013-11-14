package com.fluent.pgm.new_api;

import com.fluent.collections.FMap;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Before;
import org.junit.Test;

import static com.fluent.collections.Maps.newOrderedFMap;
import static com.fluent.pgm.new_api.CPD_Builder.CPX_from;
import static com.fluent.pgm.new_api.CPD_Builder._p;
import static com.fluent.pgm.new_api.Common.*;
import static com.fluent.pgm.new_api.New_Estimation.Estimation;
import static com.fluent.pgm.new_api.Token.END;
import static com.fluent.pgm.new_api.Token.START;


public class Estimation_Spec extends AbstractSpec
{
    MoMC model = example_model_1(), the_smoothed_model;

    @Before
    public void CONTEXT()
    {
        double weight = New_Estimation.SMOOTHING, normal_2 = 1 + weight * 2, normal_3 = 1 + weight * 3;

        CPX C1_transitions = CPX_from(
                _p(A, START, (.3 + weight) / normal_2),
                _p(B, START, (.7 + weight) / normal_2),
                _p(A, A, (.01 + weight) / normal_3),
                _p(B, A, (.79 + weight) / normal_3),
                _p(END, A, (.2 + weight) / normal_3),
                _p(A, B, (.79 + weight) / normal_3),
                _p(B, B, (.01 + weight) / normal_3),
                _p(END, B, (.2 + weight) / normal_3));

        CPX C2_transitions = CPX_from(
                _p(X, START, (.6 + weight) / normal_2),
                _p(Y, START, (.4 + weight) / normal_3),
                _p(X, X, (.69 + weight) / normal_3),
                _p(Y, X, (.01 + weight) / normal_3),
                _p(END, X, (.3 + weight) / normal_3),
                _p(X, Y, (.01 + weight) / normal_3),
                _p(Y, Y, (.69 + weight) / normal_3),
                _p(END, Y, (.3 + weight) / normal_3));

        FMap conditionals = newOrderedFMap().plus(C1, C1_transitions).plus(C2, C2_transitions);

        the_smoothed_model = new MoMC(model.prior(), conditionals);
    }

    @Test
    public void smooths_conditional_probabilities() throws Exception
    {
        So(Estimation.smooth(model)).shouldBe(the_smoothed_model);
    }
}
