package com.fluent.pgm.mixtures;

import com.fluent.collections.FList;
import org.junit.Test;

import static com.fluent.pgm.mixtures.Initialisation.Initialisation;

public class Initialisation_Spec extends Base_Spec
{
    FList<Sequence> data;

    @Test
    public void initialises_momc_from_data() throws Exception
    {
        So(Initialisation.initialise_with(data)).shouldBe( example_model());
    }
}
