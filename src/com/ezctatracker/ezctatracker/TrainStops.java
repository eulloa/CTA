package com.ezctatracker.ezctatracker;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class TrainStops extends Activity implements OnItemClickListener,
SearchView.OnQueryTextListener{
	
	ListView lv;
	SearchView searchView;
	TrainStopsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_stops);
				
		Intent intent = getIntent();
		String line = intent.getStringExtra("line");
		lv = (ListView)findViewById(R.id.stopsTrainListView);
		String[] stops = getTrainStops(line);
		ArrayList<String> al = new ArrayList<String>(Arrays.asList(stops));

		adapter = new TrainStopsAdapter(al, this);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}

	//--------------- MENU INTERACTION ----------------------------//
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.train_stops, menu);
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.train_stops, menu);
		MenuItem searchItem = menu.findItem(R.id.train_stops_search);
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
		searchView.setQueryHint("Filter train stops...");
	}
	
	@Override
	public boolean onQueryTextChange(String newText) {
		TrainStops.this.adapter.getFilter().filter(newText);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {		
		return false;
	}
	
	public boolean isAlwaysExpanded() {
		return false;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
			
		//ALERT
		case R.id.backFromTrainStops:
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
	//--------------------------------------------------------//

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
		String stopName = adapter.getItemAtPosition(pos).toString();
		//Toast.makeText(getApplicationContext(), stopName, Toast.LENGTH_LONG).show();
		Intent intent = getIntent();
		//int position = intent.getIntExtra("position", 0);
		String line = intent.getStringExtra("line");
		
		Intent goToPredictions = new Intent(this, TrainPredictions.class);
		//goToPredictions.putExtra("position", position);
		goToPredictions.putExtra("stopName", stopName);
		goToPredictions.putExtra("line", line); //what line (Blue, Brown, Green, etc)
		startActivity(goToPredictions);		
	}
	
	public String[] getTrainStops(String line) {
		String[] blue = {"O'Hare", "Rosemont", "Cumberland", "Harlem (O'Hare)", "Jefferson Park", 
				"Montrose", "Irving Park", "Addison", "Belmont", "Logan Square", 
				"California", "Western (O'Hare)", "Damen", "Division", "Chicago", "Grand", "State/Lake", 
				"Washington", "Monroe", "Jackson", "LaSalle", "Clinton", "UIC-Halsted", "Racine", 
				"Illinois Medical District", "Western (Forest Park)", "Kedzie-Homan", "Pulaski", "Cicero", 
				"Austin", "Oak Park", "Harlem (Forest Park)", "Forest Park"};
		
		String[] brown = {"Kimball", "Kedzie", "Francisco", "Rockwell", "Western", "Damen",
				"Montrose", "Irving Park", "Addison", "Paulina", "Southport", "Belmont", "Wellington",
				"Diversey", "Fullerton", "Armitage", "Sedgwick", "Chicago", "Merchandise Mart",
				"Washington/Wells", "Quincy", "LaSalle/Van Buren", "Harold Washington Library-State/Van Buren",
				"Adams/Wabash", "Madison/Wabash", "Randolph/Wabash", "State/Lake", "Clark/Lake"};
		
		String[] green = {"Harlem/Lake", "Oak Park", "Ridgeland", "Austin", "Central", "Laramie", "Cicero",
				"Pulaski", "Conservatory-Central Park Drive", "Kedzie", "California", "Ashland", "Morgan",
				"Clinton", "Clark/Lake", "State/Lake", "Randolph/Wabash", "Madison/Wabash", "Adams/Wabash",
				"Roosevelt", "35th-Bronzeville-IIT", "Indiana", "43rd", "47th", "51st", "Garfield", "King Drive",
				"Cottage Grove", "Halsted", "Ashland/63rd"};
		
		String[] orange = {"Midway", "Pulaski", "Kedzie", "Western", "35th/Archer", "Ashland", "Halsted", 
				"Roosevelt", "Harold Washington Library-State/Van Buren", "LaSalle/Van Buren", "Quincy",
				"Washington/Wells", "Clark/Lake", "State/Lake", "Randolph/Wabash", "Madison/Wabash",
				"Adams/Wabash"};
		
		String[] pink = {"54th/Cermak", "Cicero", "Kostner", "Pulaski", "Central Park", "Kedzie", 
				"California", "Western", "Damen", "18th", "Polk", "Ashland", "Morgan", "Clinton", 
				"Clark/Lake", "State/Lake", "Randolph/Wabash", "Madison/Wabash", "Adams/Wabash", 
				"Harold Washington Library-State/Van Buren", "LaSalle/Van Buren", "Quincy",
				"Washington/Wells"};
		
		String[] purple = {"Linden", "Central", "Noyes", "Foster", "Davis", "Dempster", "Main", "South Blvd",
				"Howard", "Belmont", "Wellington", "Diversey", "Fullerton", "Armitage", "Sedgwick", 
				"Chicago", "Merchandise Mart", "Clark/Lake", "State/Lake", "Randolph/Wabash", 
				"Madison/Wabash", "Adams/Wabash", "Harold Washington Library-State/Van Buren", 
				"LaSalle/Van Buren", "Quincy", "Washington/Wells"};
		
		String[] red = {"Howard", "Jarvis", "Morse", "Loyola", "Granville", "Thorndale", "Bryn Mawr", 
				"Berwyn", "Argyle", "Lawrence", "Wilson", "Sheridan", "Addison", "Belmont", "Fullerton", 
				"North/Clybourn", "Clark/Division", "Chicago", "Grand", "Lake", "Monroe", "Jackson", 
				"Harrison" ,"Roosevelt", "Cermak-Chinatown", "Sox-35th", "47th", "Garfield", "63rd", 
				"69th", "79th", "87th", "95th/Dan Ryan"};
		
		String[] yellow = {"Dempster-Skokie", "Oakton-Skokie", "Howard"};
		
		//DETERMINES WHICH ARRAY OF STOPS IS CHOSEN
		if (line.equalsIgnoreCase("Blue Line")) {
			return blue;
		} else if (line.equalsIgnoreCase("Brown Line")) {
			return brown;
		} else if (line.equalsIgnoreCase("Green Line")) {
			return green;
		} else if (line.equalsIgnoreCase("Orange Line")) {
			return orange;
		} else if (line.equalsIgnoreCase("Pink Line")) {
			return pink;
		} else if (line.equalsIgnoreCase("Purple Line")) {
			return purple;
		} else if (line.equalsIgnoreCase("Red Line")) {
			return red;
		} else if (line.equalsIgnoreCase("Yellow Line")) {
			return yellow;
		}
		return null;
	}

}
