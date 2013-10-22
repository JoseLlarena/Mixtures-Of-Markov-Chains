package com.fluent.pgm.hmm;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.core.oo.oo;

import com.fluent.collections.Lists;
import com.fluent.pgm.Sequence;
import org.junit.Test;
import org.mockito.Mock;

import com.fluent.collections.FList;
import com.fluent.math.P;
import com.fluent.pgm.CPD;
import com.fluent.specs.unit.AbstractSpec;

@SuppressWarnings("unchecked")
public class HMM_EM_Spec extends AbstractSpec
{
	@Mock HMM_Estimation2 estimation;
	@Mock HMM_EM.Context context;
	FList<Sequence> data = Lists.asFList((Sequence) new Sequence("X"));

	@Mock CPD A, B, newA, newB;
	HMM_Counts anyNewCounts = new HMM_Counts();
	P anyLikelihood = P.ZERO;

	@Test public void expects_maximises_with_some_data() throws Exception
	{
		GIVEN(estimation.expectation(A, B, data)).RETURNS(oo(anyNewCounts, anyLikelihood));
		GIVEN(estimation.maximisation(anyNewCounts, context)).RETURNS(oo(newA, newB));

		WHEN(new HMM_EM(estimation).of(new HMM(A, B), data, context));

		THEN(theOutcome).shouldBe(oo(new HMM(newA, newB), anyLikelihood));
	}
}
