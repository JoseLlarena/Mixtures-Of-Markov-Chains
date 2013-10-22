package com.fluent.pgm;

import com.fluent.collections.FMap;
import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.pgm.estimation.FHashTriMap;
import com.fluent.pgm.estimation.FTriMap;

import static com.fluent.core.Preconditions.reject;
import static com.fluent.math.P.*;
import static com.fluent.pgm.Sequence.START;

public class CPDBuilder
{
	private final FTriMap<String, String, P> contextTokenProability;
	private final boolean withOOV;

	private CPDBuilder(final boolean withOOV)
	{
		contextTokenProability = new FHashTriMap<>();
		this.withOOV = withOOV;
	}

	public CPD and(final String token, final String context, final double probability)
	{
		return p(token, context, probability).done();
	}

	public CPD and(final String token, final String context, final P probability)
	{
		return p(token, context, probability).done();
	}

	public CPD done()
	{
		for (final FMap<String, P> conditional : contextTokenProability.asFMap().values())
		{
			final P mass = conditional.values().collect(ZERO, (sum, item) -> sum.add(item));

			reject(mass.lt(P(.999999)), "All probabilities should plus up to 1 but got " + mass + "  in " + conditional);
		}

		return withOOV ? new CPT_with_OOV(contextTokenProability.asFMap()) : new CPT(contextTokenProability);
	}

	public CPDBuilder p(final oo<String, String> transit, final double probability)
	{
		return p(transit, P(probability));
	}

	public CPDBuilder p(final oo<String, String> transit, final P probability)
	{
		contextTokenProability.add(transit.$1, transit.$2, probability);

		return this;
	}

	public CPDBuilder p(final String token, final double probability)
	{
		return p(token, P(probability));
	}

	public CPDBuilder p(final String token, final P probability)
	{
		return p(token, START, probability);
	}

	public CPDBuilder p(final String token, final String context, final double probability)
	{
		return p(token, context, P(probability));
	}

	public CPDBuilder p(final String token, final String context, final P probability)
	{
		contextTokenProability.add(context, token, probability);

		return this;
	}

	public static CPDBuilder CPD()
	{
		return new CPDBuilder(false);
	}

	public static CPDBuilder CPD_with_OOV()
	{
		return new CPDBuilder(true);
	}
}