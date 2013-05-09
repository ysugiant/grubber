package com.example.grubber;

import com.example.grubber.R;
import com.example.grubber.R.layout;
import com.example.grubber.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.support.v4.app.Fragment;


public class Results extends FragmentActivity {
	public FragmentManager fragMan;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}


}
