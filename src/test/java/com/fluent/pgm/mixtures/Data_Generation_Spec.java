package com.fluent.pgm.mixtures;

import com.fluent.specs.unit.AbstractSpec;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.pgm.mixtures.Common.SWITCHING;
import static com.fluent.pgm.mixtures.Common.SEED_1;
import static com.fluent.pgm.mixtures.Generation.Generation;

public class Data_Generation_Spec extends AbstractSpec
{
    int N;
    MoMC model;

    @Before
    public void CONTEXT()
    {
        model = Common.example_model();
    }

    @Test//FIXME WE WANT TO TEST SEQUENCES ARE GENERATED WITH CORRECT FREQUENCY
    public void generates_data_from_momc() throws Exception
    {
        THEN(Generation.generate_untagged(N = 2, model, SEED_1)).shouldBe(asFList(Sequence.from_chars("a"),
                Sequence.from_chars("a")));
    }

    @Test//FIXME WE WANT TO TEST SEQUENCES ARE GENERATED WITH CORRECT FREQUENCY
    public void generates_sequences_from_classes() throws Exception
    {
        THEN(Generation.sequences_from(asFList(SWITCHING, SWITCHING), model.transitions_per_tag(),
                new Random(SEED_1))).shouldBe(asFList
                (Sequence.from_chars("ab"), Sequence.from_chars("b")));
    }

    //    @Test
    //    public void generates_classes_from_momc_with_correct_frequency() throws Exception
    //    {
    //        THEN(Counting.of.frequency(Generation.generate_classes(N = 1000, model, new Random(SEED_1))).get(C1)).
    //                shouldBe(closeTo(.3, .05));
    //    }
}
