//i want to test
/**
 * Main application activity.
 * This is the screen displayed when you open the application

 */

package org.nice.droidwall;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.nice.droidwall.Api.DroidApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import org.nice.droidwall.R;

/**
 * Main application activity.
 * This is the screen displayed when you open the application
 */
public class MainActivity extends Activity implements OnCheckedChangeListener, OnClickListener {
	
	// Menu options
	private static final int MENU_DISABLE	= 0; //防火墙启用开关
	private static final int MENU_TOGGLELOG	= 1; //日志启用
	private static final int MENU_APPLY		= 2; //规则应用开关
	private static final int MENU_EXIT		= 3; //退出菜单
	private static final int MENU_HELP		= 4; //帮助菜单
	private static final int MENU_SHOWLOG	= 5; //显示日志菜单
	private static final int MENU_SHOWRULES	= 6; //显示规则菜单
	private static final int MENU_CLEARLOG	= 7; //清除日志菜单
	private static final int MENU_SETPWD	= 8; //设置密码菜单
	private static final int SMS_PREVENT    = 9; //设置短信拦截
	private static final int DIAL_PREVENT   = 10; //设置来电拦截
	
	/** progress dialog instance */
	private ListView listview;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPreferences();
		setContentView(R.layout.main);
		//监听模式选择
		this.findViewById(R.id.label_mode).setOnClickListener(this);
		Api.assertBinaries(this, true);
    }
    @Override
    protected void onStart() {
    	super.onStart();
    	// Force re-loading the application list
		Log.d("NetWall", "onStart() - Forcing APP list reload!");
    	Api.applications = null;
    }
    @Override
    protected void onResume() {
    	super.onResume();
    	if (this.listview == null) {
    		//打印展示所有的应用程序
    		this.listview = (ListView) this.findViewById(R.id.listview);
    	}
    	//重置程序的头部
    	refreshHeader();
		final String pwd = getSharedPreferences(Api.PREFS_NAME, 0).getString(Api.PREF_PASSWORD, "");
		if (pwd.length() == 0) {
			// No password lock
			showOrLoadApplications();
		} else {
			// Check the password
			requestPassword(pwd);
		}
    }
    @Override
    protected void onPause() {
    	super.onPause();
    	this.listview.setAdapter(null);
    }
    /**
     * Check if the stored preferences are OK
     * 检查是否已设置好
     */
    private void checkPreferences() {
    	final SharedPreferences prefs = getSharedPreferences(Api.PREFS_NAME, 0);
    	final Editor editor = prefs.edit();
    	boolean changed = false;
    	if (prefs.getString(Api.PREF_MODE, "").length() == 0) {
    		editor.putString(Api.PREF_MODE, Api.MODE_BLACKLIST);
    		changed = true;
    	}
    	/* delete the old preference names */
    	if (prefs.contains("AllowedUids")) {
    		editor.remove("AllowedUids");
    		changed = true;
    	}
    	if (prefs.contains("Interfaces")) {
    		editor.remove("Interfaces");
    		changed = true;
    	}
    	if (changed) 
    		editor.commit();
    }
    /**
     * Refresh informative header
     */
    private void refreshHeader() {
    	final SharedPreferences prefs = getSharedPreferences(Api.PREFS_NAME, 0);
    	final String mode = prefs.getString(Api.PREF_MODE, Api.MODE_BLACKLIST);
		final TextView labelmode = (TextView) this.findViewById(R.id.label_mode);
    	final Resources res = getResources();
		int resid = (mode.equals(Api.MODE_BLACKLIST) ? R.string.mode_blacklist : R.string.mode_whitelist);
		labelmode.setText(res.getString(R.string.mode_header, res.getString(resid)));
		resid = (Api.isEnabled(this) ? R.string.title_enabled : R.string.title_disabled);
		setTitle(res.getString(resid, Api.VERSION));
    }
    /**
     * Displays a dialog box to select the operation mode (black or white list)
     * 弹出选择（黑白名单）的对话框
     */
    private void selectMode() {
    	final Resources res = getResources();
    	new AlertDialog.Builder(this).setItems(new String[]{res.getString(R.string.mode_whitelist),
    			res.getString(R.string.mode_blacklist)}, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				final String mode = (which==0 ? Api.MODE_WHITELIST : Api.MODE_BLACKLIST);
				final Editor editor = getSharedPreferences(Api.PREFS_NAME, 0).edit();
				editor.putString(Api.PREF_MODE, mode);
				editor.commit();
				refreshHeader();
			}
    	}).setTitle("Select mode:")
    	.show();
    }
    /**
     * Set a new password lock
     * 设置新密码锁
     * @param pwd new password (empty to remove the lock)
     */
	private void setPassword(String pwd) {
    	final Resources res = getResources();
		final Editor editor = getSharedPreferences(Api.PREFS_NAME, 0).edit();
		editor.putString(Api.PREF_PASSWORD, pwd);
		String msg;
		if (editor.commit()) {
			if (pwd.length() > 0) {
				msg = res.getString(R.string.passdefined);
			} else {
				msg = res.getString(R.string.passremoved);
			}
		} else {
			msg = res.getString(R.string.passerror);
		}
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	/**
	 * Request the password lock before displayed the main screen.
	 */
	private void requestPassword(final String pwd) {
		new PassDialog(this, false, new android.os.Handler.Callback() {
			public boolean handleMessage(Message msg) {
				if (msg.obj == null) {
					MainActivity.this.finish();
					android.os.Process.killProcess(android.os.Process.myPid());
					return false;
				}
				if (!pwd.equals(msg.obj)) {
					requestPassword(pwd);
					return false;
				}
				// Password correct
				showOrLoadApplications();
				return false;
			}
		}).show();
	}
	/**
	 * Toggle iptables log enabled/disabled
	 */
	private void toggleLogEnabled() {
		final SharedPreferences prefs = getSharedPreferences(Api.PREFS_NAME, 0);
		final boolean enabled = !prefs.getBoolean(Api.PREF_LOGENABLED, false);
		final Editor editor = prefs.edit();
		editor.putBoolean(Api.PREF_LOGENABLED, enabled);
		editor.commit();
		if (Api.isEnabled(this)) {
			Api.applySavedIptablesRules(this, true);
		}
		Toast.makeText(MainActivity.this, (enabled?R.string.log_was_enabled:R.string.log_was_disabled), Toast.LENGTH_SHORT).show();
	}
	/**
	 * If the applications are cached, just show them, otherwise load and show
	 */
	private void showOrLoadApplications() {
    	final Resources res = getResources();
    	if (Api.applications == null) {
    		// The applications are not cached.. so lets display the progress dialog
    		final ProgressDialog progress = ProgressDialog.show(this, res.getString(R.string.working), 
    				res.getString(R.string.reading_apps), true);
        	final Handler handler = new Handler() {
        		public void handleMessage(Message msg) {
        			try {
        				progress.dismiss();
        			} catch(Exception ex){}
        			showApplications();
        		}
        	};
        	new Thread() {
        		public void run() {
        			Api.getApps(MainActivity.this);
        			handler.sendEmptyMessage(0);
        		}
        	}.start();
    	} else {
    		// the applications are cached, just show the list
        	showApplications();
    	}
	}
    /**
     * Show the list of applications
     */
    private void showApplications() {
        final DroidApp[] apps = Api.getApps(this);
        // Sort applications - selected first, then alphabetically
        Arrays.sort(apps, new Comparator<DroidApp>() {
			@Override
			public int compare(DroidApp o1, DroidApp o2) {
				if ((o1.selected_wifi|o1.selected_3g) == (o2.selected_wifi|o2.selected_3g)) {
					return o1.names[0].compareTo(o2.names[0]);
				}
				if (o1.selected_wifi || o1.selected_3g) return -1;
				return 1;
			}
        });
        final LayoutInflater inflater = getLayoutInflater();
		final ListAdapter adapter = new ArrayAdapter<DroidApp>(this,R.layout.listitem,R.id.itemtext,apps) {
        	@Override
        	public View getView(int position, View convertView, ViewGroup parent) {
       			ListEntry entry;
        		if (convertView == null) {
        			// Inflate a new view
        			convertView = inflater.inflate(R.layout.listitem, parent, false);
       				entry = new ListEntry();
       				entry.box_wifi = (CheckBox) convertView.findViewById(R.id.itemcheck_wifi);
       				entry.box_3g = (CheckBox) convertView.findViewById(R.id.itemcheck_3g);
       				entry.text = (TextView) convertView.findViewById(R.id.itemtext);
       				convertView.setTag(entry);
       				entry.box_wifi.setOnCheckedChangeListener(MainActivity.this);
       				entry.box_3g.setOnCheckedChangeListener(MainActivity.this);
       				entry.text.setOnClickListener(MainActivity.this);
        		} else {
        			// Convert an existing view
        			entry = (ListEntry) convertView.getTag();
        		}
        		final DroidApp app = apps[position];
        		entry.text.setText(app.toString());
        		final CheckBox box_wifi = entry.box_wifi;
        		box_wifi.setTag(app);
        		box_wifi.setChecked(app.selected_wifi);
        		final CheckBox box_3g = entry.box_3g;
        		box_3g.setTag(app);
        		box_3g.setChecked(app.selected_3g);
       			return convertView;
        	}
        };
        this.listview.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_DISABLE, 0, R.string.fw_enabled).setIcon(android.R.drawable.button_onoff_indicator_on);
    	menu.add(0, MENU_TOGGLELOG, 0, R.string.log_enabled).setIcon(android.R.drawable.button_onoff_indicator_on);
    	menu.add(0, MENU_APPLY, 0, R.string.applyrules).setIcon(R.drawable.apply);
    	menu.add(0, MENU_EXIT, 0, R.string.exit).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	menu.add(0, MENU_HELP, 0, R.string.help).setIcon(android.R.drawable.ic_menu_help);
    	menu.add(0, MENU_SHOWLOG, 0, R.string.show_log).setIcon(R.drawable.show);
    	menu.add(0, MENU_SHOWRULES, 0, R.string.showrules).setIcon(R.drawable.show);
    	menu.add(0, MENU_CLEARLOG, 0, R.string.clear_log).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	menu.add(0, MENU_SETPWD, 0, R.string.setpwd).setIcon(android.R.drawable.ic_lock_lock);
    	menu.add(0, SMS_PREVENT, 0, R.string.sms_prevent).setIcon(R.drawable.mms);
    	menu.add(0, DIAL_PREVENT, 0, R.string.dial_prevent).setIcon(R.drawable.dial);
    	
    	return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	final MenuItem item_onoff = menu.getItem(MENU_DISABLE);
    	final MenuItem item_apply = menu.getItem(MENU_APPLY);
    	final boolean enabled = Api.isEnabled(this);
    	if (enabled) {
    		item_onoff.setIcon(android.R.drawable.button_onoff_indicator_on);
    		item_onoff.setTitle(R.string.fw_enabled);
    		item_apply.setTitle(R.string.applyrules);
    	} else {
    		item_onoff.setIcon(android.R.drawable.button_onoff_indicator_off);
    		item_onoff.setTitle(R.string.fw_disabled);
    		item_apply.setTitle(R.string.saverules);
    	}
    	final MenuItem item_log = menu.getItem(MENU_TOGGLELOG);
    	final boolean logenabled = getSharedPreferences(Api.PREFS_NAME, 0).getBoolean(Api.PREF_LOGENABLED, false);
    	if (logenabled) {
    		item_log.setIcon(android.R.drawable.button_onoff_indicator_on);
    		item_log.setTitle(R.string.log_enabled);
    	} else {
    		item_log.setIcon(android.R.drawable.button_onoff_indicator_off);
    		item_log.setTitle(R.string.log_disabled);
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
    //menu choice
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId()) {
    	case MENU_DISABLE:
    		disableOrEnable();
    		return true;
    	case MENU_TOGGLELOG:
    		toggleLogEnabled();
    		return true;
    	case MENU_APPLY:
    		applyOrSaveRules();
    		return true;
    	case MENU_EXIT:
    		finish();
    		System.exit(0);
    		return true;
    	case MENU_HELP:
    		new HelpDialog(this).show();
    		return true;
    	case MENU_SETPWD:
    		setPassword();
    		return true;
    	case MENU_SHOWLOG:
    		showLog();
    		return true;
    	case MENU_SHOWRULES:
    		showRules();
    		return true;
    	case MENU_CLEARLOG:
    		clearLog();
    		return true;
    	case SMS_PREVENT:
    		preventSMS();
    		return true;
    	case DIAL_PREVENT:
    		preventDial();
    		return true;
    	}
    	return false;
    }
   /**
    * prevent SMS
    */
	private void preventSMS()
	{
		Log.d("NetWall", "before sms ");
		//new Dial_prevent(this);
		final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this, res.getString(R.string.working), 
				res.getString(R.string.please_wait), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
    			try {
    				progress.dismiss();
    				} 
    			catch(Exception ex){}
    			Intent intent = new Intent();
    			intent.setClass(MainActivity.this, SMSPrevent.class);
    			startActivity(intent);
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
		
	}
	
	 private void preventDial()
	{
		Log.d("NetWall", "before dial");
		//new Dial_prevent(this);
		final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this, res.getString(R.string.working), 
				res.getString(R.string.please_wait), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
    			try {
    				progress.dismiss();
    				} 
    			catch(Exception ex){}
    			Intent intent = new Intent();
    			intent.setClass(MainActivity.this, Dialprevent.class);
    			startActivity(intent);
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
		
		//MainActivity.this.finish();
	}
	/**
     * Enables or disables the firewall
     */
	private void disableOrEnable() {
		final boolean enabled = !Api.isEnabled(this);
		Log.d("NetWall", "Changing enabled status to: " + enabled);
		Api.setEnabled(this, enabled);
		if (enabled) {
			applyOrSaveRules();
		} else {
			purgeRules();
		}
		refreshHeader();
	}
	/**
	 * Set a new lock password
	 */
	private void setPassword() {
		new PassDialog(this, true, new android.os.Handler.Callback() {
			public boolean handleMessage(Message msg) {
				if (msg.obj != null) {
					setPassword((String)msg.obj);
				}
				return false;
			}
		}).show();
	}
	/**
	 * Show iptable rules on a dialog
	 */
	private void showRules() {
    	final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this, res.getString(R.string.working), 
				res.getString(R.string.please_wait), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
    			try {
    				progress.dismiss();
    				} catch(Exception ex){}
				if (!Api.hasRootAccess(MainActivity.this, true)) return;
				Api.showIptablesRules(MainActivity.this);
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}
	/**
	 * Show logs on a dialog
	 */
	private void showLog() {
    	final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this, res.getString(R.string.working), 
				res.getString(R.string.please_wait), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
    			try {progress.dismiss();} catch(Exception ex){}
				Api.showLog(MainActivity.this);
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}
	/**
	 * Clear logs
	 */
	private void clearLog() {
    	final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this, res.getString(R.string.working), res.getString(R.string.please_wait), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
    			try {
    				progress.dismiss();
    				} catch(Exception ex){}
				if (!Api.hasRootAccess(MainActivity.this, true)) return;
				if (Api.clearLog(MainActivity.this)) {
					Toast.makeText(MainActivity.this, R.string.log_cleared, Toast.LENGTH_SHORT).show();
				}
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}
	/**
	 * Apply or save iptable rules, showing a visual indication
	 */
	private void applyOrSaveRules() {
    	final Resources res = getResources();
		final boolean enabled = Api.isEnabled(this);
		final ProgressDialog progress = ProgressDialog.show(this, res.getString(R.string.working), res.getString(enabled?R.string.applying_rules:R.string.saving_rules), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
    			try {progress.dismiss();} catch(Exception ex){}
				if (enabled) {
					Log.d("NetWall", "Applying rules.");
					if (Api.hasRootAccess(MainActivity.this, true) && Api.applyIptablesRules(MainActivity.this, true)) {
						Toast.makeText(MainActivity.this, R.string.rules_applied, Toast.LENGTH_SHORT).show();
					} else {
						Log.d("NetWall", "Failed - Disabling firewall.");
						Api.setEnabled(MainActivity.this, false);
					}
				} else {
					Log.d("NetWall", "Saving rules.");
					Api.saveRules(MainActivity.this);
					Toast.makeText(MainActivity.this, R.string.rules_saved, Toast.LENGTH_SHORT).show();
				}
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}
	/**
	 * Purge iptable rules, showing a visual indication
	 */
	private void purgeRules() {
    	final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this, res.getString(R.string.working), res.getString(R.string.deleting_rules), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
    			try {progress.dismiss();} catch(Exception ex){}
				if (!Api.hasRootAccess(MainActivity.this, true)) return;
				if (Api.purgeIptables(MainActivity.this, true)) {
					Toast.makeText(MainActivity.this, R.string.rules_deleted, Toast.LENGTH_SHORT).show();
				}
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}
	/**
	 * Called an application is check/unchecked
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		final DroidApp app = (DroidApp) buttonView.getTag();
		if (app != null) {
			switch (buttonView.getId()) {
				case R.id.itemcheck_wifi: app.selected_wifi = isChecked; break;
				case R.id.itemcheck_3g: app.selected_3g = isChecked; break;
			}
		}
	}
	/**
	 * show detail of an application 
	 */
	public void showApplicationDetail(){
		final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this, res.getString(R.string.working), 
				res.getString(R.string.reading_apps), true);
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg)
			{
				try
				{
					progress.dismiss();
				}
				catch (Exception e){}
				//Api.alert(MainActivity.this, "come");
			}
			
		};
		
		new Thread(){
			public void run(){
				//Api.getApps(MainActivity.this);
				final PackageManager pkgmanager = getPackageManager();
				final List<ApplicationInfo> installed = pkgmanager.getInstalledApplications(0);
				final HashMap<Integer, DroidApp> map = new HashMap<Integer, DroidApp>();
				
				DroidApp app = null;
				
				for (ApplicationInfo apinfo : installed)
				{
					map.get(apinfo.uid);
					
					
				}
				
				
				
				handler.sendEmptyMessage(0);
			}
		}.start();
	}
	
	private static class ListEntry {
		private CheckBox box_wifi;
		private CheckBox box_3g;
		private TextView text;
	}

	@Override
	public void onClick(View v) {
		//System.out.println(v.getId());
		switch (v.getId()) {
		case R.id.label_mode:
			selectMode();
			break;
		case R.id.itemtext:
			//Api.alert(this, "just a test ");
			Log.d("NetWall", getString(v.getId()));
			//showApplicationDetail();
			break;
		}
	}
}
