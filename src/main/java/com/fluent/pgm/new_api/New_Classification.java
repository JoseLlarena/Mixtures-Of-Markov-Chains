package com.fluent.pgm.new_api;

import com.fluent.math.*;

public class New_Classification extends New_Inference
{
    public static final New_Classification New_Classification = new New_Classification();

    public String classify(Seqence sequence, MoMC model)
    {
        return joint(sequence, model).max_as((String the_class, P posterior) -> posterior).$1;
    }

    //untested
    public MPX posterior_pdf(Seqence sequence, MoMC model)
    {
        return MPX.from(posterior_density(sequence, model));
    }
}
