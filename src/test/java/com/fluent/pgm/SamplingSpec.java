package com.fluent.pgm;

import com.fluent.collections.FMap;
import com.fluent.math.*;
import com.fluent.pgm.estimation.Counting;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import java.util.TreeMap;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.math.P.*;
import static com.fluent.pgm.CPDBuilder.CPD_with_OOV;
import static com.fluent.pgm.Sequence.END_STATE;
import static java.lang.System.out;
import static org.hamcrest.Matchers.closeTo;

public class SamplingSpec extends AbstractSpec
{
    static String A = "A", B = "B";
    static String C1 = "1", C2 = "2";
    FMap<Sequence, Integer> sequenceCounts;
    FMap<String, Integer> counts;
    CPD cpd;

    @Test
    public void generates_data_from_conditional_probability_distribution() throws Exception
    {
        GIVEN:
        cpd = CPD_with_OOV().p(A, .5).p(A, A, .2).p(B, A, 0).p(END_STATE, A, .8).p(B, .5).p(B, B, .1).p(A, B, .1)
                .and(END_STATE, B, .8);

        WHEN(sequenceCounts = new Counting().count(Sampling.of.sequencesFromCPD(cpd, 10000, 1234567890)));

        out.println(new TreeMap<>(counts));

        THEN((double) counts.get("A")).shouldBe(closeTo(5000, 50));
        AND((double) counts.get("B")).shouldBe(closeTo(5000, 50));
    }

    @Test
    public void generates_data_from_marginal_probability_distribution() throws Exception
    {
        WHEN(counts = new Counting().count(Sampling.of.ofMPD(newFMap(String.class, P.class).plus("A", P(.5)).plus("B",
                P(.5)), 10000,
                1234567890)));

        THEN((double) counts.get("A")).shouldBe(closeTo(5000, 50));
        AND((double) counts.get("B")).shouldBe(closeTo(5000, 50));
    }

    @Test
    public void samples_from_conditional_probability_distribution() throws Exception
    {
        GIVEN:
        cpd = CPD_with_OOV().p(A, .5).p(A, A, .2).p(B, A, 0).p(END_STATE, A, .8).p(B, .5).p(B, B, .1).p(A, B, .1)
                .and(END_STATE, B, .8);

        WHEN(sequenceCounts = new Counting().count(Sampling.of.sequencesFromCPD(cpd, 10000,
                1234567890).apply(sequence -> new Sequence(sequence.at(1)))));

        out.println(new TreeMap<>(sequenceCounts));

        THEN((double) sequenceCounts.get(new Sequence("A"))).shouldBe(closeTo(5000., 100.));
        AND((double) sequenceCounts.get(new Sequence("B"))).shouldBe(closeTo(5000., 100));
    }
}
