package com.fluent.pgm;

import com.fluent.collections.FMap;
import com.fluent.core.oo;
import com.fluent.math.P;
import com.fluent.pgm.momc.Mixture;

import static com.fluent.core.oo.oo;
import static com.fluent.math.P.ONE;
import static com.fluent.math.P.ZERO;
import static com.fluent.pgm.MPDBuilder.MPD;

public class Inference
{
    public static final Inference of = new Inference();

    private Inference()
    {
    }

    public oo<P[], P> jointsAndMarginal(final Sequence sequence, final MPD priors, final FMap<String, CPD> conditionals)
    {
        final P[] joints = new P[priors.size()];

        P marginal = ZERO;

        int k = 0;

        for (final String tag : priors.tokens())
        {
            joints[k] = priors.p(tag).x(likelihood(sequence, conditionals.of(tag)));

            marginal = marginal.add(joints[k++]);
        }

        return oo(joints, marginal);
    }

    public P likelihood(final Sequence O, final CPD A)
    {
        return O.bigrams().collect(ONE, (likelihood, bigram) -> likelihood.x(A.p(bigram.$2, bigram.$1)));
    }

    public oo<String, P> maxJoint(final Sequence sequence, final Mixture mixture)
    {
        return maxJoint(sequence, mixture.prior(), mixture.conditionals());
    }

    public oo<String, P> maxJoint(final Sequence sequence, final MPD priors, final FMap<String, CPD> conditionals)
    {
        return priors.tokens().collect(oo("?", ZERO), (best, tag) ->
                {
                    final P joint = priors.p(tag).x(likelihood(sequence, conditionals.of(tag)));

                    return joint.gt(best.$2) ? oo(tag, joint) : best;
                });
    }

    public oo<MPD, P> posteriorAndMarginal(final Sequence sequence, final MPD priors, final FMap<String, CPD> conditionals)
    {
        return jointsAndMarginal(sequence, priors, conditionals).both((joints, marginal) -> oo(posterior(priors,
                joints, marginal), marginal))  ;
    }

    public MPD posterior(final MPD prior, final P[] joints, final P marginal)
    {
        final MPDBuilder posterior = MPD();

        int j = 0;

        for (final String tag : prior.tokens())
        {
            posterior.p(tag, joints[j++].div(marginal));
        }

        return posterior.done();
    }
}
