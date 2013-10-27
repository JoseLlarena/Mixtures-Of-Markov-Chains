package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import static com.fluent.pgm.new_api.New_Initialisation.Initialisation;

public class Initialisation_Spec extends AbstractSpec
{
     FList<Seqence> data;

    @Test
    public void initialises_momc_from_data() throws Exception
    {
       So(Initialisation.initialise_with(data)).shouldBe(Common.example_model_1());
    }
}
