package com.fluent.pgm.new_api;

import com.fluent.collections.FMap;
import com.fluent.math.*;

public interface MPX extends FMap<String, P>
{
    public P p(String item);


    public static MPX from(FMap<String,P> raw)
    {
        return new MPTX(raw);
    }
}
