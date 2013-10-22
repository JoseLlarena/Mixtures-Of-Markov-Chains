package com.fluent.pgm.hmm;

import com.fluent.collections.FList;
import com.fluent.core.F2;
import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.pgm.CPD;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.estimation.Estimation;

import static com.fluent.core.oo.oo;
import static com.fluent.math.P.*;

public class HMM_Estimation2
{
    private HMM_Counting counting;
    private Estimation estimation;

    public HMM_Estimation2(final HMM_Counting counting, final Estimation estimation)
    {
        this.counting = counting;
        this.estimation = estimation;
    }

    public oo<HMM_Counts, P> expectation(final CPD A, final CPD B, final FList<Sequence> data)
    {
        return data.collect(oo(counting.initial(), ONE), collect(A, B));
    }

    public oo<CPD, CPD> maximisation(final HMM_Counts counts, final HMM_EM.Context context)
    {
        final CPD new_A = estimation.CPD_from(counts.transitions(), counts.allStates(), context);
        final CPD new_B = estimation.CPD_from(counts.emissions(), counts.allStates(), context);

        return oo(new_A, new_B);
    }

    private F2<oo<HMM_Counts, P>,Sequence,oo<HMM_Counts, P>> collect(final CPD A, final CPD B)
    {
        return (counts_likelihood, O) ->
                {
                    final HMM_Counts sofar = counts_likelihood.$1;
                    final P likelihood = counts_likelihood.$2;

                    return counting.countsAndMarginal(A, B, O).both((counts, marginal) -> oo(sofar.add(counts),
                            likelihood.x(marginal)));
                };

    }
}
