package com.fluent.pgm.estimation;

import static java.util.Arrays.asList;
import static com.fluent.collections.Maps.newFMap;

import org.junit.Test;

import com.fluent.specs.unit.AbstractSpec;

public class CountingSpec extends AbstractSpec
{
	@Test public void calculates$relative_frequencies_of_items_in_collection() throws Exception
	{
		WHEN(new Counting().frequency(asList("A", "A", "B", "B")));

		THEN(theOutcome).shouldBe(newFMap().plus("A", 2 / 4.).plus("B", 2 / 4.));
	}

	@Test public void counts_occurencies_of_items_in_collection() throws Exception
	{
		WHEN(new Counting().count(asList("A", "A", "B", "B")));

		THEN(theOutcome).shouldBe(newFMap().plus("A", 2).plus("B", 2));
	}
}
