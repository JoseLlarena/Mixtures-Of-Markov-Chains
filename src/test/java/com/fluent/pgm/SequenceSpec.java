package com.fluent.pgm;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.core.oo.oo;
import static com.fluent.pgm.Sequence.END;
import static com.fluent.pgm.Sequence.START;

import com.fluent.collections.Lists;
import org.junit.Test;

import com.fluent.specs.unit.AbstractSpec;

public class SequenceSpec extends AbstractSpec
{
	Sequence ab = new Sequence("ab");

	@Test(expected = ArrayIndexOutOfBoundsException.class) public void complains_if_asked_for_character_at_T() throws Exception
	{
		ab.at(ab.T());
	}

	@Test public void equals_to_a_sequence_constructed_with_same_string() throws Exception
	{
		So(ab).shouldBe(new Sequence("ab"));
	}

	@Test public void knows_its_bigrams() throws Exception
	{
		So(ab.bigrams()).shouldBe(Lists.asFList(oo(START, "a"), oo("a", "b"), oo("b", END)));
	}

	@Test public void knows_its_terms() throws Exception
	{
		So(ab.terms()).shouldBe(Lists.asFList("a", "b"));
	}

	@Test public void knows_its_size() throws Exception
	{
		So(ab.T()).shouldBe(4);
	}

	@Test public void not_equal_to_a_sequence_constructed_with_a_different_string() throws Exception
	{
		So(ab).shouldNotBe(new Sequence("bb"));
	}

	@Test public void returns_END_if_asked_for_character_at_T_minus_1() throws Exception
	{
		So(ab.at(ab.T() - 1)).shouldBe(END);
	}

	@Test public void returns_first_given_character_as_string_if_asked_for_character_at_1() throws Exception
	{
		So(ab.at(1)).shouldBe("a");
	}

	@Test public void returns_START_if_asked_for_character_at_0() throws Exception
	{
		So(ab.at(0)).shouldBe(START);
	}

	@Test public void shows_given_characters_as_string() throws Exception
	{
		So(ab.toString()).shouldBe("ab");
	}
}
