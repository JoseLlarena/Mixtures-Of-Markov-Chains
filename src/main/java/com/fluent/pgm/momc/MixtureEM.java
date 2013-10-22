package com.fluent.pgm.momc;

import com.fluent.collections.FList;
import com.fluent.core.F3;
import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.pgm.Sequence;

import static com.fluent.core.oo.oo;

public class MixtureEM implements F3<Mixture, FList<Sequence>, MixtureEstimation.Context, oo<Mixture, P>>
{
	private final MixtureEstimation estimation;

	public MixtureEM(final MixtureEstimation estimateOf)
	{
		estimation = estimateOf;
	}

	public oo<Mixture, P> of(final Mixture mixture, final FList<Sequence> observations, final MixtureEstimation.Context context)
	{
		final oo<MixtureCounts, P> result = estimation.expectation(mixture.prior(), mixture.conditionals(), observations);

       return  estimation.maximisation(result.$1, context).both((prior, conditionals) -> oo(new Mixture(prior,
               conditionals), result.$2)) ;

	}

}