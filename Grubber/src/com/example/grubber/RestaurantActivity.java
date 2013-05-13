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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RestaurantActivity extends Activity {

	private TextView restNameTV;
	private TextView restAddressTV;
	private TextView restCityTV;
	private ImageView restImageIV;
	private ListView foodListLV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant);
		
		//***set restaurant name, address, etc
		//get value pass from previous page
		final HashMap<String, String> rest = (HashMap<String, String>) getIntent().getSerializableExtra("rest");
		
		//define textview
		restNameTV = (TextView)findViewById(R.id.restNameTV);
		restAddressTV = (TextView)findViewById(R.id.restAddressTV);
		restAddressTV.setClickable(true);
		restCityTV = (TextView)findViewById(R.id.restCityTV);
		restImageIV = (ImageView)findViewById(R.id.restImageIV);
		foodListLV = (ListView)findViewById(R.id.foodListLV);
		
		//show picture
		//String picurl = "http://maps.googleapis.com/maps/api/streetview?size=100x100&location="+ rest.get("longitude")+","+rest.get("latitude") +"&fov=90&heading=235&pitch=10&sensor=false";
		//new DownloadImageTask((ImageView) findViewById(R.id.imageView1)).execute(picurl);
		
		//set the textview
		restNameTV.setText(rest.get("name"));
		restAddressTV.setText(rest.get("address"));
		restCityTV.setText(rest.get("city") + ", " +rest.get("state") + ", " + rest.get("zip"));
		
		//***Open navigation app when click on the address
		restAddressTV.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				String uri = "google.navigation:q="+rest.get("longitude")+","+rest.get("latitude");
			    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			    startActivity(i); 
			}
		});
		
		
		
		//query to get the top 3 food list
		try {
			getFoodList(rest.get("rest_id"));
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

	public void getFoodList(String id) throws Exception {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("rest_id", id));
		nameValuePair.add(new BasicNameValuePair("min", "0"));
		nameValuePair.add(new BasicNameValuePair("max", "3"));
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
			String json = "wrong";
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				json = reader.readLine();
				Log.d("bug", json);
				getFood(json);
			} catch (Exception e) { 
				Log.d("bugs","reader"); 
				runOnUiThread(new Runnable() {
					public void run() {
					    Toast.makeText(RestaurantActivity.this, "Failed to get the data", Toast.LENGTH_SHORT).show();
					}
				});
			}

		}
		
		protected void getFood(String json)
		{
			
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(json);
            JsonArray jarr = (JsonArray) obj.get("result");
			// SET TEXT FIELD TO RESPONSE 

			ArrayList<String> foodlist = new ArrayList<String>();
			Log.d("bug",jarr.size() +"");
			if (jarr.size() == 0)
			{	//no data
				
				return;
			}
			for(int i = 0; i < jarr.size(); i++){
				JsonObject o = (JsonObject) jarr.get(i);
				foodlist.add(o.get("name").getAsString());
			}
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RestaurantActivity.this, android.R.layout.simple_list_item_1, foodlist);
			foodListLV.setAdapter(adapter); 
		}
	}
	
	/*private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	        bmImage.setImageBitmap(result);
	    }
	}*/
}
