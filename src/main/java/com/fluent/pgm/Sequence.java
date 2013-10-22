package com.fluent.pgm;

import com.fluent.collections.FList;
import com.fluent.collections.Lists;
import com.fluent.core.Syntax;
import com.fluent.core.oo;
import com.google.common.collect.Iterables;

import java.util.Arrays;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.core.Words.to;
import static com.fluent.core.oo.oo;
import static com.google.common.base.Joiner.on;
import static java.lang.String.valueOf;

public class Sequence
{
    public static final String START = "^!";
    public static final String END = "$!";
    public static final String START_STATE = "&!";
    public static final String END_STATE = "£!";
    public static final String OOV = "?!";
    private final String[] strings;

    public Sequence(final String string)
    {
        final char[] chars = string.toCharArray();

        strings = new String[chars.length];
        int i = 0;
        for (final char c : chars)
        {
            strings[i++] = valueOf(c);
        }
    }

    public Sequence(final String[] terms)
    {
        strings = terms;
    }

    public static Sequence from(final Iterable<String> terms)
    {
        return new Sequence(Iterables.toArray(terms, String.class));
    }

    public static Sequence from(final String... terms)
    {
        return new Sequence(terms);
    }

    //[
    public String at(final int index)
    {
        return index == 0 ? START : index == strings.length + 1 ? END : strings[index - 1];
    }//]

    public boolean equals(final Object obj)
    {
        return this == obj || obj != null && getClass() == obj.getClass() && Arrays.equals(strings, ((Sequence) obj).strings);
    }

    public FList<oo<String, String>> bigrams()
    {
        final FList<oo<String, String>> bigrams = newFList();

        Syntax.from(1, to(T() - 1)).forEach(t -> bigrams.add(oo(at(t - 1), at(t))));

        return bigrams;
    }

    public FList<String> terms()
    {
        return Lists.newFList(strings);
    }


    public int hashCode()
    {
        return Arrays.hashCode(strings);
    }

    public int T()
    {
        return strings.length + 2;
    }

    public String toString()
    {
        return on("").join(strings);
    }
}
