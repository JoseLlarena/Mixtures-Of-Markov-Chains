package com.fluent.pgm;

import com.fluent.collections.Sets;
import com.fluent.math.*;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Before;
import org.junit.Test;

import static com.fluent.collections.Sets.newFSet;
import static com.fluent.core.Words.by;
import static com.fluent.math.P.*;
import static com.fluent.pgm.CPDBuilder.CPD_with_OOV;
import static com.fluent.pgm.Sequence.*;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CPTWithOOVSpec extends AbstractSpec
{
	CPD cpt;

	static final String A = "A", B = "B";
	static final P PI_A = P(.50), P_AA = P(.45), P_BA = P(.40), P_OOV_A = P(.10), P_END_A = P(.05);
	static final P PI_B = P(.49), P_AB = P(.35), P_BB = P(.30), P_OOV_B = P(.20), P_END_B = P(.15);
	static final P PI_OOV = P(.01);

	@Before public void BACKGROUND()
	{
		assertThat(PI_A.add(PI_B).add(PI_OOV), is(ONE));
		assertThat(P_AA.add(P_BA).add(P_OOV_A).add(P_END_A), is(ONE));
		assertThat(P_AB.add(P_BB).add(P_OOV_B).add(P_END_B).toDouble(), is(closeTo(1, by(.000000000000001))));

		//[
		GIVEN:cpt = CPD_with_OOV()
				.p(A, PI_A).p(A, A, P_AA).p(B, A, P_BA).p(OOV, A, P_OOV_A).p(END_STATE, A, P_END_A)
				.p(B, PI_B).p(A, B, P_AB).p(B, B, P_BB).p(OOV, B, P_OOV_B).p(END_STATE, B, P_END_B)
				.p(OOV, PI_OOV).p(A, OOV, ZERO).p(B, OOV, ZERO).p(OOV, OOV, ZERO).and(END_STATE, OOV, ONE);//]
	}

	@Test public void knows_its_contexts() throws Exception
	{
		So(cpt.contexts()).shouldBe(Sets.asFSet(OOV, START_STATE, A, B));
	}

	@Test public void knows_its_tokens() throws Exception
	{
		So(cpt.tokens()).shouldBe(Sets.asFSet(OOV, END_STATE, A, B));
	}

	@Test public void knows_probability_of_a_normal_token_after_end_is_zero() throws Exception
	{
		So(cpt.p(A, END_STATE)).shouldBe(ZERO);
	}

	@Test public void knows_probability_of_end_after_end_is_zero() throws Exception
	{
		So(cpt.p(END_STATE, END_STATE)).shouldBe(ZERO);
	}

	@Test public void knows_probability_of_start_after_start_is_zero() throws Exception
	{
		So(cpt.p(START_STATE, START_STATE)).shouldBe(ZERO);
	}

	@Test public void knows_seen_conditional_probability() throws Exception
	{
		So(cpt.p(A, B)).shouldBe(P_AB);
	}

	@Test public void should_know_probability_of_a_seen_token_given_an_unseen_condition_is_the_same_as_the_token_given_oov()
			throws Exception
	{
		So(cpt.p(A, "X")).shouldBe(ZERO);
	}

	@Test public void should_know_probability_of_an_unseen_token_given_a_seen_condition_is_the_same_as_oov() throws Exception
	{
		So(cpt.p("X", A)).shouldBe(P_OOV_A);
	}

	@Test public void should_know_probability_of_an_unseen_token_given_an_unseen_context_is_the_same_as_oov_given_oov() throws Exception
	{
		So(cpt.p("X", "Y")).shouldBe(ZERO);
	}

	@Test public void should_know_probability_of_end_after_start_is_0() throws Exception
	{
		So(cpt.p(END_STATE, START_STATE)).shouldBe(ZERO);
	}

	@Test public void should_know_probability_of_start_after_a_normal_token_is_zero() throws Exception
	{
		So(cpt.p(START_STATE, A)).shouldBe(ZERO);
	}

	@Test public void should_know_probability_of_start_after_end_is_zero() throws Exception
	{
		So(cpt.p(START_STATE, END_STATE)).shouldBe(ZERO);
	}

	@Test public void should_know_seen_prior_probability() throws Exception
	{
		So(cpt.p(A, START_STATE)).shouldBe(PI_A);
	}
}
