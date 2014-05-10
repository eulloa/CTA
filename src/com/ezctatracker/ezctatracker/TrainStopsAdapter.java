package com.ezctatracker.ezctatracker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class TrainStopsAdapter extends BaseAdapter implements Filterable{
	
	ArrayList<String> stops;
	ArrayList<String> orgStops;
	Context context;
	LayoutInflater inflater;
	Filter myFilter;
	
	public TrainStopsAdapter (ArrayList<String> stops, Context context) {
		this.stops = stops;
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return stops.size();
	}

	@Override
	public Object getItem(int pos) {
		return stops.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return 0;
	}

	@Override
	public View getView(int pos, View v, ViewGroup vg) {
		if (v == null) {
			v = inflater.inflate(R.layout.train_stops_row, vg, false);
		}
		
		TextView tv = (TextView)v.findViewById(R.id.trainStopsTextView);
		tv.setText(stops.get(pos));
		
		v.setBackgroundColor(pos % 2 == 1 ? Color.WHITE : Color.LTGRAY);		
		
		return v;
	}

	@Override
	public Filter getFilter() {
		myFilter = new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				ArrayList<String> results = new ArrayList<String>();
				
				if (orgStops == null) {
					orgStops = new ArrayList<String>(stops);
				}
				
				if (constraint == null || constraint.length() == 0) {
					filterResults.values = orgStops;
					filterResults.count = orgStops.size();
				} else {
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < orgStops.size(); i++) {
						String stop = orgStops.get(i);
						if (stop.toLowerCase().startsWith(constraint.toString())) {
							results.add(stop);
						}
					}
					filterResults.values = results;
					filterResults.count = results.size();
				}
				
				return filterResults;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				stops = (ArrayList<String>) results.values;
				notifyDataSetChanged();
			}			
		};
		
		return myFilter;
	}

}
