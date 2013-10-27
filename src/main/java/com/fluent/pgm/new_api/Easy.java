package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.core.F1;
import com.fluent.core.F2;
import com.fluent.math.*;
import com.google.common.util.concurrent.AtomicDouble;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.concurrent.atomic.AtomicInteger;

import static com.fluent.pgm.new_api.IO.IO;
import static com.fluent.pgm.new_api.New_Estimation.Estimation;
import static com.fluent.pgm.new_api.New_Inference.New_Inference;
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

    public MoMC estimate_from(String data_file) throws Exception
    {
        return estimate_from(data_file, Seqence::from_chars_in);
    }

    public MoMC estimate_from(String data_file, F1<String,Seqence> pipeline) throws Exception
    {
        FList<Seqence> data = io.read_char_data_from(data_file,pipeline);

        F2<MoMC, FList<Seqence>, MoMC> em = estimation::parallel_em_iteration;


        AtomicDouble previous = new AtomicDouble(Double.NEGATIVE_INFINITY);
        AtomicInteger iterator = new AtomicInteger();
        em = em.append
                (model ->
                        {
                            final double likelihood = New_Inference.likelihood(model, data).asLog();
                            System.out.printf("%s [%d] %f %s %s %n",
                                    DateTimeFormat.fullDateTime().print(DateTime.now()),
                                    iterator.getAndIncrement(),
                                    likelihood,
                                    2 + previous.get() - likelihood > .1,
                                    P.terms_to_sum.stats());

                            previous.set(likelihood);

                        })   ;


        return optimisation.optimise(init.initialise_with(data), em.with_arg_2(data)::of);
    }

}
