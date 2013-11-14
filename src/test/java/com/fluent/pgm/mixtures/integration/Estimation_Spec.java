package com.fluent.pgm.mixtures.integration;

import com.fluent.collections.FList;
import com.fluent.pgm.mixtures.*;
import com.fluent.specs.unit.AbstractSpec;
import com.fluent.util.ReadLines;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Random;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.pgm.mixtures.Easy.Easy;
import static com.fluent.pgm.mixtures.Inference.Inference;

public class Estimation_Spec extends AbstractSpec
{

    @Test
    public void estimates_momc_from_data() throws Exception
    {
        Estimation e = Estimation.Estimation;

        Random r = new Random(Common.SEED_1);

        MoMC model = Easy.mixture_from(
        "C:/Users/Jose/project-workspace/Sequensir/src/main/resources/de_dk_nl_no_en_se.txt",
                line -> Sequence.from(newFList(process(line), Token::from)), "momc.json");

        FList<Sequence> data = ReadLines.INSTANCE.from(
                Paths.get("C:/Users/Jose/project-workspace/Sequensir/src/main/resources/de_dk_nl_no_en_se.txt"),
                line -> Sequence.from(newFList(process(line), Token::from)),
                line -> r.nextDouble() > 0.);


        Sequence sentence = Sequence.from(newFList("Dieser Satz sollte als Deutsch eingestuft werden".toLowerCase()
                .split(""),
                Token::from));

        System.out.println(Inference.posterior_density(sentence, model));

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
