package org.msf.android.activities.malnutrition;

import org.msf.android.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

public class MSFBackupActivity extends Activity {
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public void displayProgress() {
		ProgressDialog.show(this, getString(R.string.malnutrition_backup_progressdialog_title), "", false);
	}
}
