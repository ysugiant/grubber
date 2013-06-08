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
import android.app.ProgressDialog;
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
import android.view.inputmethod.InputMethodManager;
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

public class FoodPageActivity extends Activity {

	Context context = this;

	final int MIN =0;
	final int MAX = 3;

	private  ArrayList<Review> reviewList = new ArrayList<Review>();
	private TextView foodName;
	private ImageView foodImg;
	protected TextView totalVoteTV; // private
	private TextView description;
	private ImageButton voteBtn;
	private TextView reviewUsrName;
	private TextView reviewContent;
	private TextView reviewListLabel;
	protected TextView noEntries;
	private EditText voteComment;
	private ListView reviewLV;
	private ImageLoader imageLoader = new ImageLoader (context);
	private String food_id;
	private ProgressDialog progDialog; 

	//private TextView reviewMoreTV;

	private int current_page;
	private int itemsPerPage = 8;
	protected int totalVotes = 0; // private
	private Button loadMoreBtn;



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
	                reviewHolder.time.setText("•  " +review.getTime());
	            }

	            return v;
	        }
	    }



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_page);
		LayoutInflater inflater = getLayoutInflater();
		View header = inflater.inflate(R.layout.review_list_header, null);
		View footer = inflater.inflate(R.layout.review_list_footer, null);
									  // (ViewGroup) findViewById(R.id.header_root_reviewlist));
		reviewLV = (ListView) findViewById(R.id.foogpage_reviewList);
		foodName = (TextView) header.findViewById(R.id.foogpage_foodNameTV);
		foodImg = (ImageView) header.findViewById(R.id.foogpage_foodimgIV);
		description = (TextView) header.findViewById(R.id.foodpage_description);
		
		// vote stuff
		totalVoteTV = (TextView) header.findViewById(R.id.foodpage_totalVoteNumTV);
		voteBtn = (ImageButton) header.findViewById(R.id.foodpage_voteBtn);
		voteComment = (EditText) header.findViewById(R.id.foodpage_commentET);
		
		// review list
		reviewListLabel = (TextView) header.findViewById(R.id.foodpage_reviewListLabel);
		loadMoreBtn = (Button) footer.findViewById(R.id.foodpage_loadMore);
		
		reviewLV.addFooterView(footer, null, false);
		reviewLV.addHeaderView(header, null, false);

		
		current_page = 0;
		/*
		foodName = (TextView) findViewById(R.id.foogpage_foodNameTV);
		foodImg = (ImageView)findViewById(R.id.foogpage_foodimgIV);
		totalVoteTV = (TextView) findViewById(R.id.foodpage_totalVoteNumTV);
		voteBtn = (ImageButton) findViewById(R.id.foodpage_voteBtn);
		voteComment = (EditText) findViewById(R.id.foodpage_commentET);
		reviewLV = (ListView) findViewById(R.id.foogpage_reviewList);
		//reviewMoreTV = (TextView) findViewById(R.id.foodpage_reviewMoreTV);
*/
		getActionBar().setDisplayHomeAsUpEnabled(true);

		//reviewMoreTV.setOnClickListener(this);  

		//("bug", getIntent().getStringExtra("total_vote") );
		foodName.setText( getIntent().getStringExtra("name"));
		
		
		totalVotes = Integer.parseInt(getIntent().getStringExtra("total_vote"));
		String voteString = totalVotes > 1 ? " votes" : " vote";
		totalVoteTV.setText(getIntent().getStringExtra("total_vote")+ voteString);
		description.setText(getIntent().getStringExtra("description") );
		/*
		if (totalVotes == 0) {
			//reviewLV.setVisibility(View.GONE);
			//reviewListLabel.setText("No reviews for this food yet.");
			loadMoreBtn.setVisibility(View.GONE);
		}
		else {*/
			loadMoreBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						current_page += 1;
						getComment();
					} catch (Exception e) {
						Log.d("bugs", "caught getComment when loading more");
						e.printStackTrace();
					}
					
				}
			});
		//}

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
		
		voteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					setComment();
					// reset vote comment 
					voteComment.clearFocus();
					voteComment.getText().clear();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(voteComment.getWindowToken(), 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					getComment();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
		});
	}

	public void onResume() {
    	super.onResume();
    	//Refresh the options menu when this activity comes in focus
    	invalidateOptionsMenu();
    	if(SaveSharedPreference.getUserId(context) == 0){
	  		voteBtn.setClickable(false);
	  		voteComment.setEnabled(false);
	  	}else{
	  		voteBtn.setClickable(true);
	  		voteComment.setEnabled(true);
	  	}
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
        					voteBtn.setClickable(false);
        			  		voteComment.setEnabled(false);
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
		nameValuePair.add(new BasicNameValuePair("min", String.valueOf((itemsPerPage * (current_page)))));
		if (totalVotes == 0)
			nameValuePair.add(new BasicNameValuePair("max","0"));
		else
		nameValuePair.add(new BasicNameValuePair("max", String.valueOf((itemsPerPage * (current_page + 1))-1)));

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
				totalVotes = Integer.parseInt(obj.get("total").getAsString());
				String voteString = totalVotes > 1 ? " votes" : " vote";
				totalVoteTV.setText(obj.get("total").getAsString());		    
			    
				if (current_page == 0)
					reviewList = new ArrayList<Review>();

				if (current_page >= ((int)totalVotes/(int)itemsPerPage)) {
					loadMoreBtn.setVisibility(View.GONE);
				}
			    

			    JsonArray results = (JsonArray) obj.get("result");

			    //get the last 3 comment from the server
			    for(int i =0; i < results.size(); i++ ){
			    	JsonObject res = (JsonObject) results.get(i);
		        	reviewList.add(new Review(res.get("username").getAsString(),res.get("comment").getAsString(),res.get("time").getAsString().split("\\s+")[0]));
			    }
			    
		        
			    if (totalVotes > 0) {
				    int currentPosition = reviewLV.getFirstVisiblePosition();
			    	reviewLV.setAdapter(new ReviewAdapter(context, R.layout.review_list_item, reviewList)); 
			    	
			    	// set new scroll position 
			        reviewLV.setSelectionFromTop(currentPosition,  0);
			    }
			    else
			    	reviewLV.setAdapter(new ArrayAdapter<String>(context, R.layout.food_misc, new String[] {"No reviews yet."}));

			} catch (Exception e) { Log.d("bugs","reader"); }
		    }
		}





}