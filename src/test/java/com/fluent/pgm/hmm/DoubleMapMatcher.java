package com.fluent.pgm.hmm;

import java.util.Map;
import java.util.Map.Entry;

import static java.lang.Math.abs;

import static java.lang.String.format;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class DoubleMapMatcher<KEY> extends TypeSafeDiagnosingMatcher<Map<KEY, Double>>
{
	private final Map<KEY, Double> expectedAnswer;
	private final double threshold;

	DoubleMapMatcher(final Map<KEY, Double> expectedAnswer, final double threshold)
	{
		this.expectedAnswer = expectedAnswer;
		this.threshold = threshold;
	}

	public void describeTo(final Description description)
	{
		description.appendText(format("%nmap badfrom probabilities badfrom roughly %n%s", expectedAnswer));
	}

	protected boolean matchesSafely(final Map<KEY, Double> actualAnswer, final Description description)
	{
		try
		{
			for (final Entry<KEY, Double> expected : expectedAnswer.entrySet())
			{
				final Double actual = actualAnswer.get(expected.getKey());

				if (actual == null)
				{
					description.appendText(format("%ncouldnt find double value for key [%s] in %s.", expected.getKey(), actualAnswer));
					return false;
				}

				if (abs(actual - expected.getValue()) > threshold)
				{
					description.appendText(format("%nthis entry    %s:%s   went over the threshold (%s) in %s.", expected.getKey(), actual,
							threshold, actualAnswer));
					return false;
				}

			}

			return true;
		}
		catch (final Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}

	public static <KEY> DoubleMapMatcher<KEY> roughly(final Map<KEY, Double> expectedAnswer, final double threshold)
	{
		return new DoubleMapMatcher<KEY>(expectedAnswer, threshold);
	}
}