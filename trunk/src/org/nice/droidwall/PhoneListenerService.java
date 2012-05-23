package org.nice.droidwall;

import java.lang.reflect.Method;

import org.nice.droidwall.db.TelphoneDao;
import org.nice.droidwall.db.TelphoneMangerDBHelper;

import com.android.internal.telephony.ITelephony;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PhoneListenerService extends Service {

	final String TAG = "NetWall";
	private TelphoneDao dao;
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		Log.d(TAG, "onBind");
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d(TAG, "service create");
		dao = new TelphoneDao(new TelphoneMangerDBHelper(this));
		
		//�绰�������
		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	private PhoneStateListener listener = new PhoneStateListener(){
		
		public void onCallStateChanged(int state, String incomingNumber) 
		{
			super.onCallStateChanged(state, incomingNumber);
			Log.d(TAG, "onCallStateChanged state= " + state);
			
			switch (state)
			{
			case TelephonyManager.CALL_STATE_IDLE:
				Log.d(TAG, "no incoming call");
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				Log.d(TAG, "call incoming");
				Cursor cursor = dao.findTelphone(incomingNumber);
				if (cursor != null && cursor.getCount() != 0)
				{
					stop(incomingNumber);
				}
				cursor.close();
				dao.closeDB();
				break;
				
			default:
				break;
			}
		}

		
	};
	
	/**
	 * ����java�ķ�����ƽ��е绰�ĹҶϴ���
	 * @param incomingNumber
	 */
	private void stop(String incomingNumber)
	{
		Log.d(TAG, "����" + incomingNumber +"Ϊ������");
		try
		{
			 Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
			 IBinder binder = (IBinder) method.invoke(null, new Object[] { TELEPHONY_SERVICE });
			 //��ȡ�󶨽ӿ�
			 ITelephony telephony = ITelephony.Stub.asInterface(binder);
			 //���÷����е�endCall����
			 telephony.endCall();
		}
		catch (Exception e)
		{
			Log.d(TAG, "method stop exception");
			e.printStackTrace();
		}
	}

}
