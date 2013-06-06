package com.example.grubber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;



import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class FoodPageActivity extends Activity implements View.OnClickListener {

	Context context = this;
	
	final int MIN =0;
	final int MAX = 3;
	
	private  ArrayList<Review> reviewList = new ArrayList<Review>();
	private TextView foodName;
	private ImageView foodImg;
	private TextView totalVoteTV;
	private ImageButton voteBtn;
	private TextView reviewUsrName;
	private TextView reviewContent;
	private EditText voteComment;
	private ListView reviewLV;
	private ImageLoader imageLoader = new ImageLoader (context);
	private String food_id;
	//private TextView reviewMoreTV;

	
	
	 class Review {
	        private String usrName;
	        private String usrContext;
	        private String gTime;

	        public String getName() {
	            return usrName;
	        }

	        public void setName(String name) {
	        	usrName = name;
	        }

	        public String getContext() {
	            return usrContext;
	        }

	        public void setContent(String content) {
	        	usrContext = content;
	        }
	        public String getTime(){
	        	return gTime;
	        }
	        public void setDate(String time){
	        	gTime = time;
	        }

	        public Review(String name, String content,String time) {
	        	usrName = name;
	        	usrContext = content;
	        	gTime = time;
	        	
	        }
	    }

	    public class ReviewAdapter extends ArrayAdapter<Review> {
	        private ArrayList<Review> items;
	        private ReviewViewHolder reviewHolder;

	        private class ReviewViewHolder {
	            TextView name;
	            TextView content; 
	            TextView time;
	            
	        }

	        public ReviewAdapter(Context context, int tvResId, ArrayList<Review> items) {
	            super(context, tvResId, items);
	            this.items = items;
	        }

	        @Override
	        public View getView(int pos, View convertView, ViewGroup parent) {
	            View v = convertView;
	            if (v == null) {
	                LayoutInflater vi = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
	                v = vi.inflate(R.layout.review_list_item, null);
	                reviewHolder = new ReviewViewHolder();
	                reviewHolder.name = (TextView)v.findViewById(R.id.review_username);
	                reviewHolder.content = (TextView)v.findViewById(R.id.review_content);
	                reviewHolder.time = (TextView)v.findViewById(R.id.review_time);
	                v.setTag(reviewHolder);
	            } else reviewHolder = (ReviewViewHolder)v.getTag(); 

	            Review review = items.get(pos);

	            if (review != null) {
	                reviewHolder.name.setText(review.getName());
	                reviewHolder.content.setText(review.getContext());
	                reviewHolder.time.setText(review.getTime());
	            }

	            return v;
	        }
	    }
	    

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_page);
		foodName = (TextView) findViewById(R.id.foogpage_foodNameTV);
		foodImg = (ImageView)findViewById(R.id.foogpage_foodimgIV);
		totalVoteTV = (TextView) findViewById(R.id.foodpage_totalVoteNumTV);
		voteBtn = (ImageButton) findViewById(R.id.foodpage_voteBtn);
		voteComment = (EditText) findViewById(R.id.foodpage_commentET);
		reviewLV = (ListView) findViewById(R.id.foogpage_reviewList);
		//reviewMoreTV = (TextView) findViewById(R.id.foodpage_reviewMoreTV);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if(SaveSharedPreference.getUserId(context) == 0){
	  		voteBtn.setClickable(false);
	  		voteComment.setEnabled(false);
	  		
	  	}else{
	  		voteBtn.setClickable(true);
	  		voteComment.setEnabled(true);
	  		voteBtn.setOnClickListener(this);
	  	}
		//reviewMoreTV.setOnClickListener(this);  
		
		//("bug", getIntent().getStringExtra("total_vote") );
		foodName.setText( getIntent().getStringExtra("name"));
		totalVoteTV.setText(getIntent().getStringExtra("total_vote"));
		
		food_id = getIntent().getStringExtra("food_id");
		String picurl = "http://cse190.myftp.org/picture/"+ food_id + ".jpg";
		imageLoader.DisplayImage(picurl, foodImg);
 
		try {
			getComment();
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
		reviewLV.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				  Object o = reviewLV.getItemAtPosition(position);
				  Review review = (Review)o;
				  LayoutInflater inflater = (LayoutInflater)
				       context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				  View pv = inflater.inflate(R.layout.activity_review_single, null, false);
				  PopupWindow pw = new PopupWindow(pv, 450, 500, true);
				
				  pw.setBackgroundDrawable(new BitmapDrawable());
				  pw.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0); 
				  TextView userName = (TextView) pv.findViewById(R.id.reviewSingle_usernameTV);
				  TextView userComment = (TextView) pv.findViewById (R.id.reviewSingle_userCommentTV);
				  TextView time = (TextView) pv.findViewById (R.id.reviewSingle_timeTV);
					
				  userName.setText(review.getName() + " wrote:");
				  userComment.setText(review.getContext());
				  time.setText(review.getTime());
			  } 
		});
	}
	
	public void onResume() {
    	super.onResume();
    	//Refresh the options menu when this activity comes in focus
    	invalidateOptionsMenu();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_food_page, menu);
		//Change profile button to login/register if they are not logged in
        if(SaveSharedPreference.getUserId(FoodPageActivity.this) == 0)
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
	    	  if(SaveSharedPreference.getUserId(FoodPageActivity.this) != 0){
	    		  Intent intent3 = new Intent(context, ProfileActivity.class);
	    		  startActivity(intent3);   
	    	  } else {
	    		  Intent intent3 = new Intent(context, SignInTabsActivity.class);
	    		  startActivity(intent3);   
	    	  }
	          break; 
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.foodpage_voteBtn){
			try {
				setComment();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				reviewList = new ArrayList<Review>();
				getComment();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*
		if(v.getId() == R.id.foodpage_reviewMoreTV){
		
			Intent intent = new Intent(FoodPageActivity.this, ReviewActivity.class);
        	
        	intent.putExtra("rest_id", getIntent().getStringExtra("rest_id"));
        	intent.putExtra("food_id", getIntent().getStringExtra("food_id"));
        	intent.putExtra("total_vote", getIntent().getStringExtra("total_vote"));
       
	    	startActivity(intent);	
			
		}
		*/
		
	}
	
	

	public void setComment() throws Exception {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		// need to get the food_id from the resutrant page
		
		nameValuePair.add(new BasicNameValuePair("food_id", getIntent().getStringExtra("food_id")));
		nameValuePair.add(new BasicNameValuePair("rest_id", getIntent().getStringExtra("rest_id")));
		nameValuePair.add(new BasicNameValuePair("user_id", SaveSharedPreference.getUserId(context)+""));
		nameValuePair.add(new BasicNameValuePair("comment", voteComment.getText().toString()));
		
		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/createVote");

		// sets the post request as the resulting string
		httpost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		// Handles what is returned from the page
		//ResponseHandler responseHandler = new BasicResponseHandler();
		
		new setHttpRequest().execute(httpost);
	}
	
	
	
	

	private class setHttpRequest extends AsyncTask<HttpPost, Void, HttpResponse> {
		
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
				JsonParser parser = new JsonParser();
				JsonObject obj = (JsonObject) parser.parse(json);
			    JsonObject result = (JsonObject) obj.get("result");
			    if(result.getAsBoolean())
			    	Toast.makeText(FoodPageActivity.this, "Success", Toast.LENGTH_SHORT).show();
			    else
			    	Toast.makeText(FoodPageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
		    }
			catch(Exception e)
			{
				Log.d("bugs", "reading http request failed");
			}
		}
	}
	
	
	
	
	
	
	public void getComment() throws Exception {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		// need to get the food_id from the resutrant page
		
		nameValuePair.add(new BasicNameValuePair("food_id", getIntent().getStringExtra("food_id")));
		nameValuePair.add(new BasicNameValuePair("min", MIN+""));
		nameValuePair.add(new BasicNameValuePair("max", MAX+""));
				
		// url with the post data
		HttpPost httpost = new HttpPost("http://cse190.myftp.org:8080/cse190/getComment");

		// sets the post request as the resulting string
		httpost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		// Handles what is returned from the page
		//ResponseHandler responseHandler = new BasicResponseHandler();
		//Log.d("bugs", "execute request");
		
		new getHttpRequest().execute(httpost);

	}
	
	
	
	

	private class getHttpRequest extends AsyncTask<HttpPost, Void, HttpResponse> {
		
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
				JsonParser parser = new JsonParser();
			    JsonObject obj = (JsonObject) parser.parse(json);
			    
			    //wait for total implement
				totalVoteTV.setText(obj.get("total").getAsString());

			    JsonArray results = (JsonArray) obj.get("result");
			  
			    //get the last 3 comment from the server
			    for(int i =0; i < results.size(); i++ ){
			    	JsonObject res = (JsonObject) results.get(i);
		        	reviewList.add(new Review(res.get("username").getAsString(),res.get("comment").getAsString(),res.get("time").getAsString().split("\\s+")[0]));
			    }

		        reviewLV.setAdapter(new ReviewAdapter(context, R.layout.review_list_item, reviewList)); 
			} catch (Exception e) { Log.d("bugs","reader"); }

		    	
		    	
		    }
		}
	

	
	
	
}
