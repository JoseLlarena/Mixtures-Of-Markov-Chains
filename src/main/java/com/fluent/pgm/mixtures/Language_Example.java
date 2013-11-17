package com.fluent.pgm.mixtures;

import com.fluent.collections.FList;
import com.fluent.core.F1;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.fluent.collections.Lists.parse;
import static com.fluent.pgm.mixtures.Easy.Easy;
import static com.fluent.pgm.mixtures.Inference.Inference;
import static com.fluent.pgm.mixtures.Initialisation.Options;
import static com.fluent.util.ReadLines.Read_Lines;
import static java.lang.System.out;

public class Language_Example
{
    static final F1<String, Sequence> string_to_sequence = line -> Sequence.from(parse(line,
            "").apply(word -> Token.from(word.toLowerCase())));

    public static void main(String... args) throws Exception
    {
        language_example(Paths.get(args[0]),Paths.get(args[1]));
    }

    static void language_example(Path data_file, Path model_file) throws Exception
    {
        MoMC model = Easy.mixture_from(
                data_file.toString(),
                string_to_sequence,
                model_file.toString(),
                Options.DEFAULT.with_tag_count(6));

        FList<Sequence> data = Read_Lines.from( data_file, string_to_sequence);

        data.apply(datum -> Inference.joint(datum, model).max_as((tag, posterior) -> posterior).$1 + " ->" + datum)
                .each(out::println);
    }
}
