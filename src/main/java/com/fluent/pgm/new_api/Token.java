package com.fluent.pgm.new_api;

import static com.fluent.pgm.new_api.Common.id_from;
import static java.lang.String.valueOf;

public interface Token extends Context
{

    static final long START_ID = id_from("!^!"), END_ID = id_from("!$!"),OOV_ID = id_from("!?!");
    public static final Token START = new Token()
    {
        long id = START_ID;

        public String toString()
        {
            return "!^!";
        }

        public long id()
        {
            return id;
        }
    };
    public static final Token OOV = new Token()
    {
        long id = id_from(toString());

        public String toString()
        {
            return "!?!";
        }

        public long id()
        {
            return id;
        }
    };
    public static final Token END = new Token()
    {
        long id = END_ID;

        public String toString()
        {
            return "!$!";
        }

        public long id()
        {
            return id;
        }
    };

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

        return new Token()
        {
            public String toString()
            {
                return string;
            }

            public int hashCode()
            {
                return (int) id;
            }

            public long id()
            {
                return id;
            }

            public boolean equals(Object o)
            {
                return o == this || o instanceof Token && ((Token) o).id() == id;
            }
        };
    }

    public long id();

}
