package com.fluent.pgm.new_api;

import com.fluent.collections.FConcurrentHashMap;
import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F2;
import com.fluent.core.OP1;
import com.fluent.core.oo;
import com.fluent.core.ooo;
import com.fluent.math.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.new_api.CPD_Builder.CPX_from;
import static com.fluent.pgm.new_api.Seqence.Ngram;
import static com.fluent.pgm.new_api.Token.OOV;
import static java.lang.System.out;

public class New_Estimation extends New_Inference
{
    public static final New_Estimation Estimation = new New_Estimation();
    static final double SMOOTHING = .00001;

    public MoMC reestimate(MoMC model, FList<FList<Seqence>> data, ExecutorService executor)
    {
        out.println(DateTimeFormat.fullDateTime().print(DateTime.now()) + " EM STARTS");
        EM_Counts counts = new EM_Counts(new EM_Counter<>(), new FConcurrentHashMap<>(), new EM_Counter<>());

        try
        {
            executor.invokeAll(data.apply(split -> (Callable<EM_Counts>) () -> expectation(model, split, counts)));
        }
        catch (Exception cause)
        {
            throw new RuntimeException(cause);
        }

        return maximisation(counts, data.aggregate(0, (N, split) -> N + split.size()));
    }

    public MoMC smooth(MoMC model, FList<Seqence> data)
    {
        FMap<String, CPX> transitions_per_tag = model.transitions_per_tag();

        return new MoMC(model.prior(), transitions_per_tag);
    }

    public MoMC estimate(FList<oo<Seqence, String>> data)
    {
        F2<DecimalCounter<Ngram>, Double, DecimalCounter<Ngram>> smoothing = this::add_delta_smoothing;

        return smoothed_estimate(data, smoothing.with_arg_2(SMOOTHING / data.size())::of);
    }

    public MoMC smoothed_estimate(FList<oo<Seqence, String>> data, Smoothing smoothing)
    {
        DecimalCounter<String> tags = new DecimalCounter<>();
        FMap<String, DecimalCounter<Ngram>> ngrams_per_tag = newFMap();

        data.each(tagged_datum ->
                {
                    Seqence datum = tagged_datum.$1;
                    String tag = tagged_datum.$2;

                    tags.plus(tag, 1.);

                    datum.ngrams().each(ngram ->
                            {
                                ngrams_per_tag.computeIfAbsent(tag, key -> new DecimalCounter<Ngram>()).plus(ngram, 1.);
                            });
                }
        );

        FMap<String, DecimalCounter<Ngram>> smoothed = ngrams_per_tag.apply_to_values(
                (tag, ngrams) -> smoothing.of(ngrams));

        FMap<String, CPX> new_conditionals = smoothed.apply_to_values(to_conditionals(contexts_per_tag(smoothed)));

        MPX new_priors = MPX.from(tags.applyToValues(count -> P(count / data.size())));

        return new MoMC(new_priors, new_conditionals);
    }

    MoMC maximisation(EM_Counts counts, int N)
    {
        FMap<String, CPX> new_conditionals = counts.for_ngrams().apply_to_values(to_conditionals(counts
                .for_tag_context()));

        MPX new_priors = MPX.from(counts.for_priors().applyToValues(count -> P(count.sum() / N)));

        return new MoMC(new_priors, new_conditionals);
    }

    F2<String, EM_Counter<Ngram>, CPX> to_conditionals(EM_Counter<oo<String, Context>> ngram_counts)
    {
        return (tag, counts) -> CPX_from(counts.apply_to_values(
                (ngram, count) -> P(count.sum() / ngram_counts.count_of(oo(tag, ngram.context())))));
    }

    F2<String, DecimalCounter<Ngram>, CPX> to_conditionals(DecimalCounter<oo<String, Context>> ngram_counts)
    {
        return (tag, counts) -> CPX_from(counts.apply_to_values(
                (ngram, count) -> P(count / ngram_counts.get(oo(tag, ngram.context())))));
    }

    EM_Counts expectation(MoMC model, FList<Seqence> data, EM_Counts counts)
    {
        data.each(datum ->
                {
                    posterior_density(datum, model).each((tag, p) ->
                            {
                                double weight = p.toDouble();
                                counts.for_priors().plus(tag, weight);

                                datum.ngrams().each(ngram ->
                                        {
                                            counts.for_tag_context().plus(oo(tag, ngram.context()), weight);
                                            counts.for_ngrams().computeIfAbsent(tag,
                                                    key -> new EM_Counter<Ngram>()).plus(ngram, weight);
                                        });
                            });
                }
        );

        return counts;
    }

    DecimalCounter<oo<String, Context>> contexts_per_tag(FMap<String, DecimalCounter<Ngram>> ngrams_per_tag)
    {
        return ngrams_per_tag.aggregate(new DecimalCounter<>(),

                (contexts_per_tag, tag, ngram_counts) -> ngram_counts.aggregate(contexts_per_tag,

                        (ongoing_counts, ngram, count) -> ongoing_counts.plus(oo(tag, ngram.context()), count)));
    }

    DecimalCounter<Ngram> add_delta_smoothing(DecimalCounter<Ngram> ngrams, double delta)
    {
        return ngrams.aggregate(new DecimalCounter<Ngram>(), (new_ngrams, ngram, count) ->
                {
                    new_ngrams.put(Ngram.from(OOV, ngram.token()), delta);
                    new_ngrams.put(Ngram.from(ngram.context(), OOV), delta);
                    return new_ngrams.plus(ngram, count + delta);

                }).plus(Ngram.from(OOV, OOV), delta);
    }


    public interface Smoothing extends OP1<DecimalCounter<Ngram>>
    {
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

        EM_Counter<String> for_priors()
        {
            return $1;
        }

        FMap<String, EM_Counter<Ngram>> for_ngrams()
        {
            return $2;
        }

        EM_Counter<oo<String, Context>> for_tag_context()
        {
            return $3;
        }
    }
}





