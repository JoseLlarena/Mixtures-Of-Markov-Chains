package com.fluent.pgm.new_api;

import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import static com.fluent.math.P.*;
import static com.fluent.pgm.new_api.CPD_Builder.CPX_from;
import static com.fluent.pgm.new_api.CPD_Builder._p;
import static com.fluent.pgm.new_api.Token.END;
import static com.fluent.pgm.new_api.Token.START;

public class CPD_Spec extends AbstractSpec
{
    Token A = Token.from("A"), B = Token.from("B");
    CPX cpx = CPX_from(_p(A, START, .3), _p(B, START, .7),
            _p(A, A, .1), _p(B, A, .6), _p(END, A, .3),
            _p(A, B, .35), _p(B, B, .35), _p(END, B, .3));

    @Test
    public void gets_correct_probability_of_token_given_context() throws Exception
    {
        So(cpx.p(Token.from("A"), Token.from("B"))).shouldBe(P(.35));
    }
}
