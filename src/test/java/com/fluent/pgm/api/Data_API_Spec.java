package com.fluent.pgm.api;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.collections.Lists;
import com.fluent.math.P;
import com.fluent.pgm.CPD;
import com.fluent.pgm.MPD;
import com.fluent.pgm.Sampling;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.hmm.HMM;
import com.fluent.pgm.momc.Mixture;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.Random;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.oo;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;

@SuppressWarnings("unchecked")
public class Data_API_Spec extends AbstractSpec
{
    @Mock Sampling sampling;
    @Mock CPD A, B;
    @Mock MPD P;
    @Mock FMap<String, P> conditionedOnAMap, conditionedOnBMap;
    @Mock FMap<String, FMap<String, P>> AMap, BMap;
    FList<Sequence> stateSequences = Lists.asFList(new Sequence("XY"));
    long seed;
    @Mock Random random;

    @Test
    public void samples_from_HMM() throws Exception
    {
        BMap = newFMap(oo("X", conditionedOnAMap), oo("Y", conditionedOnBMap));

        GIVEN(sampling.sequencesFromCPD(eq(AMap), eq(1), anyLong())).RETURNS(stateSequences);
        GIVEN(sampling.ofMPD(eq(conditionedOnAMap), Matchers.any(Random.class))).RETURNS("a");
        GIVEN(sampling.ofMPD(eq(conditionedOnBMap), Matchers.any(Random.class))).RETURNS("b");

        WHEN(new Data_API(null, null, sampling).dataFrom(new HMM(A, B), 1, 1234567890L));

        THEN(theOutcome).shouldBe(Lists.asFList(new Sequence("ab")));
    }

    @Test
    public void samples_from_mixture() throws Exception
    {
        final Mixture mixture = new Mixture(P, newFMap(oo("A", A), oo("B", B)));

        GIVEN(sampling.ofMPD(P, random)).RETURNS("A", "B");
        GIVEN(sampling.sequenceFromCPD(A, random)).RETURNS(Sequence.from("y","y"));
        GIVEN(sampling.sequenceFromCPD(B, random)).RETURNS(Sequence.from("x","x"));

        WHEN(new Data_API(null, null, sampling).dataFrom(mixture, 2, random));

        THEN(theOutcome).shouldBe(Lists.asFList(Sequence.from("yy"), Sequence.from("xx")));
    }
}
