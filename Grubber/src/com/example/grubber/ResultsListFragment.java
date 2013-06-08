package com.example.grubber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.example.grubber.ResultContent;
import com.example.grubber.ResultAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.AsyncTask;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import android.view.*;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grubber.R;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Dialog;


public class ResultsListFragment extends Fragment {
	View thisview;	
	private ListView result_list;
	private ProgressDialog progDialog; 
	//private View main_view;
	private GoogleMap mMap;	
	private Marker marker;
	
	int current_page = 0;
	ArrayList<ResultContent> list_result = null;
	final int itemsPerPage = 10;
	Button loadMore = null;
	TextView noResults = null;
	int totalResults = 0;

	DialogFragment servicesDialog = new NeedServicesDialogFragment();

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	// Inflate the layout for this fragment
    	
		thisview = inflater.inflate(R.layout.fragment_resultslist, container, false);

		result_list = (ListView) thisview.findViewById(R.id.restaurantLV);
		loadMore = new Button(getActivity());//(Button) findViewById(R.id.results_loadmore);//new Button(this); // style you later
		loadMore.setText("Load More");
		result_list.addFooterView(loadMore);
		
		
		noResults = new TextView(getActivity());
		noResults.setText("There are no results to display, please search again.");
		result_list.addHeaderView(noResults);
    	noResults.setVisibility(View.GONE);
		
