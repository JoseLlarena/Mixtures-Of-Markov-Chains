package com.fluent.pgm.new_api;

class Simple_Token implements Token
{
    String string;
    long id;

    Simple_Token(String string, long id)
    {
        this.string = string;
        this.id = id;
    }

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
}