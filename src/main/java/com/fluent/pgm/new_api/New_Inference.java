package com.fluent.pgm.new_api;

import com.fluent.collections.FCollection;
import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F2;
import com.fluent.math.*;

import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;

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

     P marginal_from(FMap<String, P> joint_pdf)
    {
        return joint_pdf.aggregate(ZERO, (marginal_sofar, tag, joint) -> marginal_sofar.add(joint));
    }

    public P conditional(Seqence seq, CPX transition)
    {
        F2<P, Seqence.N_gram, P> product = (conditional, n_gram) -> conditional.x(transition.p(n_gram.$2, n_gram.$1));

        return seq.n_grams().aggregate(ONE, product);
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
