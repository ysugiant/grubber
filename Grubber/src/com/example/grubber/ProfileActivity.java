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
import com.google.gson.reflect.TypeToken;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

	//johnzhu@ucsd.edu
	//private ImageView userIV ;
	//private TextView userIDTV;
	private Context context = this;
	private EditText firstNameET;
	private EditText lastNameET;
	private EditText emailET;
	private EditText userNameET;
	private EditText pwdET;
	
	/*static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
		"Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
		"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };*/
	public static final String GUESTMSG = "N/a";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//hidden the keyboard!!!
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		userNameET= (EditText)findViewById(R.id.profile_userNameET);
		firstNameET = (EditText)findViewById(R.id.profile_firstNameET);
		lastNameET = (EditText)findViewById(R.id.profile_lastNameET);
		emailET = (EditText)findViewById(R.id.profile_emailET);
		pwdET = (EditText) findViewById(R.id.profile_newPwdET);
		
			
		try {
			getUser();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		
		final Button button = (Button) findViewById(R.id.profile_updateBtn);
		button.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				try {
					updateUser();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//NavUtils.navigateUpFromSameTask(this);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public final static boolean isValidEmail(CharSequence target) {
	    if (target == null) {
	        return false;
	    } else {
	    	 return  android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	    
	}
	
	public void getUser() throws Exception {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		int userID = SaveSharedPreference.getUserId(context);
		nameValuePair.add(new BasicNameValuePair("user_id", userID + ""));
				
		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/getUser");

		// sets the post request as the resulting string
		httpost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		//Log.d("bug", (new UrlEncodedFormEntity(nameValuePair)).toString());
		// Handles what is returned from the page
		//ResponseHandler responseHandler = new BasicResponseHandler();
		//Log.d("bugs", "execute request");
		new GetHttpRequest().execute(httpost);

	}
	
	public void updateUser() throws Exception {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		//int userID = SaveSharedPreference.getUserId(context);
		nameValuePair.add(new BasicNameValuePair("username", "ysugiant"));
		nameValuePair.add(new BasicNameValuePair("first_name", firstNameET.getText().toString()));	
		nameValuePair.add(new BasicNameValuePair("last_name", lastNameET.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("email", emailET.getText().toString()));
		if (!pwdET.getText().toString().equals(""))
			nameValuePair.add(new BasicNameValuePair("password", pwdET.getText().toString()));
		
		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/updateUser");

		// sets the post request as the resulting string
		httpost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		Log.d("bug", (new UrlEncodedFormEntity(nameValuePair)).toString());
		// Handles what is returned from the page
		//ResponseHandler responseHandler = new BasicResponseHandler();
		//Log.d("bugs", "execute request");
		new UpdateHttpRequest().execute(httpost);

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
			Gson gson = new Gson();
			Type mapType = new TypeToken<Map<String,String>>(){}.getType();
			Map<String, String> answer = gson.fromJson(json, mapType);
            
			// SET TEXT FIELD TO RESPONSE 
			if(answer.get("result").equals("true"))
			{
				userNameET.setText(answer.get("username"));
				lastNameET.setText(answer.get("last_name"));
				firstNameET.setText(answer.get("first_name"));
				emailET.setText(answer.get("email"));
			}
		}
	}

	private class UpdateHttpRequest extends AsyncTask<HttpPost, Void, String> {

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
			Gson gson = new Gson();
			Type mapType = new TypeToken<Map<String,String>>(){}.getType();
			final Map<String, String> answer = gson.fromJson(json, mapType); 
			//Strip off the quotes around the message!
			String msg = answer.get("message").toString();
			msg = msg.substring(1, msg.length()-1);			
			Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();				    
			if(answer.get("result").equals("true"))
			{
				finish();
			}
		}
	}
}