		loadMore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					getRestaurant();
				} catch (Exception e) {
					Log.d("bugs", "caught getRest when loading more");
					e.printStackTrace();
				}
				
			}
		});
		
		
		loadMore.setVisibility(View.GONE);
		
		try {
			getRestaurant();
		} catch (Exception e) {
			Log.d("bugs", "caught getRest");
			e.printStackTrace();
		}	
		
		
    	return thisview;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	public void getRestaurant() throws Exception {
		//start progress bar
		progDialog = ProgressDialog.show( getActivity(), "Process ", "Loading Data...",true,true);
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		
		String key = getActivity().getIntent().getStringExtra("key");
		if (key!=null) {
			nameValuePair.add(new BasicNameValuePair("key", key));
		}
		
		float radius = getActivity().getIntent().getFloatExtra("radius", -1.0f);
		if (radius!=-1.0f) {
			nameValuePair.add(new BasicNameValuePair("radius", String.valueOf(radius)));
		}
		nameValuePair.add(new BasicNameValuePair("min", 
												  String.valueOf(itemsPerPage * current_page)));//"0"));
		nameValuePair.add(new BasicNameValuePair("max", 
												  String.valueOf((itemsPerPage * (current_page + 1))-1)));//"10"));
		if (getActivity().getIntent().getStringExtra("latitude") != null && getActivity().getIntent().getStringExtra("longitude") != null) {
			nameValuePair.add(new BasicNameValuePair("latitude", getActivity().getIntent().getStringExtra("latitude")));
			nameValuePair.add(new BasicNameValuePair("longitude", getActivity().getIntent().getStringExtra("longitude")));
		}	
				
		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/findRestaurants");

		// sets the post request as the resulting string
		httpost.setEntity(new UrlEncodedFormEntity(nameValuePair));

		new GetHttpRequest().execute(httpost);
	}
	
	public class NeedServicesDialogFragment extends DialogFragment {
	    
	    
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.need_services)
	               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            	   
	                   public void onClick(DialogInterface dialog, int id) {
	                	   //return to main screen
	               	    	Intent intent = new Intent(getActivity().getBaseContext(), MainActivity.class);
	               	    	startActivity(intent);   
	               	    	   
	                   }
	               });

	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
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
				Log.d("bugs", "Caught in HTTPGETTER");
			}
			return null;
		}

		protected void onPostExecute(String json) {
			try {
				setView(json);
			} catch (Exception e) { 
				Toast.makeText(getActivity(), "Something went wrong! Oops!", Toast.LENGTH_SHORT).show();
			};
			//stop progress bar
			progDialog.dismiss();
		}
		
		protected void setView(String jsonString)
		{
	        JsonParser jsonParser = new JsonParser();
	        JsonObject jo = (JsonObject)jsonParser.parse(jsonString);
	        if (list_result == null) {
	        	totalResults = jo.get("total").getAsInt();
	        	if(totalResults == 0)
	        		noResults.setVisibility(View.VISIBLE);
	        	else
	        		noResults.setVisibility(View.GONE);
        		loadMore.setVisibility(View.VISIBLE);
	        }
	        
			current_page += 1;
	        // want to load the next page? ASSUMING there are results available
	        if (current_page >= ((int)totalResults/(int)itemsPerPage) + 1 ) {
	        	loadMore.setVisibility(View.GONE);
	        }

	        JsonArray jarr = jo.getAsJsonArray("result");    
	    	        
	        if (list_result == null)
	        	list_result = new ArrayList<ResultContent>();
	        
			/* create map */
			/* check we haven't instantiated the map already */
			if (mMap == null) {
				mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
				
				if( mMap == null ) { //could not load map
					servicesDialog.show(getChildFragmentManager(), "results_services_dialog");
				} else {
					//set up map
					CameraUpdate center = CameraUpdateFactory.newLatLng( new LatLng( Double.parseDouble( getActivity().getIntent().getStringExtra("latitude")),  Double.parseDouble(getActivity().getIntent().getStringExtra("longitude"))) );
				    CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);

				    mMap.moveCamera(center);
				    mMap.animateCamera(zoom);
				    
				    //mMap.clear(); //reset any markers
				    
				    
				}	
			}	
	        	        
	        //Log.d("bug", jarr.toString());
	        for (int i = 0; i < jarr.size(); i++) {
	        	JsonObject result = (JsonObject) jarr.get(i);
	        	Log.d("bug", result.toString());
	        	//set for adapter value
	        	list_result.add(new ResultContent(result.get("rest_id").getAsString(), result.get("name").getAsString(),
						  result.get("address").getAsString(), result.get("city").getAsString(), result.get("state").getAsString(),
						  result.get("zip").getAsString(), result.get("longitude").getAsString(), result.get("latitude").getAsString(),
						  result.get("phone").getAsString(), result.get("website").getAsString(), result.get("distance").getAsString(),
						  result.get("votes").getAsString()));
	        	

	        	//setting up map markers	 
	        	Log.d("bug", result.get("latitude").getAsString());
	        	mMap.addMarker(new MarkerOptions()
	            .position(new LatLng(result.get("latitude").getAsDouble(), result.get("longitude").getAsDouble()))
	            .title(result.get("name").getAsString()));

	        }
	        int currentPosition = result_list.getFirstVisiblePosition();
	        ResultAdapter radapter = new ResultAdapter(getActivity(), list_result);

	        //Show the restaurant list to ListView
	        result_list.setAdapter(radapter);
	        
	        // set new scroll position
	        result_list.setSelectionFromTop(currentPosition,  0);
	        
	        
	        result_list.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	            {//set onClick
	            	Intent intent = new Intent(getActivity(), RestaurantActivity.class);
	            	ResultContent tmp = list_result.get((int) id);
	            	intent.putExtra("rest_id", tmp.getId());
	            	intent.putExtra("name", tmp.getName());
	            	intent.putExtra("address", tmp.getAddress());
	            	intent.putExtra("city", tmp.getCity() + ", " + tmp.getState() + ", " + tmp.getZip());
	            	intent.putExtra("longitude", tmp.getLongitude());
	            	intent.putExtra("latitude", tmp.getLatitude());
	            	intent.putExtra("phone", tmp.getPhone());
	            	intent.putExtra("website", tmp.getWebsite());
	            	intent.putExtra("distance", tmp.getDistance());
	        		startActivity(intent);
	            }
	        });
		}
	}	
	
}

