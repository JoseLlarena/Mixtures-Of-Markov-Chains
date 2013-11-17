package com.fluent.pgm.mixtures.integration;

import com.fluent.pgm.mixtures.Base_Spec;
import com.fluent.pgm.mixtures.MoMC;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.fluent.pgm.mixtures.IO.IO;

public class IO_Spec extends Base_Spec
{
    MoMC model;
    Path file = Paths.get("src/test/resources/momc.json");

    @Before
    public void CONTEXT()
    {
        model = example_model();
    }


    @Test
    public void reads_model_from_json_file() throws Exception
    {
        So(IO.to_json(model, file).model_from(file)).shouldBe(model);
    }

    @After
    public void CLEAN() throws Exception
    {
        Paths.get(System.getProperty("user.dir"), "momc.json").toFile().delete();
    }
}
