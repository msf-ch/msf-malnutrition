package org.msf.android.activities;

import org.msf.android.R;

import android.app.Activity;
import android.os.Bundle;

public class PatientFormActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.creation_patient_fragment);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
}