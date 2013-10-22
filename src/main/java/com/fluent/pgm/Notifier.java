package com.fluent.pgm;

public interface Notifier
{
	public static Notifier NO_OP = new Notifier()
	{

		public void notifyOf(final Object message)
		{}

		public void notifyOfEnd()
		{}

		public void notifyOfStart()
		{}
	};

	public void notifyOf(Object message);

	public void notifyOfEnd();

	public void notifyOfStart();

}
