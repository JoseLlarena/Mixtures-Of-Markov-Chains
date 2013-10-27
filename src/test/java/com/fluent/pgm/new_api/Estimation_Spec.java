package com.fluent.pgm.new_api;

import com.fluent.collections.FList;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;
import org.mockito.Mock;

public class Estimation_Spec extends AbstractSpec
{
    @Mock MoMC model,expected_model;
    @Mock FList<Seqence> data;

    @Test
    public void runs_em_iteration_on_data_in_parallel() throws Exception
    {
        WHEN(New_Estimation.Estimation.parallel_em_iteration(model, data));

        THEN(theOutcome).shouldBe(expected_model);
    }
}
