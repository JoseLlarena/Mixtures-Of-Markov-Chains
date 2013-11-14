package com.fluent.pgm.mixtures;

import com.fluent.collections.FMap;
import com.fluent.core.oo;
import com.fluent.math.*;

import static com.fluent.collections.Maps.EMPTY_FMAP;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.Preconditions.reject;
import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static java.lang.Math.abs;

/**
 * Marginal Probability Distribution
 */
public interface MPD
{
    public static final MPD EMPTY = new MPD()
    {
        public P p(String item)
        {
            return ZERO;
        }
    };
    static final double tolerance = .000000001;

    public static MPD from(FMap<String, P> raw)
    {
        return new Map_MPD(raw);
    }

    public static MPD from(Iterable<oo<String, P>> raw)
    {
        FMap<String, P> map = newFMap();

        raw.forEach(entry -> map.plus(entry.$1, entry.$2));

        return MPD.from(map);
    }

    public static oo<String, Double> prior(final String item, final double probability)
    {
        return oo(item, probability);
    }

    @SafeVarargs
    public static MPD from(final oo<String, Double> entry, final oo<String, Double>... otherEntries)
    {
        final FMap<String, P> tag_to_p = newFMap();
        double mass = entry.$2;
        tag_to_p.put(entry.$1, P(entry.$2));

        for (final oo<String, Double> each : otherEntries)
        {
            mass = mass + each.$2;
            tag_to_p.put(each.$1, P(each.$2));
        }

        reject(abs(1 - mass) > tolerance, "Total probability mass should add up to 1 but got %s in %s", mass, tag_to_p);

        return new Map_MPD(tag_to_p);
    }

    public P p(String item);

    public default FMap<String, P> as_map()
    {
        return EMPTY_FMAP;
    }
}
