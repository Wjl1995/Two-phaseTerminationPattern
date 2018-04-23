package pers.ThreadAlarm;

/**
 * @author WJL
 * GuardedAction������
 */

import java.util.concurrent.Callable;

public abstract class GuardedAction<V> implements Callable<V> {

	protected final Predicate guard;
	
	public GuardedAction(Predicate guard)
	{
		this.guard = guard;
	}
}
