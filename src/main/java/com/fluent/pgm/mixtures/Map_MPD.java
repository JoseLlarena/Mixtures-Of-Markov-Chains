package com.fluent.pgm.mixtures;

import com.fluent.collections.FHashMap;
import com.fluent.collections.FMap;
import com.fluent.math.*;

import java.util.Map;

public class Map_MPD extends FHashMap<String, P> implements MPD
{
    public Map_MPD(Map<String, P> map)
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
