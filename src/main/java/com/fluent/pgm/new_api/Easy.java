package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.core.Condition;
import com.fluent.core.F1;
import com.fluent.core.OP1;
import com.google.common.util.concurrent.AtomicDouble;
import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static com.fluent.collections.Lists.parse;
import static com.fluent.core.Functions.f2;
import static com.fluent.core.Functions.f3;
import static com.fluent.pgm.new_api.IO.IO;
import static com.fluent.pgm.new_api.New_Estimation.Estimation;
import static com.fluent.pgm.new_api.New_Inference.New_Inference;
import static com.fluent.pgm.new_api.New_Initialisation.Initialisation;
import static com.fluent.pgm.new_api.New_Optimisation.Optimisation;
import static com.fluent.pgm.new_api.Token.OOV;
import static java.lang.Math.log10;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.out;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.joda.time.format.DateTimeFormat.fullDateTime;

public class Easy
{
    public static final Easy Easy = new Easy();
   // private static  final
    //
    New_Initialisation init;
    New_Estimation estimation;
    New_Optimisation optimisation;
    IO io;
    New_Inference inference;

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
        FList<Seqence> data = io.data_from(data_file, pipeline);

        ExecutorService executor = newFixedThreadPool(thread_count());

        F1<MoMC, MoMC> em = f3(estimation::reestimate, MoMC.class, FList.class, ExecutorService.class)
                .with_args_2_3(data.split(thread_count()), executor);

        MoMC model = (MoMC) f3(optimisation::optimise, MoMC.class, OP1.class, Condition.class)
                .and_then(f2(estimation::smooth, MoMC.class, FList.class).with_arg_2(data))
                .of(init.initialise_with(data), (OP1<MoMC>) em::apply, stopping_criterion(data, 99, 5));

        executor.shutdown();

        return model;
    }

    public String complete_characters(String datum, String model_file) throws IOException
    {
        FList<Token> tokens = parse(datum, "").apply(chunk -> chunk.equals("¬") ? OOV : Token.from(chunk))
                .minus(Token.from(""));

        return inference.complete(Seqence.from(tokens), io.model_from(Paths.get(model_file))).toString().replaceAll
                ("\\s+", "");
    }

    public MoMC mixture_from_tagged_data(String data_directory, String output_file) throws Exception
    {
        MoMC model = estimation.estimate(io.tagged_char_data_from(data_directory));

        io.to_json(model, Paths.get(output_file));

        return model;
    }

    public String tag(String untagged, String model_file) throws IOException
    {
        final Seqence sequence = Seqence.from_chars(untagged);
        final MoMC model = io.model_from(Paths.get(model_file));

        return inference.joint(sequence, model).max_as((tag, posterior) -> posterior).$1;
    }

    static int thread_count()
    {
        return getRuntime().availableProcessors();
    }

    Condition<MoMC> stopping_criterion(FList<Seqence> data,
                                       double percent_convergence,
                                       int min_improvement_iterations)
    {
        AtomicDouble old_loglikelihood = new AtomicDouble(Double.NEGATIVE_INFINITY);
        AtomicInteger iteration = new AtomicInteger();
        AtomicInteger improvement_iterations = new AtomicInteger();
        double log_percent_convergence = log10(percent_convergence);

        return model ->
                {
                    double loglikelihood = inference.likelihood(model, data).asLog();
                    out.printf("%s [%d] %f %n",
                            fullDateTime().print(DateTime.now()),
                            iteration.getAndIncrement(),
                            loglikelihood);

                    boolean improved = (2 + old_loglikelihood.getAndSet(loglikelihood) - loglikelihood) >
                            log_percent_convergence;

                    improvement_iterations.getAndAdd(improved ? 1 : -improvement_iterations.get());

                    return improvement_iterations.get() >= min_improvement_iterations;
                };
    }
}
