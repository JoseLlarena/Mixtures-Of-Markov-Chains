package com.fluent.pgm;

import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.math.P;

/**
 * Conditional Probability Function
 */
public interface CPD  extends FMap<String, FMap<String, P>>
{
	public FSet<String> contexts();

	public P p(final String token, final String context);

	public FSet<String> tokens();

}