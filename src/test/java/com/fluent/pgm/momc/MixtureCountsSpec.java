package com.fluent.pgm.momc;

import com.fluent.pgm.Sequence;
import com.fluent.pgm.WeightMap;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.oo;
import static com.fluent.core.ooo.ooo;
import static com.fluent.math.P.*;
import static com.fluent.pgm.Sequence.END;
import static com.fluent.pgm.Sequence.START;
import static com.fluent.pgm.hmm.DoubleMapMatcher.roughly;

@SuppressWarnings("unchecked")
public class MixtureCountsSpec extends AbstractSpec
{
	Sequence sequence = Sequence.from("A");

	@Test public void calculates_prior_counts() throws Exception
	{
		So(new MixtureCounts().add(P(.3), "tag", sequence).forPrior()).shouldBe(roughly((Map) newFMap(oo("tag", .3)), .000000000001));
	}

	@Ignore("Think what to do from trimap matching") @Test public void calculates_conditional_counts() throws Exception
	{
		So(new MixtureCounts().add(P(.3), "tag", sequence).forConditionals()).shouldBe(
				newFMap(oo("tag", new WeightMap(ooo(START, "A", .3), ooo("A", END, .3)))));
	}
}
