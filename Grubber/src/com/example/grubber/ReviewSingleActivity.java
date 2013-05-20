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
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ReviewSingleActivity extends Activity implements View.OnClickListener  {
	private TextView foodName;
	private TextView userName;
	private TextView userComment;
	private TextView time;
	private Button	backBtn;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review_single);
		Intent intent = getIntent();
		
		foodName = (TextView) findViewById(R.id.reviewSingle_foodnameTV);
		userName = (TextView) findViewById(R.id.reviewSingle_usernameTV);
		userComment = (TextView) findViewById (R.id.reviewSingle_userCommentTV);
		time = (TextView) findViewById (R.id.reviewSingle_timeTV);
		backBtn = (Button) findViewById(R.id.reviewSingle_backBtn);
		
		userName.setText(intent.getStringExtra("username"));
		userComment.setText(intent.getStringExtra("comment"));
		time.setText(intent.getStringExtra("time"));
		
		backBtn.setOnClickListener(this);
		
	}


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.reviewSingle_backBtn){
			Intent intent = new Intent(this, FoodPageActivity.class);
			startActivity(intent);
		}
		
	}
	
}
