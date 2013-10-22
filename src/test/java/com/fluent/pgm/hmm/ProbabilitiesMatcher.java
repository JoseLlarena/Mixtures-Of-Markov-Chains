package com.fluent.pgm.hmm;

import java.util.Collection;

import static java.lang.Math.abs;

import static java.lang.String.format;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.fluent.math.P;

public class ProbabilitiesMatcher extends TypeSafeDiagnosingMatcher<Collection<P>>
{
	private final Collection<P> expectedAnswer;
	private final double threshold = 1 / 10e14;

	ProbabilitiesMatcher(final Collection<P> expectedAnswer)
	{
		this.expectedAnswer = expectedAnswer;
	}

	public void describeTo(final Description description)
	{
		description.appendText(format("collection badfrom probabilities badfrom roughly %n%s", expectedAnswer));
	}

	protected boolean matchesSafely(final Collection<P> actualAnswer, final Description description)
	{
		try
		{
			for (final P actual : actualAnswer)
			{
				for (final P expected : expectedAnswer)
				{
					if (abs(actual.asLog() - expected.asLog()) > threshold)
					{
						description.appendText(format("but this [%s] went over the threshold (%s).%n", actual, threshold));
						return false;
					}
				}
			}

			return true;
		}
		catch (final Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}
}