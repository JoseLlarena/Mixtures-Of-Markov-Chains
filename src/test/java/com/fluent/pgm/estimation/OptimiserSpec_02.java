package com.fluent.pgm.estimation;

import com.fluent.specs.unit.AbstractSpec;

@SuppressWarnings({ "unchecked" })
public class OptimiserSpec_02 extends AbstractSpec
{
//	@Mock F3<Object, FList<Sequence>, Optimiser.Context, oo<Object, P>> function;
//	@Mock FList<Sequence> data;
//	@Mock Optimiser.Context context;
//	@Mock Object initial, reestimated, reestimatedAgain;
//
//	@Mock Clock stopper;
//	P initialEstimate = P.ZERO, firstEstimate = P.P(.5), secondEstimate = P.ONE;
//
//	@Before public void BACKGROUND()
//	{
//		IF(context.clock()).RETURNS(stopper);
//		IF(function.of(initial, data, context)).RETURNS(oo(reestimated, firstEstimate));
//	}
//
//	@Test public void stopping_criterion_first_says_continue_then_stop() throws Exception
//	{
//		GIVEN(stopper.tick()).RETURNS(true, false);
//		GIVEN(function.of(reestimated, data, context)).RETURNS(oo(reestimatedAgain, secondEstimate));
//
//		WHEN(new Optimiser(null).optimise(function, initial, data, context));
//
//		THEN(theOutcome).shouldBe(oo(reestimatedAgain, secondEstimate));
//	}
//
//	@Test public void stopping_criterion_says_stop() throws Exception
//	{
//		GIVEN(stopper.tick()).RETURNS(false);
//
//		WHEN(new Optimiser(null).optimise(function, initial, data, context));
//
//		THEN(theOutcome).shouldBe(oo(reestimated, firstEstimate));
//	}
}
