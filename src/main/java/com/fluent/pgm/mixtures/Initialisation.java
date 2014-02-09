package com.fluent.pgm.mixtures;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F0;
import com.fluent.core.OP1;
import com.fluent.math.*;

import java.util.Random;

import static com.fluent.collections.Ints.range;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.Functions.f0;
import static com.fluent.core.Words.to;
import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.mixtures.Sequence.Ngram;

public class Initialisation
{
    public static final Initialisation Initialisation = new Initialisation();
    //
    static final OP1<Double> NOISE_SCALING = weight -> weight * 1e-3;

    public MoMC initialise_with(FList<Sequence> data)
    {
        return initialise_with(data, Options.DEFAULT);
    }

    public MoMC initialise_with(FList<Sequence> data, Options options)
    {
        F0<Double> prior_weights = f0(new Random(options.seed())::nextDouble)
                .and_then(weight -> weight * 1. / options.tag_count());

        MPD priors = priors_from(options.tag_count(), prior_weights);

        F0<Double> noise = f0(new Random(options.seed())::nextDouble).and_then(NOISE_SCALING);

        F0<Double> transition_weights = () -> f0(new Random(options.seed())::nextDouble)
                .and_then(weight -> (weight + noise.value()) * 1. / data.size()).value();

        return new MoMC(priors, priors.as_map().keys().zip(tag -> transitions_from(data, transition_weights)));
    }

    CPD transitions_from(FList<Sequence> data, F0<Double> new_weight)
    {
        FMap<Ngram, P> transitions = data.flatmap(datum -> datum.ngrams()).aggregate(newFMap(),
                (ngram_to_p, ngram) -> ngram_to_p.plus(ngram, P((new_weight.value()))));

        return CPD.lenient_from(transitions);
    }

    MPD priors_from(int k, F0<Double> new_weight)
    {
        return MPD.from(range(1, to(k)).apply(index -> oo("C" + index, P(new_weight.value()))));
    }

    public static class Options
    {
        public static final Options DEFAULT = new Options();
        //
        int tag_count = 2;
        long seed = Common.SEED_1;

        public int tag_count()
        {
            return tag_count;
        }

        public Options with_tag_count(int tag_count)
        {
            Options options = new Options();
            options.seed = seed;
            options.tag_count = tag_count;

            return options;
        }

        public long seed()
        {
            return seed;
        }
    }
}
