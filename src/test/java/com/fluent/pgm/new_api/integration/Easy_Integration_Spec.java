package com.fluent.pgm.new_api.integration;

import com.fluent.pgm.new_api.IO;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static com.fluent.pgm.new_api.Common.example_model_1;
import static com.fluent.pgm.new_api.Easy.Easy;

public class Easy_Integration_Spec extends AbstractSpec
{
    String model_file = "momc-completion.json";

    @Before
    public void CONTEXT() throws Exception
    {
        IO.IO.write_json(example_model_1(), Paths.get(model_file));
    }

    @Test
    public void completes_datum_with_missing_symbols() throws Exception
    {
        So(Easy.complete("aa¬b", model_file)).shouldBe("aaab");

    }

    @After
    public void CLEAN() throws Exception
    {
        Paths.get(model_file).toFile().delete();
    }
}
