package com.fluent.pgm.integration;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.oo;
import static com.fluent.math.P.P;
import static com.fluent.pgm.CPDBuilder.CPD;
import static com.fluent.pgm.MPDBuilder.MPD;
import static com.fluent.pgm.Sequence.END_STATE;

import org.junit.Test;

import com.fluent.collections.FList;
import com.fluent.pgm.CPD;
import com.fluent.pgm.MPD;
import com.fluent.pgm.Sampling;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.api.Data_API;
import com.fluent.pgm.momc.Mixture;
import com.fluent.specs.unit.AbstractSpec;

public class Data_API_Spec extends AbstractSpec
{
	int seed = 12341234;
	String Y = "Y", X = "X", S = "S", R = "R";
	MPD prior = MPD(oo(S, .5), oo(R, .5));
	CPD switching = CPD()//[
            .p(X, P(1)).p(X, X, P(.1)).p(Y, X, P(0)).p(END_STATE, X, P(.9))
            .p(Y, P(0)).p(X, Y, P(0)).p(Y, Y, P(.0)).and(END_STATE, Y, P(1));//]
	CPD repeating = CPD()//[
            .p(X, P(0)).p(X, X, P(.1)).p(Y, X, P(0)).p(END_STATE, X, P(.9))
            .p(Y, P(1)).p(X, Y, P(0)).p(Y, Y, P(.1)).and(END_STATE, Y, P(.9));//]

	@Test public void samples_from_mixture() throws Exception
	{
		final FList<Sequence> data = new Data_API(null, null, Sampling.of).dataFrom(
				new Mixture(prior, newFMap(oo(S, switching), oo(R, repeating))), 1000, seed);

		THEN(data.size()).shouldBe(1000);
		AND(data.select(sequence -> sequence.toString().matches("X+")).size()).shouldBe(495);
		AND(data.select(sequence -> sequence.toString().matches("Y+")).size()).shouldBe(505);
	}
}
