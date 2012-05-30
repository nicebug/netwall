package org.nice.droidwall;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class MainTabHostActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabactivity);
		
		//��ȡTabHost
		TabHost tabHost = getTabHost();
		//��ȡͼ����Դ
		Resources resources = getResources();
		
		Intent appIntent = new Intent();
		appIntent.setClass(this, MainActivity.class);
		TabHost.TabSpec app = tabHost.newTabSpec("App");
		//����tab����ʾ���ƺ�ͼƬ
		app.setIndicator("APP", resources.getDrawable(R.drawable.icon));
		app.setContent(appIntent);
		tabHost.addTab(app);
		
		
		Intent dialIntent = new Intent();
		dialIntent.setClass(this, DialPrevent.class);
		TabHost.TabSpec dial = tabHost.newTabSpec("Dial");
		dial.setIndicator("Dial", resources.getDrawable(R.drawable.dial));
		dial.setContent(dialIntent);
		tabHost.addTab(dial);
		
		Intent smsIntent = new Intent();
		smsIntent.setClass(this, SMSPrevent.class);
		TabHost.TabSpec sms = tabHost.newTabSpec("SMS");
		sms.setIndicator("SMS",resources.getDrawable(R.drawable.mms));
		sms.setContent(smsIntent);
		tabHost.addTab(sms);
		
//		Log.d("NetWall", tabHost.getCurrentTabTag());
//		setDefaultTab("SMS");
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		//TabHost tabHost = getTabHost();
		//Log.d("NetWall", "tab onPause" + tabHost.getCurrentTabTag());
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		//TabHost tabHost = getTabHost();
		//Log.d("NetWall", "tab onResume" + tabHost.getCurrentTabTag());
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		//TabHost tabHost = getTabHost();
		//Log.d("NetWall", "tab onStop" + tabHost.getCurrentTabTag());
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		//TabHost tabHost = getTabHost();
		//Log.d("NetWall", "tab onStart" + tabHost.getCurrentTabTag());
	}
}
