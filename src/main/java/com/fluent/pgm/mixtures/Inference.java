package com.fluent.pgm.mixtures;

import com.fluent.collections.FCollection;
import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F2;
import com.fluent.math.*;

import static com.fluent.math.P.*;
import static com.fluent.pgm.mixtures.Sequence.Ngram;

public class Inference extends Viterbi
{
    public static final Inference Inference = new Inference();

    public FMap<String, P> joint(Sequence sequence, MoMC model)
    {
        return model.prior().as_map()
                .apply_to_values((tag, prior) -> prior.x(conditional(sequence, model.transitions_for(tag))));
    }

    public FMap<String, P> posterior_density(Sequence sequence, MoMC model)
    {
        FMap<String, P> joint_pdf = joint(sequence, model);

        P marginal = marginal_from(joint_pdf);

        return joint_pdf.applyToValues(joint -> joint.div(marginal));
    }

    public FMap<String, P> joint(Ngram ngram, MoMC model)
    {
        return model.prior().as_map().apply_to_values((tag, prior) ->
                prior.x(model.transitions_for(tag).p(ngram.token(), ngram.context())));
    }

    P marginal_from(FMap<String, P> joint_pdf)
    {
        return joint_pdf.aggregate(ZERO, (marginal_sofar, tag, joint) -> marginal_sofar.add(joint));
    }

    public P conditional(Sequence sequence, CPD transition)
    {
        F2<P, Ngram, P> product = (conditional, ngram) -> conditional.x(transition.p(ngram.token(), ngram.context()));

        return sequence.ngrams().aggregate(ONE, product);
    }

    //untested
    public P likelihood(MoMC model, FCollection<Sequence> data)
    {
        return data.aggregate(ONE, (likelihood, datum) -> likelihood.x(marginal(datum, model)));
    }

    P marginal(Sequence sequence, MoMC model)
    {
        return joint(sequence, model).aggregate(ZERO, (marginal_sofar, the_class, joint) -> marginal_sofar.add(joint));
    }

    public FList<Sequence> most_likely(int n, FList<Sequence> data, MoMC model)
    {
        return data.sorted(datum -> -marginal(datum, model).asLog() / datum.size()).first(n);
    }

    public FList<Sequence> least_likely(int n, FList<Sequence> data, MoMC model)
    {
        return data.sorted(datum -> marginal(datum, model).asLog() / datum.size()).first(n);
    }

}
