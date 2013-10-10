package com.android.ckstudent;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		Button btn1 = (Button)this.findViewById(R.id.button1);
		Button btn4 = (Button)this.findViewById(R.id.button4);
		
		//¤Á´«activity
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				
				intent.setClass(MenuActivity.this, SelectListActivity.class);
				MenuActivity.this.startActivity(intent);
				//MenuActivity.this.finish();
			}
		});
		
		btn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				
				intent.setClass(MenuActivity.this, AssociationActivity.class);
				MenuActivity.this.startActivity(intent);
				//MenuActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}
