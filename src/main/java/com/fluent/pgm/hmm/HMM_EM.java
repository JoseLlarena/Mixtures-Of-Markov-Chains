package com.fluent.pgm.hmm;

import com.fluent.collections.FList;
import com.fluent.core.F3;
import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.pgm.CPD;
import com.fluent.pgm.Notifier;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.estimation.Estimation;
import com.fluent.pgm.estimation.Optimiser;

import java.util.concurrent.TimeUnit;

import static com.fluent.core.oo.*;

public class HMM_EM implements F3<HMM, FList<Sequence>, HMM_EM.Context, oo<HMM, P>>
{
	private final HMM_Estimation2 estimation;

	public HMM_EM(final HMM_Estimation2 estimation)
	{
		this.estimation = estimation;
	}

	public oo<HMM, P> of(final HMM hmm, final FList<Sequence> data, final HMM_EM.Context context)
	{
		final oo<HMM_Counts, P> expectation = estimation.expectation(hmm.transitions(), hmm.emissions(), data);

		final oo<CPD, CPD> maximisation = estimation.maximisation(expectation.$1, context);

		return oo(new HMM(maximisation.$1, maximisation.$2), expectation.$2);
	}

	public interface Context extends Optimiser.Context, Estimation.Context
	{
		int stateCount();

        HMM_EM.Context DEFAULT = new HMM_EM.Context()
        {
            private final Optimiser.Clock clock = new Optimiser.Clock()
            {
                int duration;

                public boolean tick()
                {
                    return duration++ <= 1000;
                }
            };

            public Optimiser.Clock clock()
            {
                return clock;
            }

            public double delta()
            {
                return 1e-6;
            }

            public Notifier notifier()
            {
                return null;
            }

            public double OOV_weight()
            {
                return 0;
            }

            public long seed()
            {
                return 0;
            }

            public int stateCount()
            {
                return 2;
            }

//            public <COMPUTATION extends Execution.Computation<?>> F1<COMPUTATION, Boolean> stopping()
//            {
//                return null;
//            }

            public oo<Long, TimeUnit> timeout()
            {
                return null;
            }

            public boolean withOOV()
            {
                return false;
            }
        };
	}
}
