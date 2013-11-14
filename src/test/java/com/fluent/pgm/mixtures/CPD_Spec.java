package com.fluent.pgm.mixtures;

import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import static com.fluent.math.P.*;
import static com.fluent.pgm.mixtures.CPD.p;
import static com.fluent.pgm.mixtures.Token.END;
import static com.fluent.pgm.mixtures.Token.START;

public class CPD_Spec extends AbstractSpec
{
    Token A = Token.from("A"), B = Token.from("B");

    CPD cpd = CPD.from(p(A, START, .3), p(B, START, .7),
            p(A, A, .1), p(B, A, .6), p(END, A, .3),
            p(A, B, .35), p(B, B, .35), p(END, B, .3));

    @Test
    public void gets_correct_probability_of_token_given_context() throws Exception
    {
        So(cpd.p(Token.from("A"), Token.from("B"))).shouldBe(P(.35));
    }
}
