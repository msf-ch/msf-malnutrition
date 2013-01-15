package org.msf.android.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MSFClinicApp extends Application {
	private static Context context;

	public MSFClinicApp() {
		super();
	}
	
    public void onCreate(){
    	super.onCreate();
    	MSFClinicApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
    	return context;
    }
    
    public static SharedPreferences getAppPreferences() {
    	SharedPreferences result = PreferenceManager.getDefaultSharedPreferences(getAppContext());
    	return result;
    }
}
