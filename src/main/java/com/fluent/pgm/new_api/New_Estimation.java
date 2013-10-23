package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F2;
import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.util.Clock;
import com.google.common.util.concurrent.AtomicDouble;
import org.joda.time.DateTime;

import java.util.concurrent.atomic.AtomicInteger;

import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.new_api.CPD_Builder.CPX_from;
import static com.fluent.pgm.new_api.Seqence.Ngram;
import static java.lang.System.out;
import static java.time.temporal.ChronoUnit.SECONDS;

public class New_Estimation extends New_Inference implements New_Optimisation, New_Initialisation
{
    public static New_Estimation New_Estimation = new New_Estimation();

    public MoMC estimate(FList<Seqence> data)
    {
        F2<MoMC, FList<Seqence>, MoMC> em_iteration = this::em_iteration;

        AtomicDouble previous = new AtomicDouble(Double.NEGATIVE_INFINITY);
        AtomicInteger iterator = new AtomicInteger();

        return optimise(initialise_with(data), em_iteration.with_second(data).then(model ->
                {
                    final double likelihood = likelihood(model, data).asLog();
                    out.printf("%s [%d] %f %s %n", DateTime.now(),iterator.getAndIncrement(), likelihood,
                            2 + previous.get() - likelihood > .1
                    );
                    previous.set(likelihood);

                })
                ::of, Clock.tickFor(20, SECONDS));
    }

    //FIXME  k(k + n + ngram(n)) -> kn
    MoMC em_iteration(MoMC model, FList<Seqence> data)
    {
        FMap<String, DecimalCounter<Ngram>> conditional_counts = model.tags().zip(tag -> new DecimalCounter<>());
        DecimalCounter<String> prior_counts = new DecimalCounter<>();
        DecimalCounter<oo<String, Context>> conditional_context_counts = new DecimalCounter<>();

        data.each(datum ->
                {
                    posterior_density(datum, model).each((tag, p) ->
                            {
                                prior_counts.plus(tag, p.toDouble());

                                datum.ngrams().each(ngram ->
                                        {
                                            conditional_context_counts.plus(oo(tag, ngram.$1), p.toDouble());
                                            conditional_counts.get(tag).plus(ngram, p.toDouble());
                                        });
                            });
                }
        );

        FMap<String, CPX> new_conditionals = conditional_counts.apply(
                (tag, counts) -> oo(tag, CPX_from(counts.apply(
                        (ngram, count) -> oo(ngram, P(count / conditional_context_counts.get(oo(tag, ngram.$1))))))));

        MPX new_priors = MPX.from(prior_counts.applyToValues(count -> P(count / data.size())));

        return new MoMC(new_priors, new_conditionals);
    }
}





