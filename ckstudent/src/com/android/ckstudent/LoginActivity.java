package com.android.ckstudent;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
		final EditText edt1 = (EditText)this.findViewById(R.id.editText1);
		final EditText edt2 = (EditText)this.findViewById(R.id.editText2);
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (edt1.getEditableText().toString() == "" || edt2.getEditableText().toString() == "") {
					alertDialog(LoginActivity.this,"不可空白","請輸入帳號");
				}
				loginProcess.execute();
			}
		});
	}
	
	AsyncTask<Void, Void, Void> loginProcess = new AsyncTask<Void, Void, Void>() {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {				
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpRequest = new HttpGet("http://172.17.171.175:52968/api/Values/");
				HttpResponse httpResponse = httpClient.execute(httpRequest);		   
			    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			        HttpEntity httpEntity = httpResponse.getEntity();
			        InputStream content = httpEntity.getContent();
			    }
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	};

	//pop alert
	public void alertDialog(Context context, String title, String message) {
		AlertDialog.Builder passcancel = new AlertDialog.Builder(context);
		passcancel.setTitle(title);
		passcancel.setMessage(message);
		AlertDialog passdialog = passcancel.create();
		passdialog.show();
	}
}
