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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NewRestaurantAddrVerifyActivity extends Activity {

	private EditText rest_nameET;
	private EditText rest_addressET;
	private EditText rest_cityET;
	private EditText rest_stateET;
	private Button verify_btn;
	private ProgressDialog progDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_restaurant_addr_verify);
		rest_nameET = (EditText) findViewById(R.id.restaurant_nameET);
		rest_addressET = (EditText) findViewById(R.id.restaurant_addressET);
		rest_cityET = (EditText) findViewById(R.id.restaurant_cityET);
		rest_stateET = (EditText) findViewById(R.id.restaurant_stateET);
		verify_btn = (Button) findViewById(R.id.restaurant_verify);
		try {
			getAddress();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_restaurant_addr_verify, menu);
		return true;
	}

	public void getAddress() throws Exception {
		//start progress bar
		progDialog = ProgressDialog.show( this, "Process ", "Loading Data...",true,true);
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);

		nameValuePair.add(new BasicNameValuePair("address", getIntent().getStringExtra("address")));
		nameValuePair.add(new BasicNameValuePair("sensor", "false"));
		//nameValuePair.add(new BasicNameValuePair("max", "10"));
				
		// url with the post data
		HttpPost httpost = new HttpPost("http://maps.googleapis.com/maps/api/geocode/json");

		// sets the post request as the resulting string
		httpost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		// Handles what is returned from the page
		//ResponseHandler responseHandler = new BasicResponseHandler();
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
			String json = "";
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				String inputLine;
		        while ((inputLine = reader.readLine()) != null) 
		        {
		        	json = json + inputLine;
		
		        }
				Log.d("bug", json);
				setView(json);
			} catch (Exception e) { 
				Log.d("bug","reader"); 
				//add button to refresh
				
				runOnUiThread(new Runnable() {
					public void run() {
					    Toast.makeText(NewRestaurantAddrVerifyActivity.this, "Failed to get the data", Toast.LENGTH_SHORT).show();
					}
				});
			}
			//stop progress bar
			progDialog.dismiss();
		}
		
		protected void setView(String jsonString)
		{
			JsonParser parser = new JsonParser();
		    
		    JsonObject obj = (JsonObject) parser.parse(jsonString);
		    JsonArray results = (JsonArray) obj.get("results");
		    JsonObject res = (JsonObject) results.get(0);
		    
		    String foradd = ((JsonObject) res.get("formatted_address")).getAsString();
		    
		    String addcom[] = foradd.split(",");
		    
		    
		    //setText()
		    rest_addressET.setText(addcom[0]);
		    rest_cityET.setText(addcom[1]);
		    
		    String ac[] = addcom[2].split(" ");
		    rest_stateET.setText(ac[0]);
		    String zip =  ac[1];
		    
		 
		    JsonObject geometry = (JsonObject) res.get("geometry");
		    JsonObject location = (JsonObject) geometry.get("location");
		    double latitude = location.get("lat").getAsDouble();
		    double longitude = location.get("lng").getAsDouble();
		}
	}
}
