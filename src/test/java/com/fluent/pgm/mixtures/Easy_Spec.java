package com.fluent.pgm.mixtures;

import com.fluent.core.OP1;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import static com.fluent.collections.Lists.EMPTY_FLIST;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.MockitoAnnotations.Mock;

public class Easy_Spec extends AbstractSpec
{
    @Mock MoMC expected_model;
    @Mock IO io;
    @Mock Initialisation init;
    @Mock Optimisation optimisation;
    //
    String data_file = "data-file";

    @Test
    public void estimates_momc_from_file() throws Exception
    {
        GIVEN(io.char_data_from(data_file)).RETURNS(EMPTY_FLIST);
        GIVEN(init.initialise_with(EMPTY_FLIST)).RETURNS(expected_model);
        GIVEN(optimisation.optimise(eq(expected_model), any(OP1.class))).RETURNS(expected_model);

        WHEN(new Easy(io, null, init, dummy(Estimation.class), optimisation,null).character_mixture_from_untagged
                (data_file,null));

        THEN(it).shouldBe(expected_model);
    }

}
