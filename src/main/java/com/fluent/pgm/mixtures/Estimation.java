package com.fluent.pgm.mixtures;

import com.fluent.collections.FConcurrentHashMap;
import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.collections.FSetMultiMap;
import com.fluent.core.F2;
import com.fluent.core.OP1;
import com.fluent.core.oo;
import com.fluent.math.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.mixtures.Sequence.Ngram;
import static com.fluent.pgm.mixtures.Token.OOV;

public class Estimation extends Inference
{
    public static final Estimation Estimation = new Estimation();
    static final double SMOOTHING = 1e-10;
    static final double MIN_WEIGHT = Double.MIN_VALUE;

    public MoMC reestimate(MoMC model, FList<FList<Sequence>> data, ExecutorService executor)
    {
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

    public MoMC smooth(MoMC model)
    {
        FMap<String, CPD> transitions_per_tag = model.transitions_per_tag();

        FMap<String, CPD> smoothed_transitions_per_tag = transitions_per_tag.apply_to_values(
                (tag, cpd) ->
                        {
                            FMap<Ngram, P> map = add_OOV(cpd.as_map());

                            FSetMultiMap<Context, oo<Ngram, P>> context_to_entry = map.entries().groupBy
                                    (ngram_with_p -> ngram_with_p.$1.$1);

                            F2<Ngram, P, oo<Ngram, P>> smoothing = (ngram, p) ->
                                    oo(ngram, smooth_ngram(ngram, p, context_to_entry.get(ngram.$1).size()));

                            return CPD.from(map.apply(smoothing));
                        });

        return new MoMC(model.prior(), smoothed_transitions_per_tag);
    }

    public MoMC estimate(FList<oo<Sequence, String>> data)
    {
        F2<DecimalCounter<Ngram>, Double, DecimalCounter<Ngram>> smoothing = this::add_delta_smoothing;

        return smoothed_estimate(data, smoothing.with_arg_2(SMOOTHING / data.size())::of);
    }

    public MoMC smoothed_estimate(FList<oo<Sequence, String>> data, Smoothing smoothing)
    {
        DecimalCounter<String> tags = new DecimalCounter<>();
        FMap<String, DecimalCounter<Ngram>> ngrams_per_tag = newFMap();

        data.each(tagged_datum ->
                {
                    Sequence datum = tagged_datum.$1;
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

        FMap<String, CPD> new_conditionals = smoothed.apply_to_values(to_conditionals(contexts_per_tag(smoothed)));

        MPD new_priors = MPD.from(tags.apply_to_values(count -> P(count / data.size())));

        return new MoMC(new_priors, new_conditionals);
    }

    MoMC maximisation(EM_Counts counts, int N)
    {
        FMap<String, CPD> new_conditionals = counts.for_ngrams().apply_to_values(to_conditionals(counts
                .for_tag_context()));

        MPD new_priors = MPD.from(counts.for_priors().apply_to_values(count -> P(count.sum() / N)));

        return new MoMC(new_priors, new_conditionals);
    }

    F2<String, EM_Counter<Ngram>, CPD> to_conditionals(EM_Counter<oo<String, Context>> ngram_counts)
    {
        return (tag, counts) -> CPD.from(counts.apply_to_values(
                (ngram, count) -> P(count.sum() / ngram_counts.count_of(oo(tag, ngram.context())))));
    }

    F2<String, DecimalCounter<Ngram>, CPD> to_conditionals(DecimalCounter<oo<String, Context>> ngram_counts)
    {
        return (tag, counts) -> CPD.from(counts.apply_to_values(
                (ngram, count) -> P(count / ngram_counts.get(oo(tag, ngram.context())))));
    }

    EM_Counts expectation(MoMC model, FList<Sequence> data, EM_Counts counts)
    {
        data.each(datum ->
                {
                    posterior_density(datum, model).each((tag, p) ->
                            {
                                double weight = p.toDouble() == 0 ? MIN_WEIGHT : p.toDouble();
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

    FMap<Ngram, P> add_OOV(FMap<Ngram, P> ngrams)
    {
        return ngrams.aggregate(newFMap(Ngram.class, P.class), (new_ngrams, ngram, count) ->
                {
                    new_ngrams.put(Ngram.from(OOV, ngram.token()), ONE);
                    new_ngrams.put(Ngram.from(ngram.context(), OOV), ZERO);
                    return new_ngrams.plus(ngram, count);

                }).plus(Ngram.from(OOV, OOV), ONE);
    }

    P smooth_ngram(Ngram ngram, P p, int size)
    {
        return P(((ngram.context() == OOV ? 1. / size : p.toDouble()) + SMOOTHING) / (1 + SMOOTHING * size));
    }


    public interface Smoothing extends OP1<DecimalCounter<Ngram>>
    {
    }

    static class EM_Counts
    {
        EM_Counter priors;
        FMap ngrams;
        EM_Counter tag_context;

        EM_Counts(EM_Counter priors, FMap ngrams, EM_Counter tag_context)
        {
            this.priors = priors;
            this.ngrams = ngrams;
            this.tag_context = tag_context;
        }

        EM_Counter<String> for_priors()
        {
            return priors;
        }

        FMap<String, EM_Counter<Ngram>> for_ngrams()
        {
            return ngrams;
        }

        EM_Counter<oo<String, Context>> for_tag_context()
        {
            return tag_context;
        }
    }
}





