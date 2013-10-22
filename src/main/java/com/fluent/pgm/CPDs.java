package com.fluent.pgm;

import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.pgm.new_api.Token;
import com.fluent.pgm.estimation.CPDX;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.oo;
import static com.fluent.math.P.*;

public class CPDs
{
    public CPDX firstOrderFrom(FMap<oo<Token, Token>, Double> bigramToWeight)
    {
        FSet<Token> contexts = bigramToWeight.keys().apply(bigram -> bigram.$1);

        FMap<oo<Token, Token>, P> bigramToP = newFMap();

        contexts.each(context ->
                {
                    FMap<oo<Token, Token>, Double> onlyBigramsWithContextToWeight =
                            bigramToWeight.select((bigram, weight) -> bigram.$1.equals(context));

                    double contextN = onlyBigramsWithContextToWeight.values().collect(0., Double::sum);

                    bigramToP.plus(onlyBigramsWithContextToWeight.applyToValues(weight -> P(weight / contextN)));
                }
        );

        return new CPDX()
        {
            public P of(Token token, Context context)
            {
                return of(token, context.at(0));
            }

            public P of(Token token, Token previous)
            {
                return bigramToP.of(oo(token, previous));
            }
        };
    }

}
