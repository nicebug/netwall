package org.nice.droidwall.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SMSManagerDBHelper extends SQLiteOpenHelper{
	
	private static final String Tag = "NetWall";
	public static final String DB_NAME = "telephone_black.db";
	public static final int VERSION = 1;

	public SMSManagerDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public SMSManagerDBHelper(Context context)
	{
		this(context, DB_NAME, null, VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.d(Tag, "DB_table sms onCreate");
		//创建短信黑名单的表
		db.execSQL("create table "
				+ TableContanst.BLACK_SMS_TABLE
				+ "(" + TableContanst.SMSColunms.ID + " Integer primary key AUTOINCREMENT, "
				+ " " + TableContanst.SMSColunms.NUMBER + " char, "
				+ " " + TableContanst.SMSColunms.MODIFY_TIME + " DATETIME)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.d(Tag, "DB onUpgrade");
	}

	
}
