package com.fluent.pgm.hmm;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.core.oo.oo;
import static com.fluent.pgm.Sequence.END_STATE;

import com.fluent.collections.Lists;
import org.junit.Test;

import com.fluent.pgm.hmm.ViterbiInference.PathMemory;
import com.fluent.specs.unit.AbstractSpec;

@SuppressWarnings("unused")
public class PathMemorySpec extends AbstractSpec
{
	PathMemory memory;
	String B = "B", A = "A";

	@Test public void should_backtrack_to_given_mappings_of_state_and_time_to_maximising_state() throws Exception
	{
		GIVEN: memory = new PathMemory();
		AND: memory.add(B, A);
		AND: memory.add(A, A);
		AND: memory.add(END_STATE, A);
		AND: memory.nextT();
		AND: memory.add(A, B);
		AND: memory.add(B, B);
		AND: memory.add(END_STATE, B);
		AND: memory.nextT();

		WHEN(memory.backtrack());

		THEN(theOutcome).shouldBe(Lists.asFList(A, B));
	}

	@Test public void should_backtrack_to_sequence_of_maximising_states() throws Exception
	{
		GIVEN: memory = new PathMemory();
		AND: memory.add(B, A);
		AND: memory.add(A, A);
		AND: memory.add(END_STATE, A);
		AND: memory.nextT();

		WHEN: memory.add(A, B);
		AND: memory.add(B, B);
		AND: memory.add(END_STATE, B);

		THEN(memory.transitionsAtT.get(oo(3, A))).shouldBe(B);
		AND(memory.transitionsAtT.get(oo(3, B))).shouldBe(B);
		AND(memory.transitionsAtT.get(oo(3, END_STATE))).shouldBe(B);
	}

	@Test public void should_map_state_and_time_2_to_maximising_state_after_creation() throws Exception
	{
		GIVEN: memory = new PathMemory();

		WHEN: memory.add(B, A);
		AND: memory.add(A, A);
		AND: memory.add(END_STATE, A);

		THEN(memory.transitionsAtT.get(oo(2, A))).shouldBe(A);
		AND(memory.transitionsAtT.get(oo(2, B))).shouldBe(A);
		AND(memory.transitionsAtT.get(oo(2, END_STATE))).shouldBe(A);
	}
}
