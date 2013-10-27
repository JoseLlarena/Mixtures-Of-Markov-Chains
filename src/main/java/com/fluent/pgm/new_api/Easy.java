package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.core.F2;

import java.io.IOException;

import static com.fluent.pgm.new_api.IO.IO;
import static com.fluent.pgm.new_api.New_Estimation.Estimation;
import static com.fluent.pgm.new_api.New_Initialisation.Initialisation;
import static com.fluent.pgm.new_api.New_Optimisation.Optimisation;

public class Easy
{
    public static final Easy Easy = new Easy();
    //
    New_Initialisation init;
    New_Estimation estimation;
    New_Optimisation optimisation;
    IO io;

    Easy(IO io, New_Initialisation init, New_Estimation estimation, New_Optimisation optimisation)
    {
        this.io = io;
        this.init = init;
        this.estimation = estimation;
        this.optimisation = optimisation;
    }

    Easy()
    {
        this(IO, Initialisation, Estimation, Optimisation);
    }

    public MoMC estimate_from(String data_file) throws IOException
    {
        FList<Seqence> data = io.read_char_data_from(data_file);

        F2<MoMC, FList<Seqence>, MoMC> em = estimation::em_iteration;

        return optimisation.optimise(init.initialise_with(data), em.with_arg_2(data)::of);
    }

}
