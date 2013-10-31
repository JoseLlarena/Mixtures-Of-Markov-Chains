package com.fluent.pgm.new_api;

import com.fluent.collections.FMap;
import com.fluent.core.oo;
import com.fluent.math.*;

import static com.fluent.collections.Maps.EMPTY_FMAP;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.math.P.ZERO;

public interface MPX
{
    public static final MPX EMPTY = new MPX(){
        public P p(String item)
        {
            return ZERO;
        }
    };

    public static MPX from(FMap<String, P> raw)
    {
        return new MPTX(raw);
    }

    public static MPX from(Iterable<oo<String, P>> raw)
    {
        FMap<String, P> map = newFMap();

        raw.forEach(entry -> map.plus(entry.$1, entry.$2));

        return MPX.from(map);
    }

    public P p(String item);


    public default FMap<String, P> as_map()
    {
        return EMPTY_FMAP;
    }
}
