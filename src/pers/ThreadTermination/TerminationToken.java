package pers.ThreadTermination;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ������TerminationToken
 * ���ܣ�toShutdown�������ڱ�־׼���׶α�־����
 * 	   reservation�������ڱ�־���ж�������������δ��ɡ�
 * @author WJL
 *
 */
public class TerminationToken {

	// ʹ��volatile���Σ��Ա�֤������ʾ��������¸ñ������ڴ�ɼ���
	protected volatile boolean toShutdown = false;
	public final AtomicInteger reservation = new AtomicInteger(0);
	
	/*
	 * �ڶ����ֹͣ�߳�ʵ������һ��TerminationTokenʵ��������£��ö������ڼ�¼��Щ����TerminationTokenʵ��
	 * �Ŀ�ֹͣ�̣߳��Ա㾡���ܼ�������ʹ�õ�����£�ʵ����Щ�̵߳�ֹͣ��
	 */
	private final Queue<WeakReference<Termintable>> coordinatedThreads;
	
	public TerminationToken()
	
	{
		coordinatedThreads = new ConcurrentLinkedQueue<WeakReference<Termintable>>();
	}
	
	public boolean isToShutdown()
	{
		return toShutdown;
	}
	
	protected void setToShutdown(boolean toShutdown)
	{
		this.toShutdown = toShutdown;
	}
	
	protected void register(Termintable thread)
	{
		coordinatedThreads.add(new WeakReference<Termintable>(thread));
	}
	
	/*
	 * ֪ͨTerminationTokenʵ���������ʵ�������п�ֹͣ�߳��е�һ���߳�ֹͣ�ˣ��Ա���ֹͣ����δ��ֹͣ���̡߳�
	 * @param thread ��ֹͣ���߳�
	 */
	protected void notifyThreadTermination(Termintable thread)
	{
		WeakReference<Termintable> wrThread;
		Termintable otherThread;
		while(null != (wrThread = coordinatedThreads.poll()))
		{
			otherThread = wrThread.get();
			if (null != otherThread && otherThread != thread)
				otherThread.terminate();
		}
	}
}
