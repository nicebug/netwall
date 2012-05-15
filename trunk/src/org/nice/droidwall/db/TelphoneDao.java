package org.nice.droidwall.db;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 
 import android.content.ContentValues;
import android.database.Cursor;
 
 public class TelphoneDao {
     private TelphoneMangerDBHelper dbHelper;
 
     public TelphoneDao(TelphoneMangerDBHelper dbHelper) {
         this.dbHelper = dbHelper;
     }
 
     /**
      * 添加一个Telphone对象数据到数据库表
      */
     public long addTelphone(Telphone phone) {
 
         ContentValues values = new ContentValues();
         values.put(TableContanst.NumberColumns.NUMBER, phone.getNumber());
         return dbHelper.getWritableDatabase().insert(
                 TableContanst.BLACK_NUMBER_TABLE, null, values);
 
     }
 
     /**
      * 删除一个number所对应的数据库表Telphone的记录
      */
     public int deleteTelphoneById(String number) {
         return dbHelper.getWritableDatabase().delete(
                 TableContanst.BLACK_NUMBER_TABLE,
                 TableContanst.NumberColumns.NUMBER + "=?",
                 new String[] { number + "" });
     }
     
     /**
      *  查询所有的记录
      */
     public List<Map<String, Object>> getAllTelphone() { 
         List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
         Cursor cursor = dbHelper.getWritableDatabase().query(
                 TableContanst.BLACK_NUMBER_TABLE, null, null, null, null, null,
                 "_id desc");
         while (cursor.moveToNext()) {
             Map<String, Object> map = new HashMap<String, Object>(8);
             String number = cursor.getString(cursor
                     .getColumnIndex(TableContanst.NumberColumns.NUMBER));
             map.put(TableContanst.NumberColumns.NUMBER, number);
             data.add(map);
         }
         cursor.close();
         return data;
     }
     /**
      * 黑名单号码查询
      */
     public Cursor findTelphone(String number) {
         Cursor cursor = dbHelper.getWritableDatabase().query(
                 TableContanst.BLACK_NUMBER_TABLE, null, "number=?",
                 new String[] { number }, null, null, null, null);
         return cursor;
     }
 
     public void closeDB() {
         dbHelper.close();
     }
 }