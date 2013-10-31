package com.fluent.pgm.new_api;

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
    @Mock New_Initialisation init;
    @Mock New_Optimisation optimisation;
    //
    String data_file = "data-file";

    @Test
    public void estimates_momc_from_file() throws Exception
    {
        GIVEN(io.read_char_data_from(data_file)).RETURNS(EMPTY_FLIST);
        GIVEN(init.initialise_with(EMPTY_FLIST)).RETURNS(expected_model) ;
        GIVEN(optimisation.optimise(eq(expected_model), any(OP1.class))).RETURNS(expected_model);

        WHEN(new Easy(io, null,init, dummy(New_Estimation.class), optimisation).estimate_from(data_file));

        THEN(it).shouldBe(expected_model);
    }

}
