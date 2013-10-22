package com.fluent.pgm.hmm;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static com.fluent.collections.Lists.newFList;

import com.fluent.collections.Lists;
import com.fluent.pgm.Sequence;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.fluent.collections.FList;
import com.fluent.pgm.CPD;
import com.fluent.specs.unit.AbstractSpec;

public class HHM_InitialiserSpec_0_1 extends AbstractSpec
{
	HMM_Initialiser initialiser;

	@Mock FList<Sequence> data;
	@Mock HMM_EM.Context context;

	@Mock CPD transitions;
	@Mock CPD emissions;

	@Before public void BACKGROUND() throws Exception
	{
		initialiser = spy(new HMM_Initialiser());
	}

	@Test public void builds_hmm_using_state_count_seed_and_data() throws Exception
	{
		GIVEN(context.stateCount()).RETURNS(2).AND(context.seed()).RETURNS(101010L);
		GIVEN_SPY(initialiser).RETURNS(transitions).ON().transitions(2, 101010L);
		GIVEN_SPY(initialiser).RETURNS(emissions).ON().emissions(2, 101010L, data);

		WHEN(initialiser.init(data, context));

		THEN(initialiser).should().transitions(2, 101010L);
		AND(initialiser).should().emissions(2, 101010L, data);
		AND(theOutcome).shouldBe(new HMM(transitions, emissions));
	}

	@Test public void makes_emission_CPD() throws Exception
	{
		So(initialiser.emissions(2, 101010L, Lists.asFList((Sequence) new Sequence("ABC"), new Sequence("BA")))).shouldBe(
				notNullValue(CPD.class));
	}

	@Test public void makes_pmf() throws Exception
	{
		final double[] pmf = HMM_Initialiser.randomPMF(10, 123412341234L);

		double sum = 0;
		for (final double d : pmf)
		{
			sum += d;
		}

		assertThat(sum, is(closeTo(1., .00001)));
	}

	@Test public void makes_transition_CPD() throws Exception
	{
		So(initialiser.transitions(2, 101010L)).shouldBe(notNullValue(CPD.class));
	}
}
