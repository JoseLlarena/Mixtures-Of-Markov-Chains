package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.specs.unit.AbstractSpec;
import com.fluent.util.ReadLines;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.nio.file.Paths;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.collections.Lists.newFList;
import static com.fluent.pgm.new_api.Common.example_model_1;
import static com.fluent.pgm.new_api.New_Estimation.New_Estimation;

public class New_Estimation_Spec extends AbstractSpec
{
    FList<Seqence> data = asFList(Seqence.from("aa"), Seqence.from("aa"), Seqence.from("ab"), Seqence.from("ab"),
            Seqence.from("ba"), Seqence.from("ba"), Seqence.from("bb"), Seqence.from("bb"));

    @Test
    public void estimates_em_iteration() throws Exception
    {
        THEN(New_Estimation.em_iteration(example_model_1(), data)).shouldBe(example_model_1());
    }

    @Test
    public void estimates_momc_from_data() throws Exception
    {
        System.out.println(DateTimeFormat.fullDateTime().print(DateTime.now()));

        FList<Seqence> data = ReadLines.INSTANCE.from(
                Paths.get("C:\\Users\\Jose\\project-workspace\\Sequensir\\src\\main\\resources\\big_langs.txt"),
                line -> Seqence.from(newFList(process(line), Token::from)),
                line -> !line.isEmpty());

        System.out.println(DateTimeFormat.fullDateTime().print(DateTime.now()) + " DATA READ IN");
        MoMC model = New_Estimation.estimate(data);
        System.out.println(DateTimeFormat.fullDateTime().print(DateTime.now()) + " FINISHED TRAINING");

        FList<Seqence> raw_data =
                ReadLines.INSTANCE.from(
                        Paths.get("C:\\Users\\Jose\\project-workspace\\Sequensir\\src\\main\\resources\\big_langs" +
                                ".txt"),
                        line -> Seqence.from(newFList(line.split("\\s+"), Token::from)),
                        line -> true);

        //data.apply((i, datum) -> New_Classification.classify(datum, model) + " -> " + datum).forEach(out::println);

    }

    FList<String> process(String line)
    {

//        if (!line.isEmpty())
//            return newFList(line.replaceFirst("^\\d+\\s+", "").split("\\s+"));

        return newFList(line.replaceAll("^\\d+\\s+", "").split("")).apply(string -> string.toLowerCase()).apply
                (chunk ->
                        {
                            if (chunk.matches("\\d+"))
                                return "6";
                            else if (chunk.matches("\\s+"))
                                return " ";
                            else if (chunk.matches("[^a-z‡Ë']"))
                                return "$";
                            else
                                return chunk;

                        });
    }
}
