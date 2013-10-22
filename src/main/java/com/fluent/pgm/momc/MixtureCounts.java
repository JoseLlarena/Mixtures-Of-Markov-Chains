package com.fluent.pgm.momc;

import com.fluent.collections.FMap;
import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.WeightMap;

import static com.fluent.collections.Maps.newFMap;
import static java.lang.String.format;

public class MixtureCounts extends oo<DecimalCounter<String>, FMap<String, WeightMap>>
{
    public MixtureCounts()
    {
        super(new DecimalCounter<>(), newFMap());
    }

    public FMap<String, Double> forPrior()
    {
        return newFMap($1);
    }

    public FMap<String, WeightMap> forConditionals()
    {
        return newFMap($2);
    }

    public MixtureCounts add(final P weight, final String tag, final Sequence o)
    {
        $1.plus(tag, weight.toDouble());

        o.bigrams().each(bigram ->
                {
                    WeightMap cpd = $2.get(tag);
                    if (cpd == null)
                    {
                        $2.plus(tag, cpd = new WeightMap());
                    }

                    cpd.add(bigram, cpd.get(bigram, 0.) + weight.toDouble());

                });

        return this;
    }

    public String toString()
    {
        return format("prior %n%s %nconditionals %n %s%n", $1, $2);
    }

}
