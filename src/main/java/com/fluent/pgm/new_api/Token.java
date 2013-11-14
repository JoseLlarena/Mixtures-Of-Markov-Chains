package com.fluent.pgm.new_api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.hash.HashFunction;

import java.util.concurrent.ExecutionException;

import static com.google.common.hash.Hashing.goodFastHash;
import static java.lang.String.valueOf;

public interface Token extends Context
{
    static HashFunction hash = goodFastHash(64);
    static final long START_ID = id_from2("!^!"), END_ID = id_from2("!$!"), OOV_ID = id_from2("!?!");
    public static final Token
            START = new Special_Token("!^!", START_ID),
            END = new Special_Token("!$!", END_ID),
            OOV = new Special_Token("!?!", OOV_ID);
    public static Cache<Long, Token> id_to_token = CacheBuilder.newBuilder().maximumSize(100_000).recordStats().build
            ();

    public static Token from(char... letter)
    {
        return Token.from(valueOf(letter));
    }

    public static long id_from2(String letter) {return hash.hashString(letter).asLong();}

    public static Token from(String string)
    {
        long id = id_from2(string);

        if (END_ID == id)
            return Token.END;
        else if (START_ID == id)
            return Token.START;
        else if (OOV_ID == id)
            return Token.OOV;

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
