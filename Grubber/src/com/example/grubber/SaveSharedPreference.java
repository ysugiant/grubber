package com.example.grubber;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SaveSharedPreference 
{
    static final String PREF_USER_ID= "userid";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserId(Context ctx, int userId) 
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_USER_ID,  userId);
        editor.commit();
    }

    public static int getUserId(Context ctx)
    {
        return getSharedPreferences(ctx).getInt(PREF_USER_ID,0);
    }
    
    public static boolean isLogin(Context ctx)
    {
    	if (getUserId(ctx) == 0)
    		return false;
    	else
    		return true;
    }
    
    //logout
    public static void setLogout(Context ctx)
    {
    	setUserId(ctx, 0);
    }
}