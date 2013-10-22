package com.fluent.pgm.momc;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.collections.Lists;
import com.fluent.core.oo;
import com.fluent.pgm.*;
import com.fluent.pgm.estimation.Counting;
import com.fluent.pgm.estimation.Estimation;
import com.fluent.pgm.momc.MixtureEstimation.Context;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.Random;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.collections.Sets.asFSet;
import static com.fluent.core.oo.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings({ "unchecked", "unused" })
public class MixtureInitialiserSpec extends AbstractSpec
{
	MixtureInitialiser initialiser;

	@Mock Counting counting;
	@Mock Estimation estimation;
	FList<Sequence> observations = Lists.asFList((Sequence) new Sequence("A"));
	@Mock Context context;

	oo<MPD, FMap<String, CPD>> expected;
	@Mock MPD prior;
	@Mock CPD conditional;
	@Mock Random weights;
	int N = 2;

	@Test public void should_estimate_initial_parameters() throws Exception
	{
		GIVEN(context.mixtureCount()).RETURNS(N);
		GIVEN(prior.tokens()).RETURNS(asFSet("1", "2"));
		GIVEN_SPY(initialiser).RETURNS(prior).ON().initPrior(eq(N), Matchers.any(Random.class));
		GIVEN_SPY(initialiser).RETURNS(newFMap(oo("1", conditional), oo("2", conditional))).ON()
				.initConditionals(asFSet("1", "2"), observations, context);

		WHEN(initialiser.init(observations, context));

		THEN(theOutcome).shouldBe(new Mixture(prior, newFMap(oo("1", conditional), oo("2", conditional))));
	}

	@Test public void should_initialise_conditionals_at_random() throws Exception
	{
		final double weight1 = .5;
		final double weight2 = .7;
		final WeightMap counts = mock(WeightMap.class);

		GIVEN(counting.bigramFrequency(observations)).RETURNS(counts);

		WHEN(initialiser.initConditionals(asFSet("1", "2"), observations, context).size());

		THEN(counting).should().bigramFrequency(observations);
		AND(estimation).should(times(2)).CPD_from(counts, context);
		AND(theOutcome).shouldBe(N);
	}

	@Before public void BACKGROUND() throws Exception
	{
		GIVEN: initialiser = spy(new MixtureInitialiser(counting, estimation));
	}

	@Test public void should_initialise_prior_at_random() throws Exception
	{
		final double weight = .5;

		GIVEN(weights.nextDouble()).RETURNS(weight);

		WHEN(initialiser.initPrior(N, weights));

		THEN(theOutcome).shouldBe(MPDBuilder.MPD().p("1", 1. / N - 1. / (N * N + weight)).and("2", 1 - (1. / N - 1. / (N * N + weight))));
	}
}
