package com.fluent.pgm.mixtures;

import com.fluent.collections.FHashMap;
import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.core.F2;
import com.fluent.math.*;

import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.mixtures.Sequence.Ngram;
import static com.fluent.pgm.mixtures.Token.OOV;

public class Map_CPD extends FHashMap<Ngram, P> implements CPD
{
    public Map_CPD(FMap<Ngram, P> map)
    {
        super(map);
    }

    public boolean equals(Object o)
    {
        return o == this || o instanceof CPD && super.equals(((Map_CPD) o).as_map());
    }

    public P p(Token token, Context context)
    {
        P p = get(Ngram.from(context, token));

        return p != null ? p :
                (p = get(Ngram.from(OOV, token))) != null ? p :
                        (p = get(Ngram.from(context, OOV))) != null ? p :
                                get(Ngram.from(OOV, OOV), ZERO);
    }

    public FSet<Token> tokens()
    {
        return keys().apply(Ngram::token);
    }

    public FMap<Ngram, P> as_map()
    {
        return this;
    }

    public MPD mpd_from(Context context)
    {
        final F2<Ngram, P, Boolean> with_context = (ngram, p) -> ngram.context().equals(context);

        return new Map_MPD(select(with_context).apply((ngram, p) -> oo(ngram.token().toString(), p)));
    }
}