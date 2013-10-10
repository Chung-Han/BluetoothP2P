package com.android.ckstudent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class FestivalContent extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.festival_view);
		Bundle bundle = this.getIntent().getExtras();
        String festivalName = bundle.getString("festivalName");
        Toast.makeText(this, festivalName, Toast.LENGTH_SHORT).show();
		//getFestival.execute(festivalName);
	}

	AsyncTask<String, Void, Festival> getFestival = new AsyncTask<String, Void, Festival>() {
		@Override
		protected Festival doInBackground(String... festivalName) {
			try {				
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpRequest = new HttpGet("http://172.17.171.175:52968/api/Values/" + festivalName);
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
			        
					JSONObject jsonObject = new JSONObject(builder.toString());
					Festival festival = new Festival();
					festival.name = jsonObject.getString("Name");
					festival.date = jsonObject.getString("Date");
					festival.place = jsonObject.getString("Place");
					festival.content = jsonObject.getString("Content");
					return festival;
			    }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Festival festival) {
			super.onPostExecute(festival);
			((TextView)findViewById(R.id.textView1)).setText(festival.name);
			((TextView)findViewById(R.id.textView2)).setText(festival.date);
			((TextView)findViewById(R.id.textView3)).setText(festival.place);
			((TextView)findViewById(R.id.textView4)).setText(festival.content);
		}
	};
}
