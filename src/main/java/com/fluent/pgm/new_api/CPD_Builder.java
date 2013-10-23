package com.fluent.pgm.new_api;

import com.fluent.collections.FMap;
import com.fluent.core.F2;
import com.fluent.core.oo;
import com.fluent.core.ooo;
import com.fluent.math.*;
import com.fluent.pgm.CPD;
import com.fluent.pgm.CPT;
import com.fluent.pgm.CPT_with_OOV;
import com.fluent.pgm.estimation.FHashTriMap;
import com.fluent.pgm.estimation.FTriMap;

import java.util.Iterator;

import static com.fluent.collections.Maps.newOrderedFMap;
import static com.fluent.core.Preconditions.reject;
import static com.fluent.core.oo.*;
import static com.fluent.core.ooo.ooo;
import static com.fluent.math.P.*;
import static com.fluent.pgm.Sequence.START;
import static com.fluent.pgm.new_api.Seqence.Ngram;
import static com.google.common.collect.Iterators.transform;

public class CPD_Builder
{
    private final FTriMap<String, String, P> contextTokenProability;
    private final boolean withOOV;

    public static CPX CPX_from(oo<Ngram, P>... entries)
    {
        FMap<Ngram, P> map = newOrderedFMap();

        for (oo<Ngram, P> entry : entries)
        {
            map.plus(entry.$1, entry.$2);
        }

        return new Simple_CPD(map);
    }

    public static CPX CPX_from(FMap<Ngram, P> map)
    {
        return new Simple_CPD(map);
    }

    public static CPD_Builder CPD()
    {
        return new CPD_Builder(false);
    }

    public static CPD_Builder CPD_with_OOV()
    {
        return new CPD_Builder(true);
    }

    public static oo<Ngram, P> _p(Token item, Token context, double p)
    {
        return oo(new Ngram(context, item), P(p));
    }

    private CPD_Builder(final boolean withOOV)
    {
        contextTokenProability = new FHashTriMap<>();
        this.withOOV = withOOV;
    }

    public CPD and(final String token, final String context, final double probability)
    {
        return p(token, context, probability).done();
    }

    public CPD and(final String token, final String context, final P probability)
    {
        return p(token, context, probability).done();
    }

    public CPD done()
    {
        for (final FMap<String, P> conditional : contextTokenProability.asFMap().values())
        {
            final P mass = conditional.values().collect(ZERO, (sum, item) -> sum.add(item));

            reject(mass.lt(P(.999999)), "All probabilities should plus up to 1 but got " + mass + "  in " +
                    conditional);
        }

        return withOOV ? new CPT_with_OOV(contextTokenProability.asFMap()) : new CPT(contextTokenProability);
    }

    public CPD_Builder p(final oo<String, String> transit, final double probability)
    {
        return p(transit, P(probability));
    }

    public CPD_Builder p(final oo<String, String> transit, final P probability)
    {
        contextTokenProability.add(transit.$1, transit.$2, probability);

        return this;
    }

    public CPD_Builder p(final String token, final double probability)
    {
        return p(token, P(probability));
    }

    public CPD_Builder p(final String token, final P probability)
    {
        return p(token, START, probability);
    }

    public CPD_Builder p(final String token, final String context, final double probability)
    {
        return p(token, context, P(probability));
    }

    public CPD_Builder p(final String token, final String context, final P probability)
    {
        contextTokenProability.add(context, token, probability);

        return this;
    }

    static class Simple_CPD implements CPX
    {
        FMap<Ngram, P> map;

        public Simple_CPD(FMap<Ngram, P> map)
        {
            this.map = map;
        }

        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Simple_CPD ooos = (Simple_CPD) o;

            if (!map.equals(ooos.map)) return false;

            return true;
        }

        public int hashCode()
        {
            return map.hashCode();
        }

        public String toString()
        {
            return map.toString();
        }

        public P p(Token token, Context context)
        {
            return map.get(new Ngram(context, token), ZERO);
        }

        public MPX mpd_from(Token context)
        {
            final F2<Ngram, P, Boolean> with_context = (n_gram, p) -> n_gram.$1.equals(context);

            return new MPTX(map.select(with_context).apply((n_gram, p) -> oo(n_gram.$2.toString(), p)));
        }

        public Iterator<ooo<Context, Token, P>> iterator()
        {
            return transform(map.iterator(), pair -> ooo(pair.$1.$1, pair.$1.$2, pair.$2));
        }
    }
}