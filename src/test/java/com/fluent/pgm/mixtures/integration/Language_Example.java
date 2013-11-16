package com.fluent.pgm.mixtures.integration;

import com.fluent.collections.FList;
import com.fluent.pgm.mixtures.MoMC;
import com.fluent.pgm.mixtures.Sequence;
import com.fluent.pgm.mixtures.Token;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import java.nio.file.Paths;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.pgm.mixtures.Easy.Easy;
import static com.fluent.pgm.mixtures.Inference.Inference;
import static com.fluent.util.ReadLines.Read_Lines;
import static java.lang.System.out;

public class Language_Example extends AbstractSpec
{

    @Test
    public void estimates_momc_from_data() throws Exception
    {

//        language_example();
//        exit(0);
        MoMC model = Easy.mixture_from(

                "C:/Users/Jose/project-workspace/Sequensir/src/test/resources/tweets-processed.txt",
                line -> Sequence.from(newFList(line.split("\\s+"), Token::from)), "momc-3.json");

        FList<Sequence> data = Read_Lines.from(
                Paths.get("C:/Users/Jose/project-workspace/Sequensir/src/test/resources/tweets-processed.txt"),
                line -> Sequence.from(newFList(line.split("\\s+"), Token::from)),
                line -> true);

        data.apply(datum -> Inference.joint(datum, model).max_as((tag, posterior) -> posterior).$1 + " -> " + datum
                .toString("%s "))
                .sorted(string -> string).each(out::println);

    }

    FList<String> process(String line)
    {
        return newFList(line.split("\\d+\\s+")[1].split(""))

                .apply(string -> string.toLowerCase()).apply
                        (chunk ->
                                {
                                    return chunk;

                                });
    }

    private void language_example() throws Exception
    {
        MoMC model = Easy.mixture_from(

                "C:/Users/Jose/project-workspace/Sequensir/src/test/resources/de_dk_nl_no_en_se.txt",
                line -> Sequence.from(newFList(process(line), Token::from)), "momc.json");

        FList<Sequence> data = Read_Lines.from(
                Paths.get("C:/Users/Jose/project-workspace/Sequensir/src/test/resources/de_dk_nl_no_en_se.txt"),
                line -> Sequence.from(newFList(process(line), Token::from)),
                line -> true);

        //                Sequence sentence = Sequence.from(newFList(("This sentence should be classified as English")
        //                        .toLowerCase()
        //                        .split(""),
        //                        Token::from));

        //        out.println(Easy.complete_characters(" d¬¬ nicht ", "momc.json"));
        //        // out.println(Easy.complete_characters("¬¬¬¬¬¬", "momc.json"));
        //
        //         out.println(Inference.Inference.posterior_density(sentence, IO.IO.model_from(Paths.get("momc
        // .json"))));
        //
        data.each(datum -> out.println(Inference.joint(datum, model).max_as((tag,
                                                                             posterior) -> posterior).$1 + " ->" +
                datum));
    }
}
