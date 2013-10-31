package com.fluent.pgm.new_api;

import com.fluent.collections.FCollection;
import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F2;
import com.fluent.math.*;

import static com.fluent.math.P.*;
import static com.fluent.pgm.new_api.Seqence.Ngram;

public class New_Inference extends Viterbi
{
    public static final New_Inference New_Inference = new New_Inference();

    public FMap<String, P> joint(Seqence sequence, MoMC model)
    {
        return model.prior().as_map()
                .apply_to_values((tag, prior) -> prior.x(conditional(sequence, model.transitions_for(tag))));
    }

    public FMap<String, P> posterior_density(Seqence sequence, MoMC model)
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

    public P conditional(Seqence sequence, CPX transition)
    {
        F2<P, Ngram, P> product = (conditional, ngram) -> conditional.x(transition.p(ngram.$2, ngram.$1));

        return sequence.ngrams().aggregate(ONE, product);
    }

    //untested
    public P likelihood(MoMC model, FCollection<Seqence> data)
    {
        return data.aggregate(ONE, (likelihood, datum) -> likelihood.x(marginal(datum, model)));
    }

    P marginal(Seqence sequence, MoMC model)
    {
        return joint(sequence, model).aggregate(ZERO, (marginal_sofar, the_class, joint) -> marginal_sofar.add(joint));
    }

    public FList<Seqence> most_likely(int n, FList<Seqence> data, MoMC model)
    {  //FIXME IMPROVE nlog + n -> n
        return data.sorted(datum -> -marginal(datum, model).asLog() / datum.size()).first(n);
    }

    public FList<Seqence> least_likely(int n, FList<Seqence> data, MoMC model)
    {    //FIXME IMPROVE nlog + n -> n
        return data.sorted(datum -> marginal(datum, model).asLog() / datum.size()).first(n);
    }

    public Seqence complete(Seqence datum, MoMC model)
    {
        return null;//best_path_in(path_scores_from(datum, model));
    }

}
