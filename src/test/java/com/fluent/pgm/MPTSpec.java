package com.fluent.pgm;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.math.P.P;
import static com.fluent.math.P.ZERO;

import org.junit.Test;

import com.fluent.collections.Maps;
import com.fluent.math.P;
import com.fluent.specs.unit.AbstractSpec;

public class MPTSpec extends AbstractSpec
{
	MPT mpt;

	@Test public void should_give_correct_probability_of_known_token() throws Exception
	{
		GIVEN: mpt = new MPT(newFMap(String.class, P.class).plus("1", P(.9)).plus("2", P(.8)));

		WHEN(mpt.p("1"));

		THEN(theOutcome).shouldBe(P(.9));
	}

	@Test public void should_give_zero_probability_to_unknown_token() throws Exception
	{
		GIVEN: mpt = new MPT(Maps.<String, P> newFMap().plus("1", P(.9)).plus("2", P(.8)));

		WHEN(mpt.p("A"));

		THEN(theOutcome).shouldBe(ZERO);
	}

}
