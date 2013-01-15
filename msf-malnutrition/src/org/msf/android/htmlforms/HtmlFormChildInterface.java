package org.msf.android.htmlforms;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.msf.android.R;
import org.msf.android.activities.ViewPatientActivity;
import org.msf.android.app.MSFClinicApp;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Location;
import org.msf.android.openmrs.Patient;
import org.msf.android.openmrs.Patient.PersonAddress;
import org.msf.android.rest.DefaultMapperFactory;
import org.msf.android.utilities.MedicalTimeUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.dao.Dao;

public class HtmlFormChildInterface {

	private ObjectMapper mapper;

	private HashMap<String,Integer> hashmapStringValues = new HashMap<String, Integer>();
	
	private String[] formStringKeys = new String[] {
			"child_survey_basic_informations",
			 "lbl_suividat"};
	
	private int[] formStringValues = new int[] {
			R.string.h2_child_survey_basic_informations,
			R.string.lbl_suividat};
	
	private Patient patient;
	private boolean isNew = true;

	private ReducedFormData formData;
	private Activity parentActivity;

	public HtmlFormChildInterface(Activity parentActivity) {
		this(parentActivity, new Patient());
		initHashMap();
	}

	public HtmlFormChildInterface(Activity parentActivity, Patient patient) {
		mapper = DefaultMapperFactory.getDefaultMapper();

		setFormData(new ReducedFormData());

		this.patient = patient;
		this.parentActivity = parentActivity;
		initHashMap();
	}

	public boolean isPatientNull() {
		if (this.patient == null)
			return true;
		else {
			isNew = false;
			return false;
		}
	}

	public void storeData(String data) throws JsonParseException,
			JsonMappingException, IOException {
		try {
			System.out.println("Form result string: " + data);
			ReducedFormData result = mapper.readValue(data,
					new TypeReference<ReducedFormData>() {
					});
			setFormData(result);

			Patient patientResult = new HtmlFormChildManager()
					.storePatientFromForm(getFormData().getPatient(),
							this.patient, isNew);

			Context context = MSFClinicApp.getAppContext();
			CharSequence text;
			if (isNew) {
				text = "Patient " + result.getPatient().familyName + " "
						+ result.getPatient().givenName
						+ " stored on the phone!";
			} else {
				text = "Patient " + result.getPatient().familyName + " "
						+ result.getPatient().givenName
						+ " successfully updated!";
			}
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();

			Intent i = new Intent(parentActivity, ViewPatientActivity.class);
			i.putExtra("_id", patientResult.getDatabaseId());
			parentActivity.getIntent()
					.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			parentActivity.startActivity(i);
		} catch (Exception ex) {
			ex.printStackTrace();

			Context context = MSFClinicApp.getAppContext();
			CharSequence text = "Error: " + ex.getMessage();
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		parentActivity.finish();
	}

	

	public String serializeData() throws JsonGenerationException,
			JsonMappingException, IOException {
		return mapper.writeValueAsString(getFormData());
	}

	public ReducedFormData getFormData() {
		return formData;
	}

	private void setFormData(ReducedFormData formData) {
		this.formData = formData;
	}
	
	public void initHashMap(){
		for (int i=0;i<formStringValues.length;i++)
			this.hashmapStringValues.put(formStringKeys[i], formStringValues[i]);
	}
    //MAKE THE FORM TRANSLATED
	public String getStringValue(String key){
			return parentActivity.getString(this.hashmapStringValues.get(key));
	}
}
