package com.fluent.pgm.hmm;

import com.fluent.collections.FList;
import com.fluent.collections.FSet;
import com.fluent.pgm.CPD;
import com.fluent.pgm.CPDBuilder;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.hmm.HMM_EM.Context;

import java.util.Random;

import static com.fluent.collections.Sets.newFSet;
import static com.fluent.core.Syntax.from;
import static com.fluent.pgm.CPDBuilder.CPD;
import static com.fluent.pgm.Sequence.END;
import static com.fluent.pgm.Sequence.END_STATE;
import static java.lang.String.valueOf;

public class HMM_Initialiser
{
	public static double[] randomPMF(final int N, final long seed)
	{
		final Random random = new Random(seed);

		final double[] ps = new double[N];
		int sum = 0;

		for (int i = 0; i < ps.length; i++)
		{
			sum += ps[i] = random.nextInt(1000);
		}
		for (int i = 0; i < ps.length; i++)
		{
			ps[i] /= sum;
		}

		return ps;
	}

	private static CPDBuilder initial(final Random generator, final FSet<String> tokens)
	{
		CPDBuilder cpd = CPDBuilder.CPD();

		final double[] startRandomPmf = randomPMF(tokens.size(), generator.nextLong());

		int c = 0;
		for (final String token : tokens)
		{
			 cpd.p(token, startRandomPmf[c++]);
		}

		return cpd;
	}

	private static CPDBuilder rest(final int stateCount, final FSet<String> tokens, final Random generator, final CPDBuilder cpd)
	{
		from(0, stateCount - 1).forEach(context -> {

			final double[] randomPmf = randomPMF(tokens.size(), generator.nextLong());

			int ft = 0;

			for (final String token : tokens)
			{
				cpd.p(token, valueOf(context), randomPmf[ft++]);
			}

		});

		return cpd;
	}

	public CPD emissions(final int stateCount, final long seed, final FList<Sequence> data)
	{
		final FSet<String> tokens = data.collect(newFSet(String.class), (unique, sequence) -> unique.plus(sequence.terms()));

		return rest(stateCount, tokens, new Random(seed), CPD()).p(END, END_STATE, 1.).done();
	}

	public HMM init(final FList<Sequence> data, final Context context)
	{
		return new HMM(transitions(context.stateCount(), context.seed()), emissions(context.stateCount(), context.seed(), data));
	}

	public CPD transitions(final int stateCount, final long seed)
	{
		final CPDBuilder cpd = CPDBuilder.CPD();
		final Random generator = new Random(seed);

		final double[] startRandomPmf = randomPMF(stateCount, generator.nextLong());

		from(0, stateCount - 1).forEach(token -> cpd.p(valueOf(token), startRandomPmf[token]));

		for (final int context : from(0, stateCount - 1))
		{
			final double[] randomPmf = randomPMF(stateCount + 1, generator.nextLong());

			from(-1, stateCount - 1).forEach(
					token -> cpd.p(token == -1 ? END_STATE : valueOf(token), valueOf(context), randomPmf[token + 1]));
		}

		return cpd.done();
	}

}
