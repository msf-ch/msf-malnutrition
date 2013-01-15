package org.msf.android.activities;

import org.msf.android.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;


//From ODK Collect: Keeping for reference
public class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	public static String KEY_SERVER = "server";
	public static String KEY_USERNAME = "username";
	public static String KEY_PASSWORD = "password";
	
	public static String KEY_COHORT = "cohort";
	public static String KEY_FIRST_RUN = "firstRun";
	public static String KEY_AUTO_DOWNLOAD = "autoDownload";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.server_preferences);
		setTitle(getString(R.string.app_name) + " > "
				+ getString(R.string.server_preferences));
		updateServer();
		updateUsername();
		updatePassword();

	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(KEY_SERVER)) {
			updateServer();
		} else if (key.equals(KEY_USERNAME)) {
			updateUsername();
		} else if (key.equals(KEY_PASSWORD)) {
			updatePassword();
		}
	}

	private void updateServer() {
		EditTextPreference etp = (EditTextPreference) this
				.getPreferenceScreen().findPreference(KEY_SERVER);
		String s = etp.getText();
		if (s.endsWith("/")) {
			s = s.substring(0, s.lastIndexOf("/"));
		}
		etp.setSummary(s);
		
		//OLD: from ODK Clinic days
		//when you change servers, you should wipe the cohort list.
//		mPatientDbAdapter.open();
//		mPatientDbAdapter.deleteAllCohorts();	
//		mPatientDbAdapter.close();

	}

	private void updateUsername() {
		EditTextPreference etp = (EditTextPreference) this
				.getPreferenceScreen().findPreference(KEY_USERNAME);
		etp.setSummary(etp.getText());
	}

	private void updatePassword() {
		EditTextPreference etp = (EditTextPreference) this
				.getPreferenceScreen().findPreference(KEY_PASSWORD);
		etp.setSummary(etp.getText().replaceAll(".", "*"));

	}
	
}
