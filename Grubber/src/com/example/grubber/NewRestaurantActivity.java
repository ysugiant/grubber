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
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NewRestaurantActivity extends Activity {
	//declare EditText and Button
	private EditText rest_nameET;
	private EditText rest_addressET;
	private EditText rest_cityET;
	private ListView rest_stateLV;
	private Button verify_btn;
	private ProgressDialog progDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_restaurant);
		rest_nameET = (EditText) findViewById(R.id.restaurant_nameET);
		rest_addressET = (EditText) findViewById(R.id.restaurant_addressET);
		rest_cityET = (EditText) findViewById(R.id.restaurant_cityET);
		rest_stateLV = (ListView) findViewById(R.id.restaurant_stateLV);
		verify_btn = (Button) findViewById(R.id.restaurant_verify);
		String[] state = new String []{"","AK","AL","AR","AS","AZ","CA","CO","CT","DC","DE","FL","GA","GU","HI","IA","ID",
				"IL","IN","KS","KY","LA","MA","MD","ME","MH","MI","MN","MO","MS","MT","NC","ND","NE","NH","NJ","NM","NV","NY",
				"OH","OK","OR","PA","PR","PW","RI","SC","SD","TN","TX","UT","VA","VI","VT","WA","WI","WV","WY"};
		
		ArrayList<String> stateAL = new ArrayList<String>();
		for(int i = 0; i < state.length -1 ;i ++ ){
			stateAL.add(state[i]);
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewRestaurantActivity.this, android.R.layout.simple_list_item_1, stateAL);
		rest_stateLV.setAdapter(adapter);
		
		//verify_btn.setOnClickListener(l)
        verify_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					addRestaurant();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				Intent intent = new Intent(NewRestaurantActivity.this, NewRestaurantAddrVerifyActivity.class);
            	intent.putExtra("name", rest_nameET.getText().toString());
            	intent.putExtra("address", rest_addressET.getText().toString());
            	intent.putExtra("city", rest_cityET.getText().toString());
            	intent.putExtra("state", rest_stateET.getText().toString());
        		startActivity(intent);*/
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_restaurant, menu);
		return true;
	}

	public void addRestaurant() throws Exception {
		//start progress bar
		progDialog = ProgressDialog.show( this, "Process ", "Loading Data...",true,true);
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);

		nameValuePair.add(new BasicNameValuePair("name", rest_nameET.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("address", rest_addressET.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("city", rest_addressET.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("state", rest_addressET.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("zip", rest_addressET.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("phone", rest_addressET.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("website", rest_addressET.getText().toString()));

		
		//Log.d("bug", getIntent().getStringExtra("address"));
		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080");

		// sets the post request as the resulting string
		httpost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		// Handles what is returned from the page
		//ResponseHandler responseHandler = new BasicResponseHandler();
		new GetHttpRequest().execute(httpost);

	}

	private class GetHttpRequest extends AsyncTask<HttpPost, Void, String> {

		@Override
		protected String doInBackground(HttpPost... params) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			String json = "";
			String inputLine;
			try {
				HttpResponse resp = httpclient.execute(params[0]);
				BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
		        while ((inputLine = reader.readLine()) != null) 
		        {
		        	json = json + inputLine;
		        }
				return json;
			} catch (Exception e) {
				Log.d("bugs", "Catch in HTTPGETTER");
			}
			return null;
		}

		protected void onPostExecute(String json) {
			try {
				setView(json);
				Toast.makeText(NewRestaurantActivity.this, "Thank you for the information. We will publish it ASAP", Toast.LENGTH_SHORT).show();
			} catch (Exception e) { 
				//add button to refresh
				Toast.makeText(NewRestaurantActivity.this, "Failed to add the restaurant", Toast.LENGTH_SHORT).show();				
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
		    //rest_stateET.setText(ac[0]);
		    String zip =  ac[1];
		    
		 
		    JsonObject geometry = (JsonObject) res.get("geometry");
		    JsonObject location = (JsonObject) geometry.get("location");
		    double latitude = location.get("lat").getAsDouble();
		    double longitude = location.get("lng").getAsDouble();
		}
	}
}
