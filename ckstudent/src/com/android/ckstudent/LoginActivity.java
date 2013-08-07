package com.android.ckstudent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Button btn1 = (Button)this.findViewById(R.id.button1);
		Button btn2 = (Button)this.findViewById(R.id.button2);
		final EditText edt1 = (EditText)this.findViewById(R.id.editText1);
		final EditText edt2 = (EditText)this.findViewById(R.id.editText2);
		String type;
		
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				
				if (edt1.getEditableText().toString() == "" || edt2.getEditableText().toString() == "") {
					alertDialog(LoginActivity.this,"���i�ť�","�п�J�b��");
				}
				
				//Get data from database
				
				/*
				else if (�P��Ʈw���) {
					//�䤣��۲Ÿ��
					alertDialog(LoginActivity.this,"�b���K�X���~","�b���K�X���~");
				}
				*/
				/*
				if (��ƫ��A�O�ǥ�) {
					Intent sIntent = new Intent();
					sIntent.setClass(LoginActivity.this, MenuActivityS.class);
					LoginActivity.this.startActivity(sIntent);
				}
				else {
					//��ƫ��A�O�Ѯv
					Intent tIntent = new Intent();
					tIntent.setClass(LoginActivity.this, MenuActivity.class);
					LoginActivity.this.startActivity(tIntent);
				}
				*/
			}
		});
		
		//����activity
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				LoginActivity.this.startActivity(intent);
				//LoginActivity.this.finish();
			}
		});
	}

	//pop alert
	public void alertDialog(Context context, String title, String message) {
		AlertDialog.Builder passcancel = new AlertDialog.Builder(context);
		passcancel.setTitle(title);
		passcancel.setMessage(message);
		AlertDialog passdialog = passcancel.create();
		passdialog.show();
	}
}
