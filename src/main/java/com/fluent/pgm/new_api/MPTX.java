package com.fluent.pgm.new_api;

import com.fluent.collections.FHashMap;
import com.fluent.math.*;

import java.util.Map;

public class MPTX extends FHashMap<String, P> implements MPX
{
    public MPTX(Map<String, P> map)
    {
        this.putAll(map);
    }

    public P p(String item)
    {
        return of(item);
    }
}
