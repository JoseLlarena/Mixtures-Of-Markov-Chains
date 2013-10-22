package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.pgm.new_api.Common.example_model;
import static com.fluent.pgm.new_api.New_Estimation.New_Estimation;

public class New_Estimation_Spec extends AbstractSpec
{
    FList<Seqence> data = asFList(Seqence.from("aa"), Seqence.from("aa"), Seqence.from("ab"), Seqence.from("ab"),
            Seqence.from("ba"), Seqence.from("ba"), Seqence.from("bb"), Seqence.from("bb"));
    //Generation.generate            // (1000,example_model(),    // SEED_1);

    @Test
    public void estimates_em_iteration() throws Exception
    {
        THEN(New_Estimation.em_iteration(example_model(), data)).shouldBe(example_model());

    }

    @Test
    public void estimates_momc_from_data() throws Exception
    {
        THEN(New_Estimation.estimate(data)).shouldBe(example_model());

    }
}
