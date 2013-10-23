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
    FList<Ngram> ngrams;

    public static Seqence from(String a)
    {
        FList<Ngram> ngrams = newFList();

        Token prev = Token.START;

        for (char each : a.toCharArray())
        {
            ngrams.plus(new Ngram(prev, Token.from(each)));
            prev = Token.from(each);
        }

        ngrams.plus(new Ngram(prev, Token.END));

        long id = id_from(a);
        return new Seqence(a, ngrams);
    }

    public static <I extends Iterable<Token>> Seqence from(I tokens)
    {
        FList<Ngram> ngrams = newFList();

        Token prev = Token.START;

        for (Token each : tokens)
        {
            ngrams.plus(new Ngram(prev, each));
            prev = (each);
        }


        ngrams.plus(new Ngram(prev, Token.END));



        return new Seqence(on(" ").join(tokens), ngrams);
    }


    public Seqence(String a, FList<Ngram> ngrams)
    {
        this.a = a;

        this.ngrams = ngrams;
    }

    public String toString()
    {
        return a;
    }

    public FList<Ngram> ngrams()
    {
        return ngrams;
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
        return this.ngrams().size() + 1;
    }

    public static class Ngram extends oo<Context, Token>
    {
        public Ngram(Context context, Token token)
        {
            super(context, token);
        }

        public int hashCode()
        {
            return (int) (37 * $1.id() + $2.id());
        }

        public boolean equals(Object o)
        {
            return o == this || o instanceof Ngram && ((Ngram) o).$1.id() == $1.id() && ((Ngram) o).$2.id() == $2.id
                    ();
        }
    }
}
