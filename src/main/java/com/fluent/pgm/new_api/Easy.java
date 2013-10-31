package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.core.F1;
import com.fluent.core.F2;
import com.fluent.core.F3;
import com.fluent.math.*;
import com.google.common.util.concurrent.AtomicDouble;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.pgm.new_api.IO.IO;
import static com.fluent.pgm.new_api.New_Estimation.Estimation;
import static com.fluent.pgm.new_api.New_Inference.New_Inference;
import static com.fluent.pgm.new_api.New_Initialisation.Initialisation;
import static com.fluent.pgm.new_api.New_Optimisation.Optimisation;
import static com.fluent.pgm.new_api.Token.OOV;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.out;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class Easy
{
    public static final Easy Easy = new Easy();
    //
    New_Initialisation init;
    New_Estimation estimation;
    New_Optimisation optimisation;
    IO io;
    New_Inference inference;

    static int thread_count()
    {
        return getRuntime().availableProcessors();
    }

    Easy(IO io, New_Inference infer, New_Initialisation init, New_Estimation estimation, New_Optimisation optimisation)
    {
        this.io = io;
        this.inference = infer;
        this.init = init;
        this.estimation = estimation;
        this.optimisation = optimisation;
    }

    Easy()
    {
        this(IO, New_Inference, Initialisation, Estimation, Optimisation);
    }

    public MoMC estimate_from(String data_file) throws Exception
    {
        return estimate_from(data_file, Seqence::from_chars);
    }

    public MoMC estimate_from(String data_file, F1<String, Seqence> pipeline) throws Exception
    {
        FList<Seqence> data = io.read_char_data_from(data_file, pipeline);

        F3<MoMC, FList<FList<Seqence>>, ExecutorService, MoMC> em = estimation::em_iteration;

        F2<MoMC, ExecutorService, MoMC> em_with_data = em.with_arg_2(data.split(thread_count())).append(out(data));

        ExecutorService executor = newFixedThreadPool(thread_count());

        MoMC model = optimisation.optimise(init.initialise_with(data), em_with_data.with_arg_2(executor)::of);

        executor.shutdown();

        return model;
    }

    Consumer<? super MoMC> out(FList<Seqence> data)
    {
        AtomicDouble previous = new AtomicDouble(Double.NEGATIVE_INFINITY);
        AtomicInteger iterator = new AtomicInteger();
        return model ->
                {
                    final double likelihood = inference.likelihood(model, data).asLog();
                    out.printf("%s [%d] %f %s %s %n",
                            DateTimeFormat.fullDateTime().print(DateTime.now()),
                            iterator.getAndIncrement(),
                            likelihood,
                            2 + previous.get() - likelihood > .1,
                            P.terms_to_sum.stats());

                    previous.set(likelihood);

                };
    }

    public String complete(String datum, String model_file) throws IOException
    {
        FList<Token> tokens = newFList(datum.split("")).apply(chunk -> chunk.equals("¬") ? OOV : Token.from(chunk))
                .minus(Token.from(""));

        return inference.complete(Seqence.from(tokens), io.read_from_json(Paths.get(model_file))).toString();
    }

}
