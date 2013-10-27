package com.fluent.pgm.new_api;

import com.fluent.collections.FCollection;
import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F2;
import com.fluent.math.*;

import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.new_api.Seqence.Ngram;

public class New_Inference
{
    public static final New_Inference New_Inference = new New_Inference();

    public FMap<String, P> joint(Seqence sequence, MoMC model)
    {
        return model.prior()
                .apply((a_class, prior) -> oo(a_class, prior.x(conditional(sequence, model.transitions_for(a_class)))));
    }

    //untested
    public FMap<String, P> posterior_density(Seqence sequence, MoMC model)
    {
        FMap<String, P> joint_pdf = joint(sequence, model);

        P marginal = marginal_from(joint_pdf);

        return joint_pdf.applyToValues(joint -> joint.div(marginal));
    }

    public FMap<String, P> posterior_density(Seqence sequence, MPX prior, FMap<String, NLF> inverse_likelihoods)
    {
        return prior.apply_to_values((tag, p) -> P.from_log(p.asLog() + inverse_likelihood(sequence,
                inverse_likelihoods.get(tag))));

    }

    double inverse_likelihood(Seqence sequence, NLF inverse_likelihood)
    {
        return sequence.ngrams().aggregate(0., (like, ngram) -> like + inverse_likelihood.of(ngram));
    }

    public FMap<String, NLF> inverse_likelihoods(MoMC model)
    {
        FMap<String, NLF> f = model.tags().zip(tag -> new NLF());

        model.transitions().each((tag, transitions) ->
                transitions.forEach(triple ->
                        {
                            Ngram ngram = Ngram.from(triple.$1, triple.$2);
                            f.get(tag).plus(ngram, triple.$3.asLog() - marginal_from(joint(ngram,
                                    model)).asLog());
                        }

                )
        );

        return f;
    }

    public FMap<String, P> joint(Ngram ngram, MoMC model)
    {
        return model.prior().apply_to_values((tag, prior) ->
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
}
