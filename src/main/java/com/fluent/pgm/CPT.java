package com.fluent.pgm;

import com.fluent.collections.FImmutableHashMap;
import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.math.*;
import com.fluent.pgm.estimation.FTriMap;

import static com.fluent.math.P.*;
import static com.fluent.pgm.Sequence.*;
import static java.util.Objects.hash;

public class CPT extends FImmutableHashMap<String, FMap<String, P>> implements CPD
{
	private final FTriMap<String, String, P> contextTokenProbability;

	public CPT(final FTriMap<String, String, P> tokenContextProbability)
	{
		contextTokenProbability = tokenContextProbability;
	}

	public FSet<String> contexts()
	{
		return contextTokenProbability.firstKeys();
	}

	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final CPT other = (CPT) obj;
		if (!contextTokenProbability.equals(other.contextTokenProbability))
		{
			return false;
		}
		return true;
	}

	public int hashCode()
	{
		return hash(contextTokenProbability);
	}

	public P p(final String token, final String context)
	{
		if (token == START && context == START_STATE || context == END_STATE && token == END)
		{
			return ONE;
		}

		if (token == START_STATE || token == START || context == END_STATE || context == END || token == END_STATE
				&& context == START_STATE || token == END && context == START)
		{
			return ZERO;
		}

		final P p = contextTokenProbability.get(context, token);

		if (p != null)
		{
			return p;
		}

		return ZERO;
	}

	public FSet<String> tokens()
	{
		return contextTokenProbability.secondKeys();
	}

	public String toString()
	{
		return contextTokenProbability.toString("(%s%s) --> %s");
	}
}
