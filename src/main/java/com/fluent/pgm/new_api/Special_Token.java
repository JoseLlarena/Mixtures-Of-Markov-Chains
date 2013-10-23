package com.fluent.pgm.new_api;

 class Special_Token extends Simple_Token
{
    Special_Token(String string, long id)
    {
        super(string, id);
    }


    public boolean equals(Object o)
    {
        return o == this;
    }
}
