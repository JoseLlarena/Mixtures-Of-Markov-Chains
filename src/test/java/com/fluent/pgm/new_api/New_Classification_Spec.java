package com.fluent.pgm.new_api;

import com.fluent.math.*;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.Map;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.oo;
import static com.fluent.math.P.P;
import static com.fluent.pgm.new_api.New_Classification.New_Classification;

public class New_Classification_Spec extends AbstractSpec
{
    @Mock Seqence sequence;
    @Mock MoMC model;
    @Spy New_Classification api = New_Classification;
    Map<String, P> posterior_map = newFMap(oo("A", P(.4)), oo("B", P(.6)));

    @Test
    public void classifies_sequence() throws Exception
    {
        GIVEN_SPY(api).RETURNS(posterior_map).ON().joint(sequence, model);

        THEN(api.classify(sequence, model)).shouldBe("B");
    }
}
