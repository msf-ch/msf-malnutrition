package org.msf.android.app;

import org.msf.android.modules.FormModule;

import roboguice.RoboGuice;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.inject.Module;
import com.google.inject.Stage;

public class MSFClinicApp extends Application {
	private static Application application;

	public MSFClinicApp() {
		super();
	}
	
    public void onCreate(){
    	super.onCreate();
    	MSFClinicApp.application = this;
    }

    public static Application getApplication() {
    	return application;
    }
    
    public static SharedPreferences getAppPreferences() {
    	SharedPreferences result = PreferenceManager.getDefaultSharedPreferences(getApplication());
    	return result;
    }
}
