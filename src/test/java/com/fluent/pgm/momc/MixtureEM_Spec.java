package com.fluent.pgm.momc;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.math.*;
import com.fluent.pgm.CPD;
import com.fluent.pgm.MPD;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.momc.MixtureEstimation.Context;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;
import org.mockito.Mock;

import static com.fluent.core.oo.oo;

public class MixtureEM_Spec extends AbstractSpec
{
	MixtureEM em;

	@Mock MixtureEstimation estimation;
	Mixture mixture;
	@Mock FList<Sequence> observations;
	@Mock Context context;

	@Mock MPD prior;
	@Mock FMap<String, CPD> conditionals;
	@Mock MixtureCounts counts;
	@Mock P p;

	@Test public void steps_through_expectation_and_maximisation() throws Exception
	{
		GIVEN(estimation.expectation(prior, conditionals, observations)).RETURNS(oo(counts, p));
		GIVEN(estimation.maximisation(counts, context)).RETURNS(oo(prior, conditionals));

		WHEN(new MixtureEM(estimation).of(new Mixture(prior, conditionals), observations, context));

		THEN(theOutcome).shouldBe(oo(new Mixture(prior, conditionals), p));
	}
}
