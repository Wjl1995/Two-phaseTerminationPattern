package pers.ThreadAlarm;

/*
��Ȩ������
��Դ��ϵ��Java���̱߳��ʵսָ�ϣ����ģʽƪ����һ�飨ISBN��978-7-121-27006-2�����³�֮Ϊ��ԭ�顱��������Դ�룬
���˽Ȿ����ĸ���ϸ�ڣ���ο�ԭ�顣
�������Ϊԭ�������˵��֮�ã����������κγ�ŵ����������֤�����棩��
���κ���ʽ��������֮���ֻ���ȫ������Ӫ������;�辭��Ȩ������ͬ�⡣
��������֮���ֻ���ȫ�����ڷ�Ӫ������;��Ҫ�ڴ����б�����������
�κζԱ�������޸����ڴ�������ע�͵���ʽע���޸��ˡ��޸�ʱ���Լ��޸����ݡ�
��������Դ�������ַ���أ�
https://github.com/Viscent/javamtp
http://www.broadview.com.cn/27006
*/

public enum AlarmType {
	FAULT("fault"),
	RESUME("resume");
	
	private final String name;
	private AlarmType(String name){
		this.name=name;
	}
	
	public String toString(){
		
		return name;
	}
}
