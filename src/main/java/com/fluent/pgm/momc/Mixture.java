package com.fluent.pgm.momc;

import com.fluent.collections.FMap;
import com.fluent.core.oo;
import com.fluent.pgm.CPD;
import com.fluent.pgm.MPD;

import static java.lang.String.format;

public class Mixture extends oo<MPD, FMap<String, CPD>>
{
	public Mixture(final MPD prior, final FMap<String, CPD> conditionals)
	{
		super(prior, conditionals);
	}

    public FMap<String, CPD> conditionals()
	{
		return $2;
	}

	public String toString()
	{
		return format("%s%n%s", prior(), conditionals().toString("%s:%n%n%s"));
	}

    public MPD prior()
	{
		return $1;
	}
}