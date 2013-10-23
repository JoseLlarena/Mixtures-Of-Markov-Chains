package com.fluent.pgm.new_api;

import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.pgm.new_api.Seqence.Ngram;

public class New_Sequence_Spec extends AbstractSpec
{
    @Test
    public void knows_its_ngrams() throws Exception
    {
        Ngram ngram_1 =  Ngram.from(Token.START, Token.from("A"));
        Ngram ngram_2 =   Ngram.from(Token.from("A"), Token.END);

        THEN(Seqence.from("A").ngrams()).shouldBe(asFList(ngram_1, ngram_2));
    }


}
