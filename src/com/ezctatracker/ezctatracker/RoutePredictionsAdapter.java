package com.ezctatracker.ezctatracker;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RoutePredictionsAdapter extends BaseAdapter {
	
	ArrayList<String> predictions;
	Context context;
	LayoutInflater inflater;
	
	public RoutePredictionsAdapter(ArrayList<String> predictions, Context context) {
		this.predictions = predictions;
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return predictions.size();
	}

	@Override
	public Object getItem(int pos) {		
		return predictions.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int pos, View v, ViewGroup vg) {
		if (v == null) {
			v = inflater.inflate(R.layout.prediction_row, vg, false);
		}
		
		TextView tv = (TextView)v.findViewById(R.id.predictionsTextView);
		tv.setText(predictions.get(pos));
		
		return v;
	}

}
