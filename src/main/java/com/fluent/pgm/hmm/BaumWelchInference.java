package com.fluent.pgm.hmm;

import java.util.Formatter;

import static com.fluent.core.Syntax.from;
import static com.fluent.core.Words.to;
import static com.fluent.core.oo.oo;
import static com.fluent.core.ooo.ooo;
import static com.fluent.math.P.ONE;
import static com.fluent.math.P.ZERO;
import static com.fluent.pgm.Sequence.END_STATE;
import static com.fluent.pgm.Sequence.START_STATE;

import com.fluent.collections.FHashMap;
import com.fluent.collections.FSet;
import com.fluent.core.F2;
import com.fluent.core.F3;
import com.fluent.core.oo;
import com.fluent.core.ooo;
import com.fluent.math.P;
import com.fluent.pgm.CPD;
import com.fluent.pgm.Sequence;

public class BaumWelchInference
{
	public static final BaumWelchInference INSTANCE = new BaumWelchInference();

	public ooo<Gammas, Xis, P> posteriorsAndMarginal(final CPD A, final CPD B, final Sequence O)
	{
		final oo<Accumulator[], Accumulator[]> alphasAndBetas = alphasAndBetas(A, B, O);

		final P marginal = alphasAndBetas.$1[O.T() - 1].get(END_STATE);

		return ooo(gammas(alphasAndBetas, marginal), xis(A, B, alphasAndBetas, O), marginal);
	}

	P marginal(final CPD A, final Accumulator alpha)
	{
		return A.tokens().collect(ZERO, (marginal, Si) -> marginal.add(alpha.of(Si).x(A.p(Si, END_STATE))));
	}

	/*
	 * Computes forward part badfrom forward-backward algorithm. Uses dynamic programming to efficiently compute alpha[i,t] =
	 * p( O[1:t] , S(t) = i ), probability badfrom state i at time t and observation O badfrom time 1 to t, for all states and
	 * time steps
	 */
	static oo<Accumulator[], Accumulator[]> alphasAndBetas(final CPD A, final CPD B, final Sequence O)
	{
		final Accumulator[] alphas = estimators(O.T());
		alphas[0].plus(START_STATE, ONE);

		final Accumulator[] betas = estimators(O.T());
		betas[O.T() - 1].plus(END_STATE, ONE);

		final FSet<String> tokens = A.tokens().minus(END_STATE);

		for (final int t : from(1, to(O.T() - 2)))
		{
			for (final String Sj : tokens)
			{
				final P alphaSum = t == 1 ? A.p(Sj, START_STATE) : alphaSum(Sj, A, alphas[t - 1]);

				alphas[t].plus(Sj, alphaSum.x(B.p(O.at(t), Sj)));

				final P betaSum = betaSum(Sj, O.at(O.T() - 1 - t + 1), A, B, betas[O.T() - 1 - t + 1]);

				betas[O.T() - 1 - t].plus(Sj, betaSum);
			}
		}

		alphas[O.T() - 1].plus(END_STATE, alphaSum(END_STATE, A, alphas[O.T() - 2]));
		betas[0].plus(START_STATE, betaSum(START_STATE, O.at(1), A, B, betas[1]));

		return oo(alphas, betas);
	}

	static Gammas gammas(final oo<Accumulator[], Accumulator[]> alphasAndBetas, final P marginal)
	{
		final Accumulator[] alphas = alphasAndBetas.$1;
		final Accumulator[] betas = alphasAndBetas.$2;

		return new Gammas()
		{
			public P of(final Integer t, final String state)
			{
				return alphas[t].of(state).x(betas[t].of(state)).div(marginal);
			}

			public String toString()
			{
				final Formatter formatter = new Formatter();

				for (int t = 0; t < alphas.length; t++)
				{
					for (final String state : alphas[t].keySet())
					{
						formatter.format("t[%s] %s:%s%n", t, state, of(t, state));
					}
				}

				return formatter.toString();
			}

		};
	}

	static Xis xis(final CPD A, final CPD B, final oo<Accumulator[], Accumulator[]> alphasAndBetas, final Sequence O)
	{
		final Accumulator[] alphas = alphasAndBetas.$1;
		final Accumulator[] betas = alphasAndBetas.$2;

		final P marginal = alphas[O.T() - 1].get(END_STATE);

		return new Xis()
		{
			public P of(final Integer t, final String Si, final String Sj)
			{
				return alphas[t].of(Si).x(A.p(Sj, Si)).x(B.p(O.at(t + 1), Sj)).x(betas[t + 1].of(Sj)).div(marginal);
			}

			public String toString()
			{
				final Formatter formatter = new Formatter();

				for (int t = 0; t < alphas.length - 1; t++)
				{
					for (final String fromState : alphas[t].keySet())
					{
						for (final String toState : betas[t + 1].keySet())
						{
							formatter.format("t[%s] %s > %s:%s%n", t, fromState, toState, of(t, fromState, toState));
						}
					}
				}

				return formatter.toString();
			}
		};
	}

	/**
	 * Computes sum badfrom alpha's in previous step x transitions into given state
	 */
	private static P alphaSum(final String Sj, final CPD A, final Accumulator alphaSoFar)
	{
		return A.tokens().minus(END_STATE).collect(ZERO, (sum, Si) -> sum.add(alphaSoFar.of(Si).x(A.p(Sj, Si))));
	}

	private static P betaSum(final String Sj, final String O, final CPD A, final CPD B, final Accumulator betaAhead)
	{
		return A.tokens().collect(ZERO, (sum, Si) -> sum.add(A.p(Si, Sj).x(B.p(O, Si).x(betaAhead.of(Si)))));
	}

	private static Accumulator[] estimators(final int t)
	{
		final Accumulator[] accumulators = new Accumulator[t];

		for (int i = 0; i < accumulators.length; i++)
		{
			accumulators[i] = new Accumulator();
		}

		return accumulators;
	}

	static class Accumulator extends FHashMap<String, P>
	{
		public Accumulator plus(final String Si, final P p)
		{
			super.plus(Si, get(Si, ZERO).add(p));
			return this;
		}

		public P of(final String si)
		{
			return get(si, ZERO);
		}
	}

	interface Gammas extends F2<Integer, String, P>
	{}

	interface Xis extends F3<Integer, String, String, P>
	{}
}
