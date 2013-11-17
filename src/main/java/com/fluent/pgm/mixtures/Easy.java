package com.fluent.pgm.mixtures;

import com.fluent.collections.FList;
import com.fluent.collections.FListMultiMap;
import com.fluent.collections.FMap;
import com.fluent.core.*;
import com.fluent.math.*;
import com.google.common.util.concurrent.AtomicDouble;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static com.fluent.collections.Lists.parse;
import static com.fluent.core.oo.*;
import static com.fluent.pgm.mixtures.Common.SEED_1;
import static com.fluent.pgm.mixtures.Estimation.Estimation;
import static com.fluent.pgm.mixtures.Generation.Generation;
import static com.fluent.pgm.mixtures.IO.IO;
import static com.fluent.pgm.mixtures.Inference.Inference;
import static com.fluent.pgm.mixtures.Initialisation.Initialisation;
import static com.fluent.pgm.mixtures.Initialisation.Options;
import static com.fluent.pgm.mixtures.Optimisation.Optimisation;
import static com.fluent.pgm.mixtures.Token.MISSING;
import static com.fluent.util.WriteLines.Write_Lines;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Math.log10;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.slf4j.LoggerFactory.getLogger;

public class Easy
{
    public static final Easy Easy = new Easy();
    private static final Logger log = getLogger(Easy.getClass());
    //
    Initialisation init;
    Estimation estimate;
    Optimisation optimise;
    IO io;
    Inference infer;
    Generation generate;

    Easy(IO io, Inference infer, Initialisation init, Estimation estimate, Optimisation optimise,
         Generation generate)
    {
        this.io = io;
        this.infer = infer;
        this.init = init;
        this.estimate = estimate;
        this.optimise = optimise;
        this.generate = generate;
    }

    Easy()
    {
        this(IO, Inference, Initialisation, Estimation, Optimisation, Generation);
    }

    public MoMC character_mixture_from_untagged(String data_file, String model_file) throws Exception
    {
        return mixture_from(data_file, Sequence::from_chars, model_file);
    }

    public MoMC mixture_from(String data_file, F1<String, Sequence> pipeline, String model_file,
                             Options options) throws Exception
    {
        FList<Sequence> data = io.data_from(data_file, pipeline);

        ExecutorService executor = newFixedThreadPool(thread_count());

        OP1<MoMC> em = model  -> estimate.reestimate(model , data.split(thread_count()), executor);

        F3<MoMC, OP1<MoMC>, Condition<MoMC>, MoMC> optimised = optimise::optimise;

        MoMC model = optimised.and_then(estimate::smooth)
                .of(init.initialise_with(data, options),  em, stopping_criterion(data, 99.99, 10));

        executor.shutdown();

        io.to_json(model, Paths.get(model_file));

        return model;
    }

    public MoMC mixture_from(String data_file, F1<String, Sequence> pipeline, String model_file) throws Exception
    {
        return mixture_from(data_file,pipeline,model_file,Options.DEFAULT);
    }

    public String complete_characters(String datum, String model_file) throws IOException
    {
        FList<Token> tokens = parse(datum, "").apply(chunk -> chunk.equals("¬") ? MISSING : Token.from(chunk))
                .minus(Token.from(""));

        return infer.complete(Sequence.from(tokens), io.model_from(Paths.get(model_file))).toString();
    }

    public FList<Sequence> untagged_data_from(String model_file, String data_file, int N) throws IOException
    {
        FList<Sequence> data = generate.generate_untagged(N, io.model_from(Paths.get(model_file)), SEED_1);

        Write_Lines.to(Paths.get(data_file), data);

        return data;
    }

    public MoMC character_mixture_from_tagged(String data_directory, String output_file) throws Exception
    {
        MoMC model = estimate.estimate(io.tagged_char_data_from(data_directory));

        io.to_json(model, Paths.get(output_file));

        return model;
    }

    public String tag_characters(String untagged, String model_file) throws IOException
    {
        final Sequence sequence = Sequence.from_chars(untagged);
        final MoMC model = io.model_from(Paths.get(model_file));

        return infer.joint(sequence, model).max_as((tag, posterior) -> posterior).$1;
    }

    public FMap<String, P> fuzzy_tag_characters(String untagged, String model_file) throws IOException
    {
        final Sequence sequence = Sequence.from_chars(untagged);
        final MoMC model = io.model_from(Paths.get(model_file));

        return infer.posterior_density(sequence, model);
    }

    public FList<oo<Sequence, String>> tagged_data_from(String model_file, String data_directory, int N) throws
            IOException
    {
        FList<oo<Sequence, String>> data = generate.generate_tagged(N, io.model_from(Paths.get(model_file)), SEED_1);

        FListMultiMap<String, Sequence> tag_to_sequences = data.groupBy(sequence_with_tag -> sequence_with_tag.$2)
                .apply((tag, sequences_with_tags) ->
                        oo(tag, sequences_with_tags.apply(sequence_with_tag -> sequence_with_tag.$1)));

        Write_Lines.to(Paths.get(data_directory), tag_to_sequences);

        return data;
    }

    static int thread_count()
    {
        return getRuntime().availableProcessors();
    }

    Condition<MoMC> stopping_criterion(FList<Sequence> data,
                                       double percent_convergence,
                                       int min_improvement_iterations)
    {
        AtomicDouble old_loglikelihood = new AtomicDouble(NEGATIVE_INFINITY);
        AtomicInteger iteration = new AtomicInteger();
        AtomicInteger improvement_iterations = new AtomicInteger();
        double log_percent_convergence = log10(percent_convergence);

        return model ->
                {
                    double loglikelihood = infer.likelihood(model, data).asLog();

                    log.info(format("em iteration [%04d] log-likelihood [%15.8f]", iteration.getAndIncrement(),
                            loglikelihood));

                    boolean improved = (2 + old_loglikelihood.getAndSet(loglikelihood) - loglikelihood) >
                            log_percent_convergence;

                    improvement_iterations.getAndAdd(improved ? 1 : -improvement_iterations.get());

                    return improvement_iterations.get() >= min_improvement_iterations;
                };
    }

}
