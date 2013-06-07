package com.example.grubber;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class NewFoodActivity extends Activity {

	private EditText food_nameET;
	private EditText food_descriptionET;
	private Button submit_btn;
	private ProgressDialog progDialog;
	
	private String image = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_food);
		food_nameET = (EditText) findViewById(R.id.food_nameET);
		food_descriptionET = (EditText) findViewById(R.id.food_descriptionET);
		submit_btn = (Button) findViewById(R.id.submitBTN);
		
	    submit_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 3);
				}
			});
	   
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==3 && resultCode == Activity.RESULT_OK) {
	        Uri selectedImage = data.getData();
	        Bitmap bitmap = null;
	        try {
	        	bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
	        	ByteArrayOutputStream bao = new ByteArrayOutputStream();
	            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bao);
	            byte [] ba = bao.toByteArray();
	            image = Base64.encodeToString(ba, Base64.DEFAULT);
	            //upload file and add to the database
	            try {
					addFood();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } catch (Exception e) {
	        	// TODO Auto-generated catch block
	        	e.printStackTrace();
	        }
	    }
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_food, menu);
		return true;
	}
	
	public void addFood() throws Exception {
		//start progress bar
		progDialog = ProgressDialog.show( this, "Process ", "Loading Data...",true,true);
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);

		nameValuePair.add(new BasicNameValuePair("rest_id", getIntent().getStringExtra("rest_id")));
		nameValuePair.add(new BasicNameValuePair("name", food_nameET.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("description", food_descriptionET.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("image", image));
		
		//Log.d("bug", getIntent().getStringExtra("address"));
		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/createFood");

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
				//Log.d("bug", params[0].getURI().toString());
				HttpResponse resp = httpclient.execute(params[0]);
				BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
		        while ((inputLine = reader.readLine()) != null) 
		        {
		        	json += inputLine;
		        }
		        Log.d("bug", json);
				return json;
			} catch (Exception e) {
				Log.d("bug", "Catch in HTTPGETTER");
			}
			return null;
		}

		protected void onPostExecute(String json) {	
			boolean res = false;
			try {
				res = setView(json);
				//Toast.makeText(NewFoodActivity.this, "Thank you for the information. We will publish it ASAP", Toast.LENGTH_SHORT).show();				
			} catch (Exception e) { 
				Log.d("bug","reader"); 
				//add button to refresh
				Toast.makeText(NewFoodActivity.this, "Failed to add the restaurant", Toast.LENGTH_SHORT).show();				
			}
			//stop progress bar
			progDialog.dismiss();
			if (res)
				finish();
		}
		
		protected boolean setView(String jsonString)
		{
			JsonParser parser = new JsonParser();		    
		    JsonObject obj = (JsonObject) parser.parse(jsonString);
		    Toast.makeText(NewFoodActivity.this, obj.get("message").getAsString(), Toast.LENGTH_SHORT).show();
		    return obj.get("result").getAsBoolean();		    
		}
	}

}
