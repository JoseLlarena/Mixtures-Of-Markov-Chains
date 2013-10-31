package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.Series;
import com.fluent.math.*;

import java.util.Random;

import static com.fluent.collections.Lists.range;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.Words.to;
import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.new_api.CPD_Builder.CPX_from;
import static com.fluent.pgm.new_api.Seqence.Ngram;

public class New_Initialisation
{
    public static final New_Initialisation Initialisation = new New_Initialisation() {};

    public MoMC initialise_with(FList<Seqence> data)
    {
        return initialise_with(data, Options.DEFAULT);
    }

    public MoMC initialise_with(FList<Seqence> data, Options options)
    {
        Series<Double> weights = new Random(options.seed())::nextDouble;

        MPX priors = priors_from(options.tag_count(), weights);

        CPX transitions = transitions_from(data, weights);

        return new MoMC(priors, priors.as_map().keys().zip(tag -> transitions));
    }

    CPX transitions_from(FList<Seqence> data, Series<Double> weights)
    {
        double scaling = 1. / data.size();

        FMap<Ngram, P> transitions = data.flat(datum -> datum.ngrams()).aggregate(newFMap(),
                (ngram_to_p, ngram) -> ngram_to_p.plus(ngram, P(weights.next() * scaling)));

        return CPX_from(transitions);
    }

    MPX priors_from(int k, Series<Double> new_weight)
    {
        double scaling = 1. / k;

        return MPX.from(range(1, to(k)).apply(index -> oo("C" + index, P(new_weight.next() * scaling))));
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
