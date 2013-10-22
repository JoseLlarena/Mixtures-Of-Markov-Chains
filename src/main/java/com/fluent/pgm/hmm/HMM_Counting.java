package com.fluent.pgm.hmm;

import com.fluent.core.oo;
import com.fluent.core.ooo;
import com.fluent.math.*;
import com.fluent.pgm.CPD;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.hmm.BaumWelchInference.Gammas;
import com.fluent.pgm.hmm.BaumWelchInference.Xis;

import static com.fluent.core.Syntax.from;
import static com.fluent.core.Words.to;
import static com.fluent.core.oo.oo;
import static com.fluent.math.P.*;
import static com.fluent.pgm.Sequence.END;
import static com.fluent.pgm.Sequence.END_STATE;

public class HMM_Counting
{
	private final BaumWelchInference baumWelch;

	public HMM_Counting(final BaumWelchInference baumWelch)
	{
		this.baumWelch = baumWelch;
	}

	public oo<HMM_Counts, P> countsAndMarginal(final CPD A, final CPD B, final Sequence O)
	{
		final ooo<Gammas, Xis, P> gammasoxisomarginal = baumWelch.posteriorsAndMarginal(A, B, O);
		final Gammas gammas = gammasoxisomarginal.$1;
		final Xis xis = gammasoxisomarginal.$2;

		final HMM_Counts counts = initial();

		for (final int t : from(1, to(O.T() - 1)))
		{
			for (final String Sj : A.tokens())
			{
				final P gamma = gammas.of(t, Sj);

				counts.addState(Sj, gamma).addEmission(Sj, O.at(t), gamma);

				for (final String Si : A.contexts())
				{
					counts.addTransition(Si, Sj, xis.of(t - 1, Si, Sj));
				}
			}
		}

		return oo(counts, gammasoxisomarginal.$3);
	}

	public HMM_Counts initial()
	{
		return new HMM_Counts();
	}

	HMM_Counts countEmission(final HMM_Counts c, final String Sj, final String Ot, final P p)
	{
		if (p != ZERO && Sj != END_STATE && Ot != END)
		{
			c.emissions().add(Sj, Ot, c.emissions().get(Sj, Ot, 0.) + p.toDouble());
		}

		return c;
	}
}
