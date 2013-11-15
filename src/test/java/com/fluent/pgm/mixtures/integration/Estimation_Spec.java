package com.fluent.pgm.mixtures.integration;

import com.fluent.collections.FList;
import com.fluent.pgm.mixtures.IO;
import com.fluent.pgm.mixtures.Inference;
import com.fluent.pgm.mixtures.Sequence;
import com.fluent.pgm.mixtures.Token;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import java.nio.file.Paths;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.pgm.mixtures.Easy.Easy;
import static java.lang.System.out;

public class Estimation_Spec extends AbstractSpec
{

    @Test
    public void estimates_momc_from_data() throws Exception
    {

        //       Easy.mixture_from(
        //                "C:/Users/Jose/project-workspace/Sequensir/src/main/resources/de_dk_nl_no_en_se.txt",
        //                line -> Sequence.from(newFList(process(line), Token::from)), "momc.json");

        //        FList<Sequence> data = ReadLines.INSTANCE.from(
        //                Paths.get("C:/Users/Jose/project-workspace/Sequensir/src/main/resources/de_dk_nl_no_en_se
        // .txt"),
        //                line -> Sequence.from(newFList(process(line), Token::from)),
        //                line -> r.nextDouble() > 0.);

                Sequence sentence = Sequence.from(newFList(("This sentence should be classified as English")
                        .toLowerCase()
                        .split(""),
                        Token::from));


        out.println(Easy.complete_characters(" d¬¬ nicht ", "momc.json"));
        // out.println(Easy.complete_characters("¬¬¬¬¬¬", "momc.json"));

         out.println(Inference.Inference.posterior_density(sentence, IO.IO.model_from(Paths.get("momc.json"))));
        //
        //        data.each(datum -> out.println(Inference.joint(datum, model).max_as((tag, posterior) -> posterior).$1 + " ->" +
        //                datum));

    }

    FList<String> process(String line)
    {
        return newFList(line.split("\\d+\\s+")[1].split("")).apply(string -> string.toLowerCase()).apply
                (chunk ->
                        {
                            return chunk;

                        });
    }
}
