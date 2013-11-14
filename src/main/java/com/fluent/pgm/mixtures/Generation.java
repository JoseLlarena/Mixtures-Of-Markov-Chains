package com.fluent.pgm.mixtures;

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

    public FList<Sequence> generate_untagged(int N, MoMC model, long seed)
    {
        Random random = new Random(seed);

        return sequences_from(generate_classes(N, model, random), model.transitions_per_tag(), random);
    }

    public FList<oo<Sequence, String>> generate_tagged(int N, MoMC model, long seed)
    {
        Random random = new Random(seed);

        final FList<String> classes = generate_classes(N, model, random);

        return sequences_from(classes, model.transitions_per_tag(), random).pair(classes);
    }

    static Iterable<Token> sample(CPD cpd, Random random)
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

    FList<Sequence> sequences_from(FList<String> classes, FMap<String, CPD> transitions, Random random)
    {
        return classes.apply(a_class -> Sequence.from(sample(transitions.get(a_class), random)));
    }

    FList<String> generate_classes(int N, MoMC model, Random random)
    {
        return newFList(N, () -> sample(model.prior().as_map(), random));
    }

}
