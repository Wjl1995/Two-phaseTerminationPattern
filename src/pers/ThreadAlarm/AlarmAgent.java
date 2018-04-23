package pers.ThreadAlarm;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

/**
 * @author WJL
 * GuardedObject����
 * GuardedMethod���������� sendAlarm����
 * Predicate����������AlarmAgentʵ���뱨���������Ѿ������������ӣ���connectedToServer��ֵΪtrue
 */
public class AlarmAgent {
	
	private volatile boolean connectedToServer = false;
	
	private final Predicate agentConnected = new Predicate()
	{
		public boolean evaluate(){
			return connectedToServer;
		}
	};
	
	private final Blocker blocker = new ConditionVarBlocker();
	
	// ������ʱ��
	private final Timer heartbeatTimer = new Timer(true);
	
	/**
	 * GuardedMethod���������� sendAlarm����
	 * @param alarm ������Ϣ
	 * @throws Exception
	 */
	public void sendAlarm(final AlarmInfo alarm) throws Exception
	{
		GuardedAction<Void> guardedAction = new GuardedAction<Void>(agentConnected)
		{
			public Void call() throws Exception{
				doSendAlarm(alarm);
				return null;
			}
		};
		blocker.callWithGuard(guardedAction);
	}
	private void doSendAlarm(AlarmInfo alarm) {
		// TODO Auto-generated method stub
		System.out.println("sending alarm"+alarm);
		
		// ģ�ⷢ�;��������������ĺ�ʱ
		try{
			Thread.sleep(1000);
		}
		catch (Exception e){
		}
	}
	
	public void init()
	{
		Thread connectingThread = new Thread(new ConnectingTask());
		connectingThread.start();
		heartbeatTimer.schedule(new HeartbeatTask(), 60000, 2000);
	}
	/**
	 * �����뾯��������������������
	 */
	private class ConnectingTask implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// ģ�����Ӻ�ʱ
			try {
				Thread.sleep(100);
			}
			catch (Exception e){
			}
			onConnected();
		}
	}
	protected void onConnected() {
		// TODO Auto-generated method stub
		try{
			blocker.signalAfter(new Callable<Boolean>(){

				@Override
				public Boolean call() throws Exception {
					// TODO Auto-generated method stub
					connectedToServer = true;
					System.out.println("Connected to server.");
					return Boolean.TRUE;
				}
			});
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * ������ʱ���񣺶�ʱ����뱨���������������Ƿ����������������쳣���Զ���������
	 */
	private class HeartbeatTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!testConnection())
			{
				onDisconnected();
				reconnect();
			}
		}
	}
	protected void onDisconnected()
	{
		connectedToServer = false;
	}
	private boolean testConnection()
	{
		return true;
	}
	private void reconnect()
	{
		ConnectingTask connectingThread = new ConnectingTask();
		// ֱ����������ʱ���߳���ִ��
		connectingThread.run();
	}
	
	public void disconnect()
	{
		System.out.println("disconnected from alarm server.");
		connectedToServer = false;
	}
}
