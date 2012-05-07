/**
 * Broadcast receiver that set iptable rules on system startup.
 * This is necessary because the iptables rules are not persistent.
 */
package org.nice.droidwall;

import org.nice.droidwall.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Broadcast receiver that set iptables rules on system startup.
 * This is necessary because the rules are not persistent.
 */
public class BootBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			if (Api.isEnabled(context)) {
				if (!Api.applySavedIptablesRules(context, false)) {
					// Error enabling firewall on boot
					Toast.makeText(context, R.string.toast_error_enabling, Toast.LENGTH_SHORT).show();
					Api.setEnabled(context, false);
				}
			}
		}
	}

}
