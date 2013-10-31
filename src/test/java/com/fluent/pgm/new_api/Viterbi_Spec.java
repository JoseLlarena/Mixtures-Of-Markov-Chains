package com.fluent.pgm.new_api;

import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.math.*;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;

import static com.fluent.collections.Lists.EMPTY_FLIST;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.collections.Sets.newOrderedFSet;
import static com.fluent.core.oo.*;
import static com.fluent.pgm.new_api.Common.C1;
import static com.fluent.pgm.new_api.Common.example_model_1;
import static com.fluent.pgm.new_api.Token.END;
import static com.fluent.pgm.new_api.Token.OOV;
import static com.fluent.pgm.new_api.Viterbi.Path_Memory;
import static com.fluent.pgm.new_api.Viterbi.Viterbi;

public class Viterbi_Spec extends AbstractSpec
{
    Token a = Token.from("a"), b = Token.from("b");
    Seqence state_sequence = Seqence.from_chars("bba"), sequence = Seqence.from(OOV, OOV, OOV);
    int T = state_sequence.size();
    Path_Memory memory;
    Path_Memory expected;
    CPX transitions = example_model_1().transitions_for(C1);
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
        CPX emissions = new CPX()
        {
            public P p(Token token, Context context)
            {
                return Viterbi.SCORING.of(token, context);
            }

            public FSet<Token> tokens()
            {
                return newOrderedFSet(a, b, END);
            }
        };

        So(api.complete(sequence, transitions, emissions)).shouldBe(Seqence.from(b, a, b));
    }
}
