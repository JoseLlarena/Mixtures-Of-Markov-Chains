package com.fluent.pgm.mixtures;

import com.fluent.specs.unit.AbstractSpec;
import org.junit.Test;

import static com.fluent.collections.Lists.asFList;
import static com.fluent.pgm.mixtures.Sequence.Ngram;
import static com.fluent.pgm.mixtures.Token.END;
import static com.fluent.pgm.mixtures.Token.START;

public class Sequence_Spec extends AbstractSpec
{
    @Test
    public void knows_its_ngrams() throws Exception
    {
        Ngram ngram_1 = Ngram.from(START, Token.from("A"));
        Ngram ngram_2 = Ngram.from(Token.from("A"), END);

        So(Sequence.from_chars("A").ngrams()).shouldBe(asFList(ngram_1, ngram_2));
    }

    @Test
    public void knwows_its_size() throws Exception
    {
        So(Sequence.from_chars("A").size()).shouldBe(3);
    }

    @Test
    public void knwows_its_tokens_indeces() throws Exception
    {
        So(Sequence.from_chars("A").at(1)).shouldBe(Token.from("A"));
    }

    @Test
    public void builds_consistently() throws Exception
    {
        So(Sequence.from_chars("A")).shouldBe(Sequence.from(asFList(Token.from("A"))));
    }

}
