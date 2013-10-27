package com.fluent.pgm.hmm;

import com.fluent.core.Throwing_F1;

import java.util.function.Consumer;

public interface Throwing_F2<INPUT1, INPUT2, OUTPUT>
{
    public OUTPUT throws_of(INPUT1 input1, INPUT2 input2) throws Exception;

    public default Throwing_F1<INPUT2, OUTPUT> with_arg_1(INPUT1 input1)
    {
        return input2 -> throws_of(input1, input2);
    }

    public default Throwing_F1<INPUT1, OUTPUT> with_arg_2(INPUT2 input2)
    {
        return input1 -> throws_of(input1, input2);
    }


    public default Throwing_F2<INPUT1, INPUT2, OUTPUT> append(Consumer<? super OUTPUT> follows)
    {
        return (input1, input2) -> { OUTPUT output = throws_of(input1, input2); follows.accept(output); return output; };
    }

    public default <O> Throwing_F2<INPUT1, INPUT2, O> and_then(Throwing_F1<? super OUTPUT, ? extends O> follows)
    {
        return (input1, input2) -> follows.throws_of(throws_of(input1, input2));
    }
}