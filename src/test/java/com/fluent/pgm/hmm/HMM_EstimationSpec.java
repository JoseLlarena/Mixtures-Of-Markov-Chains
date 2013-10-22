package com.fluent.pgm.hmm;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.collections.Lists;
import com.fluent.math.*;
import com.fluent.pgm.CPD;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.WeightMap;
import com.fluent.pgm.estimation.Estimation;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;
import org.mockito.Mock;

import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;

@SuppressWarnings("unchecked")
public class HMM_EstimationSpec extends AbstractSpec
{
	static final Sequence ANY_SEQUENCE = new Sequence("X");

	HMM_Estimation2 hmmEstimation;

	@Mock HMM_Counting counting;
	@Mock Estimation estimation;

	FList<Sequence> data = Lists.asFList(ANY_SEQUENCE);
	@Mock CPD A, B;
	@Mock CPD newA, newB;
	@Mock WeightMap A_Counts, B_Counts;
	@Mock HMM_EM.Context context;
	@Mock FMap<String, Double> stateCounts;

	@Mock HMM_Counts anyNewCounts;// = new Counts().addTransition("x", "y", .1), allCounts = new Counts();
	P likelihood = P(.5), marginal = P(.5);

	@Test public void computes_expectation() throws Exception
	{
		GIVEN(counting.initial()).RETURNS(anyNewCounts);
		GIVEN(anyNewCounts.add(anyNewCounts)).RETURNS(anyNewCounts);

		GIVEN(counting.countsAndMarginal(A, B, ANY_SEQUENCE)).RETURNS(oo(anyNewCounts, marginal));

		WHEN(new HMM_Estimation2(counting, null).expectation(A, B, data));

		THEN(theOutcome).shouldBe(oo(anyNewCounts, likelihood));
	}

	@Test public void computes_maximisation() throws Exception
	{
		GIVEN(anyNewCounts.transitions()).RETURNS(A_Counts);
		GIVEN(anyNewCounts.emissions()).RETURNS(B_Counts);
		GIVEN(anyNewCounts.allStates()).RETURNS(stateCounts);
		GIVEN(estimation.CPD_from(A_Counts, stateCounts, context)).RETURNS(newA);
		GIVEN(estimation.CPD_from(B_Counts, stateCounts, context)).RETURNS(newB);

		WHEN(new HMM_Estimation2(null, estimation).maximisation(anyNewCounts, context));

		THEN(theOutcome).shouldBe(oo(newA, newB));
	}
}
