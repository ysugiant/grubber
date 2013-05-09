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
	private ListView myVoteLV;	
	
	/*static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
		"Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
		"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };*/
	public static final String GUESTMSG = "N/a";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_profile);
		//hidden the keyboard!!!
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		userNameET= (EditText)findViewById(R.id.profile_userNameET);
		firstNameET = (EditText)findViewById(R.id.profile_firstNameET);
		lastNameET = (EditText)findViewById(R.id.profile_lastNameET);
		emailET = (EditText)findViewById(R.id.profile_emailET);
		pwdET = (EditText) findViewById(R.id.profile_newPwdET);
		myVoteLV = (ListView)findViewById(R.id.profile_myVoteLV);
		
		
		//UserInfoHelper user = UserInfoHelper.getInstance();
		
		/*ArrayList<String> userVote_info = new ArrayList<String>();
		for(int i = 0; i < FRUITS.length -1 ;i ++ ){
			userVote_info.add(FRUITS[i]);
		}
		
		ArrayAdapter<String> arrayAdapter;
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userVote_info);
		myVoteLV.setAdapter(arrayAdapter); */
		
			
		
			try {
				getUser();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	
	public void getUser() throws Exception {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		int userID = SaveSharedPreference.getUserId(context);
		nameValuePair.add(new BasicNameValuePair("user_id", userID+""));
				
		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/getUser");

		// sets the post request as the resulting string
		httpost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		// Handles what is returned from the page
		//ResponseHandler responseHandler = new BasicResponseHandler();
		//Log.d("bugs", "execute request");
		new GetHttpRequest().execute(httpost);

	}
	
	public void updateUser() throws Exception {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("user_id", "12"));
		nameValuePair.add(new BasicNameValuePair("first_name", firstNameET.getText().toString()));	
		nameValuePair.add(new BasicNameValuePair("last_name", lastNameET.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("email", emailET.getText().toString()));
		if (!pwdET.getText().toString().equals(""))
			nameValuePair.add(new BasicNameValuePair("password", pwdET.getText().toString()));
		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/updateUser");

		// sets the post request as the resulting string
		httpost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		// Handles what is returned from the page
		//ResponseHandler responseHandler = new BasicResponseHandler();
		//Log.d("bugs", "execute request");
		new UpdateHttpRequest().execute(httpost);

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
			Gson gson = new Gson();
			String json = "wrong";
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				json = reader.readLine();
			} catch (Exception e) { Log.d("bugs","reader"); }

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

	private class UpdateHttpRequest extends AsyncTask<HttpPost, Void, HttpResponse> {

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
			Gson gson = new Gson();
			String json = "wrong";
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				json = reader.readLine();
			} catch (Exception e) { Log.d("bugs","reader"); }

			Type mapType = new TypeToken<Map<String,String>>(){}.getType();
			final Map<String, String> answer = gson.fromJson(json, mapType);
            
			runOnUiThread(new Runnable() {
				public void run() {
				    Toast.makeText(ProfileActivity.this, answer.get("message"), Toast.LENGTH_SHORT).show();
				    }
				});
			if(answer.get("result").equals("true"))
			{
				finish();
			}
		}
	}
}
