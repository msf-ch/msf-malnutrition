package org.msf.android.adapters;

import java.text.SimpleDateFormat;
import java.util.List;

import org.msf.android.R;
import org.msf.android.openmrs.Constants;
import org.msf.android.openmrs.Observation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ObservationAdapter extends ArrayAdapter<Observation> {

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM dd, yyyy");

	public ObservationAdapter(Context context, int textViewResourceId,
			List<Observation> items) {
		super(context, textViewResourceId, items);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.observation_list_item2, null);
		}
		
		Observation obs = getItem(position);
		if (obs != null) {

			TextView textView = (TextView) v.findViewById(R.id.fieldname_text);
			if (textView != null) {
				textView.setText(obs.getFieldName());
			}

			textView = (TextView) v.findViewById(R.id.value_text);
			if (textView != null) {
				//because we no longer have conceptDataType
//				switch (obs.getConceptDataType()) {
////				case Constants.TYPE_INT:
////					textView.setText(obs.getValueInt().toString());
////					break;
//				case NUMERIC:
////					textView.setText(obs.getValueNumeric().toString());
//					break;
//				case DATE:
//					textView.setText(mDateFormat.format(obs.getValueDate()));
//					break;
//				default:
//					textView.setText(obs.getValue());
//				}
			}

			textView = (TextView) v.findViewById(R.id.encounterdate_text);
			if (textView != null) {
				textView.setText(mDateFormat.format(obs.getEncounterDate()));
			}
		}
		return v;
	}
}
