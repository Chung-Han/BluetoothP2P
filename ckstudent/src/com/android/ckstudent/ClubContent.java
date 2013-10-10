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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class ClubContent extends Activity {
	ListView listView;
	FestivalAdapter adapter;
	ArrayList<Festival> festivals;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.club_view);
		listView = (ListView)findViewById(R.id.recentList);
		Bundle bundle = this.getIntent().getExtras();
        String clubName = bundle.getString("clubName");
        
        festivals = new ArrayList<Festival>();
		adapter = new FestivalAdapter(this, festivals);
		listView.setAdapter(adapter);
        
        //getClub.execute(clubName);
        //getClubRecent.execute(clubName);
	}

	AsyncTask<String, Void, Club> getClub = new AsyncTask<String, Void, Club>() {
		@Override
		protected Club doInBackground(String... clubName) {
			try {				
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpRequest = new HttpGet("http://172.17.171.175:52968/api/Values/" + clubName);
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
					Club club = new Club();
					club.name = jsonObject.getString("Name");
					club.place = jsonObject.getString("Place");
					club.time = jsonObject.getString("Time");
					return club;
			    }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Club club) {
			super.onPostExecute(club);
			((TextView)findViewById(R.id.textView1)).setText(club.name);
			((TextView)findViewById(R.id.textView2)).setText(club.place);
			((TextView)findViewById(R.id.textView3)).setText(club.time);
			//((TextView)findViewById(R.id.textView4)).setText(club.content);
		}
	};
	
	AsyncTask<String, Void, Club> getClubRecent = new AsyncTask<String, Void, Club>() {
		@Override
		protected Club doInBackground(String... clubName) {
			try {				
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpRequest = new HttpGet("http://172.17.171.175:52968/api/Values/" + clubName);
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
						Festival festival = new Festival();
						festival.name = jsonObject.getString("Name");
						festival.date = jsonObject.getString("Date");
						festivals.add(festival);
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
		protected void onPostExecute(Club club) {
			super.onPostExecute(club);
			adapter.notifyDataSetChanged();
		}
	};
}
