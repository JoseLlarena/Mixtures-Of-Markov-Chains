package com.fluent.pgm.momc;

import com.fluent.collections.FMap;
import com.fluent.collections.Lists;
import com.fluent.math.*;
import com.fluent.pgm.*;
import com.fluent.pgm.estimation.Estimation;
import com.fluent.pgm.momc.MixtureEstimation.Context;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;
import org.mockito.Mock;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;

public class MixtureEstimationSpec extends AbstractSpec
{
	@Mock Estimation estimation;
	@Mock Inference inference;
	@Mock FMap<String, CPD> conditionals;
	@Mock MPD prior;
	@Mock Context context;

	Sequence sequence = new Sequence("A");
	@Mock CPD conditional;
	@Mock P marginal;
	MPD posterior = MPDBuilder.MPD(oo("S", .3), oo("R", .7));
	MixtureCounts counts = new MixtureCounts().add(P(.3), "S", sequence).add(P(.7), "R", sequence);

	@Test public void calculates_expectation() throws Exception
	{
		GIVEN(inference.posteriorAndMarginal(sequence, prior, conditionals)).RETURNS(oo(posterior, marginal));

		WHEN(new MixtureEstimation(estimation, inference).expectation(prior, conditionals, Lists.asFList((Sequence) new Sequence("A"))));

		THEN(theOutcome).shouldBe(oo(new MixtureCounts().add(P(.3), "S", sequence).add(P(.7), "R", sequence), marginal));
	}

	@Test public void calulates_maximisation() throws Exception
	{
		GIVEN(estimation.MPD_from(counts.forPrior())).RETURNS(prior);
		GIVEN(estimation.CPD_from(counts.forConditionals().get("S"), context)).RETURNS(conditional);
		GIVEN(estimation.CPD_from(counts.forConditionals().get("R"), context)).RETURNS(conditional);

		WHEN(new MixtureEstimation(estimation, inference).maximisation(counts, context));

		THEN(theReturn).shouldBe(oo(prior, newFMap(oo("S", conditional), oo("R", conditional))));
	}
}
