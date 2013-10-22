package com.fluent.pgm.new_api;

import com.fluent.collections.FMap;
import com.fluent.math.*;
import com.fluent.specs.unit.AbstractSpec;
import com.fluent.util.ReadLines;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.fluent.collections.Maps.newOrderedFMap;
import static com.fluent.math.P.*;
import static com.fluent.pgm.new_api.CPD_Builder.CPX_from;
import static com.fluent.pgm.new_api.CPD_Builder._p;
import static com.fluent.pgm.new_api.IO.IO;
import static com.fluent.pgm.new_api.MPX_Builder.MPX;
import static com.fluent.pgm.new_api.Token.END;
import static com.fluent.pgm.new_api.Token.START;
import static java.lang.Double.parseDouble;

public class IO_Spec extends AbstractSpec
{
    MoMC model;
    Path file = Paths.get(System.getProperty("user.dir"), "momc.json");
    Token A = Token.from("a"), B = Token.from("b");
    CPX A_transitions = CPX_from(_p(A, START, .3), _p(B, START, .7),
            _p(A, A, .1), _p(B, A, .6), _p(END, A, .3),
            _p(A, B, .35), _p(B, B, .35), _p(END, B, .3));
    CPX B_transitions = CPX_from(_p(A, START, .3), _p(B, START, .7),
            _p(A, A, .1), _p(B, A, .6), _p(END, A, .3),
            _p(A, B, .35), _p(B, B, .35), _p(END, B, .3));

    @Before
    public void CONTEXT()
    {
        FMap<String, CPX> conditionals = newOrderedFMap();
        conditionals.plus("A", A_transitions).plus("B", B_transitions);

        MPX prior = MPX().p("A", .3).and("B", .7);


        model = new MoMC(prior, conditionals);
    }

    @Test
    public void creates_from_string() throws Exception
    {
        So(P.from_log(parseDouble(new BigDecimal(P(.1).asLog()).toString()))).shouldBe(P(.1));
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
    public void reads_mode_from_json_file() throws Exception
    {
        So(IO.write_json(model, file).read_from_json(file) ).shouldBe(model );
    }
}
