package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.core.oo;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.pgm.new_api.Common.id_from;
import static com.google.common.base.Joiner.on;

public class Seqence
{
    String a;
    long id;
    FList<N_gram> n_grams;

    public static Seqence from(String a)
    {
        FList<N_gram> n_grams = newFList();

        Token prev = Token.START;

        for (char each : a.toCharArray())
        {
            n_grams.plus(new N_gram(prev, Token.from(each)));
            prev = Token.from(each);
        }

        n_grams.plus(new N_gram(prev, Token.END));

        return new Seqence(a, n_grams);
    }

    public static Seqence from(Iterable<Token> tokens)
    {
        FList<N_gram> n_grams = newFList();

        Token prev = Token.START;

        for (Token each : tokens)
        {
            n_grams.plus(new N_gram(prev, (each)));
            prev = (each);
        }

        n_grams.plus(new N_gram(prev, Token.END));

        return new Seqence(on("").join(tokens), n_grams);
    }


    public Seqence(String a, FList<N_gram> n_grams)
    {
        this.a = a;
        id = id_from(a);
        this.n_grams = n_grams;
    }

    public String toString()
    {
        return a;
    }

    public FList<N_gram> n_grams()
    {
        return n_grams;
    }

    public int hashCode()
    {
        return (int) id;
    }

    public long id()
    {
        return id;
    }

    public boolean equals(Object o)
    {
        return o == this || o instanceof Seqence && ((Seqence) o).id() == id;
    }

    public int size()
    {
        return this.n_grams().size()+1;
    }

    public static class N_gram extends oo<Context, Token>
    {
        public N_gram(Context context, Token token)
        {
            super(context, token);
        }
    }
}
