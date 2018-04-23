package pers.ThreadTermination;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import pers.ThreadAlarm.AlarmAgent;
import pers.ThreadAlarm.AlarmInfo;
import pers.ThreadAlarm.AlarmType;

/**
 * ������AlarmSendingThread
 * Ϊ����Ŀ���߳�ConcreteTerminatableThread���ɱ��رա�
 * @author WJL
 *
 */
public class AlarmSendingThread extends AbstractTerminatableThread {

	private final AlarmAgent alarmAgent = new AlarmAgent();
	
	// �������
	private final BlockingQueue<AlarmInfo> alarmQueue;
	private final ConcurrentMap<String, AtomicInteger> submittedAlarmRegistry;
	
	public AlarmSendingThread()
	{
		alarmQueue = new ArrayBlockingQueue<AlarmInfo>(100);
		submittedAlarmRegistry = new ConcurrentHashMap<String, AtomicInteger>();
		alarmAgent.init();
	}
	
	@Override
	protected void doRun() throws Exception {
		// TODO Auto-generated method stub
		AlarmInfo alarm;
		alarm = alarmQueue.take();
		terminationToken.reservation.decrementAndGet();
		try{
			// ��������Ϣ����������������
			alarmAgent.sendAlarm(alarm);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		/*
		 * ����ָ�����������Ӧ�Ĺ��ϴ�ע�����ɾ����ʹ����Ӧ���ϻָ������ٴγ�����ͬ���ϣ��ù�����Ϣ�ܹ��ϱ�������
		 */
		if (AlarmType.RESUME == alarm.type)
		{
			String key = AlarmType.FAULT.toString()+":"+alarm.getId()+"@"+alarm.getExtraInfo();
			submittedAlarmRegistry.remove(key);
			
			key = AlarmType.RESUME.toString()+":"+alarm.getId()+"@"+alarm.getExtraInfo();
			submittedAlarmRegistry.remove(key);
		}
	}
	
	public int sendAlarm(final AlarmInfo alarmInfo)
	{
		AlarmType type = alarmInfo.type;
		String id = alarmInfo.getId();
		String extraInfo = alarmInfo.getExtraInfo();
		
		if (terminationToken.isToShutdown())
		{
			System.err.println("rejected alarm:"+id+","+extraInfo);
			return -1;
		}
		
		int duplicateSubmissionCount = 0;
		try{
			AtomicInteger prevSubmittedCounter;
			prevSubmittedCounter = submittedAlarmRegistry.putIfAbsent(type.toString()+":"+id+"@"+extraInfo,
					new AtomicInteger(0));
			if (null == prevSubmittedCounter)
			{
				terminationToken.reservation.incrementAndGet();
				alarmQueue.put(alarmInfo);
			}
			else
			{
				duplicateSubmissionCount = prevSubmittedCounter.incrementAndGet();
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		
		return duplicateSubmissionCount;
	}
	
	protected void doCleanup(Exception exp)
	{
		if (null != exp && !(exp instanceof InterruptedException))
		{
			exp.printStackTrace();
		}
		alarmAgent.disconnect();
	}

}
