package com.fluent.pgm.integration;

import com.fluent.collections.FList;
import com.fluent.collections.Lists;
import com.fluent.pgm.CPD;
import com.fluent.pgm.Sampling;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.api.Classification_API;
import com.fluent.pgm.api.Data_API;
import com.fluent.pgm.api.Estimation_API;
import com.fluent.pgm.estimation.Optimiser;
import com.fluent.pgm.hmm.HMM;
import com.fluent.pgm.hmm.HMM_EM;
import com.fluent.pgm.hmm.HMM_Initialiser;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import static com.fluent.math.P.*;
import static com.fluent.pgm.CPDBuilder.CPD;
import static com.fluent.pgm.Sequence.END;
import static com.fluent.pgm.Sequence.END_STATE;

public class Estimation_API_Spec extends AbstractSpec
{
	String Y = "Y", X = "X", x = "x", y = "y";

	CPD A = CPD()//[
			.p(X, P(.5)).p(X, X, P(.05)).p(Y, X, P(.5)).p(END_STATE, X,P(.45))
			.p(Y, P(.5)).p(X, Y, P(.5)).p(Y, Y, P(.05)).and(END_STATE, Y, P(.45));//]
	CPD B = CPD()//[
				.p(x, X, P(.9)).p(y, X, P(.1))
				.p(x, Y, P(.1)).p(y, Y, P(.9))
				.and(END, END_STATE, ONE);//]

	@Test public void creates_hmm_from_data() throws Exception
	{
		final FList<Sequence> data = new Data_API(null, null, new Sampling()).dataFrom(new HMM(A, B), 2_000, 1234L);
		System.out.println(data);

		final HMM hmm = new Estimation_API(new HMM_Initialiser(), new Optimiser( )).hmmFrom(data,
                HMM_EM.Context.DEFAULT);

		System.out.println(new Classification_API().tag(Lists.asFList(new Sequence("xyxyxx"), new Sequence("yyy")), hmm));

		THE_OUTPUT_OF(hmm).shouldBe(new HMM(A, B));
	}
}
