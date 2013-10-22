package com.fluent.pgm.api;

import com.fluent.collections.FList;
import com.fluent.core.F3;
import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.pgm.Inference;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.estimation.Estimation;
import com.fluent.pgm.estimation.Optimiser;
import com.fluent.pgm.hmm.*;
import com.fluent.pgm.momc.Mixture;
import com.fluent.pgm.momc.MixtureEM;
import com.fluent.pgm.momc.MixtureEstimation;
import com.fluent.pgm.momc.MixtureInitialiser;

import java.util.function.UnaryOperator;

import static java.lang.System.out;
import static java.util.Arrays.binarySearch;

public class Estimation_API
{
    public static final String UNKNOWN_TAG = "?";
    private static final int[] POINTS = new int[]{0, 1, 2, 5, 7, 10, 25, 50, 75, 100, 250, 500, 750, 1_000, 2_500, 5_000, 7_500, 10_000, 25_000,
            50_000, 100_000, 500_000, 1_000_000, 5_000_000, 10_000_000, 50_000_000, 100_000_000, 500_000_000, 1_000_000_000};
    private final MixtureEM mixtureEM;
    private final Optimiser optimiser;
    private final HMM_Initialiser hmmInitialiser;
    private final HMM_EM hmmEM;
    private MixtureInitialiser mixtureInitialiser;

    public Estimation_API(final HMM_Initialiser hmmInitialiser, final Optimiser optimiser)
    {
        this.optimiser = optimiser;

        this.hmmInitialiser = hmmInitialiser;
        hmmEM = new HMM_EM( new HMM_Estimation2(new HMM_Counting(BaumWelchInference.INSTANCE), Estimation.of));

        mixtureEM = null;
    }

    protected Estimation_API(final Optimiser optimiser, final HMM_Initialiser hmmInitialiser, final HMM_EM hmmEM)
    {
        this.optimiser = optimiser;

        this.hmmInitialiser = hmmInitialiser;
        this.hmmEM = hmmEM;

        mixtureEM = null;
    }

    public Estimation_API(final MixtureInitialiser mixtureInitialiser, final Optimiser optimiser)
    {
        this(mixtureInitialiser, new MixtureEM(new MixtureEstimation(Estimation.of, Inference.of)), optimiser);
    }

    public Estimation_API(final MixtureInitialiser mixtureInitialiser, MixtureEM mixtureEM, final Optimiser optimiser)
    {
        this.optimiser = optimiser;

        this.mixtureInitialiser = mixtureInitialiser;
        this.mixtureEM = mixtureEM;

        hmmEM = new HMM_EM( new HMM_Estimation2(new HMM_Counting(BaumWelchInference.INSTANCE), Estimation.of));
        hmmInitialiser = null;
    }

    private static <MODEL, CONTEXT extends Optimiser.Context> F3<MODEL, FList<Sequence>, CONTEXT, oo<MODEL, P>> notifying(
            final F3<MODEL, FList<Sequence>, CONTEXT, oo<MODEL, P>> em)
    {
        final UnaryOperator<oo<MODEL, P >> pipe = new UnaryOperator<oo<MODEL, P>>()
        {
            int iteration = 0;

            public oo<MODEL, P> apply(final oo<MODEL, P> result)
            {
                if (binarySearch(POINTS, iteration++) >= 0)
                    out.printf("@ [%s] Log-Likelihood [%f]%n", iteration - 1, result.$2.asLog(), result.$1);

                return result;
            }
        };


        return (model, data, context) -> pipe.apply(em.of(model, data, context));
    }

    public HMM hmmFrom(final FList<Sequence> data, final HMM_EM.Context context) throws Exception
    {
        final HMM init = hmmInitialiser.init(data, context);

        return null;//optimiser.optimise(notifying(hmmEM), init, data, context).$1;
    }

    public Mixture mixtureFrom(final FList<Sequence> data, final MixtureEstimation.Context context) throws Exception
    {
        final Mixture initial = mixtureInitialiser.init(data, context);

        return null;//optimiser.optimise(notifying(mixtureEM), initial, data, context).$1;
    }
}