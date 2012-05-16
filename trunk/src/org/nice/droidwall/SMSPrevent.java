package org.nice.droidwall;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SMSPrevent extends Activity{
	
	private static final int MENU_REGISET    = 21;
	private static final int MENU_UNREGISET  = 22;
	
	private Button queryPhoneNumBtn;
	private Button removePhoneNumBtn;
	private Button addPhoneNumBtn;
	private EditText phoneNumber;
	private List<String> numList;
	
	private SMSReceiver receiver;
	private boolean isRegisted = false;
	private static final String RECEIVE_SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);
		
		phoneNumber = (EditText)findViewById(R.id.phonenumber);
		addPhoneNumBtn = (Button)findViewById(R.id.add_phonenumber);
		queryPhoneNumBtn = (Button)findViewById(R.id.query_phonenumber);
		removePhoneNumBtn = (Button)findViewById(R.id.remove_phonenumber);
		
		
		numList = new ArrayList<String>();
		receiver = new SMSReceiver(numList);
		//regisetBtn.setOnClickListener(new SMSOnClickListener());
		//unregisetBtn.setOnClickListener(new SMSOnClickListener());
		addPhoneNumBtn.setOnClickListener(new SMSOnClickListener());
		removePhoneNumBtn.setOnClickListener(new SMSOnClickListener());
		queryPhoneNumBtn.setOnClickListener(new SMSOnClickListener());
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, MENU_REGISET, 0, R.string.regiset).setIcon(android.R.drawable.ic_lock_lock);
		menu.add(0, MENU_UNREGISET, 1, R.string.unregiset).setIcon(android.R.drawable.button_onoff_indicator_on);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId())
		{
		case MENU_REGISET:
			Log.d("NetWall", "MENU_REGISET CHOICE");
			regiset();
			Log.d("NetWall", "isRegiseted:" + isRegisted);
			break;
		case MENU_UNREGISET:
			Log.d("NetWall", "MENU_UNREGISET CHOICE");
			unregiset();
			break;
		default:
			break;
		}
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		//ͨ��ID����ȡ�˵������������������ �˴�����getItem()������쳣
		MenuItem menu_onff = menu.findItem(MENU_UNREGISET);
		Log.d("NetWall", "menu_onoff" + menu_onff.toString());
		if (isRegisted)
		{
			menu_onff.setIcon(android.R.drawable.button_onoff_indicator_on);
		}else {
			menu_onff.setIcon(android.R.drawable.button_onoff_indicator_off);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	public class SMSOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.add_phonenumber:
				addPhoneNumber();
				break;
			case R.id.remove_phonenumber:
				removePhoneNumber();
				break;
			case R.id.query_phonenumber:
				queryPhoneNumber();
				break;
			default:
				break;
			}
		}

	}
	
	/**
	 * ��������ѯ
	 */
	private void queryPhoneNumber()
	{
		if (numList.isEmpty() || numList == null)
		{
			Toast.makeText(this, "������Ϊ��", Toast.LENGTH_SHORT).show();
		}
		else {
			StringBuilder sb = new StringBuilder();
			for (String phone : numList)
			{
				sb.append(phone + "\n");
			}
			View view = getLayoutInflater().inflate(R.layout.sms_black_list, null);
			new AlertDialog.Builder(this)
			.setTitle("SMS�������б�")
			.setView(view)
			.setIcon(R.drawable.icon)
			.setMessage(sb.toString())
			.setPositiveButton("ȷ��",  new SMSListener())
			.show();
		}
	}
	
	private class SMSListener implements android.content.DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			dialog.dismiss();
		}
			
	}
	
	/**
	 * �����������������
	 * @param v
	 */
	public void addPhoneNumber()
	{
		if (isPhoneNumberValid(phoneNumber.getText().toString()))
		{
			if (!numList.contains(phoneNumber.getText().toString()))
			{
				numList.add(phoneNumber.getText().toString());
				Toast.makeText(this, "����" + phoneNumber.getText().toString() + "�����", Toast.LENGTH_LONG).show();
				phoneNumber.setText("");
			}
			else {
				Toast.makeText(this, "�ú�������ӵ����������������ظ����", Toast.LENGTH_LONG).show();
				phoneNumber.setText("");
			}
		}
		else {
			Toast.makeText(this, "�绰����Ƿ�", Toast.LENGTH_LONG).show();
			phoneNumber.setText("");
		}
	}
	
	/**
	 * ���������������Ƴ�
	 */
	public void removePhoneNumber()
	{
		if (isPhoneNumberValid(phoneNumber.getText().toString()))
		{
			if (numList.isEmpty())
			{
				Toast.makeText(this, "������Ϊ��", Toast.LENGTH_SHORT).show();
			}else {
				if (numList.contains(phoneNumber.getText().toString()))
				{
					numList.remove(phoneNumber.getText().toString());
					Toast.makeText(this, "����"+ phoneNumber.getText().toString() +"�ɹ����Ƴ���������", Toast.LENGTH_LONG).show();
					phoneNumber.setText("");
				}
			}
		}
		else {
			Toast.makeText(this, "�绰����Ƿ�", Toast.LENGTH_LONG).show();
		}
	}
	
	public void regiset()
	{
		if (!numList.isEmpty())
		{
			for (String num : numList)
			{
				if (isPhoneNumberValid(num))
				{
					IntentFilter filter = new IntentFilter(RECEIVE_SMS_ACTION);
					filter.setPriority(Integer.MAX_VALUE);
					registerReceiver(receiver, filter);
					isRegisted = true;
					Toast.makeText(this, "����"+ num +"ע��ɹ�", Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(this, "�绰����Ƿ�", Toast.LENGTH_LONG).show();
				}
			}
		}
		else {
			Toast.makeText(this, "������Ӻ���", Toast.LENGTH_LONG).show();
			
		}
		/**
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
		}*/
	}
	
	
	public void unregiset()
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
