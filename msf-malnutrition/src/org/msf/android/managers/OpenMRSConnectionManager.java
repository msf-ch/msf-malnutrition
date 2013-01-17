package org.msf.android.managers;

import org.msf.android.R;
import org.msf.android.app.MSFClinicApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class OpenMRSConnectionManager {
	private String baseURL;
	private String login;
	private char[] pwd;

	public OpenMRSConnectionManager(String baseURL, String login, char[] pwd) {
		this.setBaseURL(baseURL);
		this.setLogin(login);
		this.setPwd(pwd);
	}

	public static OpenMRSConnectionManager getDefaultOpenMRSConnectionManager(
			Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String basePath = prefs.getString(
				context.getString(R.string.preferences_openmrs_server_key),
				context.getString(R.string.preferences_openmrs_server_default));
		String login = prefs.getString(
				context.getString(R.string.preferences_openmrs_login_key),
				context.getString(R.string.preferences_openmrs_login_default));
		char[] pwd = prefs.getString(
				context.getString(R.string.preferences_openmrs_password_key),
				context.getString(R.string.preferences_openmrs_password_default))
				.toCharArray();

		return new OpenMRSConnectionManager(basePath, login, pwd);
	}

	public static OpenMRSConnectionManager getDefaultOpenMRSConnectionManager() {
		return getDefaultOpenMRSConnectionManager(MSFClinicApp
					.getApplication());
	}

	public String getBaseURL() {
		return baseURL;
	}

	private void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public String getLogin() {
		return login;
	}

	private void setLogin(String login) {
		this.login = login;
	}

	public char[] getPassword() {
		return pwd;
	}

	private void setPwd(char[] pwd) {
		this.pwd = pwd;
	}
}
