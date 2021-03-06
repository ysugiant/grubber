package com.example.grubber;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.http.message.BasicNameValuePair;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class SearchActivity extends Activity {
	private final double DEFAULT_COORD = 200.0;
	public double longt = DEFAULT_COORD;
	public double lat = DEFAULT_COORD;
	public boolean addr_changed = false;
	public float miles = 5.0f;
	public final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		lat = getIntent().getDoubleExtra("latitude", DEFAULT_COORD);
		longt  = getIntent().getDoubleExtra("longitude", DEFAULT_COORD);
		
		EditText address_box = (EditText) findViewById(R.id.edit_location);
		address_box.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	            addr_changed = true;
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    });
		fillCurrentLocation();
		addr_changed = false;
	}

	public void onResume() {
    	super.onResume();
    	//Refresh the options menu when this activity comes in focus
    	invalidateOptionsMenu();
    	//this.tracker.trackPageView("/TopTracksActivity");
    }	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
        //Change profile button to login/register if they are not logged in
        if(SaveSharedPreference.getUserId(SearchActivity.this) == 0)
        {
            MenuItem profileItem = menu.findItem(R.id.action_profile);
        	profileItem.setTitle(R.string.login);
            //Toast.makeText(this,"Not logged in",Toast.LENGTH_SHORT).show();
        }
        else {
        	MenuItem signout = menu.findItem(R.id.action_signout);
        	signout.setVisible(true);
            signout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            	public boolean onMenuItemClick(MenuItem item) {            		        	
        			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        			alertDialogBuilder.setTitle(R.string.logout_msg);
        			alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog,int id) {            					    							
							//int tempUserName = SaveSharedPreference.getUserId(context);    			        		
			        		dialog.cancel();    			        		
			        		SaveSharedPreference.setUserId(context, 0);
        					Toast.makeText(context , "Logged out" , Toast.LENGTH_SHORT).show();
        					invalidateOptionsMenu();
						}    						
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog,int id) {    						
    						dialog.cancel();    					
    					}}
    				  );            		
            		AlertDialog alertDialog = alertDialogBuilder.create();
            		alertDialog.show();   
            		return true;            		
            	} 	
            });        	
        }        
        return true;
      } 

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	      case R.id.action_profile:
	    	  if(SaveSharedPreference.getUserId(SearchActivity.this) != 0){
	    		  Intent intent3 = new Intent(context, ProfileActivity.class);
	    		  startActivity(intent3);   
	    	  } else {
	    		  Intent intent3 = new Intent(context, SignInTabsActivity.class);
	    		  startActivity(intent3);   
	    	  }
	          break; 		
			case android.R.id.home:
				//NavUtils.navigateUpFromSameTask(this);
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void fillCurrentLocation() {
		//Method that is called when 'Use current location' button is pressed
		if (longt != DEFAULT_COORD && lat != DEFAULT_COORD) {
			String current_location = "";
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			try {
				List<Address> addresses = geocoder.getFromLocation(lat, longt, 1);
				Address addr = addresses.get(0);
				
				if (addr.getThoroughfare()!=null && addr.getFeatureName() != null && addr.getSubAdminArea()!=null) {
					current_location = addr.getFeatureName() + " " + addr.getThoroughfare() + ", " + addr.getSubAdminArea();
				}
				else if (addr.getThoroughfare()!=null && addr.getFeatureName() != null && addr.getLocality()!=null) {
					current_location = addr.getFeatureName() + " " + addr.getThoroughfare() + ", " + addr.getLocality();			
				}
				else if (addr.getSubLocality()!=null) {
					current_location = addr.getSubLocality();
				}
				else if (addr.getAddressLine(0)!=null) {
					current_location = addr.getAddressLine(0);
					if (addr.getAddressLine(1)!=null) {
						current_location += ", " + addr.getAddressLine(1);
					}
				}			
			} catch (IOException e) {
				Log.d("bugs", e.toString());
			}
			EditText location = (EditText) findViewById(R.id.edit_location);			
			location.setText(current_location);
		}
	}
	
	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_5:
	            if (checked)
	                miles = 5.0f;
	            break;
	        case R.id.radio_10:
	            if (checked)
	                miles = 10.0f;
	            break;
	        case R.id.radio_25:
	            if (checked)
	                miles = 25.0f;
	            break;	
	    }
	}
	
	public void doSearch(View view) {
		//Method called when 'Search' button is pressed
		EditText search_box = (EditText) findViewById(R.id.edit_search);
		String term = search_box.getText().toString();
		EditText address_box = (EditText) findViewById(R.id.edit_location);
		String loc = address_box.getText().toString();
		
		
		
		Intent intent = new Intent(this, Results.class);
  	  	intent.putExtra("key", term);
  	  	intent.putExtra("radius", miles);
  	  	
		if (addr_changed) {
			Geocoder coder = new Geocoder(this);
			List<Address> address;
			double user_lat = DEFAULT_COORD;
			double user_long = DEFAULT_COORD;
			try {
			    address = coder.getFromLocationName(loc,5);
			    if (address != null) {
				    Address location = address.get(0);
				    user_lat = location.getLatitude();
				    user_long = location.getLongitude();
			  	  	intent.putExtra("latitude", String.valueOf(user_lat));
			  	  	intent.putExtra("longitude", String.valueOf(user_long));
			    }

			} catch (Exception e) {}
		} else {
  	  		intent.putExtra("latitude", String.valueOf(lat));
  	  		intent.putExtra("longitude", String.valueOf(longt));
		}
		if(loc.length() == 0)
			Toast.makeText(this,"Please insert a valid address!",Toast.LENGTH_SHORT).show();
		else
			startActivity(intent);

	}
}
