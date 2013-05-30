package com.example.grubber;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.grubber.Alerts.NeedServicesDialogFragment;
import com.example.grubber.MyLocation.LocationResult;
import com.example.grubber.R;
import com.google.analytics.tracking.android.*;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.example.grubber.MyLocation;

@SuppressLint("NewApi")
public class MainActivity extends Activity  {
	
	public final Context context = this;

	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	public Location userLoc;
	
	private ListView foodListLV;
	private ProgressDialog progDialog; 
	private ArrayList<TopFoodContent> list_result = null;

	//alerts
	DialogFragment servicesDialog = new NeedServicesDialogFragment();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGaInstance = GoogleAnalytics.getInstance(this);
        mGaTracker = mGaInstance.getTracker("UA-40885024-1");
        setContentView(R.layout.activity_main); 
		foodListLV = (ListView)findViewById(R.id.topfoodListLV);
        try {
			getTopFood();
		} catch (Exception e) {
			e.printStackTrace();
		}
        LocationResult locationResult = new LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
            	userLoc = location;
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    @SuppressLint("NewApi")
	@Override

	public void onResume() {
    	super.onResume();
    	//Refresh the options menu when this activity comes in focus
    	invalidateOptionsMenu();
    	//this.tracker.trackPageView("/TopTracksActivity");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
                      
        //Change profile button to login/register if they are not logged in
        if(SaveSharedPreference.getUserId(MainActivity.this) == 0)
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
	      case R.id.menu_search:
	    	  Intent intent = new Intent(this, SearchActivity.class);
	    	  if (userLoc!=null) {
	    		  intent.putExtra("longitude", userLoc.getLongitude());
	    		  intent.putExtra("latitude", userLoc.getLatitude());
		    	  startActivity(intent);
	    	  } else {
	    		  servicesDialog.show(getFragmentManager(), "results_services_dialog");	
	    	  }

	    	  break;
	      case R.id.action_nearby:
	    	  Intent intent2 = new Intent(this, Results.class);
	    	  if (userLoc!=null) {
	    		  intent2.putExtra("longitude", userLoc.getLongitude()+"");
	    		  intent2.putExtra("latitude", userLoc.getLatitude()+"");
		    	  startActivity(intent2);   
	    	  }
	    	  else {
	    		  servicesDialog.show(getFragmentManager(), "results_services_dialog");	
	    	  }
	    	  break;
	      case R.id.action_profile:
	    	  if(SaveSharedPreference.getUserId(MainActivity.this) != 0){
	    		  Intent intent3 = new Intent(context, ProfileActivity.class);
	    		  startActivity(intent3);   
	    	  } else {
	    		  Intent intent3 = new Intent(context, SignInTabsActivity.class);
	    		  startActivity(intent3);   
	    	  }
	          break;        
	      default:
	    	  break;
      }

      return true;
    } 
    
	public void getTopFood() throws Exception {
		progDialog = ProgressDialog.show( this, "Process ", "Loading Data...",true,true);

		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/topFood");

		// Handles what is returned from the page
		//ResponseHandler responseHandler = new BasicResponseHandler();
		//Log.d("bugs", "execute request");
		new GetHttpRequest().execute(httpost);
	}
	
	private class GetHttpRequest extends AsyncTask<HttpPost, Void, String> {

		@Override
		protected String doInBackground(HttpPost... params) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			String json = "wrong";
			try {
				HttpResponse resp = httpclient.execute(params[0]);
				BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
				json = reader.readLine();
				return json;
			} catch (Exception e) {
				Log.d("bugs", "Catch in HTTPGETTER");
			}
			return null;
		}

		protected void onPostExecute(String json) {
			try {
				getFood(json);
			} catch (Exception e) { 
				Log.d("bugs","reader"); 
				Toast.makeText(MainActivity.this, "Failed to get the data", Toast.LENGTH_SHORT).show();			
			}
			//stop progress bar
			progDialog.dismiss();
		}
		
		protected void getFood(String json)
		{
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(json);
            JsonArray jarr = (JsonArray) obj.get("result");
            
            if (list_result == null)
            	list_result = new ArrayList<TopFoodContent>();
                        
	        for (int i = 0; i < jarr.size(); i++) {
	        	JsonObject result = (JsonObject) jarr.get(i);

	        	//set for adapter value
	        	list_result.add(new TopFoodContent(result.get("food_id").getAsString(), result.get("food_name").getAsString(),
						  result.get("rest_name").getAsString(), result.get("comments").getAsString(), 
						  result.get("vote").getAsString()));
	        }
	        
	        TopFoodAdapter radapter = new TopFoodAdapter(MainActivity.this, list_result);
	        //Show the food list to ListView
	        foodListLV.setAdapter(radapter);
	        foodListLV.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	            {//set onClick and open RestaurantActivity page
	            	Intent intent = new Intent(MainActivity.this, FoodPageActivity.class);
	            	TopFoodContent tmp = list_result.get((int) id);
	            	intent.putExtra("food_id", tmp.getFoodId());
	            	intent.putExtra("name", tmp.getFoodName());
	            	//intent.putExtra("description", tmp.getDescription());
	            	intent.putExtra("total_vote", tmp.getVote());
	        		startActivity(intent);
	            }
	        });
	        
	        int currentPosition = foodListLV.getFirstVisiblePosition();
	        foodListLV.setSelectionFromTop(currentPosition,  0);
		}
	}
}

    