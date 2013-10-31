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

    public FMap<String, CPX> transitions_per_tag()
    {
        return $2;
    }

    public CPX transitions_for(String tag)
    {
        return transitions_per_tag().get(tag);
    }

    public FSet<String> tags() {return prior().as_map().keys();}
}
