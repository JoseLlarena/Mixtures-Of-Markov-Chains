package com.fluent.pgm.estimation;

import com.fluent.specs.unit.AbstractSpec;

@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class OptimisersComputation_Spec extends AbstractSpec
{
//	Computation<oo<Object, P>> computation;
//
//	@Mock F3<Object, FList<Sequence>, Optimiser.Context, oo<Object, P>> function;
//	@Mock FList<Sequence> data;
//	@Mock Optimiser.Context context;
//
//	@Mock F1<Computation<oo<Object, P>>, Boolean> stopper;
//	@Mock Object initial, reestimated, reestimatedAgain;
//	P initialEstimate = P.ZERO, firstEstimate = P.P(.5), secondEstimate = P.ONE;
//	@Mock Notifier notifier;
//
//	@Before public void BACKGROUND()
//	{
//		computation = new Optimiser(null).asComputation(function, initial, data, context);
//
//		IF((F1) context.stopping()).RETURNS(stopper).AND(context.notifier()).RETURNS(notifier);
//		IF(function.of(initial, data, context)).RETURNS(oo(reestimated, firstEstimate));
//	}
//
//	@Test public void notification_if_stopping_criterion_first_says_continue_then_stop() throws Exception
//	{
//		IF(stopper.of(computation)).RETURNS(false, true);
//		IF(function.of(reestimated, data, context)).RETURNS(oo(reestimatedAgain, secondEstimate));
//
//		CALLING: computation.compute();
//
//		WILL_CALL(notifier).notifyOfStart();
//		AND_ALSO(notifier).notifyOf(oo(firstEstimate, initialEstimate));
//		AND_ALSO(notifier).notifyOf(oo(secondEstimate, firstEstimate));
//		AND_ALSO(notifier).notifyOfEnd();
//	}
//
//	@Test public void notification_if_stopping_criterion_says_stop() throws Exception
//	{
//		IF(stopper.of(computation)).RETURNS(true);
//
//		CALLING: computation.compute();
//
//		WILL_CALL(notifier).notifyOfStart();
//		AND_ALSO(notifier).notifyOf(oo(firstEstimate, initialEstimate));
//		AND_ALSO(notifier).notifyOfEnd();
//	}
//
//	@Test public void stopping_criterion_first_says_continue_then_stop() throws Exception
//	{
//		GIVEN(stopper.of(computation)).RETURNS(false, true);
//		GIVEN(function.of(reestimated, data, context)).RETURNS(oo(reestimatedAgain, secondEstimate));
//
//		WHEN: computation.compute();
//
//		THEN(computation.result()).shouldBe(oo(reestimatedAgain, secondEstimate));
//	}
//
//	@Test public void stopping_criterion_says_stop() throws Exception
//	{
//		GIVEN(stopper.of(computation)).RETURNS(true);
//
//		WHEN: computation.compute();
//
//		THEN(computation.result()).shouldBe(oo(reestimated, firstEstimate));
//	}
}
