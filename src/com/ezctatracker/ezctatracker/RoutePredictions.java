package com.ezctatracker.ezctatracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;

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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezctatracker.ezctatracker.R;
import com.ezctatracker.ezctatracker.models.Route;

public class RoutePredictions extends Activity implements OnClickListener{
	
	ListView lv;
	ImageButton fav;
	TextView routeTV;
	TextView stopName;
	TextView currentDateTV;
	ProgressDialog pDialog;
	String key = "?key=mfZVaeUXL5HctzzxpFGyd5FNX";
	String ctaCall = "http://www.ctabustracker.com/bustime/api/v1/getpredictions";
	private static final int PROGRESS_BAR_TYPE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_predictions);
		
		fav = (ImageButton)findViewById(R.id.predictionBusFav);
		fav.setOnClickListener(this);
		
		new DownloadPredictions().execute();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_BAR_TYPE:
			pDialog = new ProgressDialog(RoutePredictions.this);
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
	
	class DownloadPredictions extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			onCreateDialog(PROGRESS_BAR_TYPE);
		}
		
		@Override
		protected String doInBackground(Void... params) {
			Intent intent = getIntent();
			String routeNumber = intent.getStringExtra("routeNumber");
			String stopID = intent.getStringExtra("stopID");
			
			String url = ctaCall + key + "&rt=" + routeNumber + "&stpid=" + stopID + "&top=5";
			String feed = getFeed(url);
			return feed;
		}
		
		@Override
		protected void onPostExecute(String result) {
			try {				
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(result));
				
				ArrayList<String> predictions = new ParseXML().getPredictions(xpp);
				
				//NAME OF STOP
				xpp.setInput(new StringReader(result));
				ArrayList<String> stopNames = new ParseXML().getStopNames(xpp);
				
				//------ HANDLES NO SERVICE SCHEDULED INSTANCES --------------//
				if (predictions.size() > 0) {
					lv = (ListView)findViewById(R.id.predictionsListView);
					RoutePredictionsAdapter adapter = new RoutePredictionsAdapter(predictions,
							RoutePredictions.this);
					lv.setAdapter(adapter);
					lv.setSelector(android.R.color.transparent);
				} else {
					ArrayList<String> array = new ArrayList<String>();
					array.add("No service scheduled");
					
					//IF NO STOP NAMES WERE PRODUCED (IF THERE IS NO MORE SCHEDULED SERVICE)
					stopNames.add("No stop selected");
					
					lv = (ListView)findViewById(R.id.predictionsListView);
					RoutePredictionsAdapter adapter = new RoutePredictionsAdapter(array, 
							RoutePredictions.this);
					lv.setAdapter(adapter);
					lv.setSelector(android.R.color.transparent);
				}				
				///////////////////////////////////////////////////////////////////
				
				//get route info, direction and time from intent
				Intent intent = getIntent();
				String routeNumber = intent.getStringExtra("routeNumber");
				String nameOfRoute = intent.getStringExtra("routeName");
				String direction = intent.getStringExtra("direction");
								
				Calendar cal = Calendar.getInstance();
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				int month = cal.get(Calendar.MONTH);
				int year = cal.get(Calendar.YEAR);
				int day = cal.get(Calendar.DAY_OF_MONTH);
				
				int hour = cal.get(Calendar.HOUR_OF_DAY);
				int mins = cal.get(Calendar.MINUTE);
				String minutes = fixMins(mins);
				
				//declare textviews	& set text	
				routeTV = (TextView)findViewById(R.id.predictionRouteInfo);
				stopName = (TextView)findViewById(R.id.predictionStopName);
				currentDateTV = (TextView)findViewById(R.id.predictionCurrentDate);
				
				routeTV.setText(routeNumber + " " + nameOfRoute + " " + direction);
				stopName.setText(stopNames.get(0).toString());
				currentDateTV.setText(getDay(dayOfWeek) + " " + getMonth(month) + " " +
									  day + ", "+ year + " " + fixHour(hour, minutes));
				
				pDialog.dismiss();
								
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_predictions, menu);
		return true;
	}
	
	//MENU (REFRESH)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
			
		case R.id.refreshPredictions:
			//refreshPredictions(item); //update with fresh data
			new DownloadPredictions().execute();
		break;
		//ALERT
		case R.id.backFromPredictions:
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
				new DownloadPredictions().execute();
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

	//---------------------- ADD ROUTE TO FAVORITES -------------------------//
	@Override
	public void onClick(View v) 
	{
		Intent intent = getIntent();
		String name = intent.getStringExtra("routeName");
		String routeNumber = intent.getStringExtra("routeNumber");
		String stopId = intent.getStringExtra("stopID");
		
		switch (v.getId()) 
		{
			case R.id.predictionBusFav:
				if (fav.getTag().toString().equals("unfav")) 
				{		
					Route busRoute = new Route();
					{
						busRoute.setIsFav(true);
						busRoute.setName(name);
						busRoute.setRouteNumber(routeNumber);
						busRoute.setStopId(stopId);
					};
					
					Toast.makeText(getApplicationContext(), "Added to your routes!", 
							Toast.LENGTH_LONG).show();
					
					Toast.makeText(getApplicationContext(), busRoute.getName() + "\n" + busRoute.getRouteNumber() + "\n" + busRoute.getStopId(), Toast.LENGTH_LONG).show();
					//to add that route to the DB
					 
					fav.setImageResource(R.drawable.fav);
					fav.setTag("fav");
				} 
				
				else if (fav.getTag().toString().equals("fav")) 
				{
					fav.setImageResource(R.drawable.unfav);
					fav.setTag("unfav");
				}
				
			break;
		}
	}
	//---------------------------------------------------------------------------//
	
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
