/**
 * Dialog displayed to request a password.
 * 
 */
package org.nice.droidwall;

import org.nice.droidwall.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Dialog displayed to request a password.
 */
public class PassDialog extends Dialog implements android.view.View.OnClickListener, android.view.View.OnKeyListener, OnCancelListener {
	private final Callback callback;
	private final EditText pass;
	/**
	 * Creates the dialog
	 * @param context context
	 * @param setting if true, indicates that we are setting a new password instead of requesting it.
	 * @param callback callback to receive the password entered (null if canceled)
	 */
	public PassDialog(Context context, boolean setting, Callback callback) {
		super(context);
		final View view = getLayoutInflater().inflate(R.layout.pass_dialog, null);
		((TextView)view.findViewById(R.id.pass_message)).setText(setting ? R.string.enternewpass : R.string.enterpass);
		((Button)view.findViewById(R.id.pass_ok)).setOnClickListener(this);
		((Button)view.findViewById(R.id.pass_cancel)).setOnClickListener(this);
		this.callback = callback;
		this.pass = (EditText) view.findViewById(R.id.pass_input);
		this.pass.setOnKeyListener(this);
		setTitle(setting ? R.string.pass_titleset : R.string.pass_titleget);
		setOnCancelListener(this);
		setContentView(view);
	}
	@Override
	public void onClick(View v) {
		final Message msg = new Message();
		if (v.getId() == R.id.pass_ok) {
			msg.obj = this.pass.getText().toString();
		}
		dismiss();
		this.callback.handleMessage(msg);
	}
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			final Message msg = new Message();
			msg.obj = this.pass.getText().toString();
			this.callback.handleMessage(msg);
			dismiss();
			return true;
		}
		return false;
	}
	@Override
	public void onCancel(DialogInterface dialog) {
		this.callback.handleMessage(new Message());
	}
}
