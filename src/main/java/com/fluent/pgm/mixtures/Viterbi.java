package com.fluent.pgm.mixtures;

import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.core.F1;
import com.fluent.core.F2;
import com.fluent.core.oo;
import com.fluent.core.ooo;
import com.fluent.math.*;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.collections.Maps.newOrderedFMap;
import static com.fluent.core.oo.*;
import static com.fluent.math.P.*;
import static com.fluent.pgm.mixtures.Token.*;
import static java.util.Collections.max;
import static java.util.Collections.reverse;

/*
* Follows viterbi formulae in
* Rabiner, Lawrence R. "A tutorial on hidden Markov models and selected applications in speech recognition."
* Proceedings of the IEEE 77.2 (1989): 257-286
 */
public class Viterbi
{
    public static final Viterbi Viterbi = new Viterbi();
    static final P OOV_MISMATCH = P(.1);
    static F2<Token, Context, P> SCORING = (symbol, state) ->
            symbol == OOV ? OOV_MISMATCH :
                    symbol.equals(state) ? P(1 - .1) :
                            ZERO;
    static CPD DEFAULT_EMISSIONS = (token, context) -> SCORING.of(token, context);

    public Sequence complete(Sequence datum, CPD A, CPD B)
    {
        return backtrack_from(final_best(datum, A, B));
    }

    public Sequence complete(Sequence datum, MoMC model)
    {
        F1<String, oo<Sequence, P>> completion_per_tag = tag ->
                {
                    final Path_Memory memory = final_best(datum, model.transitions_for(tag), DEFAULT_EMISSIONS);

                    return oo(backtrack_from(memory), marginal(memory));
                };

        return model.tags().apply(completion_per_tag).max_as(completion -> completion.$2).$1;
    }

    FSet<Score> best_transition(Token state, FSet<Score> best_states, CPD A)
    {
        return best_states.apply(previous -> previous.x(A.p(state, previous.state)));
    }

    Path_Memory best(FList<Token> datum, Path_Memory memory, CPD A, CPD B)
    {
        if (datum.isEmpty())
            return memory;

        return best(datum.rest(), next_best_states(datum.first(), memory, A, B), A, B);
    }

    Path_Memory initial_best(Sequence datum, CPD A, CPD B)
    {
        FSet<Token> states = A.tokens();
        Token first_symbol = datum.at(1);

        FSet<Score> initial_best_states = states.apply(state -> new Score(state, A.p(state, START).x(B.p(first_symbol,
                state))));

        return new Path_Memory(asFList(initial_best_states), newOrderedFMap(), datum.size());
    }

    Path_Memory next_best_states(Token symbol, Path_Memory memory, CPD A, CPD B)
    {
        FSet<Score> previous = memory.best_states().last();

        FSet<Score> current = previous.apply(score ->
                {
                    Token state = score.state;
                    Score best = max(best_transition(state, previous, A)).x(B.p(symbol, state));
                    memory.best_transitions().put(oo(memory.t(), state), best.state);
                    return new Score(state, best.value);
                });

        memory.best_states().add(current);

        return memory;
    }

    Path_Memory final_best(Sequence datum, CPD A, CPD B)
    {
        return best(datum.tokens().rest().rest(), initial_best(datum, A, B), A, B);
    }

    P marginal(Path_Memory memory)
    {
        return memory.best_states().last().find(score -> score.state == END).get().value;
    }

    Iterable<Token> step_through(FMap<oo<Integer, Token>, Token> best_transitions, FList<Token> tokens, int t)
    {
        if (t == 1)
        {
            reverse(tokens.minus(END));
            return tokens;
        }

        tokens.plus(best_transitions.get(oo(t, tokens.last())));

        return step_through(best_transitions, tokens, --t);
    }

    Sequence backtrack_from(Path_Memory memory)
    {
        return Sequence.from(step_through(memory.best_transitions(), asFList(END), memory.T() - 1));
    }

    static class Score extends oo<Token, P> implements Comparable<Score>
    {
        Token state = $1;
        P value = $2;

        Score(final Token $1, final P $2)
        {
            super($1, $2);
        }

        public int compareTo(Score o)
        {
            return this.value.compareTo(o.value);
        }

        Score x(P other_value)
        {
            return new Score(state, value.x(other_value));
        }
    }

    static class Path_Memory extends ooo<FList<FSet<Score>>, FMap<oo<Integer, Token>, Token>, Integer>
    {
        Path_Memory(FList<FSet<Score>> best_paths, FMap<oo<Integer, Token>, Token> best_transitions, int T)
        {
            super(best_paths, best_transitions, T);
        }

        FList<FSet<Score>> best_states()
        {
            return $1;
        }

        FMap<oo<Integer, Token>, Token> best_transitions()
        {
            return $2;
        }

        int t()
        {
            return best_states().size() + 1;
        }

        int T()
        {
            return $3;
        }
    }
}
