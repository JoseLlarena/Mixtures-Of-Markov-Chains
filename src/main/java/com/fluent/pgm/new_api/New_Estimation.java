package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F2;
import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.util.Clock;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.AtomicDouble;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

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
                    out.printf("%s [%d] %f %s %s %n",
                            DateTimeFormat.fullDateTime().print(DateTime.now()),
                            iterator.getAndIncrement(),
                            likelihood,
                            2 + previous.get() - likelihood > .1 ,
                            P.terms_to_sum.stats());

                    previous.set(likelihood);

                })
                ::of, Clock.tickFor(2, SECONDS));
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
                                            conditional_context_counts.plus(Tag_Context.from(tag, ngram.context()),
                                                    p.toDouble());
                                            conditional_counts.get(tag).plus(ngram, p.toDouble());
                                        });
                            });
                }
        );

        FMap<String, CPX> new_conditionals = conditional_counts.apply_to_values(
                (tag, counts) -> CPX_from(counts.apply_to_values(
                        (ngram, count) -> P(count / conditional_context_counts.get(Tag_Context.from(tag,
                                ngram.context()))))));

        MPX new_priors = MPX.from(prior_counts.applyToValues(count -> P(count / data.size())));

        return new MoMC(new_priors, new_conditionals);
    }

    static class Tag_Context extends oo<String, Context>
    {
        static Cache<Long, Tag_Context> id_to_tag_context= CacheBuilder.newBuilder()
                .maximumSize(1000_000)
                .build();

        long id;

        public static Tag_Context from(String $1, Context $2)
        {
            try
            {
                long   id = Common.hash.newHasher().putString($1).putLong($2.id()).hash().asLong();
                return id_to_tag_context.get(id, () -> new Tag_Context($1, $2,id));
            }
            catch (ExecutionException e)
            {
                long   id = Common.hash.newHasher().putString($1).putLong($2.id()).hash().asLong();
                return new Tag_Context($1, $2,id);
            }
        }
        Tag_Context(String $1, Context $2,long id)
        {
            super($1, $2);

        }

        public boolean equals(Object object)
        {
            return object == this || object instanceof Tag_Context && ((Tag_Context) object).id == id;
        }

        public int hashCode()
        {
            return (int)id;
        }
    }
}





