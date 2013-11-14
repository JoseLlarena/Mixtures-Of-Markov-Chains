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
import static com.fluent.pgm.new_api.Token.END;
import static com.fluent.pgm.new_api.Token.START;
import static com.google.common.base.Joiner.on;
import static java.util.Arrays.asList;

public class Seqence
{
    long id;
    FList<Ngram> ngrams;

    public Seqence(long id, FList<Ngram> ngrams)
    {
        this.id = id;
        this.ngrams = ngrams;
    }

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

    public static Seqence from_words(String string)
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

    public static Seqence from(Token... tokens)
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

    public static <TOKENS extends Iterable<Token>> Seqence from(TOKENS tokens, int order)
    {
        FList<Ngram> ngrams = newFList();

        Context prev1 = START;
        Context prev2 = START;
        Context bigram = START;
        Hasher hasher = Common.hash.newHasher();

        for (Token each : tokens)
        {
            ngrams.plus(Ngram.from(bigram, each));
            prev2 = prev1;
            prev1 = each;

            bigram =  Token.from(prev2+":::"+prev1);

            hasher.putLong(each.id());
        }

        ngrams.plus(Ngram.from(bigram, Token.END));

        return new Seqence(hasher.hash().asLong(), ngrams);
    }

    public String toString()
    {
        return on(" ").join(symbols().minus(START).minus(END));
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
        return ngrams.size() + 1;
    }

    public Token at(int i)
    {
        return i == 0 ? START : ngrams.get(i - 1).token();
    }

    static long hashfrom(long prev1, long prev2, Hasher hasher)
    {
        return hasher.putLong(prev1).putLong(prev2).hash().asLong();
    }

    static Context bigram(long id, String s)
    {
        return new Context()
        {
            public long id()
            {
                return id;
            }

            public String toString()
            {
                return s;
            }
        };
    }

    FList<Token> symbols()
    {
        return asFList(START).plus(ngrams.apply(ngram -> ngram.token()));
    }

    public static class Ngram extends oo<Context, Token>
    {
        static Cache<Long, Ngram> id_to_ngram = CacheBuilder.newBuilder()
                .maximumSize(10_000_000)
                .build();
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
                long id = Common.hash.newHasher().putLong(context.id()).putLong(token.id()).hash().asLong();
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
