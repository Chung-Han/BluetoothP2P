package com.android.ckstudent;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class CourseAdapter extends BaseAdapter {
	ArrayList<Course> courses;
	Context context;
	
	CourseAdapter(Context context, ArrayList<Course> courses) {
		this.context = context;
		this.courses = courses;
	}
	@Override
	public int getCount() {
		return courses.size();
	}
	@Override
	public Object getItem(int position) {
		return courses.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view;
		final String course;
		if (convertView == null) {
				view = inflater.inflate(R.layout.row_courses, null);
				TextView textView = (TextView) view.findViewById(R.id.text1);
				textView.setText(courses.get(position).Name);
	 
				textView = (TextView) view.findViewById(R.id.text2);
				textView.setText(courses.get(position).Date);
				
				course = courses.get(position).Name;
				
				ImageButton imageButton = (ImageButton)view.findViewById(R.id.imageButton1);
				imageButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						//send courseName let teacher get student list.
						Intent intent = new Intent();
						intent.setClass(context, CallActivity.class);
						
						Bundle courseName = new Bundle();
						courseName.putString("Name", course);
						intent.putExtras(courseName);
						context.startActivity(intent);
					}
				});
			} else {
				view = (View) convertView;
			}
			return view;
	}

}
