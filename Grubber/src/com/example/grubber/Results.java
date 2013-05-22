package com.example.grubber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.grubber.ResultContent;
import com.example.grubber.ResultAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class Results extends FragmentActivity {
	public FragmentManager fragMan;
	private ListView result_list;
	private ProgressDialog progDialog; 
	public final Context context = this;
	//private View main_view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		result_list = (ListView) findViewById(R.id.restaurantLV);
		try {
			getRestaurant();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onResume() {
    	super.onResume();
    	//Refresh the options menu when this activity comes in focus
    	invalidateOptionsMenu();
    	//this.tracker.trackPageView("/TopTracksActivity");
    }	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.results, menu);
                      
        //Change profile button to login/register if they are not logged in
        if(SaveSharedPreference.getUserId(Results.this) == 0)
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
	    	  if(SaveSharedPreference.getUserId(Results.this) != 0){
	    		  Intent intent3 = new Intent(context, ProfileActivity.class);
	    		  startActivity(intent3);   
	    	  } else {
	    		  Intent intent3 = new Intent(context, LoginActivity.class);
	    		  startActivity(intent3);   
	    	  }
	          break;        
	      default:
	    	  break;
      }

      return true;
    }

	public void getRestaurant() throws Exception {
		//start progress bar
		progDialog = ProgressDialog.show( this, "Process ", "Loading Data...",true,true);
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);

		nameValuePair.add(new BasicNameValuePair("key", getIntent().getStringExtra("key")));
		nameValuePair.add(new BasicNameValuePair("min", "0"));
		nameValuePair.add(new BasicNameValuePair("max", "11")); // have to reach max?
		/*Log.d("bug", getIntent().getStringExtra("latitude"));
		if(getIntent().getStringExtra("latitude") != null)
		{
			nameValuePair.add(new BasicNameValuePair("latitude", getIntent().getStringExtra("latitude")));
		}
		if(getIntent().getStringExtra("longitude") != null)
		{
			nameValuePair.add(new BasicNameValuePair("longitude", getIntent().getStringExtra("longitude")));
		}*/	
				
		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/findRestaurants");

		// sets the post request as the resulting string
		httpost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		// Handles what is returned from the page
		//ResponseHandler responseHandler = new BasicResponseHandler();
		//Log.d("bugs", "execute request");
		new GetHttpRequest().execute(httpost);

	}

	private class GetHttpRequest extends AsyncTask<HttpPost, Void, HttpResponse> {

		@Override
		protected HttpResponse doInBackground(HttpPost... params) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			try {
				HttpResponse resp = httpclient.execute(params[0]);
				return resp;
			} catch (Exception e) {
				Log.d("bugs", "Catch in HTTPGETTER");
			}
			return null;
		}

		protected void onPostExecute(HttpResponse response) {
			String json = "wrong";
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				json = reader.readLine();
				//Log.d("bug", json);
				setView(json);
			} catch (Exception e) { 
				Log.d("bugs","reader"); 
				//add button to refresh
				
				runOnUiThread(new Runnable() {
					public void run() {
					    Toast.makeText(Results.this, "Failed to get the data", Toast.LENGTH_SHORT).show();
					}
				});
			}
			//stop progress bar
			progDialog.dismiss();
		}
		
		protected void setView(String jsonString)
		{
	        JsonParser jsonParser = new JsonParser();
	        JsonObject jo = (JsonObject)jsonParser.parse(jsonString);
	        JsonArray jarr = (JsonArray) jo.get("result");
	        
	        final ArrayList<ResultContent> list_result = new ArrayList<ResultContent>();
	        for (int i = 0; i < jarr.size(); i++) {
	        	JsonObject result = (JsonObject) jarr.get(i);

	        	//set for adapter value
	        	list_result.add(new ResultContent(result.get("rest_id").getAsString(), result.get("name").getAsString(),
						  result.get("address").getAsString(), result.get("city").getAsString(), result.get("state").getAsString(),
						  result.get("zip").getAsString(), result.get("longitude").getAsString(), result.get("latitude").getAsString(),
						  result.get("phone").getAsString(), result.get("website").getAsString(), result.get("distance").getAsString()));
	        }
	        
	        ResultAdapter radapter = new ResultAdapter(Results.this, list_result);

	        //Show the restaurant list to ListView
	        result_list.setAdapter(radapter);
	        result_list.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	            {//set onClick
	            	Intent intent = new Intent(Results.this, RestaurantActivity.class);
	            	ResultContent tmp = list_result.get((int) id);
	            	intent.putExtra("rest_id", tmp.getId());
	            	intent.putExtra("name", tmp.getName());
	            	intent.putExtra("address", tmp.getAddress());
	            	intent.putExtra("city", tmp.getCity() + ", " + tmp.getState() + ", " + tmp.getZip());
	            	intent.putExtra("longitude", tmp.getLongitude());
	            	intent.putExtra("latitude", tmp.getLatitude());
	            	intent.putExtra("phone", tmp.getPhone());
	            	intent.putExtra("website", tmp.getWebsite());
	            	intent.putExtra("distance", tmp.getDistance());
	        		startActivity(intent);
	            }
	        });
		}
	}
	
	
}
