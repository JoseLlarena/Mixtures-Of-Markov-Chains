package com.fluent.pgm;

import com.fluent.collections.FMap;
import com.fluent.core.oo;
import com.fluent.math.*;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.math.P.*;

public class MPDBuilder
{
	private final FMap<String, P> itemToProbability;
	private P total = ZERO;

	private MPDBuilder()
	{
		itemToProbability = newFMap();
	}

	public MPD and(final String item, final double probability)
	{
		return p(item, probability).done();
	}

	public MPD and(final String item, final P probability)
	{
		return p(item, probability).done();
	}

	public MPD done()
	{
		return new MPT(itemToProbability);
	}

	public MPDBuilder p(final oo<String, P> itemWithProbability)
	{
		total = total.add(itemWithProbability.$2);
		itemToProbability.plus(itemWithProbability.$1, itemWithProbability.$2);

		return this;
	}

	public MPDBuilder p(final String item, final double probability)
	{
		return p(item, P(probability));
	}

	public MPDBuilder p(final String item, final P probability)
	{
		total = total.add(probability);
		itemToProbability.put(item, probability);
		return this;
	}

	public MPDBuilder pOf(final oo<String, Double> itemWithProbability)
	{
		p(itemWithProbability.$1, itemWithProbability.$2);

		return this;
	}

	public static MPDBuilder MPD()
	{
		return new MPDBuilder();
	}

	@SafeVarargs public static MPD MPD(final oo<String, Double> entry, final oo<String, Double>... otherEntries)
	{
		final FMap<String, P> itemToProbability = newFMap();
		P total = P(entry.$2);
		itemToProbability.put(entry.$1, P(entry.$2));

		for (final oo<String, Double> each : otherEntries)
		{
			total = total.add(P(each.$2));
			itemToProbability.put(each.$1, P(each.$2));
		}
		return new MPT(itemToProbability);
	}
}
