package com.fluent.pgm.new_api;

import com.fluent.collections.FMap;
import com.fluent.collections.Maps;
import com.fluent.core.oo;
import com.fluent.math.*;

public interface MPX extends FMap<String, P>
{
    public static MPX from(FMap<String, P> raw)
    {
        return new MPTX(raw);
    }

    public static MPX from(Iterable<oo<String, P>> raw)
    {
        FMap<String, P> map = Maps.newFMap();

        raw.forEach(entry -> map.plus(entry.$1, entry.$2));

        return new MPTX(map);
    }

    public P p(String item);
}
