package com.fluent.pgm.new_api.integration;

import com.fluent.pgm.new_api.MoMC;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static com.fluent.pgm.new_api.Common.example_model_1;
import static com.fluent.pgm.new_api.Easy.Easy;
import static com.fluent.pgm.new_api.IO.IO;

public class Easy_Integration_Spec extends AbstractSpec
{
    String model_file = "momc-completion.json";
    String data_directory = "../Sequensir/src/test/resources/tagged";
    String untagged_sequence = "Any English sentence should do";
    String output_file = "momc-tagged.json";

    @Before
    public void CONTEXT() throws Exception
    {
        IO.to_json(example_model_1(), Paths.get(model_file));
    }

    @Test
    public void completes_datum_with_missing_symbols() throws Exception
    {
        So(Easy.complete_characters("aa¬b", model_file)).shouldBe("aaab");
    }

    @Test
    public void estimates_from_tagged_data() throws Exception
    {
        MoMC m = Easy.mixture_from_tagged_data(data_directory, output_file);

        So(Easy.tag(untagged_sequence, output_file)).shouldBe("english");
    }

    @After
    public void CLEAN() throws Exception
    {
        Paths.get(model_file).toFile().delete();
    }

}
