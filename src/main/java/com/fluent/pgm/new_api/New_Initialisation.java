package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.oo;
import com.fluent.math.*;

import java.util.Random;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.Syntax.from;
import static com.fluent.core.Words.to;
import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.new_api.Seqence.Ngram;

public interface New_Initialisation
{
    public static String C1 = "C1", C2 = "C2", C3 = "C3";

    public default MoMC initialise_with(FList<Seqence> data)
    {
        int k = 2;
        double conditional_scaling = 1. / data.size(), prior_scaling = 1. / k;

        Random random = new Random(1234);
        FMap<Ngram, P> conditionals = newFMap();

        data.each(datum ->
                {
                    datum.ngrams().each(ngram -> conditionals.put(ngram, P(random.nextDouble() * conditional_scaling)));
                });

        FList<oo<String, P>> prior = newFList(from(1, to(k)),
                index -> oo("C" + index, P(random.nextDouble() * prior_scaling)));

        CPX cpd = CPD_Builder.CPX_from(conditionals);

        return new MoMC(MPX.from(prior), prior.apply(oo -> oo.$1).zip(tag -> cpd));
    }

}
