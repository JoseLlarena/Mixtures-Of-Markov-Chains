package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F1;
import com.fluent.core.F2;
import com.fluent.core.oo;
import com.fluent.core.ooo;
import com.fluent.math.*;
import com.fluent.util.Clock;

import static com.fluent.math.P.*;
import static com.fluent.pgm.new_api.Seqence.N_gram;
import static java.lang.System.out;
import static java.time.temporal.ChronoUnit.SECONDS;

public class New_Estimation extends New_Inference implements New_Optimisation, New_Initialisation
{
    public static New_Estimation New_Estimation = new New_Estimation();
    static F2<Double, oo<String, P>, Double> count_aggregation = (count, posterior) -> count + posterior.$2.toDouble();

    public MoMC estimate(FList<Seqence> data)
    {
        F2<MoMC, FList<Seqence>, MoMC> em_iteration = this::em_iteration;

        return optimise(initialise_with(data), em_iteration.with_second(data)
                .first(model -> out.println(model.prior()))::of, Clock.tickFor(1, SECONDS));
    }

    MoMC em_iteration(MoMC model, FList<Seqence> data)
    {
        return new MoMC(prior_from2(data, model), conditionals_from(data, model));
    }

    MPX prior_from(FList<Seqence> data, MoMC model)
    {
        final FList<oo<String, P>> posteriors = data.flat(datum -> posterior_density(datum, model).entries());

        FMap posteriors_by_tag = posteriors.groupBy(oo::$1).asFMap();

        FMap expected_counts = posteriors_by_tag.applyToValues(normalisation_with(data.size()));

        return MPX.from(expected_counts);
    }

    //FIXME  kn + knn  + k  -> kn
    MPX prior_from2(FList<Seqence> data, MoMC model)
    {
        FMap<String, Double> tag_to_count = model.tags().cross(data).aggregate(new DecimalCounter(),
                aggregation_with(model));

        return MPX.from(tag_to_count.applyToValues(count -> P(count / data.size())));
    }

    F2<DecimalCounter, oo<String, Seqence>, DecimalCounter> aggregation_with(MoMC model)
    {
        return (counter, tag_datum) -> counter.plus(tag_datum.$1, posterior_of(tag_datum.$1, tag_datum.$2,
                model).toDouble());
    }

    F1<FList<oo<String, P>>, P> normalisation_with(double N)
    {
        return posteriors -> P(posteriors.aggregate(0., count_aggregation) / N);
    }

    FMap<String, CPX> conditionals_from(FList<Seqence> data, MoMC model)
    {
//        FList<Tag_NGram_Count> counts_per_ngram =
//                count_per_ngram_from(
//                        counts_per_ngrams_from(
//                                counts_per_datum_from(data, model)));
//
//        FListMultiMap<Tag_Ngram, Tag_NGram_Count> tag_ngram_to_counts =
//                counts_per_ngram.groupBy(count -> new Tag_Ngram(count.tag(), count.n_gram()));
//
//        FMap<Tag_Ngram, Double> tag_ngram_to_count = tag_ngram_to_counts.asFMap().apply(
//                (tag_ngram, counts) -> oo(tag_ngram, sum_of(counts)));
//
//        FSetMultiMap<String, oo<Tag_Ngram, Double>> what = tag_ngram_to_count.entries().groupBy(tag_ngram_x_count ->
//                tag_ngram_x_count.$1.tag());

        return Common.example_initial_model().transitions();
    }

    Double sum_of(FList<Tag_NGram_Count> counts)
    {
        return counts.aggregate(0., (aggregate, count) -> aggregate + count.posterior());
    }

    FList<Tag_NGram_Count> count_per_ngram_from(FList<Tag_NGrams_Count> counts_per_ngrams)
    {
        return counts_per_ngrams.flat(count -> count.n_grams().apply(count::to_Tag_NGram_Count));
    }

    FList<Tag_Datum_Count> counts_per_datum_from(FList<Seqence> data, MoMC model)
    {
        return data.pair(model.tags()).apply(datum_x_tag -> Tag_Datum_Count.from(datum_x_tag,
                posterior_of(datum_x_tag, model).toDouble()));
    }

    FList<Tag_NGrams_Count> counts_per_ngrams_from(FList<Tag_Datum_Count> counts_per_datum)
    {
        return counts_per_datum.apply(Tag_Datum_Count::to_Tag_NGrams_Count);

    }

    P posterior_of(String tag, Seqence datum, MoMC model)
    {
        return posterior_density(datum, model).get(tag);
    }

    P posterior_of(oo<Seqence, String> datum_x_tag, MoMC model)
    {
        return posterior_density(datum_x_tag.$1, model).get(datum_x_tag.$2);
    }

    static class Tag_Datum_Count extends ooo<String, Seqence, Double>
    {
        static Tag_Datum_Count from(oo<Seqence, String> $1$2, Double $3)
        {
            return new Tag_Datum_Count($1$2.$2, $1$2.$1, $3);
        }

        Tag_Datum_Count(String $1, Seqence $2, Double $3)
        {
            super($1, $2, $3);
        }

        Tag_NGrams_Count to_Tag_NGrams_Count()
        {
            return Tag_NGrams_Count.from($1, $2.n_grams, $3);
        }
    }

    static class Tag_NGrams_Count extends ooo<String, FList<N_gram>, Double>
    {
        static Tag_NGrams_Count from(String $1, FList<N_gram> $2, Double $3)
        {
            return new Tag_NGrams_Count($1, $2, $3);
        }

        Tag_NGrams_Count(String $1, FList<N_gram> $2, Double $3)
        {
            super($1, $2, $3);
        }

        FList<N_gram> n_grams()
        {
            return $2;
        }

        Tag_NGram_Count to_Tag_NGram_Count(N_gram n_gram)
        {
            return Tag_NGram_Count.triple(tag(), n_gram, posterior());
        }

        Double posterior()
        {
            return $3;
        }

        String tag()
        {
            return $1;
        }
    }

    static class Tag_NGram_Count extends ooo<String, N_gram, Double>
    {
        static Tag_NGram_Count triple(String $1, N_gram $2, Double $3)
        {
            return new Tag_NGram_Count($1, $2, $3);
        }

        Tag_NGram_Count(String $1, N_gram $2, Double $3)
        {
            super($1, $2, $3);
        }

        N_gram n_gram()
        {
            return $2;
        }

        Double posterior()
        {
            return $3;
        }

        String tag()
        {
            return $1;
        }
    }

    static class Tag_Ngram extends oo<String, N_gram>
    {
        Tag_Ngram(String $1, N_gram $2)
        {
            super($1, $2);
        }

        String tag()
        {
            return $1;
        }
    }
}
