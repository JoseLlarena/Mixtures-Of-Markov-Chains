package com.fluent.pgm.hmm;

import java.util.Map;

import static org.hamcrest.Matchers.closeTo;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.oo;
import static com.fluent.math.P.ONE;
import static com.fluent.math.P.P;
import static com.fluent.pgm.CPDBuilder.CPD;
import static com.fluent.pgm.Sequence.END;
import static com.fluent.pgm.Sequence.END_STATE;
import static com.fluent.pgm.Sequence.START_STATE;
import static com.fluent.pgm.hmm.ProbabilityMapMatcher.roughly;

import com.fluent.pgm.Sequence;
import org.junit.Test;

import com.fluent.collections.FMap;
import com.fluent.core.oo;
import com.fluent.math.P;
import com.fluent.pgm.CPD;
import com.fluent.pgm.hmm.BaumWelchInference.Accumulator;
import com.fluent.pgm.hmm.BaumWelchInference.Gammas;
import com.fluent.pgm.hmm.BaumWelchInference.Xis;
import com.fluent.specs.unit.AbstractSpec;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BaumWelchInferenceSpec extends AbstractSpec
{
	static final double THRESHOLD = 10e-4;
	static String Y = "Y", X = "X", a = "a", b = "b";

	Sequence O = new Sequence("ab");
	CPD A = CPD()//[
			.p(X, P(.53)).p(X, X, P(.02)).p(Y, X, P(.79)).p(END_STATE, X,P(.19))
			.p(Y, P(.47)).p(X, Y, P(.02)).p(Y, Y, P(.61)).and(END_STATE, Y, P(.37));//]
	CPD B = CPD()//[
				.p(a, X, P(.03)).p(b, X, P(.97))
				.p(a, Y, P(.89)).p(b, Y, P(.11))
				.and(END, END_STATE, ONE);//]
	Gammas gammas;
	Xis xis;
	private Accumulator[] alphas;
	private Accumulator[] betas;

	@Test public void calculates_alphas() throws Exception
	{
		final Map<String, P>[] alphas = expectedAlphas();

		final oo<Accumulator[], Accumulator[]> actual = BaumWelchInference.alphasAndBetas(A, B, O);

		So((Map) actual.$1[0]).shouldBe(roughly(alphas[0], THRESHOLD));
		So((Map) actual.$1[1]).shouldBe(roughly(alphas[1], THRESHOLD));
		So((Map) actual.$1[2]).shouldBe(roughly(alphas[2], THRESHOLD));
		So((Map) actual.$1[3]).shouldBe(roughly(alphas[3], THRESHOLD));
	}

	@Test public void calculates_betas() throws Exception
	{
		final Map<String, P>[] betas = expectedBetas();

		new BaumWelchInference();
		final oo<Accumulator[], Accumulator[]> actual = BaumWelchInference.alphasAndBetas(A, B, O);

		So((Map) actual.$2[3]).shouldBe(roughly(betas[3], THRESHOLD));
		So((Map) actual.$2[2]).shouldBe(roughly(betas[2], THRESHOLD));
		So((Map) actual.$2[1]).shouldBe(roughly(betas[1], THRESHOLD));
		So((Map) actual.$2[0]).shouldBe(roughly(betas[0], THRESHOLD));
	}

	@Test public void calculates_gammas() throws Exception
	{
		final Accumulator[] alphas = new Accumulator[] { new Accumulator().plus("A", P(.001)).plus(END_STATE, P(.003)) };
		final Accumulator[] betas = new Accumulator[] { new Accumulator().plus("A", P(.002)).plus(END_STATE, P(.005)) };

		WHEN(gammas = BaumWelchInference.gammas(oo(alphas, betas), P(.01)));

		THEN(gammas.of(0, "A").toDouble()).shouldBe(closeTo(.001 * .002 / .01, .001));
		AND(gammas.of(0, END_STATE).toDouble()).shouldBe(closeTo(.003 * .005 / .01, .001));
	}

	@Test public void calculates_xis() throws Exception
	{
		alphas = new Accumulator[] { new Accumulator().plus(X, P(.001)), new Accumulator().plus(X, P(.002)),
				new Accumulator().plus(X, P(.003)), new Accumulator().plus(END_STATE, P(.005)) };
		betas = new Accumulator[] { new Accumulator().plus(X, P(.007)), new Accumulator().plus(Y, P(.009)) };

		WHEN(xis = BaumWelchInference.xis(A, B, oo(alphas, betas), new Sequence("ab")));

		THEN(xis.of(0, X, Y).toDouble()).shouldBe(closeTo(.001 * .79 * .89 * .009 / .005, .001));
	}

	static FMap<String, P>[] expectedAlphas()
	{
		final P betaForXAt2 = P(.53 * .03 * .02 + .47 * .89 * .02).x(P(.97));
		final P betaForYAt2 = P(.53 * .03 * .79 + .47 * .89 * .61).x(P(.11));

		final FMap<String, P> zero = newFMap(oo(START_STATE, ONE));
		final FMap<String, P> one = newFMap(oo(X, P(.53 * .03)), oo(Y, P(.47 * .89)));
		final FMap<String, P> two = new Accumulator().plus(X, betaForXAt2).plus(Y, betaForYAt2);
		final FMap<String, P> three = newFMap(oo(END_STATE, betaForXAt2.x(P(.19)).add(betaForYAt2.x(P(.37)))));
		return new FMap[] { zero, one, two, three };
	}

	static FMap<String, P>[] expectedBetas()
	{
		final P betaForXAt1 = P(.02 * .97 * .19 + .79 * .11 * .37);
		final P betaForYAt1 = P(.02 * .97 * .19 + .61 * .11 * .37);

		final FMap<String, P> zero = newFMap(oo(START_STATE, P(.53 * .03).x(betaForXAt1).add(P(.47 * .89).x(betaForYAt1))));
		final FMap<String, P> one = newFMap(oo(X, betaForXAt1), oo(Y, betaForYAt1));
		final FMap<String, P> two = newFMap(oo(X, P(.19)), oo(Y, P(.37)));
		final FMap<String, P> three = newFMap(oo(END_STATE, ONE));

		return new FMap[] { zero, one, two, three };
	}
}
