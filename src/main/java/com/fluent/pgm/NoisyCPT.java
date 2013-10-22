package com.fluent.pgm;

import com.fluent.collections.FImmutableHashMap;
import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.math.*;

import static com.fluent.math.P.*;

public class NoisyCPT extends FImmutableHashMap<String, FMap<String, P>> implements CPD
{
	private final P delta;
	private final FSet<String> symbols;
	private final P oneMinusDelta;

	public NoisyCPT(final FSet<String> symbols, final P delta)
	{
		this.symbols = symbols;
		this.delta = delta;
		oneMinusDelta = P(1 - delta.toDouble());
	}


	@Override public FSet<String> contexts()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public P p(final String token, final String condition)
	{
		return token.equals(condition) ? oneMinusDelta : delta;
	}

	public FSet<String> tokens()
	{
		return symbols;
	}

}
