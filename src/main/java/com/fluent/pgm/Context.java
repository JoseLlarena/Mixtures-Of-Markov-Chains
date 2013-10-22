package com.fluent.pgm;

import com.fluent.pgm.new_api.Token;

public interface Context extends Iterable<Token>
{
    int size();

    Token at(int position);
}
