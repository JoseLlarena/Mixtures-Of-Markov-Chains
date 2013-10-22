package com.fluent.pgm;

import com.fluent.collections.FImmutableHashMap;
import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.math.*;

import java.util.Map;

import static com.fluent.collections.Sets.newFSet;
import static com.fluent.math.P.*;
import static com.fluent.pgm.Sequence.*;

public class CPT_with_OOV extends FImmutableHashMap<String, FMap<String, P>> implements CPD
{
    public CPT_with_OOV(Map<String, FMap<String, P>> contextTokenProbability)
    {
        super(contextTokenProbability);
    }

    public FSet<String> contexts()
    {
        return keys();
    }

    public P p(final String token, final String context)
    {
        if (token == START && context == START_STATE || context == END_STATE && token == END)
        {
            return ONE;
        }

        if (token == START_STATE || token == START || context == END_STATE || context == END || token == END_STATE
                && context == START_STATE || token == END && context == START)
        {
            return ZERO;
        }
        final P p =  get(context, token);
        if (p != null)
        {
            return p;
        }

        return nonZeroProbability(token, context);
    }

    public FSet<String> tokens()
    {
        final FSet<String> secondKeys = newFSet();

        for (final FMap<String, P> key2ToValue : values())
        {
            secondKeys.addAll(key2ToValue.keySet());
        }

        return secondKeys;
    }

    P get(final String key1, final String key2)
    {
        final FMap<String, P> fMap = get(key1);
        if (fMap == null)
        {
            return null;
        }

        return fMap.get(key2);
    }

    P nonZeroProbability(final String token, final String context)
    {
        if (containsKey( context))
        {
            return get(context, OOV);
        }

        if (tokens().contains(token))
        {
            return get(OOV, token);
        }

        return get(OOV, OOV);
    }
}
