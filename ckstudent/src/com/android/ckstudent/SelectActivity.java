package com.android.ckstudent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		
		Button btn1 = (Button)this.findViewById(R.id.button1);
		Button btn2 = (Button)this.findViewById(R.id.button2);
		Button btn3 = (Button)this.findViewById(R.id.button3);
		Button btn4 = (Button)this.findViewById(R.id.button4);
		
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SelectActivity.this, PwdLockActivity.class);
				SelectActivity.this.startActivity(intent);
			}
		});
		
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//picture_lock
				Intent intent = new Intent();
				intent.setClass(SelectActivity.this, PicLockActivity.class);
				SelectActivity.this.startActivity(intent);
			}
		});
		
		btn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//move_lock
				Intent intent = new Intent();
				intent.setClass(SelectActivity.this, MoveLockActivity.class);
				SelectActivity.this.startActivity(intent);
			}
		});
		
		btn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SelectActivity.this, RunActivity.class);
				SelectActivity.this.startActivity(intent);
				//SelectActivity.this.finish();
			}
		});
	}

}
