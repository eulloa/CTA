package com.ezctatracker.ezctatracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.ezctatracker.ezctatracker.R;

public class RouteDirections extends Activity implements OnItemClickListener{
	
	ListView lv;
	ProgressDialog pDialog;
	private static final int PROGRESS_BAR_TYPE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_directions);
		
		Intent intent = getIntent();
		String routeNumber = intent.getStringExtra("routeNumber");
		
		String key = "?key=mfZVaeUXL5HctzzxpFGyd5FNX";
		String ctaCall = "http://www.ctabustracker.com/bustime/api/v1/getdirections";
		String url = ctaCall + key + "&rt=" + routeNumber;
		
		new DownloadRouteDirections().execute(url);		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_BAR_TYPE:
			pDialog = new ProgressDialog(RouteDirections.this);
			pDialog.setTitle("Download in Progress");
			pDialog.setMessage("Fetching directions...");
			pDialog.setIndeterminate(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setCancelable(true);
			pDialog.show();
			return pDialog;
		default:
			return null;
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.route_directions, menu);
		return true;
	}
	
	//MENU CLICK
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
				
			case R.id.backFromDirections:
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
	
	class DownloadRouteDirections extends AsyncTask<String, Void, String> {
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
				
				ArrayList<String> directions = new ParseXML().getRouteDirections(xpp);
				
				lv = (ListView)findViewById(R.id.directionsListView);
				DirectionsAdapter adapter = new DirectionsAdapter(directions, RouteDirections.this);
				
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(RouteDirections.this);
				
				pDialog.dismiss();
				
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
		// get routeNumber again
		Intent intent = getIntent();
		String routeNumber = intent.getStringExtra("routeNumber");
		
		//get routeName
		String routeName = intent.getStringExtra("routeName");
		
		String directionSelected = adapter.getItemAtPosition(pos).toString();
		Intent getStops = new Intent(this, RouteStops.class);
		getStops.putExtra("direction", directionSelected);
		getStops.putExtra("routeNumber", routeNumber);
		getStops.putExtra("routeName", routeName);
		startActivity(getStops);
	}

}
