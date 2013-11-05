package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F0;
import com.fluent.core.OP1;
import com.fluent.math.*;

import java.util.Random;

import static com.fluent.collections.Lists.range;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.Functions.f0;
import static com.fluent.core.Words.to;
import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.new_api.CPD_Builder.CPX_from;
import static com.fluent.pgm.new_api.Seqence.Ngram;

public class New_Initialisation
{
    public static final New_Initialisation Initialisation = new New_Initialisation() {};
    static final OP1<Double> NOISE_SCALING = weight -> weight * .001;

    public MoMC initialise_with(FList<Seqence> data)
    {
        return initialise_with(data, Options.DEFAULT);
    }

    public MoMC initialise_with(FList<Seqence> data, Options options)
    {
        F0<Double> prior_weights = f0(new Random(options.seed())::nextDouble)
                .and_then(weight -> weight * 1. / options.tag_count());

        MPX priors = priors_from(options.tag_count(), prior_weights);

        F0<Double> noise = f0(new Random(options.seed())::nextDouble).and_then(NOISE_SCALING);

        F0<Double> transition_weights = () -> f0(new Random(options.seed())::nextDouble)
                .and_then(weight -> (weight + noise.value()) * 1. / data.size()).value();

        return new MoMC(priors, priors.as_map().keys().zip(tag -> transitions_from(data, transition_weights)));
    }

    CPX transitions_from(FList<Seqence> data, F0<Double> new_weight)
    {
        FMap<Ngram, P> transitions = data.flat(datum -> datum.ngrams()).aggregate(newFMap(),
                (ngram_to_p, ngram) -> ngram_to_p.plus(ngram, P((new_weight.value()))));

        return CPX_from(transitions);
    }

    MPX priors_from(int k, F0<Double> new_weight)
    {
        return MPX.from(range(1, to(k)).apply(index -> oo("C" + index, P(new_weight.value()))));
    }

    public static class Options
    {
        public static final Options DEFAULT = new Options();
        int tag = 2;
        long seed = Common.SEED_1;

        public int tag_count()
        {
            return tag;
        }

        public long seed()
        {
            return seed;
        }
    }
}
