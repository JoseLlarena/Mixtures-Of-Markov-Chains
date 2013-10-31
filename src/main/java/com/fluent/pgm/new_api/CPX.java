package com.fluent.pgm.new_api;

import com.fluent.collections.FSet;
import com.fluent.core.ooo;
import com.fluent.math.*;

import java.util.Iterator;

import static com.fluent.collections.Sets.EMPTY_FSET;
import static com.google.common.collect.Iterators.emptyIterator;

public interface CPX extends Iterable<ooo<Context,Token,P>>
{
    public P p(Token token, Context context);

    public default MPX mpd_from(Context context)
    {
        return MPX.EMPTY;
    }

    public default Iterator<ooo<Context,Token,P>>  iterator()
    {
        return emptyIterator();
    }

    public default FSet<Token> tokens()
    {
        return EMPTY_FSET;
    }
}
