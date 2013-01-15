package org.msf.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class AbstractLaunchScreenFragment extends Fragment {
	private String tabTag;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			setTabTag(savedInstanceState.getString("tabTag"));
		}
	}
	
	public String getTabTag() {
		return tabTag;
	}

	public void setTabTag(String tabTag) {
		this.tabTag = tabTag;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString("tabTag", getTabTag());
	}
}
