package com.fluent.pgm.estimation;

import com.fluent.core.oo;
import com.fluent.pgm.Notifier;

import java.util.concurrent.TimeUnit;


public class Optimiser
{
//	private final Execution execution;
//
//	public Optimiser(final Execution execution)
//	{
//		this.execution = execution;
//	}
//
//	public <MODEL, CONTEXT extends Optimiser.Context> Computation<oo<MODEL, P>> asComputation(//[
//
//			final F3<MODEL, FList<Sequence>, CONTEXT, oo<MODEL, P>> toBeOptimised,
//			final MODEL model,
//			final FList<Sequence> data,
//			final CONTEXT context)//]
//	{
//		return new AbstractComputation<oo<MODEL, P>>()
//		{
//			public void compute() throws Exception
//			{
//				oo<P, P> newFitnessooldFitness = oo(ZERO, ZERO);
//				oo<MODEL, P> result = oo(model, ZERO);
//
//				context.notifier().notifyOfStart();
//
//				do
//				{
//					update(result = toBeOptimised.of(result.$1, data, context));
//
//					context.notifier().notifyOf(newFitnessooldFitness = oo(result.$2, newFitnessooldFitness.$1));
//				}
//				while (!context.stopping().of(this));
//
//				context.notifier().notifyOfEnd();
//			}
//		};
//	}
//
//	public <MODEL, CONTEXT extends Optimiser.Context> oo<MODEL, P> optimise(//[
//
//			final F3<MODEL, FList<Sequence>, CONTEXT, oo<MODEL, P>> toBeOptimised,
//			final MODEL model,
//			final FList<Sequence> data,
//			final CONTEXT context)//]
//	{
//		oo<P, P> newFitness_oldFitness = oo(ZERO, ZERO);
//		oo<MODEL, P> result = oo(model, ZERO);
//
//		do
//		{
//			result = toBeOptimised.of(result.$1, data, context) ;
//
//			newFitness_oldFitness = oo(result.$2, newFitness_oldFitness.$1);
//		}
//		while (context.clock().tick());
//
//		return oo(result.$1, newFitness_oldFitness.$1);
//
//	}
//
//	public <MODEL, CONTEXT extends Optimiser.Context> MODEL reestimate(//[
//
//			final F3<MODEL, FList<Sequence>, CONTEXT, oo<MODEL, P>> toBeOptimised,
//			final MODEL model,
//			final FList<Sequence> data,
//			final CONTEXT context)//]
//			throws Exception
//	{
//		final Computation<oo<MODEL, P>> computation = asComputation(toBeOptimised, model, data, context);
//
//		final Result<oo<MODEL, P>> synchronous = null;// execution.synchronous(computation, DEFAULT.timeout());
//
//		return synchronous.value().$1;
//	}
//
//	<MODEL, CONTEXT extends Optimiser.Context> Computation<oo<MODEL, P>> asComputation2(//[
//
//			final F3<MODEL, FList<Sequence>, CONTEXT, oo<MODEL, P>> toBeOptimised,
//			final MODEL model,
//			final FList<Sequence> data,
//			final CONTEXT context)//]
//	{
//		return new AbstractComputation<oo<MODEL, P>>()
//		{
//			public void compute() throws Exception
//			{
//				oo<P, P> newFitnessooldFitness = oo(ZERO, ZERO);
//				oo<MODEL, P> result = oo(model, ZERO);
//
//				do
//				{
//					update(result = toBeOptimised.of(result.$1, data, context));
//
//					newFitnessooldFitness = oo(result.$2, newFitnessooldFitness.$1);
//				}
//				while (!context.stopping().of(this));
//			}
//		};
//	}

	public interface Clock
	{
		public boolean tick();
	}

	public interface Context
	{
		public Clock clock();

		public Notifier notifier();

		public long seed();



		public oo<Long, TimeUnit> timeout();
	}
}
