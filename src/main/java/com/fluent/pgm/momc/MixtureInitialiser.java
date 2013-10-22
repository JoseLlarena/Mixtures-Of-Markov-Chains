package com.fluent.pgm.momc;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.core.oo;
import com.fluent.pgm.*;
import com.fluent.pgm.estimation.Counting;
import com.fluent.pgm.estimation.Estimation;

import java.util.Random;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.Syntax.from;
import static com.fluent.pgm.MPDBuilder.*;
import static java.lang.String.format;

/**
 * Mixture badfrom Markov Chains Initialiser
 */
public class MixtureInitialiser
{
    private final Counting counting;
    private final Estimation estimation;

    public MixtureInitialiser(final Counting counting, final Estimation estimation)
    {
        this.counting = counting;
        this.estimation = estimation;
    }

    public Mixture init(final FList<Sequence> observations, final MixtureEstimation.Context context)
    {
        final Random weights = new Random(context.seed());

        final MPD prior = initPrior(context.mixtureCount(), weights);

        return new Mixture(prior, initConditionals(prior.tokens(), observations, context));
    }

    FMap<String, CPD>
    initConditionals(final FSet<String> tags, final FList<Sequence> observations, final MixtureEstimation.Context context)
    {
        final Random weights = new Random(context.seed());

        final WeightMap rawCounts = counting.bigramFrequency(observations);

        for (final oo<String, String> allBigrams : rawCounts.contexts().cross(rawCounts.tokens()))
        {
            rawCounts.add(allBigrams, weights.nextDouble() * 100);
        }

        return tags.collect(newFMap(), (map, tag) -> map.plus(tag, estimation.CPD_from(rawCounts, context)));
    }

    MPD initPrior(final int N, final Random weights)
    {
        final double mass = 1. / N;
        final double delta = 1. / (N * N + weights.nextDouble());
        double total = 0;

        final MPDBuilder prior = MPD();

        for (final int tag : from(1, N))
        {
            double slice   = tag == N ? 1 - total : slice(mass, delta, tag);
            prior.p(format("%d", tag), slice);
            total += slice;
        }

        return prior.done();
    }

    double slice(final double mass, final double delta, final int cluster)
    {
        return mass + (cluster % 2 == 0 ? delta : -delta);
    }
}
