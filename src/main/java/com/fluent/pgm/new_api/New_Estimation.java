package com.fluent.pgm.new_api;

import com.fluent.collections.FConcurrentHashMap;
import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F1;
import com.fluent.core.F2;
import com.fluent.core.oo;
import com.fluent.core.ooo;
import com.fluent.math.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.new_api.CPD_Builder.CPX_from;
import static com.fluent.pgm.new_api.Seqence.Ngram;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.out;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class New_Estimation extends New_Inference implements New_Optimisation
{
    public static final New_Estimation Estimation = new New_Estimation();

    static Counts merge(Collection<Future<Counts>> counts_in_progress) throws Exception
    {
        return newFList(counts_in_progress).throwing_aggregate(new Counts(),
                (count, future) -> count.add(future.get()));
    }

    static MoMC maximisation(Counts counts, int N)
    {
        FMap<String, DecimalCounter<Ngram>> ngram_counts = counts.$2;
        DecimalCounter<oo<String, Context>> context_counts = counts.$3;
        FMap<String, CPX> new_conditionals = ngram_counts.apply_to_values(to_conditionals(context_counts));

        DecimalCounter<String> prior_counts = counts.$1;
        MPX new_priors = MPX.from(prior_counts.applyToValues(count -> P(count / N)));

        return new MoMC(new_priors, new_conditionals);
    }

    static MoMC maximisation(EM_Counts counts, int N)
    {
        FMap<String, CPX> new_conditionals = counts.$2.apply_to_values(to_conditionals(counts.$3));

        MPX new_priors = MPX.from(counts.$1.applyToValues(count -> P(count.sum() / (double) N)));

        return new MoMC(new_priors, new_conditionals);
    }

    static F2<String, DecimalCounter<Ngram>, CPX> to_conditionals(DecimalCounter<oo<String, Context>>
                                                                          conditional_context_counts)
    {
        return (tag, counts) -> CPX_from(counts.apply_to_values(
                (ngram, count) -> P(count / conditional_context_counts.get(oo(tag, ngram.context())))));
    }

    static F2<String, EM_Counter<Ngram>, CPX> to_conditionals(EM_Counter<oo<String, Context>> ngram_counts)
    {
        return (tag, counts) -> CPX_from(counts.apply_to_values(
                (ngram, count) -> P(count.sum() / ngram_counts.count_of(oo(tag, ngram.context())))));
    }

    public MoMC parallel_em_iteration(MoMC model, FList<Seqence> data)
    {
        out.println(DateTimeFormat.fullDateTime().print(DateTime.now()) + " EM STARTS");
        int thread_count = getRuntime().availableProcessors();
        ExecutorService executor = newFixedThreadPool(thread_count);

        Counts counts = null;

        F1<FList<Seqence>, Callable<Counts>> expectation = split -> () -> expectation(model, split);

        try
        {
            executor.invokeAll(data.split(thread_count).apply(expectation));

        }
        catch (Exception cause)
        {
            throw new RuntimeException(cause);
        }
        finally
        {
            executor.shutdown();
        }

        return maximisation(counts, data.size());
    }

    public MoMC concurrent_em_iteration(MoMC model, FList<Seqence> data)
    {
        out.println(DateTimeFormat.fullDateTime().print(DateTime.now()) + " EM STARTS");
        int thread_count = getRuntime().availableProcessors();
        ExecutorService executor = newFixedThreadPool(thread_count);

        FMap<String, EM_Counter<Ngram>> ngram_counts = model.tags().zip(tag -> new EM_Counter<>());
        ngram_counts = new FConcurrentHashMap<String, EM_Counter<Ngram>>(ngram_counts) {};

        EM_Counts counts = new EM_Counts(new EM_Counter<>(), ngram_counts, new EM_Counter<>());

        F1<FList<Seqence>, Callable<EM_Counts>> expectation = split -> () -> expectation(model, split, counts);

        try
        {

            executor.invokeAll(data.split(thread_count).apply(expectation));
        }
        catch (Exception cause)
        {
            throw new RuntimeException(cause);
        }
        finally
        {
            executor.shutdown();
        }

        return maximisation(counts, data.size());
    }

    Counts expectation(MoMC model, FList<Seqence> data)
    {
        FMap<String, DecimalCounter<Ngram>> ngram_counts = model.tags().zip(tag -> new DecimalCounter<>());
        DecimalCounter<String> prior_counts = new DecimalCounter<>();
        DecimalCounter<oo<String, Context>> context_counts = new DecimalCounter<>();

        data.each(datum ->
                {
                    posterior_density(datum, model).each((tag, p) ->
                            {
                                double weight = p.toDouble();
                                prior_counts.plus(tag, weight);

                                datum.ngrams().each(ngram ->
                                        {

                                            context_counts.plus(oo(tag, ngram.context()), weight);
                                            ngram_counts.get(tag).plus(ngram, weight);
                                        });
                            });
                }
        );

        return new Counts(prior_counts, ngram_counts, context_counts);
    }

    EM_Counts expectation(MoMC model, FList<Seqence> data, EM_Counts counts)
    {
        data.each(datum ->
                {
                    posterior_density(datum, model).each((tag, p) ->
                            {
                                double weight = p.toDouble();
                                counts.$1.plus(tag, weight);

                                datum.ngrams().each(ngram ->
                                        {

                                            counts.$3.plus(oo(tag, ngram.context()), weight);
                                            counts.$2.get(tag).plus(ngram, weight);

                                        });
                            });
                }
        );

        return counts;
    }

    static class EM_Counts extends ooo<EM_Counter<String>, FMap<String, EM_Counter<Ngram>>,
            EM_Counter<oo<String, Context>>>
    {

        EM_Counts(EM_Counter<String> $1,
                  FMap<String, EM_Counter<Ngram>> $2,
                  EM_Counter<oo<String, Context>> $3)
        {
            super($1, $2, $3);
        }

    }

    static class Counts extends ooo<DecimalCounter<String>, FMap<String, DecimalCounter<Ngram>>,
            DecimalCounter<oo<String, Context>>>
    {

        Counts(DecimalCounter<String> $1,
               FMap<String, DecimalCounter<Ngram>> $2,
               DecimalCounter<oo<String, Context>> $3)
        {
            super($1, $2, $3);
        }

        Counts()
        {
            this(new DecimalCounter<>(), newFMap(), new DecimalCounter<>());
        }

        Counts add(Counts to_this)
        {
            DecimalCounter<String> prior_counts = $1;
            FMap<String, DecimalCounter<Ngram>> ngram_counts = $2;
            DecimalCounter<oo<String, Context>> context_counts = $3;

            to_this.$1.each((tag, count) -> prior_counts.plus(tag, count));

            to_this.$2.each((tag, counts) -> counts.each((ngram, count) ->
                    {
                        ngram_counts.putIfAbsent(tag, new DecimalCounter<>());
                        ngram_counts.get(tag).plus(ngram, count);
                    }
            ));

            to_this.$3.each((tag_context, count) -> context_counts.plus(tag_context, count));

            return this;
        }
    }
}





