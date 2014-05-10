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

public class MyAdapter extends BaseAdapter implements Filterable{
	
	ArrayList<Map<String, String>> routes;
	ArrayList<Map<String, String>> orgRoutes;
	Context context;
	LayoutInflater inflater;
	Filter myFilter;
	
	public MyAdapter(ArrayList<Map<String, String>> routes, Context context) {
		this.routes = routes;
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return routes.size();
	}

	@Override
	public Object getItem(int pos) {
		//find the index where the correct route name is and pass that as the pos arg	
		// String num = routes.get(pos).getRouteNumber().toString();		
		return routes.get(pos).get("routeNumber").toString();
		//return routes.get(pos);
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
		
		TextView routeNum = (TextView)v.findViewById(R.id.routeNumber);
		TextView routeName = (TextView)v.findViewById(R.id.routeName);
				
		String num = routes.get(pos).get("routeNumber");
		String name = routes.get(pos).get("routeName");
		
		routeNum.setText(num);
		routeName.setText(name);	
		
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
				
				if(orgRoutes == null) {
					orgRoutes = new ArrayList<Map<String, String>>(routes);
				}
				
				if (constraint == null || constraint.length() == 0) {
					filterResults.values = orgRoutes;
					filterResults.count = orgRoutes.size();
				} else {
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < orgRoutes.size(); i++) {
						Map<String, String> map = new HashMap<String, String>();
						String name = orgRoutes.get(i).get("routeName").toString();
						String num = orgRoutes.get(i).get("routeNumber").toString();
						if (name.toLowerCase().startsWith(constraint.toString()) ||
								num.startsWith(constraint.toString())) {
							map.put("routeNumber", num);
							map.put("routeName", name);
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
				routes = (ArrayList<Map<String, String>>) results.values;
				notifyDataSetChanged();				
			}			
		};		
		return myFilter;
	}

}
