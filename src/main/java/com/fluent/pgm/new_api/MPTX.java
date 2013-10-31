package com.fluent.pgm.new_api;

import com.fluent.collections.FHashMap;
import com.fluent.collections.FMap;
import com.fluent.math.*;

import java.util.Map;

public class MPTX extends FHashMap<String, P> implements MPX
{
    public MPTX(Map<String, P> map)
    {
        super(map);
    }

    public P p(String item)
    {
        return of(item);
    }

    public FMap<String, P> as_map()
    {
        return this;
    }
}
