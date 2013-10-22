package com.fluent.pgm.hmm;

import com.fluent.core.oo;
import com.fluent.math.P;

class State_Probability extends oo<String, P> implements Comparable<State_Probability>
{
	protected State_Probability(final String $1, final P $2)
	{
		super($1, $2);
	}

	public int compareTo(final State_Probability o)
	{
		return $2.compareTo(o.$2);
	}

	public P p()
	{
		return $2;
	}

	public String state()
	{
		return $1;
	}

	static State_Probability state_probability(final String state, final P p)
	{
		return new State_Probability(state, p);
	}
}