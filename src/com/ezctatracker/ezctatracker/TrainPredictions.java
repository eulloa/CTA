package com.ezctatracker.ezctatracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TrainPredictions extends Activity{
	
	ListView lv;
	//ImageButton fav;
	ImageView line;
	TextView routeInfo;
	TextView stopName; 
	TextView date;
	ProgressDialog pDialog;
	private static final int PROGRESS_BAR_TYPE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_predictions);
		
		/*fav = (ImageButton)findViewById(R.id.predictionTrainFav);
		fav.setOnClickListener(this);*/
		
		line = (ImageView)findViewById(R.id.trainPredictionsLine);
		
		new FindParentId().execute(); //opens stops.txt file and finds parent id
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_BAR_TYPE:
			pDialog = new ProgressDialog(TrainPredictions.this);
			pDialog.setTitle("Download in Progress");
			pDialog.setMessage("Fetching route predictions...");
			pDialog.setIndeterminate(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setCancelable(true);
			pDialog.show();
			return pDialog;
		default:
			return null;
		}
	}
	
	class FindParentId extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			onCreateDialog(PROGRESS_BAR_TYPE);			
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			//get stop user selected
			Intent intent = getIntent();
			String line = intent.getStringExtra("line");
			line = line.replace(" Line", ""); //strip Line off
			String target = intent.getStringExtra("stopName") + "-" + line; 
			
			String parentId;
			String name;						
			String l;
			
			Scanner s = null;
			
			try {
				InputStream is = getAssets().open("stops.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				while ((l = br.readLine()) != null) {
					s = new Scanner(l).useDelimiter("\\,");
					while (s.hasNext()) {
						parentId = s.next();
						s.next();
						name = s.next();
						s.nextLine();
						name = name.replace("\"", "");						
						if (target.equalsIgnoreCase(name) || target.contains(name)) {
							new MakePrediction().execute(parentId);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {			
				s.close();
			}
			return null;
		}	
	}
	
	class MakePrediction extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			String parentId = params[0];
			String key = "?key=0372ce7eb8b041b3b0291b1c74ce1791";
			String arrivals = "http://lapi.transitchicago.com/api/1.0/ttarrivals.aspx";
			String id = "&mapid=" + parentId;
			String call = arrivals + key + id;
			String feed = getFeed(call);			
			return feed;
		}
		
		@Override
		protected void onPostExecute(String result) {
			//Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();	
			//get info from intent and set textViews
			Intent intent = getIntent();
			String line = intent.getStringExtra("line");
			String stop = intent.getStringExtra("stopName");
			routeInfo = (TextView)findViewById(R.id.predictionTrainRouteInfo);
			stopName = (TextView)findViewById(R.id.predictionTrainStopName);
			date = (TextView)findViewById(R.id.predictionTrainCurrentDate);
			
			routeInfo.setText(line);
			stopName.setText(stop);
			
			//set the date and time
			Calendar cal = Calendar.getInstance();
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int mins = cal.get(Calendar.MINUTE);
			String minutes = fixMins(mins);
			
			date.setText(getDay(dayOfWeek) + " " + getMonth(month) + " " + 
						day + ", " + year + " " + fixHour(hour, minutes));
			
			try {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(result));
				
				//get info into arrays
				ArrayList<String> predictions = new ParseXML().getTrainPredictions(xpp);
				xpp.setInput(new StringReader(result));
				ArrayList<String> headers = new ParseXML().getTrainDestinations(xpp);
				xpp.setInput(new StringReader(result));
				ArrayList<String> colors = new ParseXML().getLineColor(xpp);
				
				//-------- HANDLES NO SERVICE SCHEDULED INSTANCES----------//
				if (predictions.size() > 0) {
					lv = (ListView)findViewById(R.id.predictionsTrainsListView);
					
					TrainPredictionsAdapter adapter = new TrainPredictionsAdapter(headers, predictions,
							colors, TrainPredictions.this);
					
					lv.setAdapter(adapter);
					lv.setSelector(android.R.color.transparent);
				} else {
					predictions.add("No service scheduled");
					headers.add("No service scheduled");
					colors.add("No");
					
					lv = (ListView)findViewById(R.id.predictionsTrainsListView);
					TrainPredictionsAdapter adapter = new TrainPredictionsAdapter(headers, predictions,
							colors, TrainPredictions.this);
					lv.setAdapter(adapter);
					lv.setSelector(android.R.color.transparent);
				}
			    //-------------------------------------------------------------//
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}		
			
			pDialog.dismiss();
		}		
	}
	
	public String getFeed(String url) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e("MAIN", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.train_predictions, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
			
		case R.id.backFromTrainPredictions:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Confirm");
			alert
				.setMessage("Are you sure you want to perform a new search?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						startActivity(intent);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//no action necessary
					}
				});
			alert.show();
			break;
		
		case R.id.refreshTrainPredictions:
			//refreshPredictions(item);
			new FindParentId().execute();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//--------------------- REFRESH MENU NOT CURRENTLY USED -----------------//
	//REFRESH MENU ITEM
	public void refreshPredictions(final MenuItem item) throws InterruptedException {
		LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView) inflater.inflate(R.layout.action_refresh, null);
			
		Animation rotation = AnimationUtils.loadAnimation(getApplication(), R.anim.refresh_rotate);
		rotation.setRepeatCount(Animation.INFINITE);
		iv.startAnimation(rotation);
		item.setActionView(iv);
			
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				new FindParentId().execute();
			}			
		});
		thread.start();
		thread.join();
		
		stopAnimation(item);
	}
	
	public void stopAnimation(final MenuItem item) {
		item.getActionView().clearAnimation();
		item.setActionView(null);
		//Drawable icon = getResources().getDrawable(R.drawable.refresh);
		//item.setIcon(icon);
	}	
	//--------------------- REFRESH MENU NOT CURRENTLY USED -----------------//

	/*
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.predictionTrainFav:
			if (fav.getTag().toString().equals("unfav")) {
				Toast.makeText(getApplicationContext(), "Route added to favorites", 
						Toast.LENGTH_LONG).show();
				fav.setImageResource(R.drawable.fav);
				fav.setTag("fav");
			} else if (fav.getTag().toString().equals("fav")) {
				fav.setImageResource(R.drawable.unfav);
				fav.setTag("unfav");
			}
			break;
		}
	}*/
	
	//-------------------- DATE & TIME FORMAT METHODS ------------------//
	public String getDay(int day) {
		String dayOfWeek = null;
		switch (day) {
		case 1 :
			dayOfWeek = "Sunday";
			break;
		case 2 :
			dayOfWeek = "Monday";
			break;
		case 3 :
			dayOfWeek = "Tuesday";
			break;
		case 4 :
			dayOfWeek = "Wednesday";
			break;
		case 5 :
			dayOfWeek = "Thursday";
			break;
		case 6 :
			dayOfWeek = "Friday";
			break;
		case 7 :
			dayOfWeek = "Saturday";
			break;
		}		
		return dayOfWeek;
	}
	
	public String getMonth(int month) {
		String theMonth = null;
		switch (month) {
		case 0 : 
			theMonth = "January";
			break;
		case 1 : 
			theMonth = "February";
			break;
		case 2 : 
			theMonth = "March";
			break;
		case 3 : 
			theMonth = "April";
			break;
		case 4 : 
			theMonth = "May";
			break;
		case 5 : 
			theMonth = "June";
			break;
		case 6 : 
			theMonth = "July";
			break;
		case 7 : 
			theMonth = "August";
			break;
		case 8 : 
			theMonth = "September";
			break;
		case 9 : 
			theMonth = "October";
			break;
		case 10 : 
			theMonth = "November";
			break;
		case 11 : 
			theMonth = "December";
			break;
		}		
		return theMonth;
	}
	
	public String fixHour(int hour, String mins) {
		String time = null;
		if (hour == 0 || hour == 24) {
			time = "12" + mins + "AM";
		} else if (hour == 12) {
			time = "12" + mins + "PM";
		} else if (hour >= 13) {
			hour = hour - 12;
			time = Integer.toString(hour) + mins + "PM";
		} else if (hour <= 11) {
			time = Integer.toString(hour) + mins + "AM";
		}		
		return time;
	}
	
	public String fixMins(int mins) {
		String formattedMins = null;
		if (mins < 10) {
			formattedMins = "0" + mins;
		} else {
			formattedMins = Integer.toString(mins);
		}
		return ":" + formattedMins;
	}
}