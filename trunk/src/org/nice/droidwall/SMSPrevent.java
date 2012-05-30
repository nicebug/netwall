package org.nice.droidwall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nice.droidwall.db.SMS;
import org.nice.droidwall.db.SMSDao;
import org.nice.droidwall.db.SMSManagerDBHelper;
import org.nice.droidwall.db.TableContanst;
import org.nice.droidwall.db.Telphone;
import org.nice.droidwall.db.TelphoneDao;
import org.nice.droidwall.db.TelphoneMangerDBHelper;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SMSPrevent extends ListActivity implements OnClickListener {
	private static final int MENU_REGISET    = 21;
	private static final int MENU_UNREGISET  = 22;
	private static final String RECEIVE_SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private boolean isRegisted = false;
	
	private SMSDao dao;
	private TextView backNumberText;
	private ListView listView;
	private Button addButton;
	private Button deleteButton;
	private CheckBox selectAllCheckBox;
	private EditText numberEditText;
	private HashMap<String, Boolean> checkBoxStatus;
	private List<String> selectIds;
	
	private SMSReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dial);

		dao = new SMSDao(new SMSManagerDBHelper(this));
		listView = getListView();
		receiver = new SMSReceiver();
		// 找到各个按键
		addButton = (Button) findViewById(R.id.btn_add);
		deleteButton = (Button) findViewById(R.id.btn_delete);
		selectAllCheckBox = (CheckBox) findViewById(R.id.cb_select_all);
		backNumberText = (TextView) findViewById(android.R.id.empty);
		// 为按键设置监听
		addButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		selectAllCheckBox.setOnClickListener(this);

		backNumberText.setOnClickListener(this);
		// 显示黑名单list
		showListView();
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
		//通过ID来获取菜单项，进而进行其他操作 此处采用getItem()会产生异常
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

	public void regiset()
	{
		List<Map<String, Object>> data = dao.getAllSMS();
		if ( data.size() != 0 )
		{
			IntentFilter filter = new IntentFilter(RECEIVE_SMS_ACTION);
			filter.setPriority(Integer.MAX_VALUE);
			registerReceiver(receiver, filter);
			isRegisted = true;
			Toast.makeText(this, "号码注册成功", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(this, "请先添加号码", Toast.LENGTH_SHORT).show();
			
		}
	}
	
	public void unregiset()
	{
		if (receiver != null && isRegisted)
		{
			unregisterReceiver(receiver);
			isRegisted = false;
			Toast.makeText(this, "解注册成功", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(this, "未注册", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 显示黑名单号码
	 */
	private void showListView()
	{
		List<Map<String, Object>> data = dao.getAllSMS();
		if (data.size() != 0)
		{
			backNumberText.setClickable(false);
		}
		else
			backNumberText.setClickable(true);
		String[] from = { TableContanst.SMSColunms.NUMBER, };
		int[] to = { R.id.tv_item_number };
		SimpleAdapter adapter = new SMSAdapter(this, data,
				R.layout.blacknumber_list_item, from, to);
		listView.setAdapter(adapter);
	}

	/**
	 * 监听事件处理
	 */
	@Override
	public void onClick(View v)
	{
		if (v == addButton || v == backNumberText)
		{
			addTelphoneMethod();
		}
		else
			if (v == deleteButton)
			{
				deleteSeleteData();
				selectAllCheckBox.setChecked(false);
			}
			else
				if (v == selectAllCheckBox)
				{
					checkOrcancelAllbox(selectAllCheckBox.isChecked());
				}
	}

	/**
	 * 提示信息
	 */
	protected void showToast(String string)
	{
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 添加黑名单号码
	 */
	private void addTelphoneMethod()
	{
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.dialog, null);
		new AlertDialog.Builder(this)
				.setTitle("黑名单添加")
				.setView(view)
				.setPositiveButton("添加", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						numberEditText = (EditText) view.findViewById(R.id.et_number);
						String number = numberEditText.getText().toString();
						//boolean isValid = PhoneNumberUtils.isGlobalPhoneNumber(number);
						//Log.d("NetWall", "isValid: " + isValid);
						if (isPhoneNumberValid2(number))
						{
							// DialPrevent.this.showToast("电话号码非法");
							// dao.closeDB();
							if (number.trim().length() == 0)
							{
								SMSPrevent.this.showToast("请输入拦截号码！");
								//dao.closeDB();
							}
							else
								if (dao.findSMSByNumber(number).getCount() != 0)
								{
									SMSPrevent.this.showToast("输入号码已存在，无需增加！");
									//dao.closeDB();
								}
								else
								{
									SMS telphone = new SMS(number);
									long id = dao.addSMS(telphone);
									if (id < 0)
									{
										SMSPrevent.this.showToast("添加失败！");
										//dao.closeDB();
									}
									else
									{
										SMSPrevent.this.showToast("添加成功！");
										showListView();
										//dao.closeDB();
									}
								}
						}
						else {
							SMSPrevent.this.showToast("电话号码非法");
						}

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				}).show();
	}

	/**
	 * 黑名单号码删除
	 */
	private void deleteSeleteData()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("黑名单删除")
				.setMessage("确定删除所选号码?")
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id)
					{
						int count = 0;
						if (selectIds.size() <= 0)
						{
							Toast.makeText(SMSPrevent.this, "无选中记录，请选择！",
									Toast.LENGTH_LONG).show();
						}
						else
						{
							// Log.v("TAG", "selectIds=" + selectIds.size());
							for (int i = 0; i < selectIds.size(); i++)
							{
								String deleteNumber = selectIds.get(i);
								dao.deleteSMSById(deleteNumber);
								count++;
							}
							showListView();
							if (count > 0)
							{
								Toast.makeText(SMSPrevent.this, "删除成功!",
										Toast.LENGTH_LONG).show();
							}
							else
								Toast.makeText(SMSPrevent.this, "删除失败!",
										Toast.LENGTH_LONG).show();
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * 点击ListView条目存储被点击的条目
	 */
	protected void onListItemClick(ListView l, View view, int position, long id)
	{
		CheckBox box = (CheckBox) view.findViewById(R.id.cb_item_delete);
		TextView idView = (TextView) view.findViewById(R.id.tv_item_number);
		String deleteNumber = idView.getText().toString();
		box.toggle(); // 改变checkbox的选中状态
		if (box.isChecked())
		{
			selectIds.add(deleteNumber);
			checkBoxStatus.put(deleteNumber, true);
		}
		else
		{
			selectIds.remove(deleteNumber);
			checkBoxStatus.put(deleteNumber, false);
		}
	}

	/**
	 * 全选or取消全选
	 */
	private void checkOrcancelAllbox(boolean checked)
	{
		int itemCount = listView.getCount();
		selectIds.clear();
		checkBoxStatus.clear();
		for (int i = 0; i < itemCount; i++)
		{
			View view = listView.getChildAt(i);
			Map<String, Object> data = (Map<String, Object>) listView
					.getItemAtPosition(i);
			String number = data.get(TableContanst.SMSColunms.NUMBER)
					.toString();
			if (view != null)
			{
				CheckBox cb = (CheckBox) view.findViewById(R.id.cb_item_delete);
				cb.setChecked(checked);
			}
			checkBoxStatus.put(number, checked);
			if (checked)
			{
				selectIds.add(number);
			}
		}
	}

	/**
	 * 自定义SimpleAdapter显示ListView数据
	 */
	private class SMSAdapter extends SimpleAdapter {
		public SMSAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			selectIds = new ArrayList<String>();
			checkBoxStatus = new HashMap<String, Boolean>();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = super.getView(position, convertView, parent);
			CheckBox box = (CheckBox) view.findViewById(R.id.cb_item_delete);
			TextView idView = (TextView) view.findViewById(R.id.tv_item_number);
			String number = idView.getText().toString();
			if (checkBoxStatus.containsKey(number))
			{
				box.setChecked(checkBoxStatus.get(number));
			}
			else
			{
				box.setChecked(selectAllCheckBox.isChecked());
			}
			return view;
		}
	}
	
	
	public static boolean isPhoneNumberValid2(String phoneNumber)
	{
		boolean isValid = false;
		/**
		 * 可接受的电话格式有：
		 * ^\\(? :可使用"("开头
		 * (\\d{3}):紧接着三个数字
		 * \\):可使用")"继续
		 * [- ]?:在上述格式后可使用具有选择性的 "-"
		 * (\\d{5})$:以5个数字结束
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