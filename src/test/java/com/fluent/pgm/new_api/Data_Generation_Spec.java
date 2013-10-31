package com.fluent.pgm.new_api;

import com.fluent.pgm.estimation.Counting;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.pgm.new_api.Common.C1;
import static com.fluent.pgm.new_api.Common.SEED_1;
import static com.fluent.pgm.new_api.Generation.Generation;
import static org.hamcrest.Matchers.*;

public class Data_Generation_Spec extends AbstractSpec
{
    int N;
    MoMC model;

    @Before
    public void CONTEXT()
    {
        model = Common.example_model_1();
    }

    @Test//FIXME WE WANT TO TEST SEQUENCES ARE GENERATED WITH CORRECT FREQUENCY
    public void generates_data_from_momc() throws Exception
    {
        THEN(Generation.generate(N = 2, model, SEED_1)).shouldBe(asFList(Seqence.from_chars("a"),
                Seqence.from_chars("a")));
    }

    @Test//FIXME WE WANT TO TEST SEQUENCES ARE GENERATED WITH CORRECT FREQUENCY
    public void generates_sequences_from_classes() throws Exception
    {
        THEN(Generation.sequences_from(asFList(C1, C1), model.transitions_per_tag(), new Random(SEED_1))).shouldBe(asFList
                (Seqence.from_chars("ab"), Seqence.from_chars("b")));
    }

    @Test
    public void generates_classes_from_momc_with_correct_frequency() throws Exception
    {
        THEN(Counting.of.frequency(Generation.generate_classes(N = 1000, model, new Random(SEED_1))).get(C1)).
                shouldBe(closeTo(.3, .05));
    }
}
