package com.fluent.pgm;

import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.math.*;

/**
 * Marginal Probability Distribution
 */
public interface MPD  extends FMap<String, P>
{
	public P p(String token);

	public int size();

	public FSet<String> tokens();
}
