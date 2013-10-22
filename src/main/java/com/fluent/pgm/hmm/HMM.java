package com.fluent.pgm.hmm;

import com.fluent.core.oo;
import com.fluent.pgm.CPD;

public class HMM extends oo<CPD, CPD>
{
	public HMM(final CPD transitions, final CPD emissions)
	{
		super(transitions, emissions);
	}

	public CPD emissions()
	{
		return $2;
	}

	public String toString()
	{
		return toString("A:%n%s%nB:%n%s");
	}

	public CPD transitions()
	{
		return $1;
	}
}
