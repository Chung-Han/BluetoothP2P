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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class AssociationFragment2 extends ListFragment {
	ClubAdapter adapter;
	ArrayList<Club> clubs;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment2_list, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		clubs = new ArrayList<Club>();
		adapter = new ClubAdapter(this.getActivity(), clubs);
		this.setListAdapter(adapter);
		//getClubData.execute();
		Club club = new Club();
		club.name = "¬y»RªÀ";
		clubs.add(club);
	}

	AsyncTask<Void, Void, Void> getClubData = new AsyncTask<Void, Void, Void>() {
		@Override
		protected Void doInBackground(Void... arg0) {
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
					// Convert JSON to internal product list
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						Club club = new Club();
						club.name = jsonObject.getString("Name");
						clubs.add(club);
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
		}
	};
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("clubName", clubs.get(position).name);
		intent.putExtras(bundle);
		intent.setClass(getActivity(), ClubContent.class);
        startActivity(intent);
	}
}
