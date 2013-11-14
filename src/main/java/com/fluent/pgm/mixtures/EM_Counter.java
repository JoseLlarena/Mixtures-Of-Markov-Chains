package com.fluent.pgm.mixtures;

import com.fluent.collections.FConcurrentHashMap;

import java.util.concurrent.atomic.DoubleAdder;

public class EM_Counter<ITEM> extends FConcurrentHashMap<ITEM, DoubleAdder>
{
    public EM_Counter<ITEM> plus(final ITEM item, final Double value)
    {
        computeIfAbsent(item, key -> new DoubleAdder()).add(value);

        return this;
    }

    public Double count_of(final ITEM input)
    {
        return get(input, new DoubleAdder()).sum();
    }

}
