package org.msf.android.htmlforms;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.msf.android.activities.ViewPatientActivity;
import org.msf.android.app.MSFClinicApp;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.Observation;
import org.msf.android.openmrs.Patient;
import org.msf.android.rest.DefaultMapperFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HtmlFormEncounterInterface {
	private ObjectMapper mapper;

	private Patient patient;
	private Encounter encounter;

	private ReducedFormData formData;
	private Activity parentActivity;

	private Collection<Observation> obs;

	private boolean isNew = true;
	
	public HtmlFormEncounterInterface(Activity parentActivity, Patient patient) {
		this(parentActivity, patient, new Encounter());
	}
	
	public HtmlFormEncounterInterface(Activity parentActivity, Patient patient,
			Encounter encounter) {
		mapper = DefaultMapperFactory.getDefaultMapper();

		setFormData(new ReducedFormData());

		this.patient = patient;
		this.encounter = encounter;
		this.parentActivity = parentActivity;
	}
	
	public boolean isEncounterNull(){
		if (this.encounter==null)
			return true;
		else{
			isNew = false;
			return false;
		}
	}
	
	public String getEncounterDatas(){
		if(isEncounterNull()==false){
			List<ReducedObs> obsReduced = new ArrayList<ReducedObs>();
			
			try {
				obs = this.encounter.getObs();
				for (Observation o : obs) {
						ReducedObs observationReduced = new ReducedObs(o.getConcept(), o.getStringValue());
						obsReduced.add(observationReduced);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
			}
			
			HashMap<String, List<ReducedObs>> objectToSend = new HashMap<String, List<ReducedObs>>();
			objectToSend.put("obs", obsReduced);
			
			
			String result;
			try {
				result = DefaultMapperFactory.getDefaultMapper().writeValueAsString(objectToSend);
				return result;
			} catch (JsonGenerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
}
	//Informations about the patient
	public String getGender (){
		return patient.getGender().getFullWord();
	}
	
	public String getBirthdate(){
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");	
		Date date = patient.getBirthdate();
		return dateFormat.format(date);
	}
	
	public void storeData(String data) throws JsonParseException,
			JsonMappingException, IOException {
		try {
			System.out.println("Form result string: " + data);
			ReducedFormData result = mapper.readValue(data,
					new TypeReference<ReducedFormData>() {
					});
			setFormData(result);

			new HtmlFormEncounterManager().storeEncounterFromForm(getFormData().getEncounter(), getFormData().getObs(), this.patient, isNew);

			Context context = MSFClinicApp.getAppContext();
			CharSequence text;
			if(isNew){
			text = "Encounter for "
					+ patient.getFamilyName() + " "
					+ patient.getGivenName() + " stored on the phone!";
			}
			else{
			text = "Encounter for "
						+ patient.getFamilyName() + " "
						+ patient.getGivenName() + " successfully updated!";				
			}
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} catch (Exception ex) {
			ex.printStackTrace();

			Context context = MSFClinicApp.getAppContext();
			CharSequence text = "Error: " + ex.getMessage();
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		
		Intent i = new Intent(parentActivity, ViewPatientActivity.class);
		i.putExtra("_id", this.patient.getDatabaseId());
		i.putExtra(ViewPatientActivity.TAG_EXTRA, ViewPatientActivity.ENCOUNTER_TAB_TAG);
		parentActivity.startActivity(i);
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
}
