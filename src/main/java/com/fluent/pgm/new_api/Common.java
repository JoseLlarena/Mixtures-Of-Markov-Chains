package com.fluent.pgm.new_api;

import com.fluent.collections.FMap;
import com.google.common.hash.HashFunction;

import static com.fluent.collections.Maps.newOrderedFMap;
import static com.fluent.pgm.new_api.CPD_Builder.CPX_from;
import static com.fluent.pgm.new_api.CPD_Builder._p;
import static com.fluent.pgm.new_api.MPX_Builder.MPX;
import static com.fluent.pgm.new_api.Token.END;
import static com.fluent.pgm.new_api.Token.START;
import static com.google.common.hash.Hashing.goodFastHash;

public interface Common
{
    public static final long SEED_1 = 1234567890;
    //
    static HashFunction hash = goodFastHash(64);
    public static Token A = Token.from("a"), B = Token.from("b"), X = A, Y = B;
    public static String C1 = "SWITCHING", C2 = "REPEATING";

    public static long id_from(String letter) {return hash.hashString(letter).asLong();}

    public static MoMC example_model_1()
    {
        MPX prior = MPX().p(C1, .3).and(C2, .7);

        CPX C1_transitions = CPX_from(_p(A, START, .3), _p(B, START, .7),
                _p(A, A, .01), _p(B, A, .79), _p(END, A, .2),
                _p(A, B, .79), _p(B, B, .01), _p(END, B, .2));

        CPX C2_transitions = CPX_from(_p(X, START, .6), _p(Y, START, .4),
                _p(X, X, .69), _p(Y, X, .01), _p(END, X, .3),
                _p(X, Y, .01), _p(Y, Y, .69), _p(END, Y, .3));

        FMap conditionals = newOrderedFMap().plus(C1, C1_transitions).plus(C2, C2_transitions);

        return new MoMC(prior, conditionals);
    }

    public static MoMC example_model_2()
    {
        CPX C1_transitions = CPX_from(_p(A, START, .1), _p(B, START, .9),
                _p(A, A, .15), _p(B, A, .45), _p(END, A, .4),
                _p(A, B, .3), _p(B, B, .3), _p(END, B, .4));
        CPX C2_transitions = CPX_from(_p(X, START, .2), _p(Y, START, .8),
                _p(X, X, .5), _p(Y, X, .1), _p(END, X, .4),
                _p(X, Y, .3), _p(Y, Y, .3), _p(END, Y, .4));

        FMap<String, CPX> conditionals = newOrderedFMap();
        conditionals.plus(C1, C1_transitions).plus(C2, C2_transitions);

        MPX prior = MPX().p(C1, .51).and(C2, .49);

        return new MoMC(prior, conditionals);
    }
}
