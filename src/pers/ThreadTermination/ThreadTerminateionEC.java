package pers.ThreadTermination;

import pers.ThreadAlarm.AlarmType;

public class ThreadTerminateionEC {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AlarmMgr alarmMgr = AlarmMgr.getInstance();
		alarmMgr.init();
		int a1 = alarmMgr.sentAlarm(AlarmType.RESUME, "100001", "����1");
		int a2 = alarmMgr.sentAlarm(AlarmType.RESUME, "100002", "����2");
		int a3 = alarmMgr.sentAlarm(AlarmType.RESUME, "100003", "����3");
		alarmMgr.shundowm();
		alarmMgr.sentAlarm(AlarmType.RESUME, "100004", "����4");
	}

}
