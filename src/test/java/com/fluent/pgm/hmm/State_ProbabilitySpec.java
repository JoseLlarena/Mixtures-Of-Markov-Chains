package com.fluent.pgm.hmm;

import static com.fluent.math.P.ZERO;

import org.junit.Test;

import com.fluent.specs.unit.AbstractSpec;

public class State_ProbabilitySpec extends AbstractSpec
{
	static final String ANY_STRING = "ANY";

	@Test public void should_be_equal_to_another_instance_containing_the_same_state_and_probability() throws Exception
	{
		THEN(new State_Probability(ANY_STRING, ZERO)).shouldBe(new State_Probability(ANY_STRING, ZERO));
	}
}
