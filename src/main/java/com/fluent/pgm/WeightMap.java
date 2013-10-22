package com.fluent.pgm;

import com.fluent.collections.FSet;
import com.fluent.core.ooo;
import com.fluent.pgm.estimation.FHashTriMap;

public class WeightMap extends FHashTriMap<String, String, Double>
{
    public WeightMap(final ooo<String, String, Double> firstEntry,
                     final ooo<String, String, Double>... otherEntries)
    {

        add(firstEntry.$1, firstEntry.$2, firstEntry.$3);

        for (final ooo<String, String, Double> entry : otherEntries)
        {
            add(entry.$1, entry.$2, entry.$3);
        }

    }

    public WeightMap()
    {

    }


    public FSet<String> contexts()
    {
        return firstKeys();
    }

    public FSet<String> tokens()
    {
        return secondKeys();
    }
}
