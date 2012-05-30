package org.nice.droidwall.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

public class SMSDao {

	private SMSManagerDBHelper dbHelper;
	
	public SMSDao(SMSManagerDBHelper dbHelper) {
		this.dbHelper = dbHelper;
	}
	
	
	/**
	 * 添加一个SMS对象到数据库表
	 * @param sms
	 * @return
	 */
	public long addSMS(SMS sms)
	{
		ContentValues values = new ContentValues();
		values.put(TableContanst.SMSColunms.NUMBER, sms.getNumber());
		return dbHelper.getWritableDatabase().insert(
				TableContanst.BLACK_SMS_TABLE, null, values);
	}

	/**
	 * 删除一个number在数据库表sms中对应的记录
	 * @param number
	 * @return
	 */
	public int deleteSMSById(String number)
	{
		return dbHelper.getWritableDatabase().delete(
				TableContanst.BLACK_SMS_TABLE,
				TableContanst.SMSColunms.NUMBER + "=?",
				new String[] { number + "" });
	}

	public List<Map<String, Object>> getAllSMS()
	{
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Cursor cursor = dbHelper.getReadableDatabase().query(
				TableContanst.BLACK_SMS_TABLE, null, null, null, null, null,
				"_id desc");
		while (cursor.moveToNext())
		{
			Map<String, Object> map = new HashMap<String, Object>(8);
			String number = cursor.getString(cursor
					.getColumnIndex(TableContanst.SMSColunms.NUMBER));
			map.put(TableContanst.SMSColunms.NUMBER, number);
			data.add(map);
		}
		cursor.close();
		// closeDB();
		return data;
	}
	
	public Cursor findSMSByNumber(String number)
	{
		Cursor cursor = dbHelper.getReadableDatabase().query(
				TableContanst.BLACK_SMS_TABLE, null, "number=?",
				new String[] { number }, null, null, null, null);
		return cursor;
	}
	
	public void closeDB()
	{
		dbHelper.close();
	}
	
}
