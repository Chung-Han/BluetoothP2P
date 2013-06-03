package com.android.ckstudent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivityS extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_s);
		
		Button btn1 = (Button)this.findViewById(R.id.button1);
		Button btn2 = (Button)this.findViewById(R.id.button2);
		Button btn3 = (Button)this.findViewById(R.id.button3);
		Button btn4 = (Button)this.findViewById(R.id.button4);
		
		//change to wifidirectactivity
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MenuActivityS.this, WifiDirectActivity.class);
				MenuActivityS.this.startActivity(intent);
			}
		});
		
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		
		btn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		
		btn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}
}
