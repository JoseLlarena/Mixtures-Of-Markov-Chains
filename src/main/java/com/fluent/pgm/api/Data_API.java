package com.fluent.pgm.api;

import com.fluent.collections.FList;
import com.fluent.collections.FListMultiMap;
import com.fluent.collections.FMap;
import com.fluent.collections.Lists;
import com.fluent.core.F1;
import com.fluent.core.oo;
import com.fluent.pgm.CPD;
import com.fluent.pgm.Sampling;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.hmm.HMM;
import com.fluent.pgm.momc.Mixture;
import com.fluent.util.ReadLines;
import com.fluent.util.WriteLines;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Random;

import static com.fluent.collections.Maps.newFListMultiMap;


public class Data_API
{
    public static final long DEFAULT_SEED = 1234567890;
    static final F1<String, Sequence> conversion = input -> new Sequence(input.trim().toLowerCase());
    final WriteLines writeLines;
    final ReadLines readLines;
    final Sampling sampling;

    public Data_API(final WriteLines writeLines, final ReadLines readLines, final Sampling generationOf)
    {
        this.writeLines = writeLines;
        this.readLines = readLines;
        sampling = generationOf;
    }

    protected Data_API()
    {
        this(WriteLines.INSTANCE, ReadLines.INSTANCE, Sampling.of);
    }

    public FListMultiMap<String, Sequence> taggedDataFrom(final Mixture mixture, final int size, final long seed)
    {
        final Random random = new Random(seed);

        final FListMultiMap<String, Sequence> data = newFListMultiMap();

        for (int i = 0; i < size; i++)
        {
            final String tag = sampling.ofMPD(mixture.prior(), random);

            data.plus(tag, sampling.sequenceFromCPD(mixture.conditionals().of(tag), random));
        }

        return data;
    }

    public FList<Sequence> dataFrom(final Mixture mixture, final int N, final long seed)
    {
        return dataFrom(mixture, N, new Random(seed));
    }

    public FList<Sequence> dataFrom(final Mixture mixture, final int N, final Random random)
    {
        return Lists.newFList(N, () -> sampling.sequenceFromCPD(mixture.conditionals().of(sampling.ofMPD(mixture
                .prior(), random)),
                random));
    }

    public FList<Sequence> dataFrom(final File tagFile) throws  IOException
    {
        return readLines.from(tagFile.toPath(), conversion);
    }

    public FListMultiMap<String, Sequence> dataFrom(final File tagFile1, final File tagFile2, final File... tagFiles)
            throws IOException
    {
        final FListMultiMap<String, Sequence> data = newFListMultiMap();

        data.plus(tagFile1.getName().split("\\.txt")[0], readLines.from(tagFile1.toPath(), conversion ));
        data.plus(tagFile2.getName().split("\\.txt")[0], readLines.from(tagFile2.toPath(), conversion));

        for (final File tagFile : tagFiles)
        {
            data.plus(tagFile.getName().split("\\.txt")[0], readLines.from(tagFile.toPath(), conversion));
        }

        return data;
    }

    public FList<Sequence> dataFrom(final HMM hmm, final int size, final long seed)
    {
        final Random random = new Random(seed);

        final CPD A = hmm.transitions();
        final CPD B = hmm.emissions();

        final FList<Sequence> sampleCPD = sampling.sequencesFromCPD(A, size, random.nextLong());

        return sampleCPD.apply(stateSequence ->
                Sequence.from(stateSequence.terms().apply(state -> sampling.ofMPD(B.of(state), random))));

    }

    public <ITEM> Data_API write(final Collection<ITEM> data, final File file) throws IOException
    {
        writeLines.to(file.toPath(), data);

        return this;
    }

    public Data_API write(final FListMultiMap<String, Sequence> data, final String directory) throws IOException
    {
        for (final oo<String, FList<Sequence>> tag_sentences : data.asFMap() )
        {
            writeLines.to(Paths.get(directory, tag_sentences.$1 + ".txt"), tag_sentences.$2);
        }

        return this;
    }

    public <KEY, VALUE> Data_API write(final FMap<KEY, VALUE> data, final File file) throws IOException
    {
        writeLines.to(file.toPath(), data);

        return this;
    }
}
