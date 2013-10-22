package com.fluent.pgm.estimation;

import com.fluent.collections.FMap;
import com.fluent.core.ooo;
import com.fluent.math.*;
import com.fluent.pgm.*;

import static com.fluent.math.P.*;
import static com.fluent.pgm.MPDBuilder.*;
import static com.fluent.pgm.Sequence.OOV;

@SuppressWarnings("unchecked")
public class Estimation
{
    public static final Estimation of = new Estimation();

    protected Estimation()
    {
    }

    public CPD CPD_from(final WeightMap bigram_weight, final Estimation.Context options)
    {
        final WeightMap bigramAsContextToTokenWeight = options.withOOV() ? OOV_adjusted(bigram_weight,
                options.OOV_weight()) : bigram_weight;

        FMap<String, FMap<String, P>> smoothed = bigramAsContextToTokenWeight.asFMap().applyToValues(tokenToWeight -> smooth(tokenToWeight,
                options.delta()));

        return new CPT_with_OOV(smoothed);
    }

    public CPD CPD_from(final WeightMap joints, final FMap<String, Double> marginals, final Estimation.Context options)
    {
        final FTriMap<String, String, P> triples = new FHashTriMap<>();

        for (final ooo<String, String, Double> triple : joints.entries())
        {
            triples.add(triple.$1, triple.$2, P(joints.get(triple.$1, triple.$2) / marginals.of(triple.$1)));
        }

        return new CPT(triples);
    }

    public MPD MPD_from(final FMap<String, Double> tagToWeight)
    {
        final double N = tagToWeight.values().collect(0., Double::sum);

        return tagToWeight.collect(MPD(), (prior, tag, weight) -> prior.p(tag, weight / N)).done();
    }

    public WeightMap OOV_adjusted(final WeightMap contextTokenWeight, final double OOV_weight)
    {
        contextTokenWeight.contexts().each(context -> contextTokenWeight.add(context, OOV, OOV_weight));

        contextTokenWeight.tokens().each(token -> contextTokenWeight.add(OOV, token, OOV_weight));

        return contextTokenWeight;
    }

    FMap<String, P> smooth(final FMap<String, Double> tokenToCount, final double delta)
    {
        final double N = tokenToCount.values().collect(0., Double::sum);
        final int V = tokenToCount.size();

        return tokenToCount.applyToValues(count -> P((count + delta) / (N + V * delta)));
    }

    public interface Context
    {
        public double delta();

        public double OOV_weight();

        public boolean withOOV();
    }
}
