package com.fluent.pgm.momc;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F1;
import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.pgm.*;
import com.fluent.pgm.estimation.Estimation;
import com.fluent.pgm.estimation.MarkovChainEstimationContext;
import com.fluent.pgm.estimation.Optimiser;

import java.util.concurrent.TimeUnit;

import static com.fluent.core.oo.oo;
import static com.fluent.math.P.*;

public class MixtureEstimation
{
    private final Estimation estimateOf;
    private final Inference inference;

    public MixtureEstimation(final Estimation estimateOf, final Inference inference)
    {
        this.estimateOf = estimateOf;
        this.inference = inference;
    }

    public oo<MPD, FMap<String, CPD>> maximisation(final MixtureCounts counts, final MixtureEstimation.Context context)
    {
        return oo(estimateOf.MPD_from(counts.forPrior()), estimate(counts, context));
    }

    private FMap<String, CPD> estimate(final MixtureCounts counts, final MixtureEstimation.Context context)
    {
        return counts.forConditionals().applyToValues(null);
    }

    public oo<MixtureCounts, P> expectation(final MPD PI, final FMap<String, CPD> As, final FList<Sequence> Os)
    {
        P likelihood = ONE;

        final MixtureCounts counter = new MixtureCounts();

        for (final Sequence O : Os)
        {
            final oo<MPD, P> result = inference.posteriorAndMarginal(O, PI, As);

            final MPD posterior = result.$1;
            final P marginal = result.$2;

            likelihood = likelihood.x(marginal);
            posterior.each((tag, weight) -> counter.add(weight, tag, O));

        }

        return oo(counter, likelihood);
    }




    public interface Context extends MarkovChainEstimationContext, com.fluent.pgm.estimation.Optimiser.Context,
            com.fluent.pgm.estimation.Estimation.Context
    {
        public static final long DEFAULT_SEED = 1234567890;
        public static final int DEFAULT_MIXTURE_COUNT = 2;
        MixtureEstimation.Context DEFAULT = new MixtureEstimation.Context()
        {
            private final Optimiser.Clock clock = new Optimiser.Clock()
            {
                int duration;

                public boolean tick()
                {
                    return duration++ <= 2_000;
                }
            };

            public double delta()
            {
                return 1e-12;
            }

            public double OOV_weight()
            {
                return 1e-12;
            }

            public boolean withOOV()
            {
                return true;
            }

            public int mixtureCount()
            {
                return 2;
            }

            public long seed()
            {
                return DEFAULT_SEED;
            }

            public double threshold()
            {
                return 0;
            }

            public int maxIteration()
            {
                return 0;
            }

            public Optimiser.Clock clock()
            {
                return clock;
            }

            public Notifier notifier()
            {
                return null;
            }



            public oo<Long, TimeUnit> timeout()
            {
                return oo(1L, TimeUnit.MINUTES);
            }

        };

        public int mixtureCount();

        public long seed();

        public double threshold();

        public int maxIteration();
    }
}
