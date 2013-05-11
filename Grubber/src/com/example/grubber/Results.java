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

import com.example.grubber.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Results extends FragmentActivity {
	public FragmentManager fragMan;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		
        
		try {
			getRestaurant();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}

	public void getRestaurant() throws Exception {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);

		nameValuePair.add(new BasicNameValuePair("key", getIntent().getStringExtra("key")));
		nameValuePair.add(new BasicNameValuePair("min", "0"));
		nameValuePair.add(new BasicNameValuePair("max", "3"));

				
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
			//Gson gson = new Gson();
			String json = "wrong";
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				json = reader.readLine();
				//Log.d("bug", json);
				setView(json);
			} catch (Exception e) { Log.d("bugs","reader"); }
		}
		
		protected void setView(String jsonString)
		{
	        JsonParser jsonParser = new JsonParser();
	        JsonObject jo = (JsonObject)jsonParser.parse(jsonString);
	        JsonArray jarr = (JsonArray) jo.get("result");
	        
	        ArrayList<HashMap<String, String>> restList = new ArrayList<HashMap<String, String>>();
	        
	        String[] fields = new String[] {"rest_id", "name", "address", "city", "state",
					 "zip", "latitude", "longitude", "phone", "website", "distance"};
	        
	        for (int i = 0; i < jarr.size(); i++) {
	        	JsonObject result = (JsonObject) jarr.get(i);
	        	HashMap<String, String> restaurant = new HashMap<String, String>();

	        	for (int j = 0; j < fields.length; j++) {
	        		String field = fields[j];
	           		restaurant.put(field, result.get(field).getAsString());
	        	}

	        	restList.add(restaurant);
	        }
	        
	        ArrayList<String> list = new ArrayList<String>();
	        for (int i = 0; i < restList.size(); i++) {
	        	HashMap<String, String> rest2 = restList.get(i);
	        	list.add(rest2.get("name"));
	        }
	        
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Results.this, android.R.layout.simple_list_item_1, list);
	        ListView result_list = (ListView) findViewById(R.id.restaurantLV);
	        result_list.setAdapter(adapter);
	       
		}
	}
	
	
}
