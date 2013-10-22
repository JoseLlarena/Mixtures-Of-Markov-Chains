package com.fluent.pgm.new_api;

import com.fluent.collections.FList;

public interface New_Initialisation
{
    public default MoMC initialise_with(FList<Seqence> data)
    {
        return Common.example_initial_model();
    }

}
