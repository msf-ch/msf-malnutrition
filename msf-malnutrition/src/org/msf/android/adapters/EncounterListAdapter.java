package org.msf.android.adapters;

import org.msf.android.R;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.OpenMrsMetadataCache;
import org.msf.android.openmrs.EncounterType;
import org.msf.android.openmrs.Form;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EncounterListAdapter extends CursorAdapter {

	private int encounterTypeIdx;
	private int encounterDateIdx;
	private int patientNameIdx;
	private int formIdx;
	private int uuidIdx;

	public EncounterListAdapter(Context context, Cursor c, boolean autoRequery, ClinicAdapter ca) {
		super(context, c, autoRequery);
		
		OpenMrsMetadataCache.buildEncounterTypeCache(ca);
		OpenMrsMetadataCache.buildFormsCache(ca);

		patientNameIdx = c.getColumnIndexOrThrow("patientName");
		encounterTypeIdx = c.getColumnIndexOrThrow("encounterType");
		encounterDateIdx = c.getColumnIndexOrThrow("encounterDatetime");
		formIdx = c.getColumnIndexOrThrow("form");
		uuidIdx = c.getColumnIndexOrThrow("uuid");
	}

	@Override
	public void bindView(View v, Context context, Cursor cursor) {
		TextView textView = (TextView) v
				.findViewById(R.id.encounter_name_field);
		if (textView != null) {
			String encountetTypeUuid = cursor.getString(encounterTypeIdx);
			EncounterType et = OpenMrsMetadataCache.getEncounterType(encountetTypeUuid);
			textView.setText(et.getName());
		}

		textView = (TextView) v.findViewById(R.id.encounter_date_field);
		if (textView != null) {
			String date = cursor.getString(encounterDateIdx);
			if (date != null) {
				textView.setText(date.substring(0, 10));
			}
		}

		textView = (TextView) v.findViewById(R.id.patient_name_field);
		if (textView != null) {
			textView.setText(cursor.getString(patientNameIdx));
		}

		textView = (TextView) v.findViewById(R.id.formNameField);
		if (textView != null) {
			String formUuid = cursor.getString(formIdx);
			Form form = OpenMrsMetadataCache.getForm(formUuid);
			textView.setText(form.getName());
		}

		ImageView leftIcon = (ImageView) v
				.findViewById(R.id.encounter_left_icon);
		if (leftIcon != null) {
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = vi.inflate(R.layout.encounter_list_item, null);

		bindView(v, context, cursor);

		return v;
	}
}
