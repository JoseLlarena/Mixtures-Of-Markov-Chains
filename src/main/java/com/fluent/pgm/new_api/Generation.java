package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.oo;
import com.fluent.math.*;

import java.util.Random;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.math.P.*;

public class Generation
{
    public static final Generation Generation = new Generation();

    //TODO: MAKE FUNCTIONAL
    static Iterable<Token> sample(CPX cpd, Random random)
    {
        Token context = Token.START;
        final FList<Token> sequence = newFList();

        while (true)
        {
            final Token symbol = Token.from(sample(cpd.mpd_from(context).as_map(), random));

            if (symbol != Token.END)
            {
                sequence.add(symbol);
                context = symbol;
            }
            else
            {
                break;
            }
        }

        return sequence;
    }

    //TODO: MAKE   FUNCTIONAL
    static <KEY> KEY sample(final FMap<KEY, P> key_to_probability, final Random generator)
    {
        final P threshold = P(1 - generator.nextDouble());
        P cdf = ZERO;
        KEY choice = null;

        for (final oo<KEY, P> entry : key_to_probability)
        {
            cdf = cdf.add(entry.$2);
            choice = entry.$1;

            if (cdf.gt(threshold))
            {
                break;
            }
        }

        return choice;
    }

    public FList<Seqence> generate(int N, MoMC model, long seed)
    {
        Random random = new Random(seed);

        return sequences_from(generate_classes(N, model, random), model.transitions_per_tag(), random);
    }

    FList<Seqence> sequences_from(FList<String> classes, FMap<String, CPX> transitions, Random random)
    {
        return classes.apply(a_class -> Seqence.from(sample(transitions.get(a_class), random)));
    }

    FList<String> generate_classes(int N, MoMC model, Random random)
    {
        return newFList(N, () -> sample(model.prior().as_map(), random));
    }

}
