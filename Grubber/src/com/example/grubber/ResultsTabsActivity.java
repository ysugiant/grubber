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
import com.example.grubber.Results.GetHttpRequest;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

public class ResultsTabsActivity extends Activity {

	//borrowed from http://thepseudocoder.wordpress.com/2011/10/04/android-tabs-the-fragment-way/
    private TabHost mTabHost;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
    private TabInfo mLastTab = null;

	private ListView result_list;
	private ProgressDialog progDialog; 
	public final Context context = this;
	//private View main_view;
	private GoogleMap mMap;	
	
	int current_page = 0;
	ArrayList<ResultContent> list_result = null;
	final int itemsPerPage = 10;
	Button loadMore = null;
	int totalResults = 0;
	TextView noResults = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
  		setContentView(R.layout.activity_results_tabbed);
  		
        // setup TabHost
        initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
        
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		
		result_list = (ListView) findViewById(R.id.restaurantLV);
		loadMore = new Button(this);//(Button) findViewById(R.id.results_loadmore);//new Button(this); // style you later
		loadMore.setText("Load More");
		result_list.addFooterView(loadMore);
		
		
		noResults = new TextView(this);
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
		
		
	}
	


	public void getRestaurant() throws Exception {
		//start progress bar
		progDialog = ProgressDialog.show( this, "Process ", "Loading Data...",true,true);
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		
		String key = getIntent().getStringExtra("key");
		if (key!=null) {
			nameValuePair.add(new BasicNameValuePair("key", key));
		}
		
		float radius = getIntent().getFloatExtra("radius", -1.0f);
		if (radius!=-1.0f) {
			nameValuePair.add(new BasicNameValuePair("radius", String.valueOf(radius)));
		}
		nameValuePair.add(new BasicNameValuePair("min", 
												  String.valueOf(itemsPerPage * current_page)));//"0"));
		nameValuePair.add(new BasicNameValuePair("max", 
												  String.valueOf((itemsPerPage * (current_page + 1))-1)));//"10"));
		if (getIntent().getStringExtra("latitude") != null && getIntent().getStringExtra("longitude") != null) {
			nameValuePair.add(new BasicNameValuePair("latitude", getIntent().getStringExtra("latitude")));
			nameValuePair.add(new BasicNameValuePair("longitude", getIntent().getStringExtra("longitude")));
		}	
				
		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/findRestaurants");

		// sets the post request as the resulting string
		httpost.setEntity(new UrlEncodedFormEntity(nameValuePair));

		new GetHttpRequest().execute(httpost);
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
				Toast.makeText(ResultsTabsActivity.this, "Something went wrong! Oops!", Toast.LENGTH_SHORT).show();
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
				
				if( mMap != null ) { //could not load map
					//set up map
					CameraUpdate center = CameraUpdateFactory.newLatLng( new LatLng( Double.parseDouble( getIntent().getStringExtra("latitude")),  Double.parseDouble(getIntent().getStringExtra("longitude"))) );
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
	        	
	        	
	        	mMap.addMarker(new MarkerOptions()
	            .position(new LatLng(result.get("latitude").getAsDouble(), result.get("longitude").getAsDouble()))
	            .title(result.get("name").getAsString()));

	        }
	        int currentPosition = result_list.getFirstVisiblePosition();
	        ResultAdapter radapter = new ResultAdapter(ResultsTabsActivity.this, list_result);

	        //Show the restaurant list to ListView
	        result_list.setAdapter(radapter);
	        
	        // set new scroll position
	        result_list.setSelectionFromTop(currentPosition,  0);
	        
	        
	        result_list.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	            {//set onClick
	            	Intent intent = new Intent(ResultsTabsActivity.this, RestaurantActivity.class);
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
	
	
	public void onResume() {
    	super.onResume();
    	//Refresh the options menu when this activity comes in focus
    	invalidateOptionsMenu();
    	//this.tracker.trackPageView("/TopTracksActivity");
    }	
	
	
	
	
    private class TabInfo {
         private String tag;
         private Class<?> clss;
         private Bundle args;
         private Fragment fragment;
         TabInfo(String tag, Class<?> clazz, Bundle args) {
             this.tag = tag;
             this.clss = clazz;
             this.args = args;
         }
 
    }
 
    class TabFactory implements TabContentFactory {
 
        private final Context mContext;
 
        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }
 
        /** (non-Javadoc)
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
 
    }

 
    /** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }
 
    /**
     * Step 2: Setup TabHost
     */
    private void initialiseTabHost(Bundle args) {
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        ResultsTabsActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("ResultsListFragment").setIndicator("List"), ( tabInfo = new TabInfo("ResultsListFragment", ResultsListFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        ResultsTabsActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("ResultsMapFragment").setIndicator("Map"), ( tabInfo = new TabInfo("ResultsMapFragment", ResultsMapFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        // Default to first tab
        this.onTabChanged("ResultsListFragment");
        //
        mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
        	public void onTabChanged(String tag) {
                TabInfo newTab = (TabInfo) mapTabInfo.get(tag);
                if (mLastTab != newTab) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    if (mLastTab != null) {
                        if (mLastTab.fragment != null) {
                            ft.detach(mLastTab.fragment);
                        }
                    }
                    if (newTab != null) {
                        if (newTab.fragment == null) {
                            newTab.fragment = Fragment.instantiate(getBaseContext(),
                                    newTab.clss.getName(), newTab.args);
                            if(tag.equals("ResultsListFragment"))
                            	ft.add(R.id.results_tab, newTab.fragment, newTab.tag);
                            else if(tag.equals("ResultsMapFragment"))
                            	ft.add(R.id.map_tab, newTab.fragment, newTab.tag);
                        } else {
                            ft.attach(newTab.fragment);
                        }
                    }
         
                    mLastTab = newTab;
                    ft.commit();
                    getFragmentManager().executePendingTransactions();
                }
            }
        	});
    }
 
    
    
    /**
     * @param activity
     * @param tabHost
     * @param tabSpec
     * @param clss
     * @param args
     */
    private static void addTab(ResultsTabsActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        String tag = tabSpec.getTag();
 
        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        tabInfo.fragment = activity.getFragmentManager().findFragmentByTag(tag);
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            ft.detach(tabInfo.fragment);
            ft.commit();
            activity.getFragmentManager().executePendingTransactions();
        }
 
        tabHost.addTab(tabSpec);
    }
 
    /** (non-Javadoc)
     * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
     */
    public void onTabChanged(String tag) {
        TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
        if (mLastTab != newTab) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(this,
                            newTab.clss.getName(), newTab.args);
                    if(tag.equals("ResultsListFragment"))
                    	ft.add(R.id.results_tab, newTab.fragment, newTab.tag);
                    else if(tag.equals("MapFragment"))
                    	ft.add(R.id.map_tab, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }
 
            mLastTab = newTab;
            ft.commit();
            this.getFragmentManager().executePendingTransactions();
        }
    }
    
    
    

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.results, menu);
                      
        //Change profile button to login/register if they are not logged in
        if(SaveSharedPreference.getUserId(ResultsTabsActivity.this) == 0)
        {
            MenuItem profileItem = menu.findItem(R.id.action_profile);
        	profileItem.setTitle(R.string.login);
            //Toast.makeText(this,"Not logged in",Toast.LENGTH_SHORT).show();
        }
        else {
        	MenuItem signout = menu.findItem(R.id.action_signout);
        	signout.setVisible(true);
            signout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            	public boolean onMenuItemClick(MenuItem item) {            		        	
        			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        			alertDialogBuilder.setTitle(R.string.logout_msg);
        			alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog,int id) {            					    							
							//int tempUserName = SaveSharedPreference.getUserId(context);    			        		
			        		dialog.cancel();    			        		
			        		SaveSharedPreference.setUserId(context, 0);
        					Toast.makeText(context , "Logged out" , Toast.LENGTH_SHORT).show();
        					invalidateOptionsMenu();
						}    						
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog,int id) {    						
    						dialog.cancel();    					
    					}}
    				  );            		
            		AlertDialog alertDialog = alertDialogBuilder.create();
            		alertDialog.show();   
            		return true;            		
            	} 	
            });        	
        }        
        return true;
      }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
	      case R.id.action_profile:
	    	  if(SaveSharedPreference.getUserId(ResultsTabsActivity.this) != 0){
	    		  Intent intent3 = new Intent(context, ProfileActivity.class);
	    		  startActivity(intent3);   
	    	  } else {
	    		  Intent intent3 = new Intent(context, SignInTabsActivity.class);
	    		  startActivity(intent3);   
	    	  }
	          break;   
	       // Respond to the action bar's Up/Home button
	      case android.R.id.home:
	    	  finish();
	          return true;	          
	      default:
	    	  break;
      }

      return true;
    }
 
}
