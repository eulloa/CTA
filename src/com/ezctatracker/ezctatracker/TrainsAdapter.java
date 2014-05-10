package com.ezctatracker.ezctatracker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

public class TrainsAdapter extends BaseAdapter{
	
	ArrayList<String> trains;
	ArrayList<String> org;
	Context context;
	LayoutInflater inflater;
	Filter myFilter;
	
	public TrainsAdapter(ArrayList<String> trains, Context context) {
		this.trains = trains;
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return trains.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int pos, View v, ViewGroup vg) {		
		if (v ==  null){
			v = inflater.inflate(R.layout.trains_row, vg, false);
		}
		
		ImageView color = (ImageView)v.findViewById(R.id.trainsColor);
		TextView lineTV = (TextView)v.findViewById(R.id.trainsLine);
		
		String line = trains.get(pos).toString(); 
		lineTV.setText(line);
		
		if (line.equalsIgnoreCase("Blue Line")) {
			color.setImageResource(R.drawable.blue);
		} else if (line.equalsIgnoreCase("Brown Line")) {
			color.setImageResource(R.drawable.brown);
		} else if (line.equalsIgnoreCase("Green Line")) {
			color.setImageResource(R.drawable.green);
		} else if (line.equalsIgnoreCase("Orange Line")) {
			color.setImageResource(R.drawable.orange);
		} else if (line.equalsIgnoreCase("Pink Line")) {
			color.setImageResource(R.drawable.pink);
		} else if (line.equalsIgnoreCase("Purple Line")) {
			color.setImageResource(R.drawable.purple);
		} else if (line.equalsIgnoreCase("Red Line")) {
			color.setImageResource(R.drawable.red);
		} else if (line.equalsIgnoreCase("Yellow Line")) {
			color.setImageResource(R.drawable.yellow);
		}
		
		v.setBackgroundColor(pos % 2 == 1 ? Color.WHITE : Color.LTGRAY);
		
		return v;
	}
}
