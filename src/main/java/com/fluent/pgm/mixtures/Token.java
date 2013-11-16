package com.fluent.pgm.mixtures;

import com.google.common.cache.Cache;
import com.google.common.hash.HashFunction;

import java.util.concurrent.ExecutionException;

import static com.fluent.pgm.mixtures.Common.*;
import static com.google.common.hash.Hashing.goodFastHash;
import static java.lang.String.valueOf;

public interface Token extends Context
{
    static HashFunction hash = goodFastHash(64);
    static Cache<Long, Token> id_to_token = cache().maximumSize(100_000).build();
    //
    static final long START_ID = id_from("!^!"), END_ID = id_from("!$!"), OOV_ID = id_from("!?!"),
            MISSING_ID = id_from("¬");
    public static final Token
            START = new Special_Token("!^!", START_ID),
            END = new Special_Token("!$!", END_ID),
            OOV = new Special_Token("!?!", OOV_ID),
            MISSING = new Special_Token("¬", MISSING_ID);

    public static Token from(char... letter)
    {
        return Token.from(valueOf(letter));
    }

    public static Token from(String string)
    {
        long id = id_from(string);

        if (END_ID == id)
            return Token.END;
        else if (START_ID == id)
            return Token.START;
        else if (OOV_ID == id)
            return Token.OOV;
        else if (MISSING_ID == id)
            return Token.MISSING;

        try
        {
            return id_to_token.get(id, () -> new Simple_Token(string, id));
        }
        catch (ExecutionException e)
        {
            return new Simple_Token(string, id);
        }
    }
}
