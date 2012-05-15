package org.nice.droidwall.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库操作辅助类
 * @author NiceCoder
 *
 */
public class TelphoneMangerDBHelper extends SQLiteOpenHelper {
	
	private static final String Tag = "NetWall";
	public static final String DB_NAME = "telephone_black.db";
	public static final int VERSION = 1;

	public TelphoneMangerDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	public TelphoneMangerDBHelper(Context context)
	{
		this(context, DB_NAME, null, VERSION);
	}

	/**
	 * 创建数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.d(Tag, "DB onCreate");
		db.execSQL("create table "
				+ TableContanst.BLACK_NUMBER_TABLE
				+ "(_id Integer primary key AUTOINCREMENT,"
				+ "number char,"
                + "modify_time DATETIME)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		Log.d(Tag, "DB onUpgrade");
	}
	
}
