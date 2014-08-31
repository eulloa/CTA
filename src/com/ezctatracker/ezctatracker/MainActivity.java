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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ezctatracker.ezctatracker.R;

public class MainActivity extends Activity implements OnItemClickListener,
OnCheckedChangeListener, SearchView.OnQueryTextListener{	
	
	ListView lv;
	RadioGroup rdioGrp;
	RadioButton buses;
	RadioButton trains;
	RadioButton myRoutes;
	MyAdapter adapter;
	SearchView searchView;	
	ProgressDialog pDialog;
	
	private static final int PROGRESS_BAR_TYPE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//RADIO GROUP
		rdioGrp = (RadioGroup)findViewById(R.id.rdioGrp);
		rdioGrp.setOnCheckedChangeListener(this);
		int sel = rdioGrp.getCheckedRadioButtonId();
		//DETECTS CHANGES IN RADIO GROUP
		onCheckedChanged(rdioGrp, sel);
	}		
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_BAR_TYPE:
			pDialog = new ProgressDialog(MainActivity.this);
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
	
	class DownloadFeed extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			onCreateDialog(PROGRESS_BAR_TYPE);
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
				
				ArrayList<String> routeNumbers = new ParseXML().getRouteNumbers(xpp);
				xpp.setInput(new StringReader(result)); //reset parser
				ArrayList<String> routeNames = new ParseXML().getRouteNames(xpp);				
				
				ArrayList<Map<String, String>> routes = new ArrayList<Map<String, String>>();
				for (int i = 0; i < routeNames.size(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("routeNumber", routeNumbers.get(i));
					map.put("routeName", routeNames.get(i));
					routes.add(map);
				}				
				
				lv = (ListView)findViewById(R.id.listView1);
				adapter = new MyAdapter(routes, MainActivity.this);
				lv.setAdapter(adapter);
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(MainActivity.this);
				
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
		//getMenuInflater().inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
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
		searchView.setQueryHint("Filter bus routes...");
	}
	
	@Override
	public boolean onQueryTextChange(String newText) {
		MainActivity.this.adapter.getFilter().filter(newText);		
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}
	
	public boolean isAlwaysExpanded() {
		return false;
	}
	
	//--------------------------------------------------------------//
	
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
		// CHECK IF BUS OR TRAIN LISTVIEW IS SELECTED
		// IN ORDER TO SHOW CORRECT LIST
		buses = (RadioButton)findViewById(R.id.rdioBus);
		trains = (RadioButton)findViewById(R.id.rdioTrain);
		if (buses.isChecked()) {
			//get route number
			TextView routeNum = (TextView)v.findViewById(R.id.routeNumber);
			String routeNumber = routeNum.getText().toString();
			
			//get route name
			TextView nameOfRoute = (TextView)v.findViewById(R.id.routeName);
			String routeName = nameOfRoute.getText().toString();
			
			Intent getDirections = new Intent(this, RouteDirections.class);
			getDirections.putExtra("routeNumber", routeNumber);
			getDirections.putExtra("routeName", routeName);
			startActivity(getDirections);
		} else { //train information is sent
			TextView line = (TextView)v.findViewById(R.id.trainsLine);
			String lineName = line.getText().toString();
			Intent lineDirections = new Intent(this, TrainStops.class);
			lineDirections.putExtra("line", lineName);
			startActivity(lineDirections);
		}		
	}

	//CHECKS FOR CHANGES IN RADIO GROUP
	@Override
	public void onCheckedChanged(RadioGroup rg, int id) {
		switch (id) {
		case R.id.rdioBus:
			String key = "?key=mfZVaeUXL5HctzzxpFGyd5FNX";
			String url = "http://www.ctabustracker.com/bustime/api/v1/getroutes" + key;
			new DownloadFeed().execute(url);
			break;
		case R.id.rdioTrain:
			getTrainLines();
			break;
		case R.id.rdioRoutes:
			Toast.makeText(getApplicationContext(), "Routes", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void getTrainLines() {
		ArrayList<String> trains = new ArrayList<String>();
		trains.add("Blue Line");
		trains.add("Brown Line");
		trains.add("Green Line");
		trains.add("Orange Line");
		trains.add("Pink Line");
		trains.add("Purple Line");
		trains.add("Red Line");
		trains.add("Yellow Line");
		lv = (ListView)findViewById(R.id.listView1);
		TrainsAdapter adapter = new TrainsAdapter(trains, MainActivity.this);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(MainActivity.this);
	}

}
