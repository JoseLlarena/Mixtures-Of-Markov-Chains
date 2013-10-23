package com.fluent.pgm.new_api;

import com.fluent.core.OP1;
import com.fluent.util.Clock;

public interface New_Optimisation
{

    public default MoMC optimise(MoMC initial, OP1<MoMC> em_iteration, Clock clock)
    {
        MoMC model = initial;

        System.out.println("INITIALISED");
        System.out.println(Token.id_to_token.stats());

        while (clock.isTicking())
        {
            model = em_iteration.of(model);
        }

        return model;
    }

}
