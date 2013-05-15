package com.example.grubber;

import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;

public class NewRestaurantActivity extends Activity {
	//declare EditText and Button
	private EditText rest_nameET;
	private EditText rest_addressET;
	private EditText rest_cityET;
	private EditText rest_stateET;
	private Button verify_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_restaurant);
		rest_nameET = (EditText) findViewById(R.id.restaurant_nameET);
		rest_addressET = (EditText) findViewById(R.id.restaurant_addressET);
		rest_cityET = (EditText) findViewById(R.id.restaurant_cityET);
		rest_stateET = (EditText) findViewById(R.id.restaurant_stateET);
		verify_btn = (Button) findViewById(R.id.restaurant_verify);
		
		//verify_btn.setOnClickListener(l)
        verify_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NewRestaurantActivity.this, NewRestaurantAddrVerifyActivity.class);
            	intent.putExtra("name", rest_nameET.getText().toString());
            	intent.putExtra("address", rest_addressET.getText().toString());
            	intent.putExtra("city", rest_cityET.getText().toString());
            	intent.putExtra("state", rest_stateET.getText().toString());
        		startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_restaurant, menu);
		return true;
	}

}
