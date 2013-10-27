package com.fluent.pgm.new_api.integration;

import com.fluent.collections.FList;
import com.fluent.pgm.new_api.MoMC;
import com.fluent.pgm.new_api.Seqence;
import com.fluent.pgm.new_api.Token;
import com.fluent.specs.unit.AbstractSpec;
import com.fluent.util.ReadLines;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.nio.file.Paths;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.collections.Lists.newFList;
import static com.fluent.pgm.new_api.New_Classification.New_Classification;
import static com.fluent.pgm.new_api.New_Estimation.Estimation;
import static com.fluent.pgm.new_api.New_Inference.New_Inference;
import static java.lang.System.out;

public class New_Estimation_Spec extends AbstractSpec
{
    FList<Seqence> data = asFList(Seqence.from("aa"), Seqence.from("aa"), Seqence.from("ab"), Seqence.from("ab"),
            Seqence.from("ba"), Seqence.from("ba"), Seqence.from("bb"), Seqence.from("bb"));
    com.fluent.pgm.new_api.New_Inference api = New_Inference.New_Inference;


    @Test
    public void estimates_momc_from_data() throws Exception
    {
        out.println(DateTimeFormat.fullDateTime().print(DateTime.now()));

        FList<Seqence> data = ReadLines.INSTANCE.from(
                Paths.get("C:\\Users\\Jose\\project-workspace\\Sequensir\\src\\main\\resources\\langs.txt"),
                line -> Seqence.from(newFList(process(line), Token::from)),
                line -> !line.isEmpty());

        out.println(DateTimeFormat.fullDateTime().print(DateTime.now()) + " DATA READ IN");
        MoMC model = Estimation.estimate(data);
        out.println(DateTimeFormat.fullDateTime().print(DateTime.now()) + " FINISHED TRAINING");

        //        FList<Seqence> raw_data =
        //                ReadLines.INSTANCE.from(
        //                        Paths.get("C:\\Users\\Jose\\project-workspace\\Sequensir\\src\\main\\resources
        // \\big_langs" +
        //                                ".txt"),
        //                        line -> Seqence.from(newFList(line.split("\\s+"), Token::from)),
        //                        line -> true);

        data.select(d -> Math.random() > .9).apply((datum) -> New_Classification.classify(datum,
                model) + " -> " + datum).forEach(out::println);

    }

    FList<String> process(String line)
    {
        //        System.out.println();
        //        if (!line.isEmpty())
        //            return newFList(line.split("\\s+")).apply(word ->
        //                    {
        //                        if (!word.matches("[a-zA-Z]+"))
        //                        {
        //                           //System.out.print(word+" ");
        //                            return word;
        //                        }
        //                        else
        //                        {
        //                            //out.print("***** ");
        //                            return word;
        //                        }
        //
        //                    }
        //
        //            );
        //
        //        //                if (!line.isEmpty())
        //        //                    return newFList(line.replaceFirst("^\\d+\\s+", "").split("\\s+"));
        return newFList(line.split("")).apply(string -> string.toLowerCase()).apply
                (chunk ->
                        {
                            if (chunk.matches("\\d+"))
                                return "6";
                            else if (chunk.matches("\\s+"))
                                return " ";
                            else if (chunk.matches("[^a-zA-Z‡Ë']"))
                                return "$";
                            else
                                return chunk;

                        });
    }
}
