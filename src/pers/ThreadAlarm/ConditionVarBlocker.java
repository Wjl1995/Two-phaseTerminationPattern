package pers.ThreadAlarm;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionVarBlocker implements Blocker {

	private final Lock lock;
	private final Condition condition;
	
	public ConditionVarBlocker(Lock lock)
	{
		this.lock = lock;
		this.condition = lock.newCondition();
	}
	
	public ConditionVarBlocker()
	{
		this.lock = new ReentrantLock();
		this.condition = lock.newCondition();
	}
	
	@Override
	public <V> V callWithGuard(GuardedAction<V> guardedAction) throws Exception {
		// TODO Auto-generated method stub
		lock.lockInterruptibly();
		V result;
		try{
			final Predicate guard = guardedAction.guard;
			// 使用while而不是if，是为了防止欺骗性唤醒
			while (!guard.evaluate())
				condition.await();
			result = guardedAction.call();
			return result;
		}
		finally{
			lock.unlock();
		}
	}

	@Override
	public void signalAfter(Callable<Boolean> stateOperation) throws Exception {
		// TODO Auto-generated method stub
		lock.lockInterruptibly();
		try{
			if (stateOperation.call())
				condition.signal();
		}
		finally{
			lock.unlock();
		}
	}

	@Override
	public void signal() throws InterruptedException {
		// TODO Auto-generated method stub
		lock.lockInterruptibly();
		try{
			condition.signal();
		}
		finally{
			lock.unlock();
		}
	}

	@Override
	public void broadcastAfter(Callable<Boolean> stateOperation) throws Exception {
		// TODO Auto-generated method stub
		lock.lockInterruptibly();
		try{
			if (stateOperation.call())
				condition.signalAll();
		}
		finally{
			lock.unlock();
		}
	}
}
