package com.fluent.pgm.new_api;

import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.pgm.new_api.Seqence.N_gram;

public class New_Sequence_Spec extends AbstractSpec
{
    @Test
    public void knows_its_ngrams() throws Exception
    {
        N_gram n_gram_1 = new N_gram(Token.START, Token.from("A"));
        N_gram n_gram_2 = new N_gram(Token.from("A"), Token.END);

        THEN(Seqence.from("A").n_grams()).shouldBe(asFList(n_gram_1, n_gram_2));
    }


}
