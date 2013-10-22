package com.fluent.pgm;

import com.fluent.collections.FImmutableHashMap;
import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.math.*;

import static com.fluent.math.P.*;
import static java.lang.String.format;

/**
 * Marginal Probability Table
 */
public class MPT extends FImmutableHashMap<String, P> implements MPD
{
    public MPT(final FMap<String, P> map)
    {
        super(map);
    }

    public P p(final String token)
    {
        return get(token, ZERO);
    }

    public FSet<String> tokens()
    {
        return keys();
    }

    public String toString()
    {
        return format("%n%s", super.toString("p(%s) --> %s%n"));
    }

}
