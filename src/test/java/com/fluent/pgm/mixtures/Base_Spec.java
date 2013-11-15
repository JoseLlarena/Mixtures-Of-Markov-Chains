package com.fluent.pgm.mixtures;

import com.fluent.specs.unit.AbstractSpec;

import static com.fluent.collections.Maps.newOrderedFMap;
import static com.fluent.pgm.mixtures.CPD.p;
import static com.fluent.pgm.mixtures.MPD.prior;
import static com.fluent.pgm.mixtures.Token.END;

public class Base_Spec extends AbstractSpec
{
    public static Token A = Token.from("a"), B = Token.from("b");
    public static String SWITCHING = "SWITCHING", REPEATING = "REPEATING";

    public static MoMC example_model()
    {
        MPD prior = MPD.from(prior(SWITCHING, .3), prior(REPEATING, .7));

        CPD switching_chain = CPD.from(p(A, .3), p(B, .7),
                p(A, A, .01), p(B, A, .79), p(END, A, .2),
                p(A, B, .79), p(B, B, .01), p(END, B, .2));

        CPD repeating_chain = CPD.from(p(A, .6), p(B, .4),
                p(A, A, .69), p(B, A, .01), p(END, A, .3),
                p(A, B, .01), p(B, B, .69), p(END, B, .3));

        return new MoMC(prior, newOrderedFMap(SWITCHING, switching_chain, REPEATING, repeating_chain));
    }
}
