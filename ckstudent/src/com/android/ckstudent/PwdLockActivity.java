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
		builder.setTitle("密碼鎖");
		builder.setView(pass_view);
		
		final EditText pass_first = (EditText)pass_view.findViewById(R.id.editText1);
		final EditText pass_retry = (EditText)pass_view.findViewById(R.id.editText2);
		
		builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//如果都沒輸入
				if (pass_first.getEditableText().toString() != "NULL" || pass_retry.getEditableText().toString() != "NULL") {
					AlertDialog.Builder passcancel = new AlertDialog.Builder(PwdLockActivity.this);
					passcancel.setTitle("密碼錯誤");
					passcancel.setMessage("請輸入密碼");
					AlertDialog passdialog = passcancel.create();
					passdialog.show();
				}
				else if (pass_first.getEditableText().toString() != pass_retry.getEditableText().toString()) {
					//cancel
					AlertDialog.Builder passcancel = new AlertDialog.Builder(PwdLockActivity.this);
					passcancel.setTitle("密碼錯誤");
					passcancel.setMessage("兩次密碼不相符");
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
		builder.setNegativeButton("清除", new DialogInterface.OnClickListener() {
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
