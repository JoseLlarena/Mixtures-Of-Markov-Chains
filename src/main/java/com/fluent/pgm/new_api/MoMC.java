package com.fluent.pgm.new_api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.core.oo;

import static com.fluent.pgm.new_api.IO.MoMC_Deserialiser;
import static com.fluent.pgm.new_api.IO.MoMC_Serialiser;

@JsonSerialize(using = MoMC_Serialiser.class)
@JsonDeserialize(using = MoMC_Deserialiser.class)
public class MoMC extends oo<MPX, FMap<String, CPX>>
{
    public MoMC(MPX $1, FMap<String, CPX> $2)
    {
        super($1, $2);
    }

    public MPX prior()
    {
        return $1;
    }

    public FMap<String, CPX> transitions()
    {
        return $2;
    }

    public CPX transitions_for(String the_class)
    {
        return $2.get(the_class);
    }

    public FSet<String> tags() {return $1.keys();}
}
