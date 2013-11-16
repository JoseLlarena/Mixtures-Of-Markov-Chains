package com.fluent.pgm.mixtures;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fluent.collections.FMap;
import com.fluent.collections.FSet;
import com.fluent.core.oo;

import static com.fluent.pgm.mixtures.IO.MoMC_Deserialiser;
import static com.fluent.pgm.mixtures.IO.MoMC_Serialiser;

/**
 * Mixture of Markov Chains
 */
@JsonSerialize(using = MoMC_Serialiser.class)
@JsonDeserialize(using = MoMC_Deserialiser.class)
public class MoMC extends oo<MPD, FMap<String, CPD>>
{
    public MoMC(MPD $1, FMap<String, CPD> $2)
    {
        super($1, $2);
    }

    public MPD prior()
    {
        return $1;
    }

    public FMap<String, CPD> transitions_per_tag()
    {
        return $2;
    }

    public CPD transitions_for(String tag)
    {
        return transitions_per_tag().get(tag);
    }

    public FSet<String> tags() {return prior().as_map().keys();}
}
