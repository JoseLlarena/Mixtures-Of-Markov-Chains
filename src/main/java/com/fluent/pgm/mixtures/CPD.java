package com.fluent.pgm.mixtures;

import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.core.oo;
import com.fluent.math.*;

import static com.fluent.collections.Maps.EMPTY_FMAP;
import static com.fluent.collections.Maps.newOrderedFMap;
import static com.fluent.collections.Sets.EMPTY_FSET;
import static com.fluent.core.Preconditions.reject;
import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.mixtures.Sequence.Ngram;
import static java.lang.Math.abs;

/**
 * Conditional Probability Distribution
 */
public interface CPD
{
    static final double tolerance = .000000001;

    public P p(Token token, Context context);

    public default MPD mpd_from(Context context)
    {
        return MPD.EMPTY;
    }

    public static CPD from(oo<Ngram, P>... entries)
    {
        FMap<Ngram, P> map = newOrderedFMap();

        for (oo<Ngram, P> entry : entries)
        {
            map.plus(entry.$1, entry.$2);
        }

        return from(map);
    }

    public static CPD from(FMap<Ngram, P> map)
    {
        DecimalCounter<Context> context_mass = map.aggregate(new DecimalCounter<>(), (mass, ngram, p) ->
                mass.plus(ngram.context(), p.toDouble()));

        context_mass.each((context, mass) ->
                reject(abs(1 - mass) > tolerance,
                        "Total probability mass for %s should add up to 1 but got %s in %s", context, mass, map));

        return new Map_CPD(map);
    }

    public static CPD lenient_from(FMap<Ngram, P> map)
    {
        return new Map_CPD(map);
    }


    public static oo<Ngram, P> p(Token token, Token context, double p)
    {
        return oo(Ngram.from(context, token), P(p));
    }

    public static oo<Ngram, P> p(Token token, double p)
    {
        return p(token, Token.START, p);
    }

    public default FMap<Ngram, P> as_map()
    {
        return EMPTY_FMAP;
    }

    public default FSet<Token> tokens()
    {
        return EMPTY_FSET;
    }
}
