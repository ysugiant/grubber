package com.example.grubber;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.NavUtils;

public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void fillCurrentLocation(View view) {
		//Method that is called when 'Use current location' button is pressed
		String current_location = "";
		
		//PUT CODE TO GET CURRENT LOCATION HERE
		
		EditText location = (EditText) findViewById(R.id.edit_location);
		location.setText(current_location);
	}
	
	public void doSearch(View view) {
		//Method called when 'Search' button is pressed
		EditText search_box = (EditText) findViewById(R.id.edit_search);
		String term = search_box.getText().toString();
		EditText address_box = (EditText) findViewById(R.id.edit_location);
		String loc = address_box.getText().toString();
		
		Intent intent = new Intent(this, Results.class);
  	  	intent.putExtra("key", term);
  	  	intent.putExtra("location", loc);
  	  	startActivity(intent);
	}

}
