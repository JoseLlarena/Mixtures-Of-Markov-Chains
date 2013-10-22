package com.fluent.pgm.estimation;

public interface MarkovChainEstimationContext
{
	public double delta();

	public double OOV_weight();

	public boolean withOOV();
}
