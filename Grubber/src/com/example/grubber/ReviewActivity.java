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



import com.example.grubber.FoodPageActivity.Review;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;



import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewActivity extends Activity  {

	Context context = this;
	
	final int MIN =0;
	final int MAX = 99;
	
	private  ArrayList<Review> reviewList = new ArrayList<Review>();
    private ArrayList<HashMap <String,String>> commetArrlist = new ArrayList<HashMap <String,String>>();
	private ListView reviewLV;
	
	
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
		setContentView(R.layout.activity_reviews_list);

	
		reviewLV = (ListView) findViewById(R.id.review_reivewLV);
		
	
		  
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
			  Intent newIntent= new Intent(ReviewActivity.this, ReviewSingleActivity.class); 
	   		  newIntent.putExtra("username", review.getName());
	   		  newIntent.putExtra("comment", review.getContext());
	   		  newIntent.putExtra("time", review.getTime());
	   		  startActivityForResult(newIntent, 2 );
			  } 
			  });
		  
		  

	}

	

	
	

	
	
	
	
	
	public void getComment() throws Exception {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		// need to get the food_id from the resutrant page
		
		Log.d("bug4", getIntent().getStringExtra("food_id"));
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
			
			Gson gson = new Gson();
			String json = "wrong";
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				json = reader.readLine();
				
				JsonParser parser = new JsonParser();

			    JsonObject obj = (JsonObject) parser.parse(json);
			    JsonArray results = (JsonArray) obj.get("result");
			    //JsonObject res = (JsonObject) results.get(0);

			    //HashMap <String,String> comment;
			  
			    //get the last 3 comment from the server
			    for(int i =0; i < results.size(); i++ ){
			    	  
			    	JsonObject res = (JsonObject) results.get(i);
			    	
			    	HashMap <String,String> comment = new HashMap<String, String>();
			    	comment.put("username", res.get("username").getAsString());    	
			    	comment.put("comment", res.get("comment").getAsString());
			    	comment.put("time", res.get("time").getAsString().split("\\s+")[0]);
			    	commetArrlist.add(comment);
			    }
			    
		        for (int i = 0; i < results.size(); i++) {
		     	        	reviewList.add(new Review(commetArrlist.get(i).get("username"),commetArrlist.get(i).get("comment"),commetArrlist.get(i).get("time")));
		     	        }
		        reviewLV.setAdapter(new ReviewAdapter(context, R.layout.review_list_item, reviewList)); 
			} catch (Exception e) { Log.d("bugs","reader"); }

		    	
		    	
		    }
		}
	

	
	
	
}
