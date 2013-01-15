package org.msf.android.activities;

import org.msf.android.R;
import org.msf.android.net.OpenMRSConnectionManager;
import org.msf.android.utilities.MSFCommonUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MSFIntroActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msf_intro);
		/*
		 * ImageView jpgView = (ImageView)findViewById(R.id.msf_logo);
		 * jpgView.setImageResource(R.drawable.msf);
		 */
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.rootLayout);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		String basePath = prefs.getString(
				getString(R.string.preferences_openmrs_server_key),
				getString(R.string.preferences_openmrs_server_default));
		String login = prefs.getString(
				getString(R.string.preferences_openmrs_server_key),
				getString(R.string.preferences_openmrs_server_default));
		char[] pwd = prefs.getString(
				getString(R.string.preferences_openmrs_server_key),
				getString(R.string.preferences_openmrs_server_default))
				.toCharArray();

		((TextView) findViewById(R.id.serverUrl)).setText(basePath);
		((TextView) findViewById(R.id.usernameField)).setText(login);
		((TextView) findViewById(R.id.pwField)).setText(String.valueOf(pwd));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void continueClicked(View view) {
//		SharedPreferences prefs = PreferenceManager
//				.getDefaultSharedPreferences(getApplicationContext());
//		SharedPreferences.Editor editor = prefs.edit();
//
//		String server = ((TextView) findViewById(R.id.serverUrl)).getText()
//				.toString();
//		String login = ((TextView) findViewById(R.id.usernameField)).getText()
//				.toString();
//		String pw = ((TextView) findViewById(R.id.pwField)).getText()
//				.toString();
//
//		editor.putString(PreferencesActivity.KEY_SERVER, server);
//		editor.putString(PreferencesActivity.KEY_USERNAME, login);
//		editor.putString(PreferencesActivity.KEY_PASSWORD, pw);
//		editor.commit();

		Intent lpaIntent = new Intent(MSFIntroActivity.this,
				LaunchScreenActivity.class);
		lpaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MSFIntroActivity.this.startActivity(lpaIntent);
	}
}
