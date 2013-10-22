package com.fluent.pgm.hmm;

import java.util.Map;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.collections.Sets.newFSet;
import static com.fluent.core.oo.oo;
import static com.fluent.core.ooo.ooo;
import static com.fluent.math.P.ONE;
import static com.fluent.math.P.P;
import static com.fluent.math.P.ZERO;
import static com.fluent.pgm.Sequence.END_STATE;
import static com.fluent.pgm.Sequence.START_STATE;

import com.fluent.collections.Sets;
import com.fluent.pgm.Sequence;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.fluent.math.P;
import com.fluent.pgm.CPD;
import com.fluent.pgm.hmm.BaumWelchInference.Gammas;
import com.fluent.pgm.hmm.BaumWelchInference.Xis;
import com.fluent.specs.unit.AbstractSpec;

@SuppressWarnings("unchecked")
public class HMM_CountingSpec extends AbstractSpec
{
	HMM_Counting counting;

	@Mock BaumWelchInference inference;
	@Mock CPD A, B;
	Sequence O = new Sequence("ab");

	Gammas gammas;
	Xis xis;
	P marginal = P(.11111);
	HMM_Counts counts = new HMM_Counts();
	String Y = "Y", X = "X", a = "a", b = "b";

	@Before public void BACKGROUND()
	{
		gammas = new Gammas()
		{
			Map<String, P> gammaAt1 = newFMap(oo(X, P(.03)), oo(Y, P(.97)));
			Map<String, P> gammaAt2 = newFMap(oo(X, P(.11)), oo(Y, P(.89)));
			Map<String, P> gammaAt3 = newFMap(oo(END_STATE, ONE));

			public final P of(final Integer t, final String Sj)
			{
				final P p = (t == 1 ? gammaAt1 : t == 2 ? gammaAt2 : gammaAt3).get(Sj);
				return p == null ? ZERO : p;
			}
		};

		xis = new Xis()
		{
			Map<String, P> xisAt0 = newFMap(oo(START_STATE + X, P(.17)), oo(START_STATE + Y, P(.83)));
			Map<String, P> xisAt1 = newFMap(oo(X + X, P(.47)), oo(X + Y, P(.23)), oo(Y + X, P(.17)), oo(Y + Y, P(.13)));
			Map<String, P> xisAt2 = newFMap(oo(X + END_STATE, P(.29)), oo(Y + END_STATE, P(.71)));

			public final P of(final Integer t, final String Si, final String Sj)
			{
				final P p = (t == 0 ? xisAt0 : t == 1 ? xisAt1 : xisAt2).get(Si + Sj);
				return p == null ? ZERO : p;
			}
		};

		counts.addState(X, P(.03).add(P(.11))).addState(Y, .97 + .89);

		counts.addEmission(X, a, P(.03)).addEmission(Y, a, P(.97)).addEmission(X, b, P(.11)).addEmission(Y, b, P(.89));

		counts.addTransition(START_STATE, X, P(.17)).addTransition(START_STATE, Y, P(.83));
		counts.addTransition(X, X, P(.47)).addTransition(X, Y, P(.23)).addTransition(Y, X, P(.17)).addTransition(Y, Y, P(.13));
		counts.addTransition(X, END_STATE, P(.29)).addTransition(Y, END_STATE, P(.71));
	}

	@Test public void computes_counts_and_marginal() throws Exception
	{
		GIVEN(A.tokens()).RETURNS(Sets.asFSet(X, Y, END_STATE)).AND(A.contexts()).RETURNS(Sets.asFSet(START_STATE, X, Y));
		GIVEN(inference.posteriorsAndMarginal(A, B, O)).RETURNS(ooo(gammas, xis, marginal));

		WHEN(new HMM_Counting(inference).countsAndMarginal(A, B, O));

		THEN(theOutput).shouldBe(oo(counts, marginal));
	}
}
