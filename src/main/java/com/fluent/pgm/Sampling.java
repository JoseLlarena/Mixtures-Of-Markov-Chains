package com.fluent.pgm;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.collections.Lists;
import com.fluent.core.oo;
import com.fluent.math.*;

import java.util.Random;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.collections.Lists.newFList;
import static com.fluent.math.P.*;
import static com.fluent.pgm.Sequence.END_STATE;
import static com.fluent.pgm.Sequence.START_STATE;

public class Sampling
{
    public static final Sampling of = new Sampling();

    public FList<String> ofMPD(final FMap<String, P> mpd, final int size, final long seed)
    {
        final Random generator = new Random(seed);

        return Lists.newFList(size, () -> ofMPD(mpd, generator));
    }

    public String ofMPD(final FMap<String, P> mpd, final Random generator)
    {
        final P threshold = P(1 - generator.nextDouble());
        P cdf = ZERO;
        String choice = null;

        for (final oo<String, P> entry : mpd)
        {
            cdf = cdf.add(entry.$2);
            choice = entry.$1;

            if (cdf.gt(threshold))
            {
                break;
            }
        }

        return choice;
    }

    public FList<Sequence> sequencesFromCPD(final FMap<String, FMap<String, P>> cpd, final int size, final long seed)
    {
        final Random generator = new Random(seed);

        return Lists.newFList(size, () -> Sequence.from(ofCPD(cpd, generator)));
    }

    public Sequence sequenceFromCPD(final FMap<String, FMap<String, P>> cpd, Random random)
    {
        return Sequence.from(ofCPD(cpd, random));
    }

    public FList<String> ofCPD(final FMap<String, FMap<String, P>> cpd, final Random random)
    {
        String context = START_STATE;
        final FList<String> sequence = newFList();

        while (true)
        {
            final String symbol = ofMPD(cpd.get(context), random);

            if (symbol != END_STATE)
            {
                sequence.add(symbol);
                context = symbol;
            } else
            {
                break;
            }
        }

        return sequence;
    }

    public String ofMPD(final FMap<String, P> mpd, final long seed)
    {
        return ofMPD(mpd, new Random(seed));
    }
}
