package com.fluent.pgm.mixtures;

import com.fluent.collections.FList;
import com.fluent.core.oo;
import com.google.common.cache.Cache;
import com.google.common.hash.Hasher;

import java.util.concurrent.ExecutionException;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.collections.Lists.parse;
import static com.fluent.pgm.mixtures.Common.cache;
import static com.fluent.pgm.mixtures.Token.START;
import static java.util.Arrays.asList;

public interface Sequence
{
    public static Sequence from_chars(String string)
    {
        return Sequence.from(string.toCharArray());
    }

    public static Sequence from_words(String string)
    {
        return Sequence.from(parse(string, "\\s+").apply(word -> Token.from(word)));
    }

    public FList<Token> tokens();

    public static Sequence from(char... chars)
    {
        FList<Ngram> ngrams = newFList();

        Token prev = START;

        Hasher hasher = Common.hasher();
        for (char each : chars)
        {
            ngrams.plus(Ngram.from(prev, Token.from(each)));
            prev = Token.from(each);
            hasher.putLong(Token.from(each).id());
        }

        ngrams.plus(Ngram.from(prev, Token.END));

        return new Ngram_Sequence(hasher.hash().asLong(), ngrams);
    }

    public static Sequence from(Token... tokens)
    {
        return from(asList(tokens));
    }

    public default String toString(String format)
    {
        return this.tokens().toString(format);
    }

    public static <TOKENS extends Iterable<Token>> Sequence from(TOKENS tokens)
    {
        FList<Ngram> ngrams = newFList();

        Token prev = START;

        Hasher hasher = Common.hasher();

        for (Token each : tokens)
        {
            ngrams.plus(Ngram.from(prev, each));
            prev = (each);
            hasher.putLong(each.id());
        }

        ngrams.plus(Ngram.from(prev, Token.END));

        return new Ngram_Sequence(hasher.hash().asLong(), ngrams);
    }

    public FList<Ngram> ngrams();

    public default int size()
    {
        return ngrams().size() + 1;
    }

    public default Token at(int i)
    {
        return i == 0 ? START : ngrams().get(i - 1).token();
    }

    public long id();

    public static class Ngram extends oo<Context, Token> implements Context
    {
        static Cache<Long, Ngram> id_to_ngram = cache().maximumSize(10_000_000).build();
        //
        long id;

        Ngram(Context context, Token token, long id)
        {
            super(context, token);
            this.id = id;
        }

        public static Ngram from(Context context, Token token)
        {
            try
            {
                long id = Common.hasher().putLong(context.id()).putLong(token.id()).hash().asLong();
                return id_to_ngram.get(id, () -> new Ngram(context, token, id));
            }
            catch (ExecutionException e)
            {
                throw new RuntimeException(e);
            }
        }

        public Context context()
        {
            return $1;
        }

        public Token token()
        {
            return $2;
        }

        public long id()
        {
            return id;
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
