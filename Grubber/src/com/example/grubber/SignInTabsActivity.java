package com.example.grubber;

import java.util.HashMap;
import com.example.grubber.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.*;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

public class SignInTabsActivity extends FragmentActivity {

	//borrowed from http://thepseudocoder.wordpress.com/2011/10/04/android-tabs-the-fragment-way/
    private TabHost mTabHost;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
    private TabInfo mLastTab = null;
    
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
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //hide keyboard by default
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        
        
  		setContentView(R.layout.signin_tabbed);
  		
        // setup TabHost
        initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
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
        SignInTabsActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("LoginFragment").setIndicator("Login"), ( tabInfo = new TabInfo("LoginFragment", LoginFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        SignInTabsActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("RegisterFragment").setIndicator("Register"), ( tabInfo = new TabInfo("RegisterFragment", SignUpFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        // Default to first tab
        this.onTabChanged("LoginFragment");
        //
        mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
        	public void onTabChanged(String tag) {
                TabInfo newTab = (TabInfo) mapTabInfo.get(tag);
                if (mLastTab != newTab) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    if (mLastTab != null) {
                        if (mLastTab.fragment != null) {
                            ft.detach(mLastTab.fragment);
                        }
                    }
                    if (newTab != null) {
                        if (newTab.fragment == null) {
                            newTab.fragment = Fragment.instantiate(getBaseContext(),
                                    newTab.clss.getName(), newTab.args);
                            if(tag.equals("LoginFragment"))
                            	ft.add(R.id.login_tab, newTab.fragment, newTab.tag);
                            else if(tag.equals("RegisterFragment"))
                            	ft.add(R.id.register_tab, newTab.fragment, newTab.tag);
                        } else {
                            ft.attach(newTab.fragment);
                        }
                    }
         
                    mLastTab = newTab;
                    ft.commit();
                    getSupportFragmentManager().executePendingTransactions();
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
    private static void addTab(SignInTabsActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        String tag = tabSpec.getTag();
 
        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.detach(tabInfo.fragment);
            ft.commit();
            activity.getSupportFragmentManager().executePendingTransactions();
        }
 
        tabHost.addTab(tabSpec);
    }
 
    /** (non-Javadoc)
     * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
     */
    public void onTabChanged(String tag) {
        TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
        if (mLastTab != newTab) {
            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(this,
                            newTab.clss.getName(), newTab.args);
                    if(tag.equals("LoginFragment"))
                    	ft.add(R.id.login_tab, newTab.fragment, newTab.tag);
                    else if(tag.equals("RegisterFragment"))
                    	ft.add(R.id.register_tab, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }
 
            mLastTab = newTab;
            ft.commit();
            this.getSupportFragmentManager().executePendingTransactions();
        }
    }
    
    
 
}
