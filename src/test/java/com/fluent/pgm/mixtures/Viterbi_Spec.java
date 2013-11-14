package com.fluent.pgm.mixtures;

import com.fluent.collections.FMap;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;

import static com.fluent.collections.Lists.EMPTY_FLIST;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.*;
import static com.fluent.pgm.mixtures.Common.SWITCHING;
import static com.fluent.pgm.mixtures.Common.example_model;
import static com.fluent.pgm.mixtures.Token.END;
import static com.fluent.pgm.mixtures.Token.OOV;
import static com.fluent.pgm.mixtures.Viterbi.*;
import static com.fluent.pgm.mixtures.Viterbi.Viterbi;

public class Viterbi_Spec extends AbstractSpec
{
    Token a = Token.from("a"), b = Token.from("b");
    Sequence state_sequence = Sequence.from_chars("bba"), sequence = Sequence.from(OOV, OOV, OOV);
    int T = state_sequence.size();
    Path_Memory memory;
    Path_Memory expected;
    CPD transitions = example_model().transitions_for(SWITCHING);
    @Spy Viterbi api = Viterbi;

    @Before
    public void CONTEXT()
    {
        FMap best_transitions = newFMap()
                .plus(oo(T - 3, a), a).plus(oo(T - 3, b), b)
                .plus(oo(T - 2, a), b).plus(oo(T - 2, b), a)
                .plus(oo(T - 1, END), a);

        memory = new Path_Memory(EMPTY_FLIST, best_transitions, T);
    }

    @Test
    public void finds_best_sequence_from_path_memory() throws Exception
    {
        So(Viterbi.backtrack_from(memory)).shouldBe(state_sequence);
    }

    @Test
    public void finds_best_completion() throws Exception
    {
        So(api.complete(sequence, transitions, DEFAULT_EMISSIONS)).shouldBe(Sequence.from(b, a, b));
    }
}
