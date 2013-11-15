package com.fluent.pgm.mixtures;

import com.google.common.cache.CacheBuilder;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;

import static com.google.common.hash.Hashing.goodFastHash;

public class Common
{
    public static final long SEED_1 = 1234567890;
    //
    static HashFunction hash = goodFastHash(64);


    public static <KEY, VALUE> CacheBuilder<KEY, VALUE> cache()
    {
        return (CacheBuilder<KEY, VALUE>) CacheBuilder.newBuilder();
    }

    public static Hasher hasher()
    {
        return hash.newHasher();
    }



}
