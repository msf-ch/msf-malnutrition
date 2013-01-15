package org.msf.android.activities;

import org.msf.android.R;

import android.app.Activity;
import android.os.Bundle;

public class DebugActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_window);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
}
