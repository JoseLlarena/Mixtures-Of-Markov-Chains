package com.fluent.pgm.hmm;

import com.fluent.collections.FList;
import com.fluent.collections.FSet;
import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.pgm.CPD;
import com.fluent.pgm.Sequence;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.core.Words.from;
import static com.fluent.core.Words.using;
import static com.fluent.core.oo.oo;
import static com.fluent.pgm.Sequence.END_STATE;
import static com.fluent.pgm.Sequence.START_STATE;
import static com.fluent.pgm.hmm.State_Probability.state_probability;
import static java.util.Collections.max;
import static java.util.Collections.reverse;

public class ViterbiInference
{
    private static FSet<State_Probability> transitsTo(final String start, final FSet<State_Probability> joints,
                                                      final CPD A)
    {
        return joints.apply(joint ->
                {
                    final String end = joint.state();
                    return state_probability(end, joint.p().x(A.p(start, end)));
                });
    }

    public static final ViterbiInference of = new ViterbiInference();

    FSet<State_Probability> alignmentsAt(final String symbolAtT, final FSet<State_Probability> maxTransits, final CPD B)
    {
        return maxTransits.apply(transit -> transit.both((state, p) -> state_probability(state, p.x(B.p(symbolAtT,
                state)))));
    }

    public P maxJoint(final Sequence O, final CPD A, final CPD B)
    {
        FSet<State_Probability> maxTransits = prior(from(A));
        FSet<State_Probability> joints = alignmentsAt(O.at(1), maxTransits, B);

        for (int t = 2; t < O.T(); t++)
        {
            maxTransits = maxTransits(joints, PathMemory.NONE, A);

            joints = alignmentsAt(O.at(t), maxTransits, B);
        }

        return max(joints).p();
    }

    FSet<State_Probability> maxTransits(final FSet<State_Probability> joints, final PathMemory memory, final CPD A)
    {
        final FSet<State_Probability> maxTransits = joints.apply(joint ->
                {
                    final State_Probability max = max(transitsTo(joint.state(), using(joints), A));

                    memory.add(joint.state(), max.state());

                    return state_probability(joint.state(), max.p());
                });

        memory.nextT();

        return maxTransits;
    }

    PathMemory memory()
    {
        return new PathMemory();
    }

    public FList<String> mpss(final Sequence O, final CPD A, final CPD B)
    {
        FSet<State_Probability> maxTransits = prior(from(A));
        FSet<State_Probability> joints = alignmentsAt(O.at(1), maxTransits, B);

        final PathMemory memory = memory();

        for (int t = 2; t < O.T(); t++)
        {
            maxTransits = maxTransits(joints, memory, A);

            joints = alignmentsAt(O.at(t), maxTransits, B);
        }

        return memory.backtrack();
    }

    FSet<State_Probability> prior(final CPD A)
    {
        return A.tokens().apply(state -> state_probability(state, A.p(state, START_STATE)));
    }

    static class PathMemory
    {
        static FList<String> backtrack(final PathMemory memory)
        {
            final FList<String> sequence = newFList();
            String state = END_STATE;

            int tau = memory.t() - 1;

            while (tau > 1)
            {
                state = memory.next(oo(tau--, state));
                sequence.add(state);
            }

            reverse(sequence);
            return sequence;
        }

        static final PathMemory NONE = new PathMemory()
        {
            public void add(final String toState, final String state)
            {
            }

            public FList<String> backtrack()
            {
                return newFList();
            }
        };
        final Map<oo<Integer, String>, String> transitionsAtT;
        int t;

        PathMemory()
        {
            this(new LinkedHashMap<>());
        }

        PathMemory(final Map<oo<Integer, String>, String> transitionsAtT)
        {
            this.transitionsAtT = transitionsAtT;
            t = 2;
        }

        void add(final String toState, final String fromState)
        {
            transitionsAtT.put(oo(t, toState), fromState);
        }

        FList<String> backtrack()
        {
            return backtrack(this);
        }

        String next(final oo<Integer, String> timeState)
        {
            return transitionsAtT.get(timeState);
        }

        public void nextT()
        {
            t++;
        }

        int t()
        {
            return t;
        }
    }
}
