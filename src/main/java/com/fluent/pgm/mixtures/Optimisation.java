package com.fluent.pgm.mixtures;

import com.fluent.core.Condition;
import com.fluent.core.OP1;
import com.fluent.util.Clock;

public class Optimisation
{
    public static final Optimisation Optimisation = new Optimisation();

    public MoMC optimise(MoMC initial, OP1<MoMC> em_iteration)
    {
        return optimise(initial, em_iteration, Clock.tickTimes(25));
    }

    public MoMC optimise(MoMC initial, OP1<MoMC> em_iteration, Clock clock)
    {
        MoMC model = initial;

        while (clock.isTicking())
        {
            model = em_iteration.of(model);
        }

        return model;
    }

    public MoMC optimise(MoMC initial, OP1<MoMC> em_iteration, Condition<MoMC> stopping)
    {
        MoMC model = initial;

        while (!stopping.of(model))
        {
            model = em_iteration.of(model);
        }

        return model;
    }

}
