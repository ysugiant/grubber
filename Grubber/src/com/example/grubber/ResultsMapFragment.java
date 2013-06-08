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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ResultsMapFragment extends Fragment {
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	// Inflate the layout for this fragment
		
		
		if (thisview != null) {
	        ViewGroup parent = (ViewGroup) thisview.getParent();
	        if (parent != null)
	            parent.removeView(thisview);
	    }
		
	    try {
			thisview = inflater.inflate(R.layout.fragment_resultsmap, container, false);
	    } catch (InflateException e) { //
	    	return thisview;
	    }
		
		try {
			getRestaurant();
		} catch (Exception e) {
			Log.d("bugs", "caught getRest");
			e.printStackTrace();
		}	
		
    	return thisview;
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

	        JsonArray jarr = jo.getAsJsonArray("result");    
	    	        

	        
			/* create map */
			/* check we haven't instantiated the map already */
			if (mMap == null) {
				mMap = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.map2)).getMap();
				
				if( mMap == null ) { //could not load map
					servicesDialog.show(getFragmentManager(), "results_services_dialog");
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
	        	

	        	//setting up map markers	 
	        	Log.d("bug", result.get("latitude").getAsString());
	        	mMap.addMarker(new MarkerOptions()
	            .position(new LatLng(result.get("latitude").getAsDouble(), result.get("longitude").getAsDouble()))
	            .title(result.get("name").getAsString()));

	        }
	        
		}
	}
	
	
}
