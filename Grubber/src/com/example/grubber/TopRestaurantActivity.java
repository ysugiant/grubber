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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class TopRestaurantActivity extends Activity {
	public final Context context = this;
	private ListView restListLV;
	private ProgressDialog progDialog; 
	private ArrayList<TopRestaurantContent> list_result = null;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_restaurant);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		restListLV = (ListView)findViewById(R.id.toprestListLV);
	}
	
	public void onResume() {
		super.onResume();
    	//Refresh the options menu when this activity comes in focus
    	invalidateOptionsMenu();
        try {
			getTopRest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_top_food, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void getTopRest() throws Exception {
		progDialog = ProgressDialog.show( this, "Process ", "Loading Data...",true,true);

		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/topRestaurant");

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
				getRest(json);
			} catch (Exception e) { 
				Log.d("bugs","reader"); 
				Toast.makeText(TopRestaurantActivity.this, "Failed to get the data", Toast.LENGTH_SHORT).show();			
			}
			//stop progress bar
			progDialog.dismiss();
		}
		
		protected void getRest(String json)
		{
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(json);
            JsonArray jarr = (JsonArray) obj.get("result");

            if (list_result == null)
            	list_result = new ArrayList<TopRestaurantContent>();
	        for (int i = 0; i < jarr.size(); i++) {
	        	JsonObject result = (JsonObject) jarr.get(i);
	        	//set for adapter value
	        	list_result.add(new TopRestaurantContent(result.get("rest_id").getAsString(), result.get("count").getAsString(),
						  result.get("rest_name").getAsString(), result.get("address").getAsString(), result.get("city").getAsString(),
						  result.get("state").getAsString(), result.get("zip").getAsString(), result.get("latitude").getAsString(),
						  result.get("longitude").getAsString(), result.get("phone").getAsString(), result.get("website").getAsString()));
	        }
	        
	        TopRestaurantAdapter radapter = new TopRestaurantAdapter(TopRestaurantActivity.this, list_result);
	        //Show the food list to ListView
	        restListLV.setAdapter(radapter);
	        restListLV.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	            {//set onClick and open RestaurantActivity page	        
	            	Intent intent = new Intent(TopRestaurantActivity.this, RestaurantActivity.class);
	            	TopRestaurantContent tmp = list_result.get((int) id);
	            	intent.putExtra("rest_id", tmp.getRestId());
	            	intent.putExtra("name", tmp.getRestName());
	            	intent.putExtra("address", tmp.getAddress());
	            	intent.putExtra("city", tmp.getCity() + ", " + tmp.getState() + ", " + tmp.getZip());
	            	intent.putExtra("longitude", tmp.getLongitude());
	            	intent.putExtra("latitude", tmp.getLatitude());
	            	intent.putExtra("phone", tmp.getPhone());
	            	intent.putExtra("website", tmp.getWebsite());
	        		startActivity(intent);
	            }
	        });
	        
	        int currentPosition = restListLV.getFirstVisiblePosition();
	        restListLV.setSelectionFromTop(currentPosition,  0);
		}
	}	
}
