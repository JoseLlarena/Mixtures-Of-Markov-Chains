package com.fluent.pgm.mixtures;

import com.fluent.collections.FMap;
import org.junit.Before;
import org.junit.Test;

import static com.fluent.collections.Lists.EMPTY_FLIST;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.*;
import static com.fluent.pgm.mixtures.Token.END;
import static com.fluent.pgm.mixtures.Token.MISSING;
import static com.fluent.pgm.mixtures.Viterbi.*;
import static com.fluent.pgm.mixtures.Viterbi.Viterbi;

public class Viterbi_Spec extends Base_Spec
{
    Sequence state_sequence = Sequence.from_chars("bba"), sequence = Sequence.from(MISSING, MISSING, MISSING);
    int T = state_sequence.size();
    Path_Memory memory;
    CPD transitions = example_model().transitions_for(SWITCHING);

    @Before
    public void CONTEXT()
    {
        FMap best_transitions = newFMap()
                .plus(oo(T - 3, A), A).plus(oo(T - 3,B), B)
                .plus(oo(T - 2, A), B).plus(oo(T - 2, B), A)
                .plus(oo(T - 1, END), A);

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
        So(Viterbi.complete(sequence, transitions, DEFAULT_EMISSIONS)).shouldBe(Sequence.from(B, B, B));
    }
}
