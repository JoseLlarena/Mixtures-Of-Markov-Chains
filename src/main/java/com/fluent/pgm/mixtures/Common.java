package com.fluent.pgm.mixtures;

import com.google.common.cache.CacheBuilder;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;

import static com.fluent.collections.Maps.newOrderedFMap;
import static com.fluent.pgm.mixtures.CPD.p;
import static com.fluent.pgm.mixtures.MPD.prior;
import static com.fluent.pgm.mixtures.Token.END;
import static com.google.common.hash.Hashing.goodFastHash;

public interface Common
{
    public static final long SEED_1 = 1234567890;
    //
    static HashFunction hash = goodFastHash(64);
    public static Token A = Token.from("a"), B = Token.from("b");
    public static String SWITCHING = "SWITCHING", REPEATING = "REPEATING";

    public static <KEY, VALUE> CacheBuilder<KEY, VALUE> cache()
    {
        return (CacheBuilder<KEY, VALUE>) CacheBuilder.newBuilder();
    }

    public static Hasher hasher()
    {
        return hash.newHasher();
    }

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
