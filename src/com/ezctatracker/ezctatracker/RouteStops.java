package com.ezctatracker.ezctatracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.ezctatracker.ezctatracker.R;

public class RouteStops extends Activity implements OnItemClickListener,
SearchView.OnQueryTextListener{
	
	ListView lv;
	StopsAdapter adapter;
	SearchView searchView;
	ProgressDialog pDialog;
	private static final int PROGRESS_BAR_STYLE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_stops);
		
		Intent intent = getIntent();
		String routeNumber = intent.getStringExtra("routeNumber");
		String direction = intent.getStringExtra("direction");
		
		String key = "?key=mfZVaeUXL5HctzzxpFGyd5FNX";
		String ctaCall = "http://www.ctabustracker.com/bustime/api/v1/getstops";
		String url = ctaCall + key + "&rt=" + routeNumber + "&dir=" + direction;
		
		new DownloadStops().execute(url);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_BAR_STYLE: 
			pDialog = new ProgressDialog(RouteStops.this);
			pDialog.setTitle("Download in Progress");
			pDialog.setMessage("Fetching stops...");
			pDialog.setIndeterminate(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setCancelable(true);
			pDialog.show();
			return pDialog;
		default:
			return null;
		}		
	}
	
	class DownloadStops extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			onCreateDialog(PROGRESS_BAR_STYLE);
		}

		@Override
		protected String doInBackground(String... params) {
			String url = params[0];
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
				
				ArrayList<String> stops = new ParseXML().getStopNames(xpp);
				xpp.setInput(new StringReader(result)); //reset parser
				ArrayList<String> stopIDs = new ParseXML().getStopIDs(xpp);
				
				ArrayList<Map<String, String>> listOfStops = new ArrayList<Map<String, String>>();
				for (int i = 0; i < stops.size(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("stopId", stopIDs.get(i));
					map.put("stopName", stops.get(i));
					listOfStops.add(map);
				}
				
				lv = (ListView)findViewById(R.id.stopsListView);				
				adapter = new StopsAdapter(listOfStops, RouteStops.this);				
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(RouteStops.this);
				
				pDialog.dismiss();
				
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}

	//---------------------- SET UP MENU AND SEARCHVIEW ---------------//
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.route_stops, menu);
		MenuItem searchItem = menu.findItem(R.id.stops_search);
		searchView = (SearchView) searchItem.getActionView();
		setUpSearchView(searchItem);
		return true;
	}
	
	public void setUpSearchView(MenuItem searchItem) {
		if (isAlwaysExpanded()) {
			searchView.setIconifiedByDefault(false);
		} else {
			searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
					| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}
		searchView.setOnQueryTextListener(this);		
		searchView.setQueryHint("Filter bus stops...");
	}
	
	@Override
	public boolean onQueryTextChange(String newText) {
		RouteStops.this.adapter.getFilter().filter(newText);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}
	
	public boolean isAlwaysExpanded() {
		return false;
	}
	
	//--------------------------------------------------------------//
	
	//MENU CLICK
			@Override
			public boolean onOptionsItemSelected(MenuItem item) {
				switch (item.getItemId()) {
				case android.R.id.home:
					NavUtils.navigateUpFromSameTask(this);
					return true;
					
				case R.id.backFromStops:
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
				}
				return super.onOptionsItemSelected(item);
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
	public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
		// get routeNumber
		Intent intent = getIntent();
		String routeNumber = intent.getStringExtra("routeNumber");
		
		//get routeName and direction
		String routeName = intent.getStringExtra("routeName");
		String direction = intent.getStringExtra("direction");
		
		TextView stopID = (TextView)v.findViewById(R.id.routeNumber);
		String ID = stopID.getText().toString();
		
		Intent routePredictions = new Intent(this, RoutePredictions.class);
		//send route number and stop id
		routePredictions.putExtra("routeNumber", routeNumber);
		routePredictions.putExtra("stopID", ID);
		routePredictions.putExtra("routeName", routeName);
		routePredictions.putExtra("direction", direction);
		startActivity(routePredictions);
	}

}
