package pers.ThreadTermination;

import pers.ThreadAlarm.AlarmInfo;
import pers.ThreadAlarm.AlarmType;

/**
 * ������AlarmMgr
 * Ϊ�߳�ӵ���ߡ������ṩshutdown()������ִ���߳�ֹͣ������
 * @author WJL
 *
 */
public class AlarmMgr {

	private static final AlarmMgr instance = new AlarmMgr();
	
	private volatile boolean shutdownRequested = false;
	
	// Ŀ���̡߳�����Ϊ˽�����ԣ�����¶���ⲿ����ֹ������ֹͣ��
	private final AlarmSendingThread alarmSendingThread;
	
	private AlarmMgr()
	{
		alarmSendingThread = new AlarmSendingThread();
	}
	
	public static AlarmMgr getInstance()
	{
		return instance;
	}
	
	public void init()
	{
		alarmSendingThread.start();
	}
	
	/**
	 * ���ܣ����;���
	 * @param type
	 * @param id
	 * @param extraInfo
	 * @return ��type+id+extraInfoΨһȷ���ľ�����Ϣ���ύ�Ĵ�����-1��ʾ�����������Ѿ����ر�
	 */
	public int sentAlarm(AlarmType type, String id, String extraInfo)
	{
		// System.err.println("Trigger alarm "+type+","+id+","+extraInfo);
		int duplicateSubmissionCount = 0;
		try{
			AlarmInfo alarmInfo = new AlarmInfo(id, type);
			alarmInfo.setExtraInfo(extraInfo);
			duplicateSubmissionCount = alarmSendingThread.sendAlarm(alarmInfo);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		return duplicateSubmissionCount;
	}
	
	public synchronized void shundowm()
	{
		System.err.println("�߳�׼��ֹͣ.");
		if (shutdownRequested)
		{
			throw new IllegalStateException("shutdown already requested!");
		}
		alarmSendingThread.terminate();
		shutdownRequested = true;
	}
}
