package com.fluent.pgm.new_api;

import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import static com.fluent.collections.Lists.asFList;

public class New_Sequence_Spec extends AbstractSpec
{
    @Test
    public void knows_its_ngrams() throws Exception
    {
        Seqence.Ngram ngram_1 = new Seqence.Ngram(Token.START, Token.from("A"));
        Seqence.Ngram ngram_2 = new Seqence.Ngram(Token.from("A"), Token.END);

        THEN(Seqence.from("A").ngrams()).shouldBe(asFList(ngram_1, ngram_2));
    }


}
