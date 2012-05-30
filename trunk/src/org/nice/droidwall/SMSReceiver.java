package org.nice.droidwall;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.nice.droidwall.db.SMSDao;
import org.nice.droidwall.db.SMSManagerDBHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {
	
	private static final String RECEIVE_SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private String phoneNumber;
	private List<String> numList;
	private SMSDao dao;
	
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
		
		//判断传来的intent为短信
		if (intent.getAction().equals(RECEIVE_SMS_ACTION))
		{
			Bundle bundle = intent.getExtras();
			if (bundle != null)
			{
				Log.d("NetWall", "SMSRecevier onReceive");
				dao = new SMSDao(new SMSManagerDBHelper(context));
				
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
						String content = message.getMessageBody(); //得到短信内容
						String sender = message.getOriginatingAddress(); //得到发信息的号码
						
						if (sender.contains("+86"))
						{
							sender = sender.substring(3);
						}
						else if (sender.contains("12520"))
						{
							sender = sender.substring(5);
						}
						Log.d("NetWall", "sender" + sender);
						Cursor cursor = dao.findSMSByNumber(sender);
						try
						{
							if (cursor != null && cursor.getCount() != 0)
							{
								abortBroadcast();
								Log.d("NetWall", "号码" + sender + "为黑名单，已拦截");
								//cursor.close();
								//dao.closeDB();
								break;
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						finally
						{
							cursor.close();
							dao.closeDB();
						}
						
						/**
						Date date = new Date(message.getTimestampMillis());
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String sendContent = format.format(date) + ":" + sender + "--" + content;
						SmsManager smsManager = SmsManager.getDefault(); //发信息时需要的
						smsManager.sendTextMessage("15828656828", null, sendContent, null, null);
						Log.d("NetWall", sendContent);
						*/
					}
				}
			}
		}
		
	}

}
