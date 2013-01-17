package org.msf.android.adapters;

import java.util.TreeMap;

import org.msf.android.R;
import org.msf.android.app.MSFClinicApp;
import org.msf.android.utilities.MSFCommonUtils;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class PatientListAdapter extends CursorAdapter implements SectionIndexer {

	private int familyNameIdx;
	private int givenNameIdx;
	private int birthdateIdx;
	private int genderIdx;
	private int deathDateIdx;
	private int deadIdx;
	private int uuidIdx;
	private int preferredIdentifierIdx;
	
	public static final int LIST_GRAY = 0xFFF0F0F0;
	public static final int LIST_WHITE = 0xFFFFFFFF;
	
	private AlphabetIndexer alphabetIndexer;
	private TreeMap<Integer, Integer> sectionToPosition;
	private TreeMap<Integer, Integer> sectionToOffset;
	

	public PatientListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);

		familyNameIdx = c.getColumnIndexOrThrow("familyName");
		givenNameIdx = c.getColumnIndexOrThrow("givenName");
		birthdateIdx = c.getColumnIndexOrThrow("birthdate");
		genderIdx = c.getColumnIndexOrThrow("gender");
		deathDateIdx = c.getColumnIndexOrThrow("deathDate");
		deadIdx = c.getColumnIndexOrThrow("dead");
		uuidIdx = c.getColumnIndexOrThrow("uuid");
		preferredIdentifierIdx = c
				.getColumnIndexOrThrow("preferredPatientIdentifier");
		
		alphabetIndexer = new AlphabetIndexer(c, familyNameIdx, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		sectionToPosition = new TreeMap<Integer, Integer>();
		sectionToOffset = new TreeMap<Integer, Integer>();
	}

	@Override
	public void bindView(View v, Context context, Cursor cursor) {
		//Alternate gray/white
		if (cursor.getPosition() % 2 == 0) {
			v.setBackgroundColor(LIST_WHITE);
		} else {
			v.setBackgroundColor(LIST_GRAY);
		}
		
		//Identifier
		TextView textView = (TextView) v.findViewById(R.id.identifier_text);
		if (textView != null) {
			String preferredId = cursor.getString(preferredIdentifierIdx);
			if (preferredId != null) {
				textView.setText(preferredId);
			}
		}

		//Birthdate
		textView = (TextView) v.findViewById(R.id.birthdate_text);
		if (textView != null) {
			String birthDate = cursor.getString(birthdateIdx);
			if (birthDate != null) {
				textView.setText(birthDate.substring(0, 10));
			}
		}

		//Patient name
		textView = (TextView) v.findViewById(R.id.name_text);
		if (textView != null) {
			String givenName = cursor.getString(givenNameIdx);
			String familyName = cursor.getString(familyNameIdx);
			String fullName = "";
			if (familyName != null && !familyName.isEmpty()) {
				fullName += familyName;
			}
			if (familyName != null && givenName != null && !givenName.isEmpty()
					&& !familyName.isEmpty()) {
				fullName += ", ";
			}
			if (givenName != null && !givenName.isEmpty()) {
				fullName += givenName;
			}
			if (textView != null) {
				textView.setText(fullName);
			}
		}

		// Gender image
		ImageView imageView = (ImageView) v.findViewById(R.id.gender_image);
		if (imageView != null) {
			String gender = cursor.getString(genderIdx);
			if (gender.equals("M")) {
				imageView.setImageResource(R.drawable.male24);
			} else if (gender.equals("F")) {
				imageView.setImageResource(R.drawable.female24);
			}
		}

		// Left icon
		imageView = (ImageView) v.findViewById(R.id.leftIcon);
		if (imageView != null) {
			String uuid = cursor.getString(uuidIdx);
			if (uuid == null || uuid.isEmpty()) {
				imageView.setColorFilter(MSFCommonUtils.GRAYSCALE_FILTER);
				imageView.setImageDrawable(MSFClinicApp.getApplication()
						.getResources().getDrawable(R.drawable.memorycard24));
			} else {
				imageView.setColorFilter(MSFCommonUtils.COLOR_FILTER);
				imageView
						.setImageDrawable(MSFClinicApp.getApplication()
								.getResources()
								.getDrawable(R.drawable.openmrs_icon_24));
			}
		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = vi.inflate(R.layout.patient_list_item, null);

		bindView(v, context, cursor);

		return v;
	}

	@Override
	public int getPositionForSection(int section) {
		return alphabetIndexer.getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		return alphabetIndexer.getSectionForPosition(position);
	}

	@Override
	public Object[] getSections() {
		return alphabetIndexer.getSections();
	}
}
