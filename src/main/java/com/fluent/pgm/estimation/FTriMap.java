package com.fluent.pgm.estimation;

import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.core.F1;
import com.fluent.core.oo;
import com.fluent.core.ooo;

public interface FTriMap<KEY1, KEY2, VALUE>
{
	public VALUE get(KEY1 key1, KEY2 key2, VALUE defaultValue);

	public VALUE get(oo<KEY1, KEY2> key1key2, VALUE defaultValue);

	public FTriMap<KEY1, KEY2, VALUE> add(KEY1 key1, KEY2 key2, VALUE value);

	public FTriMap<KEY1, KEY2, VALUE> add(oo<KEY1, KEY2> key1key2, VALUE value);

	public FSet<KEY1> firstKeys();

	public String toString(String format);

	public VALUE get(KEY1 key1, KEY2 key2);

	public boolean hasFirstKey(KEY1 key1);

	public boolean hasSecondKey(String token);

	public FSet<KEY2> secondKeys();

	public FSet<ooo<KEY1, KEY2, VALUE>> entries();

	public <NEW_VALUE> FTriMap<KEY1, KEY2, NEW_VALUE> applyToValues(F1<VALUE, NEW_VALUE> function);

    public   FMap<KEY1, FMap<KEY2, VALUE>> asFMap();

}
