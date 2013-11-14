package com.fluent.pgm.new_api;

import com.fluent.core.Condition;
import com.fluent.core.OP1;
import com.fluent.util.Clock;

public interface New_Optimisation
{
    public static final New_Optimisation Optimisation = new New_Optimisation() {};

    public default MoMC optimise(MoMC initial, OP1<MoMC> em_iteration)
    {
        return optimise(initial, em_iteration, Clock.tickTimes(25));
    }

    public default MoMC optimise(MoMC initial, OP1<MoMC> em_iteration, Clock clock)
    {
        MoMC model = initial;

        while (clock.isTicking())
        {
            model = em_iteration.of(model);
        }

        return model;
    }

    public default MoMC optimise(MoMC initial, OP1<MoMC> em_iteration, Condition<MoMC> stopping)
    {
        MoMC model = initial;

        while (!stopping.of(model))
        {
            model = em_iteration.of(model);
        }

        return model;
    }

}
