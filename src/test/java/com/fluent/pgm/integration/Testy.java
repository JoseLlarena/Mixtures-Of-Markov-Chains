package com.fluent.pgm.integration;

import com.fluent.pgm.estimation.Optimiser;

/**
 * Created from IntelliJ IDEA.
 * User: Jose
 * Date: 28/06/13
 * Time: 20:14
 * To change this template use File | Settings | File Templates.
 */
public class Testy
{
    private final Optimiser.Clock clock = new Optimiser.Clock()
    {
        int duration;

        public boolean tick()
        {
            return duration++ <= 2_500;
        }
    };

    public static void main(String... args)
    {


    }

}
