package com.fluent.pgm.estimation;

import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.core.F1;
import com.fluent.core.oo;
import com.fluent.core.ooo;

import java.util.Formatter;
import java.util.Map.Entry;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.collections.Sets.newFSet;
import static com.fluent.core.Syntax.cast;
import static com.fluent.core.ooo.ooo;
import static java.util.Objects.hash;

public class FHashTriMap<KEY1, KEY2, VALUE> implements FTriMap<KEY1, KEY2, VALUE>
{
    private final FMap<KEY1, FMap<KEY2, VALUE>> delegate;

    public FHashTriMap()
    {
        delegate = newFMap();
    }

    public FHashTriMap(final FMap<KEY1, FMap<KEY2, VALUE>> delegate)
    {
        this.delegate = delegate;
    }

    public FMap<KEY1, FMap<KEY2, VALUE>> asFMap()
    {
        return newFMap(delegate);
    }

    public FHashTriMap<KEY1, KEY2, VALUE> add(final oo<KEY1, KEY2> key1key2, final VALUE value)
    {
        return add(key1key2.$1, key1key2.$2, value);
    }

    public FHashTriMap<KEY1, KEY2, VALUE> add(final KEY1 key1, final KEY2 key2, final VALUE value)
    {
        FMap<KEY2, VALUE> key2ToValue = delegate.get(key1);

        if (key2ToValue == null)
        {
            delegate.plus(key1, key2ToValue = newFMap());
        }

        key2ToValue.plus(key2, value);

        return this;
    }

    public <NEW_VALUE> FHashTriMap<KEY1, KEY2, NEW_VALUE> applyToValues(final F1<VALUE, NEW_VALUE> function)
    {
        final FHashTriMap<KEY1, KEY2, NEW_VALUE> newMap = new FHashTriMap<>();

        for (final Entry<KEY1, FMap<KEY2, VALUE>> keyAndValue : delegate.entrySet())
        {
            for (final oo<KEY2, VALUE> key2_value : keyAndValue.getValue())
            {
                newMap.add(keyAndValue.getKey(), key2_value.$1, function.of(key2_value.$2));
            }
        }

        return newMap;
    }

    public FSet<ooo<KEY1, KEY2, VALUE>> entries()
    {
        final FSet<ooo<KEY1, KEY2, VALUE>> entries = newFSet();

        for (final oo<KEY1, FMap<KEY2, VALUE>> key1_key2ToValue : delegate)
        {
            for (final oo<KEY2, VALUE> key2_value : key1_key2ToValue.$2)
            {
                entries.add(ooo(key1_key2ToValue.$1, key2_value.$1, key2_value.$2));
            }
        }

        return entries;
    }

    public boolean equals(final Object obj)
    {
        FHashTriMap<?, ?, ?> other = null;

        return this == obj || (other = cast(obj, this)) != null && delegate.equals(other.delegate);
    }


    public FSet<KEY1> firstKeys()
    {
        return delegate.keys();
    }

    public VALUE get(final oo<KEY1, KEY2> key1key2, final VALUE defaultValue)
    {
        return get(key1key2.$1, key1key2.$2, defaultValue);
    }

    public VALUE get(final KEY1 key1, final KEY2 key2)
    {
        final FMap<KEY2, VALUE> fMap = delegate.get(key1);
        if (fMap == null)
        {
            return null;
        }

        return fMap.get(key2);
    }

    public VALUE get(final KEY1 key1, final KEY2 key2, final VALUE defaultValue)
    {
        return delegate.get(key1, newFMap()).get(key2, defaultValue);
    }

    public boolean hasFirstKey(final KEY1 key1)
    {
        return delegate.containsKey(key1);
    }

    public int hashCode()
    {
        return hash(this.delegate);
    }

    public boolean hasSecondKey(final String token)
    {
        return secondKeys().contains(token);
    }

    public FSet<KEY2> secondKeys()
    {
        final FSet<KEY2> secondKeys = newFSet();

        delegate.values().each(key2ToValue -> secondKeys.addAll(key2ToValue.keySet()));

        return secondKeys;
    }

    public String toString()
    {
        return toString("%s : %s : %s%n");
    }

    @SuppressWarnings("resource")
    public String toString(final String format)
    {
        final Formatter formatter = new Formatter();

        for (final oo<KEY1, FMap<KEY2, VALUE>> entry :asFMap())
        {
            for (final oo<KEY2, VALUE> key2_value : entry.$2)
            {
                formatter.format(format, entry.$1, key2_value.$1, key2_value.$2);
            }
        }

        return formatter.toString();
    }

}
