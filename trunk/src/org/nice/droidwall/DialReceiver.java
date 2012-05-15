package org.nice.droidwall;

import java.util.List;



import android.R.string;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class DialReceiver extends BroadcastReceiver {
	private final String TAG = "NetWall";
	private final String PHONE_STATE_ACTION = "android.intent.action.PHONE_STATE";

	private List<String> numList;
	
	public DialReceiver(List<String> numList) {
		this.numList = numList;
		Log.d("NetWall", "DialReceiver create");
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Intent i = new Intent(context, PhoneStateListener.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(i);
		
		/**Log.d("NetWall", "onReceive create");
		String action = intent.getAction();
		System.out.println("��ǰaction" + action);
		Log.d(TAG, "[Broadcast]"+ action);
		if (action.equals(PHONE_STATE_ACTION))
		{
			doReceivePhone(context, intent);
		}
		/**
		mPhoneCallListener phoneCallListener = new mPhoneCallListener(context);
		TelephonyManager telManager = 
			(TelephonyManager) context.getSystemService(android.content.Context.TELEPHONY_SERVICE);
		
		telManager.listen(phoneCallListener, mPhoneCallListener.LISTEN_CALL_STATE);
		*/
	}

	
	private void doReceivePhone(Context context, Intent intent)
	{
		String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
		int state = telManager.getCallState();
		switch (state)
		{
		case TelephonyManager.CALL_STATE_RINGING:
			Log.d(TAG, "�ȴ�����绰" + phoneNumber);
			break;
		case TelephonyManager.CALL_STATE_IDLE:
			Log.d(TAG, "�ѹҶϵ绰" + phoneNumber);
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Log.d(TAG, "ͨ����"+ phoneNumber);
			break;
		default:
			break;
		}
	}


	public class mPhoneCallListener extends PhoneStateListener
	{
		private Context context;
		public mPhoneCallListener(Context context)
		{
			this.context = context;
		}
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber)
		{
			switch (state)
			{
			case TelephonyManager.CALL_STATE_IDLE:
				//�ֻ�Ϊ����״̬
				//info.setText("���ڴ���״̬");
				try
				{
					AudioManager audioManager = 
						(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
					if ( audioManager != null )
					{
						//�����ֻ�Ϊ����ʱ������ģʽ����
						audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						audioManager.getStreamVolume(AudioManager.RINGER_MODE_NORMAL);
					}
				}
				catch (Exception e)
				{
					//info.setText(e.toString());
					e.printStackTrace();
				}
				break;
			
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//�ֻ�Ϊͨ��״̬ʱ
				//info.setText("ͨ����");
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				Log.d("NetWall", "����" + incomingNumber +"����");
				//info.setText("����" + incomingNumber);
				//Log.d("NetWall", "phonetext:" + phonetext.getText().toString());
				for (String num : numList)
				{
					if (incomingNumber.equals(num))
					{
						try
						{
							AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
							if (audioManager != null)
							{
								//��������ģʽΪ����
								audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
								audioManager.getStreamVolume(AudioManager.STREAM_RING);
								//Toast.makeText(, incomingNumber + "������", Toast.LENGTH_LONG).show();
							}
							break;//end of for
						}
						catch (Exception e)
						{
							//info.setText(e.toString());
							e.printStackTrace();
							break;
						}
					}
				}
				
				break;
				
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}
	
}
