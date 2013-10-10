package com.android.ckstudent;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class StudentAdapter extends BaseAdapter{
	ArrayList<Student> students;
	Context context;
	
	StudentAdapter(Context context, ArrayList<Student> students) {
		this.context = context;
		this.students = students;
	}
	@Override
	public int getCount() {
		return students.size();
	}
	@Override
	public Object getItem(int position) {
		return students.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view;
		final int listPosition = position;
		if (convertView == null) {
				view = inflater.inflate(R.layout.row_student, null);
				TextView textView = (TextView) view.findViewById(R.id.text1);
				textView.setText(students.get(position).name);
				final CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkBox1);
				checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (checkBox.isChecked() == true) {
							students.get(listPosition).status = "Present";
						} else {
							students.get(listPosition).status = "Absent";
						}
					}
				});
			} else {
				view = (View) convertView;
			}
			return view;
	}
}
