package com.android.ckstudent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class PwdLockActivity extends Activity {
	private static final String TAG = "PwdLockActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//pop pass_lock
		LayoutInflater inflater = LayoutInflater.from(PwdLockActivity.this);
		View pass_view = inflater.inflate(R.layout.pass_view, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(PwdLockActivity.this);
		builder.setTitle("�K�X��");
		builder.setView(pass_view);
		
		final EditText pass_first = (EditText)pass_view.findViewById(R.id.editText1);
		final EditText pass_retry = (EditText)pass_view.findViewById(R.id.editText2);
		
		builder.setPositiveButton("�T�{", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//�p�G���S��J
				if (pass_first.getEditableText().toString() != "NULL" || pass_retry.getEditableText().toString() != "NULL") {
					AlertDialog.Builder passcancel = new AlertDialog.Builder(PwdLockActivity.this);
					passcancel.setTitle("�K�X���~");
					passcancel.setMessage("�п�J�K�X");
					AlertDialog passdialog = passcancel.create();
					passdialog.show();
				}
				else if (pass_first.getEditableText().toString() != pass_retry.getEditableText().toString()) {
					//cancel
					AlertDialog.Builder passcancel = new AlertDialog.Builder(PwdLockActivity.this);
					passcancel.setTitle("�K�X���~");
					passcancel.setMessage("�⦸�K�X���۲�");
					AlertDialog passdialog = passcancel.create();
					passdialog.show();
				} else {
					//send to wifi-direct as password
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("Type", "PwdLock");
					bundle.putString("Pwd_Lock", pass_retry.getEditableText().toString());
					intent.putExtras(bundle);
					PwdLockActivity.this.startActivity(intent);
				}
			}
		});
		builder.setNegativeButton("�M��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				pass_first.setText("");
				pass_retry.setText("");
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
