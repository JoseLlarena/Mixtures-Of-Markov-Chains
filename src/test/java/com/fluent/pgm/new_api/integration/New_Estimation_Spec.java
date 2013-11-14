package com.fluent.pgm.new_api.integration;

import com.fluent.collections.FList;
import com.fluent.pgm.new_api.*;
import com.fluent.specs.unit.AbstractSpec;
import com.fluent.util.ReadLines;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Random;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.pgm.new_api.Easy.Easy;
import static com.fluent.pgm.new_api.New_Classification.New_Classification;
import static com.fluent.pgm.new_api.New_Inference.New_Inference;
import static java.lang.System.out;

public class New_Estimation_Spec extends AbstractSpec
{

    @Test
    public void estimates_momc_from_data() throws Exception
    {
        New_Estimation e = New_Estimation.Estimation;

        Random r = new Random(Common.SEED_1);

        MoMC model = Easy.mixture_from(
                "C:/Users/Jose/project-workspace/Sequensir/src/main/resources/de_dk_nl_no_en_se.txt",
                line -> Seqence.from(newFList(process(line), Token::from)));

        FList<Seqence> data = ReadLines.INSTANCE.from(
                Paths.get("C:/Users/Jose/project-workspace/Sequensir/src/main/resources/de_dk_nl_no_en_se.txt"),
                line -> Seqence.from(newFList(process(line), Token::from)),
                line -> r.nextDouble() > 0.);

        data.apply((datum) -> New_Classification.classify(datum,
                model) + " -> " + datum.toString().replaceAll("\\s\\s", " ")).forEach(out::println);

        IO.IO.to_json(model, Paths.get("C:/Users/Jose/project-workspace/Sequensir/src/main/resources" +
                "/test-momc.json"));

        Seqence sentence = Seqence.from(newFList( "Dieser Satz sollte als Deutsch eingestuft werden".toLowerCase()
                .split(""),
                Token::from));

        System.out.println(New_Inference.posterior_density(sentence, model));

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
