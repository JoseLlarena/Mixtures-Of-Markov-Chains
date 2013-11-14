package com.fluent.pgm.mixtures.integration;

import com.fluent.collections.FList;
import com.fluent.pgm.mixtures.MoMC;
import com.fluent.pgm.mixtures.Sequence;
import com.fluent.specs.unit.AbstractSpec;
import com.fluent.util.WriteLines;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.pgm.mixtures.Common.example_model;
import static com.fluent.pgm.mixtures.IO.IO;

public class IO_Spec extends AbstractSpec
{
    public static final String DATA_FILE = "C:/Users/Jose/project-workspace/Sequensir/src/test/resources/data.txt";
    MoMC model;
    Path file = Paths.get(System.getProperty("user.dir"), "momc.json");
    String data_directory = "C:/Users/Jose/project-workspace/Sequensir/src/test/resources/tagged";
    FList<Sequence> data_1, data_2;

    @Before
    public void CONTEXT()
    {
        model = example_model();
        data_1 = asFList(Sequence.from_words("Jazz for a Rainy Afternoon:  {link}"),
                Sequence.from_words("RT: @mention: I love rainy days."));
        data_2 = asFList(Sequence.from_words("Insomma parleremo delle scelte di vita della gente."),
                Sequence.from_words("Ma la Lega si astiene."));

        Paths.get(data_directory).toFile().mkdir();
    }



    @Test
    public void reads_char_data_from_file() throws Exception
    {
        So(IO.char_data_from(DATA_FILE)).shouldBe(asFList(Sequence.from_chars("aa"), Sequence.from_chars("bb")));
    }

    @Test
    public void reads_tagged_data_from_directory() throws Exception
    {
        WriteLines.INSTANCE.to(Paths.get(data_directory, "english.txt"), data_1);
        WriteLines.INSTANCE.to(Paths.get(data_directory, "italian.txt"), data_2);

        So(IO.tagged_word_data_from(data_directory)).shouldBe(
                data_1.cross(asFList("english")).plus(data_2.cross(asFList("italian"))));
    }

    @Test
    public void reads_model_from_json_file() throws Exception
    {
        So(IO.to_json(model, file).model_from(file)).shouldBe(model);
    }

    @After
    public void CLEAN() throws Exception
    {
        Paths.get(data_directory, "english.txt").toFile().delete();
        Paths.get(data_directory, "italian.txt").toFile().delete();
        Paths.get(data_directory).toFile().delete();
    }
}
