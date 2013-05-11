package com.example.grubber;

import com.example.grubber.R;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends Activity  {
	
	public final Context context = this;
<<<<<<< HEAD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
=======
	public final static String USER_PROFILE= "View Profile";
	public final static String ABOUT= "About Us";
	public final static String SIGNOUT= "Sign Out";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /* 
        if(!SaveSharedPreference.isLogin(MainActivity.this))
        {
             Toast.makeText(this,"No login",Toast.LENGTH_SHORT).show();
        }
        else
        {
             // Call Next Activity
        	 Toast.makeText(this,SaveSharedPreference.getUserId(MainActivity.this) + "Logged in",Toast.LENGTH_SHORT).show();
        }*/

>>>>>>> 30a8de57a5e849536070036cd1e02578a56084f4
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	//Refresh the options menu when this activity comes in focus
    	invalidateOptionsMenu();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
<<<<<<< HEAD
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
=======
      
        menu.add(ABOUT);
        
   
  	  // if the user already login go to profile page, otherwise go to login page
  	  MenuItem profile  = menu.add(R.string.user_profile);
  	  profile.setOnMenuItemClickListener(new OnMenuItemClickListener() {
        	public boolean onMenuItemClick(MenuItem item) {
        		
        		//Log.d("bug", SaveSharedPreference.getUserId(MainActivity.this)+"");
        	//	UserInfoHelper userInfo = UserInfoHelper.getInstance();
				if(SaveSharedPreference.isLogin(MainActivity.this)){
					Intent intent = new Intent(context, ProfileActivity.class);
			    	startActivity(intent);   
				}else{
					Intent intent = new Intent(context, LoginActivity.class);
			    	startActivity(intent);   
				}
        		
        		return true;
        	}
        		
        	});
>>>>>>> 30a8de57a5e849536070036cd1e02578a56084f4
        
        //Change profile button to login/register if they are not logged in
        if(SaveSharedPreference.getUserId(MainActivity.this) == 0)
        {
            MenuItem profileItem = menu.findItem(R.id.action_profile);
        	profileItem.setTitle(R.string.login);
            Toast.makeText(this,"Not logged in",Toast.LENGTH_SHORT).show();
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
							int tempUserName = SaveSharedPreference.getUserId(context);    			        		
			        		dialog.cancel();    			        		
			        		SaveSharedPreference.setUserId(context, 0);
        					Toast.makeText(context ,tempUserName + " logged out" , Toast.LENGTH_SHORT).show();
        					invalidateOptionsMenu();
						}    						
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
<<<<<<< HEAD
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
=======
					public void onClick(DialogInterface dialog,int id) {
						
						dialog.cancel();
					
					}}
				  );
        		
        		AlertDialog alertDialog = alertDialogBuilder.create();
        		alertDialog.show();
        	}else{ 
        		Toast.makeText(context ,"You haven't login yet!" , Toast.LENGTH_SHORT).show();
        	}
        		
        	
        		return true;
        		
        	} 	
        });
        
     
        return super.onCreateOptionsMenu(menu);
    }
    
    public void doSearchNearby(View view) {
    	Intent intent = new Intent(this, Results.class);
    	//get long lat 
    	LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
    	Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	intent.putExtra("longitude", location.getLongitude());
    	intent.putExtra("latitude", location.getLatitude());
    	
    	startActivity(intent);   
    }
    
    public void doLogin(View view) {
    	Intent intent = new Intent(this, LoginActivity.class);
    	startActivity(intent);	
    }
    
    public void openSearchDialog(View view) {
    	Intent intent = new Intent(this, Results.class);
    	intent.putExtra("key", "taco");
    	startActivity(intent);
    	
    	
    	//onSearchRequested();
    }
>>>>>>> 30a8de57a5e849536070036cd1e02578a56084f4
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
	      case R.id.menu_search:
	    	  Intent intent = new Intent(this, Results.class);
	    	  intent.putExtra("key", "taco");
	    	  startActivity(intent);
	    	  break;
	      case R.id.action_nearby:
	    	  Intent intent2 = new Intent(this, Results.class);
	    	  //get long lat 
	    	  LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
	    	  Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    	  intent2.putExtra("longitude", location.getLongitude());
	    	  intent2.putExtra("latitude", location.getLatitude());			
	    	  startActivity(intent2);   
	    	  break;
	      case R.id.action_profile:
				if(SaveSharedPreference.getUserId(MainActivity.this) != 0){
					Intent intent3 = new Intent(context, ProfileActivity.class);
			    	startActivity(intent3);   
				}else{
					Intent intent3 = new Intent(context, LoginActivity.class);
			    	startActivity(intent3);   
				}
	          break;        
	      default:
	    	  break;
      }

      return true;
    } 
}