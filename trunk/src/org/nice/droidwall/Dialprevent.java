package org.nice.droidwall;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Dialprevent extends Activity implements TextWatcher{
	private TextView info;
	private EditText number;
	private TextView phonetext;
	private Button add_tel;
	private Button show_tel;
	
	 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dial);
		Log.d("NetWall", "程序已跳转到Dial_prevent");
		
		info = (TextView)findViewById(R.id.info);
		number = (EditText)findViewById(R.id.telphone);
		phonetext = (TextView)findViewById(R.id.tel);
		add_tel = (Button)findViewById(R.id.add_tel);
		show_tel = (Button)findViewById(R.id.show_tel);
		
		number.addTextChangedListener(this);
		add_tel.setOnClickListener(new MyOnClickListener());
		/**
		number.setOnKeyListener(new EditText.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				phonetext.setText(number.getText().toString());
				return false;
			}
		});*/
		
		
		mPhoneCallListener phoneCallListener = new mPhoneCallListener();
		TelephonyManager telManager = 
			(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		telManager.listen(phoneCallListener, mPhoneCallListener.LISTEN_CALL_STATE);
	}
	

	
	
	public class mPhoneCallListener extends PhoneStateListener
	{
		@Override
		public void onCallStateChanged(int state, String incomingNumber)
		{
			switch (state)
			{
			case TelephonyManager.CALL_STATE_IDLE:
				//手机为待机状态
				info.setText("处于待机状态");
				try
				{
					AudioManager audioManager = 
						(AudioManager) getSystemService(Context.AUDIO_SERVICE);
					if ( audioManager != null )
					{
						//设置手机为待机时，响铃模式正常
						audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						audioManager.getStreamVolume(AudioManager.RINGER_MODE_NORMAL);
					}
				}
				catch (Exception e)
				{
					info.setText(e.toString());
					e.printStackTrace();
				}
				break;
			
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//手机为通话状态时
				info.setText("通话中");
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				Log.d("NetWall", incomingNumber);
				info.setText("来电" + incomingNumber);
				Log.d("NetWall", "phonetext:" + phonetext.getText().toString());
				if (incomingNumber.equals(phonetext.getText().toString()))
				{
					try
					{
						AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						if (audioManager != null)
						{
							//设置响铃模式为静音
							audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
							audioManager.getStreamVolume(AudioManager.STREAM_RING);
							Toast.makeText(Dialprevent.this, incomingNumber + "已屏蔽", Toast.LENGTH_LONG).show();
						}
					}
					catch (Exception e)
					{
						info.setText(e.toString());
						e.printStackTrace();
						break;
					}
				}
				break;
				
			default:
				break;
			}
			
			super.onCallStateChanged(state, incomingNumber);
			
			
		}
	}

	public class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.add_tel:
				
				break;
			case R.id.show_tel:
				
				break;
			default:
				break;
			}
		}
		
	}

	@Override
	public void afterTextChanged(Editable s)
	{
		// TODO Auto-generated method stub
		
	}




	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after)
	{
		// TODO Auto-generated method stub
		
	}




	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		phonetext.setText(number.getText());
	}
}
