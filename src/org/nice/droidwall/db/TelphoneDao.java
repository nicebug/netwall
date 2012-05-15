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
      * ���һ��Telphone�������ݵ����ݿ��
      */
     public long addTelphone(Telphone phone) {
 
         ContentValues values = new ContentValues();
         values.put(TableContanst.NumberColumns.NUMBER, phone.getNumber());
         return dbHelper.getWritableDatabase().insert(
                 TableContanst.BLACK_NUMBER_TABLE, null, values);
 
     }
 
     /**
      * ɾ��һ��number����Ӧ�����ݿ��Telphone�ļ�¼
      */
     public int deleteTelphoneById(String number) {
         return dbHelper.getWritableDatabase().delete(
                 TableContanst.BLACK_NUMBER_TABLE,
                 TableContanst.NumberColumns.NUMBER + "=?",
                 new String[] { number + "" });
     }
     
     /**
      *  ��ѯ���еļ�¼
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
      * �����������ѯ
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