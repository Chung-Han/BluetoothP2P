package com.android.ckstudent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class CallActivity extends ListActivity{
	StudentAdapter adapter;
	ArrayList<Student> students;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_list);
		students = new ArrayList<Student>();
		adapter = new StudentAdapter(this, students);
		this.setListAdapter(adapter);
		getStudentData.execute();
	}
	
	AsyncTask<Void, Void, Void> getStudentData = new AsyncTask<Void, Void, Void>() {
		@Override
		protected Void doInBackground(Void... arg0) {
	        Bundle courseName = CallActivity.this.getIntent().getExtras();
	        String course = courseName.getString("Name");
			try {				
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpRequest = new HttpGet("http://172.17.171.175:52968/api/Values/" + course);
				HttpResponse httpResponse = httpClient.execute(httpRequest);		   
			    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			        HttpEntity httpEntity = httpResponse.getEntity();
			        InputStream content = httpEntity.getContent();			        
			        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
			        StringBuilder builder = new StringBuilder();
			        String line;
			        while ((line = reader.readLine()) != null) {
			        	builder.append(line);
			        }
					JSONArray jsonArray =  new JSONArray(builder.toString());
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						Student student = new Student();
						student.name = jsonObject.getString("Name");
						students.add(student);
					}			        
			    }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			adapter.notifyDataSetChanged();
	        Toast.makeText(CallActivity.this, "Please select course.", Toast.LENGTH_LONG).show();
		}
	};
	
	AsyncTask<Void, Void, Void> putStudentData = new AsyncTask<Void, Void, Void>() {
		@Override
		protected Void doInBackground(Void... arg0) {
			Bundle courseName = CallActivity.this.getIntent().getExtras();
	        String course = courseName.getString("Name");
			try {				
				HttpClient httpClient = new DefaultHttpClient();
				//加上日期 + 0920 九月二十日
				HttpPut httpRequest = new HttpPut("http://172.17.171.175:52968/api/Values/" + course);
				JSONArray jsonArray = new JSONArray();
				//讀進學生名單 for each 轉JSON物件 再轉ARRAY put 上WEB
				try {
					for(int i = 0; i < students.size(); i++) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("Name", students.get(i).name.toString());
						jsonObject.put("Status", students.get(i).status.toString());
						jsonArray.put(jsonObject);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				StringEntity stringEntity = new StringEntity(jsonArray.toString());
				httpRequest.setEntity(stringEntity);
				
				HttpResponse httpResponse = httpClient.execute(httpRequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					Toast.makeText(CallActivity.this, "Upload success!", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(CallActivity.this, "Something wrong!", Toast.LENGTH_LONG).show();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			finish();
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "結束");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0:
			Toast.makeText(CallActivity.this, "End and Send finished list", Toast.LENGTH_LONG).show();
			//putStudentData.execute();
		}
		return true;
	}
}
