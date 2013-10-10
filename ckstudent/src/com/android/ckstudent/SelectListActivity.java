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
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;

public class SelectListActivity extends ListActivity {
	CourseAdapter adapter;
	ArrayList<Course> courses;
	
	AsyncTask<Void, Void, Void> getCourseData = new AsyncTask<Void, Void, Void>() {
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			adapter.notifyDataSetChanged();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			//get teacher name
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpRequest = new HttpGet("http://172.17.171.175:52968/api/Values/");
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
						Course course = new Course();
						course.Name = jsonObject.getString("Name");
						course.Date = jsonObject.getString("Date");
						courses.add(course);
					}		
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		courses = new ArrayList<Course>();
		adapter = new CourseAdapter(this, courses);
		this.setListAdapter(adapter);
		getCourseData.execute();
	}
}
