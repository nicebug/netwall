package org.nice.droidwall;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {
	
	private static final String RECEIVE_SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private String phoneNumber;
	private List<String> numList;
	
	public SMSReceiver() {
		Log.d("NetWall", "SMSRecevier create");
	}
	
	public SMSReceiver(String phoneNumber){
		
		Log.d("NetWall", "SMSRecevier String create");
		this.phoneNumber = phoneNumber;
	}
	
	public SMSReceiver(List<String> numList)
	{
		Log.d("NetWall", "SMSRecevier List create");
		this.numList = numList;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		//�жϴ�����intentΪ����
		if (intent.getAction().equals(RECEIVE_SMS_ACTION))
		{
			Bundle bundle = intent.getExtras();
			if (bundle != null)
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
						for (String num : numList)
						{
							Log.d("NetWall", "num:" + num);
							if (sender.equals(num))
							{
								abortBroadcast(); //��ָ����
								Log.d("NetWall", "�˺���Ϊ���������룬������");
								break;
							}
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
		
	}

}
