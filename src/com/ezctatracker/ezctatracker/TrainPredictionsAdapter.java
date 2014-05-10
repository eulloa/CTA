package com.ezctatracker.ezctatracker;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TrainPredictionsAdapter extends BaseAdapter {
	
	ArrayList<String> serviceHeaders;
	ArrayList<String> predictions;
	ArrayList<String> line;
	Context context;
	LayoutInflater inflater;
	
	public TrainPredictionsAdapter(ArrayList<String> serviceHeaders, ArrayList<String> predictions,
			ArrayList<String> line, Context context) {
		this.serviceHeaders = serviceHeaders;
		this.predictions = predictions;
		this.line = line;
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return serviceHeaders.size();
	}

	@Override
	public Object getItem(int pos) {
		return serviceHeaders.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int pos, View v, ViewGroup vg) {
		if (v == null) {
			v = inflater.inflate(R.layout.train_prediction_row, vg, false);
		}
		
		ImageView lineColor = (ImageView)v.findViewById(R.id.trainPredictionsLine);
		TextView header = (TextView)v.findViewById(R.id.trainPredictionsService);
		TextView prediction = (TextView)v.findViewById(R.id.trainPredictionsTime);		
		
		String color = line.get(pos).toString();
		if (color.equalsIgnoreCase("Blue")) {
			lineColor.setImageResource(R.drawable.blue);
		} else if (color.equalsIgnoreCase("Brn")) {
			lineColor.setImageResource(R.drawable.brown);
		} else if (color.equalsIgnoreCase("G")) {
			lineColor.setImageResource(R.drawable.green);
		} else if (color.equalsIgnoreCase("Org")) {
			lineColor.setImageResource(R.drawable.orange);
		} else if (color.equalsIgnoreCase("Pink")) {
			lineColor.setImageResource(R.drawable.pink);
		} else if (color.equalsIgnoreCase("P")) {
			lineColor.setImageResource(R.drawable.purple);
		} else if (color.equalsIgnoreCase("Red")) {
			lineColor.setImageResource(R.drawable.red);
		} else if (color.equalsIgnoreCase("Y")) {
			lineColor.setImageResource(R.drawable.yellow);
		} else if (color.equalsIgnoreCase("No")) {
			lineColor.setImageResource(R.drawable.noservice);
		}
		
		header.setText(serviceHeaders.get(pos));
		prediction.setText(predictions.get(pos));
				
		return v;
	}

}
