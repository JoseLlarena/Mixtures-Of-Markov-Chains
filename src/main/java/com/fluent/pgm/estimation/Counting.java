package com.fluent.pgm.estimation;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.core.oo;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.WeightMap;
import com.fluent.pgm.new_api.Token;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.oo;
import static com.fluent.pgm.new_api.Token.*;
import static java.lang.System.out;

public class Counting
{

    public static void main(String... args)
    {
        final FList<Token> tokens = newFList("Markov".split("")).minus("").apply(letter -> Token.from
                (letter));

        out.println(tokens);
        out.println((Counting.of.biTokensIn(tokens)).toString("%-10s : %s%n"));

        out.println(Counting.of.smoothed(Counting.of.biTokensIn(tokens), 1).toString("%-10s : %s%n"));
    }

    public static final Counting of = new Counting();

    public FMap<oo<Token, Token>, Integer> biTokensIn(final Iterable<Token> data)
    {
        final FMap<oo<Token, Token>, Integer> counts = newFMap();

        Token previous = START;

        for (Token item : data)
        {
            if (previous != END && item != START)
                counts.plus(oo(previous, item), counts.get(oo(previous, item), 0) + 1);

            previous = item;
        }

        return counts.plus(oo(previous, END), 1);
    }

    public FMap<oo<Token, Token>, Integer> biTokensIn2(final Iterable<Token> data)
    {
        final FMap<oo<Token, Token>, Integer> counts = newFMap();

        AtomicReference<Token> previous = new AtomicReference<>(START);

        data.forEach(item ->
                {
                    if (previous != END && item != START)
                        counts.plus(oo(previous.get(), item), counts.get(oo(previous.getAndSet(item), item), 0) + 1);
                });

        return counts.plus(oo(previous.get(), END), 1);
    }

    public WeightMap bigramFrequency(final FList<Sequence> observations)
    {
        final WeightMap bigramFrequency = new WeightMap();

        observations.each(O -> {
            O.bigrams().each(bigram -> bigramFrequency.add(bigram, bigramFrequency.get(bigram, 0.) + 1));
        });

        return bigramFrequency;
    }

    public <ITEM> FMap<oo<ITEM, ITEM>, Integer> bigramsIn(final Iterable<ITEM> data)
    {
        final FMap<oo<ITEM, ITEM>, Integer> counts = newFMap();

        ITEM previous = null;
        for (ITEM item : data)
        {
            counts.plus(oo(previous, item), counts.get(oo(previous, item), 0) + 1);

            previous = item;
        }

        return counts.plus(oo(previous, null), 1);
    }

    public <ITEM> FMap<ITEM, Integer> count(final Collection<ITEM> data)
    {
        final FMap<ITEM, Integer> counts = newFMap();

        data.forEach(item -> counts.plus(item, counts.get(item, 0) + 1));

        return counts;
    }

    public FMap<oo<Token, Token>, Double> frequency(FMap<oo<Token, Token>, Integer> frequency)
    {
        double N = frequency.size();

        return frequency.applyToValues(count -> count / N);
    }

    public <ITEM> FMap<ITEM, Double> frequency(final Collection<ITEM> data)
    {
        final double N = data.size();

        return count(data).applyToValues(count -> count / N);
    }

    public FMap<oo<Token, Token>, Double> smoothed(FMap<oo<Token, Token>, Integer> raw, double delta)
    {
        final FSet<oo<Token, Token>> unseen = raw.keys().apply(bigram -> oo(bigram.$1, OOV)).plus(oo(OOV, OOV));

        FMap<oo<Token, Token>, Double> discountedUnseen = unseen.zip(bigram -> delta);

        FMap<oo<Token, Token>, Double> discountedSeen = raw.applyToValues(count -> count + delta);

        FMap<oo<Token, Token>, Double> allDiscounted = discountedSeen.plus(discountedUnseen);

        double N = allDiscounted.values().collect(0., Double::sum);
        double V = allDiscounted.size();

        return allDiscounted.applyToValues(count -> count / (N + V * delta));
    }

}
