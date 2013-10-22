package com.fluent.pgm.hmm;

import java.util.Map;
import java.util.Map.Entry;

import static java.lang.Math.abs;

import static java.lang.String.format;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.fluent.math.P;

public class ProbabilityMapMatcher<KEY> extends TypeSafeDiagnosingMatcher<Map<KEY, P>>
{
	private final Map<KEY, P> expectedAnswer;
	private final double threshold;

	ProbabilityMapMatcher(final Map<KEY, P> expectedAnswer, final double threshold)
	{
		this.expectedAnswer = expectedAnswer;
		this.threshold = threshold;
	}

	public void describeTo(final Description description)
	{
		description.appendText(format("%nmap badfrom probabilities badfrom roughly %n%s", expectedAnswer));
	}

	protected boolean matchesSafely(final Map<KEY, P> actualAnswer, final Description description)
	{
		try
		{
			for (final Entry<KEY, P> expected : expectedAnswer.entrySet())
			{
				final P value = actualAnswer.get(expected.getKey());

				if (value == null)
				{
					description.appendText(format("%ncouldnt find probability for key [%s] in %s.", expected.getKey(), actualAnswer));
					return false;
				}

				if (abs(value.asLog() - expected.getValue().asLog()) > threshold)
				{
					description.appendText(format("%nthis entry    %s:%s   went over the threshold (%s) in %s.", expected.getKey(), value,
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

	public static <KEY> ProbabilityMapMatcher<KEY> roughly(final Map<KEY, P> expectedAnswer, final double threshold)
	{
		return new ProbabilityMapMatcher<KEY>(expectedAnswer, threshold);
	}
}