package com.android.ckstudent;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class AssociationFragment3 extends ListFragment {
	FestivalAdapter adapter;
	ArrayList<Festival> festivals;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment1_list, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		festivals = new ArrayList<Festival>();
		adapter = new FestivalAdapter(this.getActivity(), festivals);
		this.setListAdapter(adapter);
		
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Toast.makeText(getActivity(), "You press " + festivals.get(position).name, Toast.LENGTH_SHORT).show();
	}
}
