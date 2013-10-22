package com.fluent.pgm;

import static com.fluent.collections.Maps.newFMap;

import com.fluent.collections.FMap;

public class ConditionalsBuilder
{
	private final FMap<String, CPD> map = newFMap();

	public ConditionalsBuilder add(final String tag, final CPD cpd)
	{
		map.put(tag, cpd);

		return this;
	}

	public FMap<String, CPD> plus(final String tag, final CPD cpd)
	{
		return add(tag, cpd).map;
	}

	public static ConditionalsBuilder conditionals()
	{
		return new ConditionalsBuilder();
	}
}