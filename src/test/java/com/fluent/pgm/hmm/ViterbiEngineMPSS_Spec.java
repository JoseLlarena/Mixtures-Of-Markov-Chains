package com.fluent.pgm.hmm;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static com.fluent.collections.Lists.newFList;
import static com.fluent.collections.Sets.newFSet;
import static com.fluent.math.P.ONE;
import static com.fluent.math.P.P;
import static com.fluent.math.P.ZERO;
import static com.fluent.pgm.Sequence.END_STATE;
import static com.fluent.pgm.hmm.State_Probability.state_probability;

import com.fluent.collections.Lists;
import com.fluent.collections.Sets;
import com.fluent.pgm.Sequence;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.fluent.collections.FSet;
import com.fluent.math.P;
import com.fluent.pgm.CPD;
import com.fluent.pgm.hmm.ViterbiInference.PathMemory;
import com.fluent.specs.unit.AbstractSpec;

public class ViterbiEngineMPSS_Spec extends AbstractSpec
{
	ViterbiInference engine;

	String y = "b", x = "a", Y = "B", X = "A";
	@Mock CPD transitions, emissions;
	Sequence O;
	@Mock PathMemory memory;

	@Before public void BACKGROUND()
	{
		GIVEN(transitions.tokens()).RETURNS(Sets.asFSet(X, Y, END_STATE));
		AND: engine = spy(new ViterbiInference());
	}

	@Test public void should_add_correct_max_transits_to_memory() throws Exception
	{
		GIVEN(transitions.p(X, X)).RETURNS(P(.49)).AND(transitions.p(Y, X)).RETURNS(P(.49)).AND(transitions.p(END_STATE, X))
				.RETURNS(P(.02));
		GIVEN(transitions.p(X, Y)).RETURNS(P(.49)).AND(transitions.p(Y, Y)).RETURNS(P(.49)).AND(transitions.p(END_STATE, Y))
				.RETURNS(P(.02));
		GIVEN(transitions.p(X, END_STATE)).RETURNS(ZERO).AND(transitions.p(Y, END_STATE)).RETURNS(ZERO)
				.AND(transitions.p(END_STATE, END_STATE)).RETURNS(ONE);

		final P PofX = P(.49).x(P(.99));
		final P PofY = P(.51).x(P(.02));
		final FSet<State_Probability> joints = Sets.asFSet(o(X, PofX), o(Y, PofY), o(END_STATE, ZERO));

		WHEN(engine.maxTransits(joints, memory, transitions));

		THEN(memory).should().add(X, X);
		AND(memory).should().add(Y, X);
		AND(memory).should().add(END_STATE, X);
	}

	@Test public void should_know_how_to_calculate_max_transits() throws Exception
	{
		GIVEN(transitions.p(X, X)).RETURNS(P(.49)).AND(transitions.p(Y, X)).RETURNS(P(.49)).AND(transitions.p(END_STATE, X))
				.RETURNS(P(.02));
		GIVEN(transitions.p(X, Y)).RETURNS(P(.49)).AND(transitions.p(Y, Y)).RETURNS(P(.49)).AND(transitions.p(END_STATE, Y))
				.RETURNS(P(.02));
		GIVEN(transitions.p(X, END_STATE)).RETURNS(ZERO).AND(transitions.p(Y, END_STATE)).RETURNS(ZERO)
				.AND(transitions.p(END_STATE, END_STATE)).RETURNS(ONE);

		final P PofX = P(.49).x(P(.99));
		final P PofY = P(.51).x(P(.02));
		final FSet<State_Probability> joints = Sets.asFSet(o(X, PofX), o(Y, PofY), o(END_STATE, ZERO));

		WHEN(engine.maxTransits(joints, memory, transitions));

		THEN(theOutcome).shouldBe(Sets.asFSet(o(X, PofX.x(P(.49))), o(Y, PofX.x(P(.49))), o(END_STATE, PofX.x(P(.02)))));
	}

	@Test public void should_know_most_probable_state_sequence() throws Exception
	{
		final State_Probability mostLikely = o(X, ONE);
		final State_Probability lessLikely = o(Y, ZERO);
		final FSet<State_Probability> anyJoints = Sets.asFSet(mostLikely, lessLikely);
		final FSet<State_Probability> anyMaxTransitions = newFSet();

		GIVEN(memory.backtrack()).RETURNS(Lists.asFList(X, Y));
		GIVEN_SPY(engine).RETURNS(memory).ON().memory();
		GIVEN_SPY(engine).RETURNS(anyMaxTransitions).ON().prior(transitions);
		GIVEN_SPY(engine).RETURNS(anyMaxTransitions).ON().maxTransits(anyJoints, memory, transitions);
		GIVEN_SPY(engine).RETURNS(anyJoints).ON().alignmentsAt(anyString(), eq(anyMaxTransitions), eq(emissions));

		WHEN(engine.mpss(new Sequence(x + y), transitions, emissions));

		THEN(engine).should(times(2)).maxTransits(anyJoints, memory, transitions);
		AND(engine).should(times(3)).alignmentsAt(anyString(), eq(anyMaxTransitions), eq(emissions));
		AND(theOutcome).shouldBe(Lists.asFList(X, Y));
	}

	private static State_Probability o(final String state, final P probability)
	{
		return state_probability(state, probability);
	}

}