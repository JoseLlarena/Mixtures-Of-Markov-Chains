package com.fluent.pgm.api;

import com.fluent.collections.FCollection;
import com.fluent.collections.FList;
import com.fluent.collections.FListMultiMap;
import com.fluent.collections.FMap;
import com.fluent.core.F2;
import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.pgm.CPD;
import com.fluent.pgm.Inference;
import com.fluent.pgm.MPD;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.hmm.HMM;
import com.fluent.pgm.hmm.ViterbiInference;
import com.fluent.pgm.momc.Mixture;

import static com.google.common.base.Joiner.on;

public class Classification_API
{
    public static final String UNKNOWN_TAG = "?";
    private final Inference inferenceOf;

    public Classification_API()
    {
        this(Inference.of);
    }

    protected Classification_API(final Inference inferenceOf)
    {
        this.inferenceOf = inferenceOf;
    }

    public FListMultiMap<String, Sequence> split(final FList<Sequence> data, final Mixture mixture)
    {
        return data.groupBy(item -> inferenceOf.maxJoint(item, mixture).$1);
    }

    public FListMultiMap<String, Sequence> split(final FList<Sequence> data, final Mixture mixture,
                                                 final double threshold)
    {
        F2<String, P, String> unknownIfTooLow = (tag, posterior) -> posterior.toDouble() > threshold ? tag : UNKNOWN_TAG;

        return data.groupBy(item ->  maxPosterior(item, mixture).both(unknownIfTooLow));
    }

    public <S extends Sequence> FMap<S, oo<String, P>> tag(final FCollection<S> data, final Mixture mixture)
    {
        return data.zip(item -> maxPosterior(item, mixture));
    }

    public oo<String, P> maxPosterior(final Sequence sequence, final Mixture mixture)
    {
        return posterior(sequence, mixture).max_as((String token, P probability) -> probability);
    }

    public MPD posterior(final Sequence sequence, final Mixture mixture)
    {
        return posterior(sequence, mixture.prior(), mixture.conditionals());
    }

    public MPD posterior(final Sequence sequence, final MPD prior, final FMap<String, CPD> conditionals)
    {
        return inferenceOf.jointsAndMarginal(sequence, prior, conditionals).both((joints, marginal) -> inferenceOf.posterior(prior, joints, marginal));
    }

    public FList<Sequence> tag(final FList<? extends Sequence> sentences, final HMM hmm)
    {
        return sentences.apply(sentence -> (Sequence) new Sequence(on("").join(ViterbiInference.of.mpss(sentence, hmm.transitions(), hmm.emissions()))));

    }
}