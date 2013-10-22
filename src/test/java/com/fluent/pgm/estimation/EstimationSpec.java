package com.fluent.pgm.estimation;

import com.fluent.pgm.CPD;
import com.fluent.pgm.WeightMap;
import com.fluent.pgm.hmm.ProbabilityMapMatcher;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;
import org.mockito.Mock;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.*;
import static com.fluent.core.ooo.ooo;
import static com.fluent.math.P.*;
import static org.hamcrest.Matchers.*;

public class EstimationSpec extends AbstractSpec
{
	@Mock Estimation.Context context;

	CPD cpd;

	@Test public void makes_cpd_from_bigram_probabilities() throws Exception
	{
		GIVEN(context.withOOV()).RETURNS(false).AND(context.delta()).RETURNS(.1);

		WHEN(cpd = Estimation.of.CPD_from(new WeightMap(ooo("A", "A", .5), ooo("A", "B", .5), ooo("A", "C", .0)), context));

		THEN(cpd.p("A", "A").toDouble()).shouldBe(allOf(greaterThan(0.), lessThan(.5)));
		AND(cpd.p("B", "A").toDouble()).shouldBe(allOf(greaterThan(0.), lessThan(.5)));
		AND(cpd.p("C", "A").toDouble()).shouldBe(allOf(greaterThan(0.), lessThan(.5)));

	}

	@Test public void smoothes_according_to_add_delta() throws Exception
	{
		GIVEN(context.withOOV()).RETURNS(false).AND(context.delta()).RETURNS(.1);

		WHEN(Estimation.of.smooth(newFMap(oo("A", .5), oo("B", .5), oo("C", .0)), .1));

		THEN(theOutcome).shouldBe(ProbabilityMapMatcher.roughly(newFMap(oo("A", P(.46)), oo("B", P(.46)), oo("C", P(.077))), .01));

	}
}
