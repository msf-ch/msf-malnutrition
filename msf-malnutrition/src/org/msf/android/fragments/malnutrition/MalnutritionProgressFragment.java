package org.msf.android.fragments.malnutrition;

import org.msf.android.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MalnutritionProgressFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup layout = (ViewGroup)inflater.inflate(R.layout.malnutrition_progress_fragment, null);
		return layout;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
}
