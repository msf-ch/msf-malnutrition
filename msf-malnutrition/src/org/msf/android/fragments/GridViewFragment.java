package org.msf.android.fragments;

import org.msf.android.R;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class GridViewFragment extends AbstractLaunchScreenFragment {
	
	public GridViewFragment() {
		super();
	}

	public void setUpGridView(GridView gv) {
		gv.setColumnWidth(200);
		gv.setGravity(Gravity.CENTER);
		gv.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
		gv.setNumColumns(GridView.AUTO_FIT);
		gv.setVerticalSpacing(15);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		GridView gv = (GridView)inflater.inflate(R.layout.grid_view, container, false);
		setUpGridView(gv);

		return gv;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
