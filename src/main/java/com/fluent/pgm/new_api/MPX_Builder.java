package com.fluent.pgm.new_api;

import com.fluent.collections.FMap;
import com.fluent.core.oo;
import com.fluent.math.*;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.math.P.*;

public class MPX_Builder
{
	private final FMap<String, P> itemToProbability;
	private P total = ZERO;

	private MPX_Builder()
	{
		itemToProbability = newFMap();
	}

	public MPX and(final String item, final double probability)
	{
		return p(item, probability).done();
	}

	public MPX and(final String item, final P probability)
	{
		return p(item, probability).done();
	}

	public MPX done()
	{
		return new MPTX(itemToProbability);
	}

	public MPX_Builder p(final oo<String, P> itemWithProbability)
	{
		total = total.add(itemWithProbability.$2);
		itemToProbability.plus(itemWithProbability.$1, itemWithProbability.$2);

		return this;
	}

	public MPX_Builder p(final String item, final double probability)
	{
		return p(item, P(probability));
	}

	public MPX_Builder p(final String item, final P probability)
	{
		total = total.add(probability);
		itemToProbability.put(item, probability);
		return this;
	}


	public static MPX_Builder MPX()
	{
		return new MPX_Builder();
	}

	@SafeVarargs public static MPTX MPD(final oo<String, Double> entry, final oo<String, Double>... otherEntries)
	{
		final FMap<String, P> itemToProbability = newFMap();
		P total = P(entry.$2);
		itemToProbability.put(entry.$1, P(entry.$2));

		for (final oo<String, Double> each : otherEntries)
		{
			total = total.add(P(each.$2));
			itemToProbability.put(each.$1, P(each.$2));
		}
		return new MPTX(itemToProbability);
	}
}
