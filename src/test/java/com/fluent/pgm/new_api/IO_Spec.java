package com.fluent.pgm.new_api;

import com.fluent.math.*;
import com.fluent.specs.unit.AbstractSpec;
import com.fluent.util.ReadLines;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.math.P.*;
import static com.fluent.pgm.new_api.IO.IO;
import static java.lang.Double.parseDouble;

public class IO_Spec extends AbstractSpec
{
    public static final String DATA_FILE = "C:/Users/Jose/project-workspace/Sequensir/src/test/resources/data.txt";
    MoMC model;
    Path file = Paths.get(System.getProperty("user.dir"), "momc.json");


    @Before
    public void CONTEXT()
    {
        model = Common.example_model_1();
    }

    @Test
    public void creates_from_string() throws Exception
    {
        So(P.from_log(parseDouble(new BigDecimal(P(.1).asLog()).toString()))).shouldBe(P(.1));
    }

    @Test
    public void reads_char_data_from_file() throws Exception
    {
        So(IO.read_char_data_from(DATA_FILE)).shouldBe(asFList(Seqence.from_chars("aa"),Seqence.from_chars("bb")));
    }


    @Test
    public void writes_model_to_json_file() throws Exception
    {
        IO.write_json(model, file);

        THEN(ReadLines.INSTANCE.from(file).toString("%s")).shouldBe("{\"prior\":[{\"A\":-0" +
                ".5228787452803376201160290293046273291110992431640625}," +
                "{\"B\":-0.15490195998574318725360399184864945709705352783203125}]," +
                "\"conditionals\":[{\"A\":[{\"!^!\":{\"a\":-0.5228787452803376201160290293046273291110992431640625}}," +
                "{\"!^!\":{\"b\":-0.15490195998574318725360399184864945709705352783203125}},{\"a\":{\"a\":-1}}," +
                "{\"a\":{\"b\":-0.2218487496163563943429863911660504527390003204345703125}}," +
                "{\"a\":{\"!$!\":-0.5228787452803376201160290293046273291110992431640625}}," +
                "{\"b\":{\"a\":-0.455931955649724385271071014358312822878360748291015625}}," +
                "{\"b\":{\"b\":-0.455931955649724385271071014358312822878360748291015625}}," +
                "{\"b\":{\"!$!\":-0.5228787452803376201160290293046273291110992431640625}}]}," +
                "{\"B\":[{\"!^!\":{\"a\":-0.5228787452803376201160290293046273291110992431640625}}," +
                "{\"!^!\":{\"b\":-0.15490195998574318725360399184864945709705352783203125}},{\"a\":{\"a\":-1}}," +
                "{\"a\":{\"b\":-0.2218487496163563943429863911660504527390003204345703125}}," +
                "{\"a\":{\"!$!\":-0.5228787452803376201160290293046273291110992431640625}}," +
                "{\"b\":{\"a\":-0.455931955649724385271071014358312822878360748291015625}}," +
                "{\"b\":{\"b\":-0.455931955649724385271071014358312822878360748291015625}}," +
                "{\"b\":{\"!$!\":-0.5228787452803376201160290293046273291110992431640625}}]}]}");
    }

    @Test
    public void reads_model_from_json_file() throws Exception
    {
        So(IO.write_json(model, file).read_from_json(file) ).shouldBe(model );
    }
}
