package org.nice.droidwall;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SMSPrevent extends Activity{
	
	private Button regisetBtn;
	private Button unregisetBtn;
	private EditText phoneNumber;
	
	private SMSReceiver receiver;
	private boolean isRegisted = false;
	private static final String RECEIVE_SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);
		
		regisetBtn = (Button)findViewById(R.id.register);
		unregisetBtn = (Button)findViewById(R.id.unregister);
		phoneNumber = (EditText)findViewById(R.id.phonenumber);
		
		
		receiver = new SMSReceiver(phoneNumber.getText().toString());
		regisetBtn.setOnClickListener(new SMSOnClickListener());
		unregisetBtn.setOnClickListener(new SMSOnClickListener());
		
	}
	
	public class SMSOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.register:
				regiset(v);
				break;
			case R.id.unregister:
				unregiset(v);
				break;
			default:
				break;
			}
		}
		
	}
	
	public void regiset(View v)
	{
		if (isPhoneNumberValid(phoneNumber.getText().toString()))
		{
			IntentFilter filter = new IntentFilter(RECEIVE_SMS_ACTION);
			filter.setPriority(1000);
			registerReceiver(receiver, filter);
			isRegisted = true;
			Toast.makeText(this, "ע��ɹ�", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(this, "�绰����Ƿ�", Toast.LENGTH_LONG).show();
		}
	}
	
	
	public void unregiset(View v)
	{
		if (receiver != null && isRegisted)
		{
			unregisterReceiver(receiver);
			isRegisted = false;
			Toast.makeText(this, "��ע��ɹ�", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(this, "δע��", Toast.LENGTH_LONG).show();
		}
	}
	
	public boolean isPhoneNumberValid(String phoneNumber)
	{
		return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
	}
	
	/**
	 * ���������ַ����Ƿ�Ϊ�绰����
	 */
	public static boolean isPhoneNumberValid2(String phoneNumber)
	{
		boolean isValid = false;
		/**
		 * �ɽ��ܵĵ绰��ʽ�У�
		 * ^\\(? :��ʹ��"("��ͷ
		 * (\\d{3}):��������������
		 * \\):��ʹ��")"����
		 * [- ]?:��������ʽ���ʹ�þ���ѡ���Ե� "-"
		 * (\\d{5})$:��5�����ֽ���
		 */
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		CharSequence inputStr = phoneNumber;
		
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		
		Pattern pattern2 = Pattern.compile(expression2);
		Matcher matcher2 = pattern2.matcher(inputStr);
		
		if (matcher.matches() || matcher2.matches())
		{
			isValid = true;
		}
		return isValid;
		
	}
}
