package org.msf.android.adapters;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class LaunchGridListAdapter extends SimpleAdapter {

	private int gridSquareDimensions;
	private static final int DEFAULT_GRID_SQUARE_DIMENSIONS = 190;

	public LaunchGridListAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		this(context, data, resource, from, to, DEFAULT_GRID_SQUARE_DIMENSIONS);
	}
	
	public LaunchGridListAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to, int gridSquareDimensions) {
		super(context, data, resource, from, to);
		this.gridSquareDimensions = gridSquareDimensions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View result = super.getView(position, convertView, parent);
		result.setLayoutParams(new GridView.LayoutParams(gridSquareDimensions, gridSquareDimensions));
		
		return result;
	}
	
	
}
