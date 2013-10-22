package com.fluent.pgm.hmm;

import com.fluent.pgm.WeightMap;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.*;
import static com.fluent.core.ooo.ooo;
import static com.fluent.pgm.Sequence.*;

public class CountsSpec extends AbstractSpec
{
	@Test public void knows_probability_of_END_observation_emitted_by_END_state_is_always_one() throws Exception
	{
		So(new HMM_Counts().emissions()).shouldBe(new WeightMap(ooo(END_STATE, END, 1.)));
	}

	@Test public void knows_probability_of_END_states_is_always_one() throws Exception
	{
		So(new HMM_Counts().allStates()).shouldBe(newFMap(oo(START_STATE, 0.), oo(END_STATE, 1.)));
	}

	@Test public void never_updates_count_of_END_state() throws Exception
	{
		So(new HMM_Counts().addState(END_STATE, 1.)).shouldBe(new HMM_Counts());
	}

	@Test public void never_updates_count_of_END_state_emitting_END_observation() throws Exception
	{
		So(new HMM_Counts().addEmission(END_STATE, END, 1.)).shouldBe(new HMM_Counts());
	}

	@Test public void updates_transition_counts() throws Exception
	{
		final HMM_Counts update = new HMM_Counts().addTransition("x", "y", .2);

		WHEN(new HMM_Counts().addTransition("x", "y", .3).add(update));

		THEN(theOutcome).shouldBe(new HMM_Counts().addTransition("x", "y", .5));
	}
}
