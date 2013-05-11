package com.example.grubber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class RestaurantActivity extends Activity {

	private TextView restNameTV;
	private TextView restAddressTV;
	private ExpandableListView foodList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant);
		//***set restaurant name, address, etc
		//get value pass from previous page
		String restName = getIntent().getStringExtra("rest_name");
		final String restAddress = getIntent().getStringExtra("rest_address");
		final String restCity = getIntent().getStringExtra("rest_city");
		final String restState = getIntent().getStringExtra("rest_state");
		final String restZip = getIntent().getStringExtra("rest_zip");
		/* get from previous page (code)
		             @Override
            public void onClick(View v) {
                            Intent intent = new Intent(Activity1.this, Activity2.class); 
                            intent.putExtra("extra", data);
                            startActivity(intent); 
             });  
		 */
		restNameTV = (TextView)findViewById(R.id.restNameTV);
		restAddressTV = (TextView)findViewById(R.id.restAddressTV);
		restAddressTV.setClickable(true);
		
		restNameTV.setText(restName);
		restAddressTV.setText(restAddress);
		
		//***Open navigation app when click on the address
		restAddressTV.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//*******need change
				String uri = "google.navigation:q="+restAddress+restCity+restState;
			    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			    startActivity(i); 
			}
		});
		
		
		
		//query to get the top 3 food list
		try {
			getFoodList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restaurant, menu);
		return true;
	}

	public void getFoodList() throws Exception {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("rest_id", "1"));
				
		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/findFood");

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
			} catch (Exception e) { Log.d("bugs","reader"); }

			
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(json);
            JsonArray jarr = (JsonArray) obj.get("result");
			// SET TEXT FIELD TO RESPONSE 

			ArrayList<String> restlist = new ArrayList<String>();
			for(JsonElement o : jarr){
				restlist.add(o.toString());
			}
			
			//ArrayAdapter<String> arrayAdapter;
			//arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, restlist);
			//foodList.setAdapter(arrayAdapter); 
		}
	}
}
