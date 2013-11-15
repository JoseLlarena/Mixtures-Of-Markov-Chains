package com.fluent.pgm.mixtures;

import com.fluent.collections.FList;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.pgm.mixtures.Token.END;
import static com.fluent.pgm.mixtures.Token.START;
import static com.google.common.base.Joiner.on;

class Ngram_Sequence implements Sequence
{
    long id;
    FList<Ngram> ngrams;

    Ngram_Sequence(long id, FList<Ngram> ngrams)
    {
        this.id = id;
        this.ngrams = ngrams;
    }

    public String toString()
    {
        return on("").join(tokens().minus(START).minus(END));
    }

    public String toString(String format)
    {
        return tokens().toString(format);
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

    public FList<Token> tokens()
    {
        return asFList(START).plus(ngrams.apply(ngram -> ngram.token()));
    }

    public boolean equals(Object o)
    {
        return o == this || o instanceof Sequence && ((Sequence) o).id() == id;
    }

}
