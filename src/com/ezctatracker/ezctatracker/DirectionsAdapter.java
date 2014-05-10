package com.ezctatracker.ezctatracker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DirectionsAdapter extends BaseAdapter {
	
	ArrayList<String> directions;
	Context context;
	LayoutInflater inflater;
	
	public DirectionsAdapter(ArrayList<String> directions, Context context) {
		this.directions = directions;
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return directions.size();
	}

	@Override
	public Object getItem(int pos) {
		return directions.get(pos);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int pos, View v, ViewGroup vg) {
		if (v == null) {
			v = inflater.inflate(R.layout.direction_row, vg, false);
		}
		
		TextView tv = (TextView)v.findViewById(R.id.directionsTextView);
		tv.setText(directions.get(pos));
		
		v.setBackgroundColor(pos % 2 == 1 ? Color.WHITE : Color.LTGRAY);
		
		return v;
	}

}
