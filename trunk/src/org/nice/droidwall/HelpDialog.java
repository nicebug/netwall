/**
 * Dialog displayed when the "Help" menu option is selected
 * 
 */
package org.nice.droidwall;

import org.nice.droidwall.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

/**
 * Dialog displayed when the "Help" menu option is selected
 */
public class HelpDialog extends AlertDialog {
	protected HelpDialog(Context context) {
		super(context);
		final View view = getLayoutInflater().inflate(R.layout.help_dialog, null);
		setButton(context.getText(R.string.close), (OnClickListener)null);
		setIcon(R.drawable.icon);
		setTitle("DroidWall v" + Api.VERSION);
		setView(view);
	}
}
