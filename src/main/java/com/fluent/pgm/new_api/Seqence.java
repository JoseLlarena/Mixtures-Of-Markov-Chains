package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.core.oo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.hash.Hasher;

import java.util.concurrent.ExecutionException;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.collections.Lists.newFList;
import static com.fluent.pgm.new_api.Common.id_from;
import static com.fluent.pgm.new_api.Token.START;
import static com.google.common.base.Joiner.on;
import static java.util.Arrays.asList;

public class Seqence
{
    long id;
    FList<Ngram> ngrams;

    @Deprecated
    public static Seqence from(String a)
    {
        FList<Ngram> ngrams = newFList();

        Token prev = START;

        for (char each : a.toCharArray())
        {
            ngrams.plus(Ngram.from(prev, Token.from(each)));
            prev = Token.from(each);
        }

        ngrams.plus(Ngram.from(prev, Token.END));

        return new Seqence(id_from(a), ngrams);
    }

    public static Seqence from_chars(String string)
    {
        return Seqence.from(string.toCharArray());
    }

    public static Seqence from_words_in(String string)
    {
        return Seqence.from(newFList(string.split("\\s+")).apply(word -> Token.from(word)));
    }

    public static Seqence from(char... chars)
    {
        FList<Ngram> ngrams = newFList();

        Token prev = START;

        Hasher hasher = Common.hash.newHasher();
        for (char each : chars)
        {
            ngrams.plus(Ngram.from(prev, Token.from(each)));
            prev = Token.from(each);
            hasher.putLong(Token.from(each).id());
        }

        ngrams.plus(Ngram.from(prev, Token.END));

        return new Seqence(hasher.hash().asLong(), ngrams);
    }

    public static  Seqence from(Token... tokens)
    {
        return from(asList(tokens));
    }
    public static <TOKENS extends Iterable<Token>> Seqence from(TOKENS tokens)
    {
        FList<Ngram> ngrams = newFList();

        Token prev = START;

        Hasher hasher = Common.hash.newHasher();

        for (Token each : tokens)
        {
            ngrams.plus(Ngram.from(prev, each));
            prev = (each);
            hasher.putLong(each.id());
        }

        ngrams.plus(Ngram.from(prev, Token.END));

        return new Seqence(hasher.hash().asLong(), ngrams);
    }


    public Seqence(long id, FList<Ngram> ngrams)
    {
        this.id = id;
        this.ngrams = ngrams;
    }

    public String toString()
    {
        return on(" ").join(symbols());
    }

    public FList<Ngram> ngrams()
    {
        return ngrams;
    }

    public int hashCode()
    {
        return (int) id;
    }

     FList<Token> symbols()
    {
        return asFList(START).plus(ngrams.apply(ngram -> ngram.token()));
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
        return ngrams.size() + 1;
    }

    public Token at(int i)
    {
        return i == 0 ? START : ngrams.get(i-1).token();
    }

    public static class Ngram extends oo<Context, Token>
    {
        static Cache<Long, Ngram> id_to_ngram = CacheBuilder.newBuilder()
                .maximumSize(10_000_000)
                .build();
        long id;

        public static Ngram from(Context context, Token token)
        {
            try
            {
                long id = Common.hash.newHasher().putLong(context.id()).putLong(token.id()).hash().asLong();
                return id_to_ngram.get(id, () -> new Ngram(context, token, id));
            }
            catch (ExecutionException e)
            {
                throw new RuntimeException(e);
            }
        }

        Ngram(Context context, Token token, long id)
        {
            super(context, token);
            this.id = id;
        }

        public Context context()
        {
            return $1;
        }

        public Token token()
        {
            return $2;
        }

        public int hashCode()
        {
            return (int) id;
        }

        public boolean equals(Object o)
        {
            return o == this || o instanceof Ngram && ((Ngram) o).id == id;
        }
    }
}
