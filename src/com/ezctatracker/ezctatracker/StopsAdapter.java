package com.ezctatracker.ezctatracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.ezctatracker.ezctatracker.R;

public class StopsAdapter extends BaseAdapter implements Filterable{
	
	ArrayList<Map<String, String>> stops;
	ArrayList<Map<String, String>> orgStops;
	Context context;
	LayoutInflater inflater;
	Filter myFilter;
	
	public StopsAdapter(ArrayList<Map<String, String>> stops, Context context) {
		this.stops = stops;
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return stops.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View v, ViewGroup vg) {
		if (v == null) {
			v = inflater.inflate(R.layout.row, vg, false);
		}
		
		TextView stopID = (TextView)v.findViewById(R.id.routeNumber);
		TextView stopName = (TextView)v.findViewById(R.id.routeName);
		
		stopID.setText(stops.get(pos).get("stopId"));
		stopID.setVisibility(8); //"gone" textView is no longer in the cell
		stopName.setText(stops.get(pos).get("stopName"));
		
		v.setBackgroundColor(pos % 2 == 1 ? Color.WHITE : Color.LTGRAY);
		
		return v;
	}

	@Override
	public Filter getFilter() {
		myFilter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				ArrayList<Map<String, String>> results = new ArrayList<Map<String, String>>();
				
				if (orgStops == null) {
					orgStops = new ArrayList<Map<String, String>>(stops);
				}
				
				if (constraint == null || constraint.length() == 0) {
					filterResults.values = orgStops;
					filterResults.count = orgStops.size();
				} else {
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < orgStops.size(); i++) {
						Map<String, String> map = new HashMap<String, String>();
						String id = orgStops.get(i).get("stopId").toString();
						String stop = orgStops.get(i).get("stopName").toString();
						if (stop.toLowerCase().startsWith(constraint.toString()) ||
								stop.contains(constraint.toString())) {
							map.put("stopId", id);
							map.put("stopName", stop);
							results.add(map);
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
				stops = (ArrayList<Map<String, String>>) results.values;
				notifyDataSetChanged();				
			}			
		};
		
		return myFilter;
	}

}
