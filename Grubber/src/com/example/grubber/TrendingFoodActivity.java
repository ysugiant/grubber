package com.example.grubber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class TrendingFoodActivity extends Activity {
	public final Context context = this;
	private ListView foodListLV;
	private ProgressDialog progDialog; 
	private ArrayList<TopFoodContent> list_result = null;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_food);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		foodListLV = (ListView)findViewById(R.id.topfoodListLV);
	}
	
	public void onResume() {
		super.onResume();
    	//Refresh the options menu when this activity comes in focus
    	invalidateOptionsMenu();
        try {
        	getTrendingFood();
        } catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_top_food, menu);
        //Change profile button to login/register if they are not logged in
        if(SaveSharedPreference.getUserId(TrendingFoodActivity.this) == 0)
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
	    	  if(SaveSharedPreference.getUserId(TrendingFoodActivity.this) != 0){
	    		  Intent intent3 = new Intent(context, ProfileActivity.class);
	    		  startActivity(intent3);   
	    	  } else {
	    		  Intent intent3 = new Intent(context, SignInTabsActivity.class);
	    		  startActivity(intent3);   
	    	  }
	          break;				
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void getTrendingFood() throws Exception {
		progDialog = ProgressDialog.show( this, "Process ", "Loading Data...",true,true);

		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/trendingFood");

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
				Toast.makeText(TrendingFoodActivity.this, "Failed to get the data", Toast.LENGTH_SHORT).show();			
			}
			//stop progress bar
			progDialog.dismiss();
		}
		
		protected void getFood(String json)
		{
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(json);
            JsonArray jarr = (JsonArray) obj.get("result");
            
            list_result = new ArrayList<TopFoodContent>();
                        
	        for (int i = 0; i < jarr.size(); i++) {
	        	JsonObject result = (JsonObject) jarr.get(i);

	        	//set for adapter value
	        	list_result.add(new TopFoodContent(result.get("food_id").getAsString(), result.get("food_name").getAsString(),
						  result.get("rest_name").getAsString(), result.get("comments").getAsString(), 
						  result.get("vote").getAsString(), result.get("food_description").getAsString(), result.get("rest_id").getAsString()));
	        }
	        
	        TopFoodAdapter radapter = new TopFoodAdapter(TrendingFoodActivity.this, list_result);
	        //Show the food list to ListView
	        foodListLV.setAdapter(radapter);
	        foodListLV.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	            {//set onClick and open RestaurantActivity page
	            	Intent intent = new Intent(TrendingFoodActivity.this, FoodPageActivity.class);
	            	TopFoodContent tmp = list_result.get((int) id);
	            	intent.putExtra("food_id", tmp.getFoodId());
	            	intent.putExtra("name", tmp.getFoodName());
	            	intent.putExtra("description", tmp.getDescription());
	            	intent.putExtra("total_vote", tmp.getVote());
	            	intent.putExtra("rest_id", tmp.getRestId());
	        		startActivity(intent);
	            }
	        });
	        
	        int currentPosition = foodListLV.getFirstVisiblePosition();
	        foodListLV.setSelectionFromTop(currentPosition,  0);
		}
	}	
}
