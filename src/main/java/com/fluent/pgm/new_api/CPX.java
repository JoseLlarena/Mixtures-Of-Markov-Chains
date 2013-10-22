package com.fluent.pgm.new_api;

import com.fluent.core.ooo;
import com.fluent.math.*;

public interface CPX extends Iterable<ooo<Context,Token,P>>
{
    public P p(Token token, Context context);

    MPX mpd_from(Token context);

}
