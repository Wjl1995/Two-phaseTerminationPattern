package pers.ThreadTermination;

public abstract class AbstractTerminatableThread extends Thread implements Termintable{
	
	public final TerminationToken terminationToken;
	
	public AbstractTerminatableThread()
	{
		this(new TerminationToken());
	}
	
	/**
	 * @param terminationToken �̼߳乲����߳���ֹ��־ʵ��
	 */
	public AbstractTerminatableThread(TerminationToken terminationToken) {
		// TODO Auto-generated constructor stub
		super();
		this.terminationToken = terminationToken;
		terminationToken.register(this);
	}
	
	/**
	 * ��������ʵ�����̴߳����߼���
	 * @throws Exception
	 */
	protected abstract void doRun() throws Exception;
	
	/**
	 * ��������ʵ�֡�����ʵ���߳�ֹͣ���һЩ��������
	 * @param cause
	 */
	protected void doCleanup(Exception cause)
	{
	}
	
	/**
	 * ��������ʵ�֡�����ʵ���߳�ֹͣ����Ĳ�����
	 * �����̴߳���ĳЩ�ض�״�����޷���Ӧinterrupt����ʱ��������ִ�е�һЩ������
	 * �磺�̰߳���Socket I/O,������ڴ˷�����ʵ�ֹر�Socket
	 */
	protected void doTerminate()
	{
	}
	
	/**
	 * �߳�ִ�з�����������ִ�з�����doRun()�����С�
	 * ���ܣ����ϴӾ�������ȡ��������Ϣ�����͡�
	 * ֹͣ������ 1��ִ����terminate������
	 * 			 �߳�ֹͣ��־Ϊtrue��׼���׶Σ���
	 * 		             �Ҿ�����Ϣ������ϣ�terminationToken.reservation����Ϊ0��ִ�н׶Σ���
	 *        2��ִ�����̵߳�super.interrupt()������
	 */
	public void run()
	{
		System.out.println("�ѽ����߳�.");
		Exception ex = null;
		try{
			for(; ;)
			{
				// ��ִ���̵߳Ĵ����߼�ǰ�����ж��߳�ֹͣ�ı�־��
				if (terminationToken.isToShutdown() && terminationToken.reservation.get() <= 0)
					break;
				doRun();
			}
		}
		catch (Exception e)
		{
			// ʹ�߳��ܹ���Ӧinterrupt���ö��˳�
			ex = e;
		}
		finally
		{
			System.err.println("������������ɣ�ִ��ֹͣ.");
			try {
				doCleanup(ex);
			}
			finally
			{
				terminationToken.notifyThreadTermination(this);
			}
		}
		
	}
	
	public void interrupt()
	{
		terminate();
	}
	
	/**
	 * ��ֹ�̵߳ķ�����
	 * ���ȴ���doTerminate()����������ر��߳�֮ǰ�ı�Ҫ����;
	 * Ȼ���ж��Ƿ��д����������terminationToken?���У�������������񣻷���ֱ��ִ��interrupt������ֹ�̡߳�
	 */
	public void terminate()
	{
		terminationToken.setToShutdown(true);
		try{
			doTerminate();
		}
		finally
		{
			// ���޴��������������ͼǿ����ֹ�߳�
			if (terminationToken.reservation.get() <= 0)
				super.interrupt();
		}
	}
	
	public void terminate(boolean waitUtilThreadTerminated)
	{
		terminate();
		if (waitUtilThreadTerminated)
		{
			try{
				this.join();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	}
	
}
