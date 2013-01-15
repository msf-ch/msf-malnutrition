package org.msf.android.fragments;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.msf.android.R;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.database.OpenMrsMetadataCache;
import org.msf.android.openmrs.Concept;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.EncounterType;
import org.msf.android.openmrs.Form;
import org.msf.android.openmrs.Location;
import org.msf.android.openmrs.Observation;

import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ViewEncounterFragment extends ListFragment {
	private Encounter encounter;

	private TextView formNameTextView;
	private TextView encounterDateTextView;
	private TextView encounterTypeTextView;
	private TextView encounterLocationTextView;
	private TextView encounterProviderTextView;

	private ViewGroup encounterDetailsLayout;
	private ViewGroup encounterTitleLayout;

	private ArrayList<HashMap<String, String>> listItemEncounterObs;

	private Form form;
	private EncounterType encounterType;
	private Location location;

	private SimpleAdapter observationAdapter;

	public static final SimpleDateFormat OBS_DATE_VALUE_DISPLAY = new SimpleDateFormat(
			"dd MMMMM yyyy");

	public ViewEncounterFragment() {
		super();
	}

	public ViewEncounterFragment(Encounter encounter, ClinicAdapter ca)
			throws SQLException {
		super();

		setUpEncounter(encounter, ca);
	}

	public void setUpEncounter(Encounter encounter, ClinicAdapter ca)
			throws SQLException {
		this.encounter = encounter;
		this.form = OpenMrsMetadataCache.getForm(ca, encounter.getForm());
		this.encounterType = OpenMrsMetadataCache.getEncounterType(ca,
				encounter.getEncounterType());
		this.location = OpenMrsMetadataCache.getLocation(ca,
				encounter.getLocation());

		setUpEncounterList(encounter, ca);
	}

	private void setEncounterData() {
		String formName = (form != null) ? form.getName() : encounter.getForm();
		formNameTextView.setText(formName);

		String encounterDateText = new SimpleDateFormat("dd-MM-yyyy")
				.format(encounter.getEncounterDatetime());
		encounterDateTextView.setText(encounterDateText);
		String encounterTypeText = (encounterType != null) ? encounterType
				.getName() : encounter.getEncounterType();
		encounterTypeTextView.setText(encounterTypeText);

		String locationText = (location != null) ? location.getName()
				: encounter.getLocation();
		encounterLocationTextView.setText(locationText);

		encounterProviderTextView.setText(encounter.getProvider());
	}

	private void setUpEncounterList(Encounter encounter, ClinicAdapter ca)
			throws SQLException {
		listItemEncounterObs = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> map = new HashMap<String, String>();
		Collection<Observation> observations = encounter.getObs();
		for (Iterator<Observation> iter = observations.iterator(); iter
				.hasNext();) {
			Observation observation = (Observation) iter.next();

			Concept storedValueConcept = null;
			Concept storedConcept = null;

			storedConcept = OpenMrsMetadataCache.getConcept(ca,
					observation.getConcept());
			storedValueConcept = OpenMrsMetadataCache.getConcept(ca,
					observation.getValue().toString());

			String titleString;
			if (storedConcept != null) {
				titleString = storedConcept.getName();
			} else {
				titleString = observation.getConcept();
			}

			String valueString;
			if (storedValueConcept != null) {
				valueString = storedValueConcept.getName();
			} else if (observation.getValue() instanceof Date) {
				valueString = OBS_DATE_VALUE_DISPLAY.format((Date) observation
						.getValue());
			} else {
				valueString = observation.getValue().toString();
			}

			map = new HashMap<String, String>();
			map.put("title", titleString);
			map.put("description", valueString);
			map.put("encounter_date", ""); // don't want this shown when it's
											// all the same encounter

			listItemEncounterObs.add(map);

		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

		outState.putLong("_id", encounter.getDatabaseId());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null && savedInstanceState.containsKey("_id")) {
			Long _id = savedInstanceState.getLong("_id");
			try {
				ClinicAdapter ca = ClinicAdapterManager
						.lockAndRetrieveClinicAdapter();
				Encounter e = ca.getEncounterDao().queryForId(_id);

				setUpEncounter(e, ca);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				ClinicAdapterManager.unlock();
			}
		}

		View v = inflater.inflate(R.layout.encounter_panel_fragment, container,
				false);

		formNameTextView = (TextView) v.findViewById(R.id.encounter_form_title);
		encounterDateTextView = (TextView) v
				.findViewById(R.id.encounter_form_date);
		encounterTypeTextView = (TextView) v.findViewById(R.id.encounter_type);
		encounterLocationTextView = (TextView) v
				.findViewById(R.id.encounter_location);
		encounterProviderTextView = (TextView) v
				.findViewById(R.id.encounter_provider);
		encounterDetailsLayout = (ViewGroup) v
				.findViewById(R.id.encounterDetailsLayout);
		encounterTitleLayout = (ViewGroup) v
				.findViewById(R.id.encounterTitleLayout);
		
		PaintDrawable pd = new PaintDrawable(0x00FFFFFF);
		pd.setShape(new RectShape());
		pd.setCornerRadius(9.0f);
		pd.getPaint().setShadowLayer(5.0f, 0.0f, 2.0f, 0x99CCCCCC);
		v.findViewById(R.id.shadowLayer).setBackgroundDrawable(pd);

		setEncounterData();
		observationAdapter = new SimpleAdapter(getActivity(),
				listItemEncounterObs, R.layout.observation_list_item,
				new String[] { "title", "description", "encounter_date" },
				new int[] { R.id.concept_name, R.id.obs_value,
						R.id.encounter_date });
		setListAdapter(observationAdapter);
		encounterTitleLayout.setOnClickListener(new TitleClickListener());

		return v;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private class TitleClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (encounterDetailsLayout.getVisibility() == View.GONE) {
				encounterDetailsLayout.setVisibility(View.VISIBLE);
			} else {
				encounterDetailsLayout.setVisibility(View.GONE);
			}
			getView().requestLayout();
		}

	}
}
