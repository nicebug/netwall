package org.nice.droidwall;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

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
 import android.os.Bundle;
 import android.util.Log;
 import android.view.LayoutInflater;
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
 
 public class DialPrevent extends ListActivity implements
         OnClickListener {
     private TelphoneDao dao;
     private TextView backNumberText;
     private ListView listView;
     private Button addButton;
     private Button deleteButton;
     private CheckBox selectAllCheckBox;
     private EditText numberEditText;
     private HashMap<String, Boolean> checkBoxStatus;
     private List<String> selectIds;
     
     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.dial);
         
         dao = new TelphoneDao(new TelphoneMangerDBHelper(this));
         //addressService = new AddressService(this);
         
         listView = getListView();
         regist();
         // �ҵ���������
         addButton = (Button) findViewById(R.id.btn_add);
         deleteButton = (Button) findViewById(R.id.btn_delete);
         selectAllCheckBox = (CheckBox) findViewById(R.id.cb_select_all);
         backNumberText = (TextView) findViewById(android.R.id.empty);
         // Ϊ�������ü���
         addButton.setOnClickListener(this);
         deleteButton.setOnClickListener(this);
         selectAllCheckBox.setOnClickListener(this);
         
         backNumberText.setOnClickListener(this);
         //��ʾ������list
         showListView();
     }
 
     /**
      * ע���������������
      */
     private void regist() {
    	 Intent intent = new Intent(DialPrevent.this, PhoneListenerService.class);
    	 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	 
         startService(intent);
         Log.d("NetWall", "DialPrevent start service");
     }
 
    
 
     /**
      * ��ʾ����������
      */
     private void showListView() {
         List<Map<String, Object>> data = dao.getAllTelphone();
         if (data.size() != 0) {
             backNumberText.setClickable(false);
         } else
             backNumberText.setClickable(true);
         String[] from = { TableContanst.NumberColumns.NUMBER, };
         int[] to = { R.id.tv_item_number };
         SimpleAdapter adapter = new TelphoneAdpter(this, data,
                 R.layout.blacknumber_list_item, from, to);
         listView.setAdapter(adapter);
     }
 
     /**
      * �����¼�����
      */
     @Override
     public void onClick(View v) {
         if (v == addButton || v == backNumberText) {
             addTelphoneMethod();
         } else if (v == deleteButton) {
             deleteSeleteData();
             selectAllCheckBox.setChecked(false);
         } else if (v == selectAllCheckBox) {
             checkOrcancelAllbox(selectAllCheckBox.isChecked());
         }
     }
 
     /**
      * ��ʾ��Ϣ
      */
     protected void showToast(String string) {
         Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
     }
 
     /**
      * ��Ӻ���������
      */
     private void addTelphoneMethod() {
         LayoutInflater inflater = (LayoutInflater) this
                 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         final View view = inflater.inflate(R.layout.dialog, null);
         new AlertDialog.Builder(this)
                 .setTitle("���������")
                 .setView(view)
                 .setPositiveButton("���", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         numberEditText = (EditText) view
                                 .findViewById(R.id.et_number);
                         String number = numberEditText.getText().toString();
                         if (number.trim().length() == 0) {
                             DialPrevent.this.showToast("���������غ��룡");
                         } else if (dao.findTelphone(number).getCount() != 0) {
                             DialPrevent.this
                                     .showToast("��������Ѵ��ڣ��������ӣ�");
                         } else {
                             Telphone telphone = new Telphone(number);
                             long id = dao.addTelphone(telphone);
                             if (id < 0) {
                                 DialPrevent.this.showToast("���ʧ�ܣ�");
                             } else {
                                 DialPrevent.this.showToast("��ӳɹ���");
                                 showListView();
                             }
                         }
                     }
                 })
                 .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.cancel();
                     }
                 }).show();
     }
 
     /**
      * ����������ɾ��
      */
     private void deleteSeleteData() {
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("������ɾ��")
                 .setMessage("ȷ��ɾ����ѡ����?")
                 .setCancelable(false)
                 .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                         int count = 0;
                         if (selectIds.size() <= 0) {
                             Toast.makeText(DialPrevent.this,
                                     "��ѡ�м�¼����ѡ��", Toast.LENGTH_LONG).show();
                         } else {
                             // Log.v("TAG", "selectIds=" + selectIds.size());
                             for (int i = 0; i < selectIds.size(); i++) {
                                 String deleteNumber = selectIds.get(i);
                                 dao.deleteTelphoneById(deleteNumber);
                                 count++;
                             }
                             showListView();
                             if (count > 0) {
                                 Toast.makeText(DialPrevent.this,
                                         "ɾ���ɹ�!", Toast.LENGTH_LONG).show();
                             } else
                                 Toast.makeText(DialPrevent.this,
                                         "ɾ��ʧ��!", Toast.LENGTH_LONG).show();
                         }
                     }
                 })
                 .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                         dialog.cancel();
                     }
                 });
         AlertDialog alert = builder.create();
         alert.show();
     }
 
     /**
      * ���ListView��Ŀ�洢���������Ŀ
      */
     protected void onListItemClick(ListView l, View view, int position, long id) {
         CheckBox box = (CheckBox) view.findViewById(R.id.cb_item_delete);
         TextView idView = (TextView) view.findViewById(R.id.tv_item_number);
         String deleteNumber = idView.getText().toString();
         box.toggle(); // �ı�checkbox��ѡ��״̬
         if (box.isChecked()) {
             selectIds.add(deleteNumber);
             checkBoxStatus.put(deleteNumber, true);
         } else {
             selectIds.remove(deleteNumber);
             checkBoxStatus.put(deleteNumber, false);
         }
     }
 
     /**
      * ȫѡorȡ��ȫѡ
      */
     private void checkOrcancelAllbox(boolean checked) {
         int itemCount = listView.getCount();
         selectIds.clear();
         checkBoxStatus.clear();
         for (int i = 0; i < itemCount; i++) {
             View view = listView.getChildAt(i);
             Map<String, Object> data = (Map<String, Object>) listView.getItemAtPosition(i);
             String number = data.get(TableContanst.NumberColumns.NUMBER).toString();
             if (view != null) {
                 CheckBox cb = (CheckBox) view.findViewById(R.id.cb_item_delete);
                 cb.setChecked(checked);
             }
             checkBoxStatus.put(number, checked);
             if (checked) {
                 selectIds.add(number);
             }
         }
     }
 
     /**
      * �Զ���SimpleAdapter��ʾListView����
      */
     private class TelphoneAdpter extends SimpleAdapter {
         public TelphoneAdpter(Context context,
                 List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
             super(context, data, resource, from, to);
             selectIds = new ArrayList<String>();
             checkBoxStatus = new HashMap<String, Boolean>();
         }
 
         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
             View view = super.getView(position, convertView, parent);
             CheckBox box = (CheckBox) view.findViewById(R.id.cb_item_delete);
             TextView idView = (TextView) view.findViewById(R.id.tv_item_number);
             String number = idView.getText().toString();
             if (checkBoxStatus.containsKey(number)) {
                 box.setChecked(checkBoxStatus.get(number));
             } else {
                 box.setChecked(selectAllCheckBox.isChecked());
             }
             return view;
         }
     }
 }