package org.msf.android.htmlforms;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.Observation;
import org.msf.android.openmrs.Patient;
import org.msf.android.openmrs.Patient.PatientIdentifier;
import org.msf.android.openmrs.Patient.PersonAddress;
import org.msf.android.utilities.MSFCommonUtils;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

public class HtmlFormManager {

	SimpleDateFormat ddmmyyyy = new SimpleDateFormat("dd-MM-yyyy");
	


	public void submitCholeraForm(ReducedFormData formData) {
		ReducedEncounter reducedEncounter = formData.getEncounter();
		ReducedPatient reducedPatient = formData.getPatient();
		List<ReducedObs> reducedObs = formData.getObs();

		Encounter e = new Encounter();
		Patient p = new Patient();
		List<Observation> obs = new ArrayList<Observation>();

		PatientIdentifier ident = new PatientIdentifier();
		ident.identifier = reducedPatient.identifier;
		ident.identifierType = "8d79403a-c2cc-11de-8d13-0010c6dffd0f"; // Old ID
		ident.location = "e343e91a-e058-4f28-9506-927c21e9cdd4"; // Cameroon
		List<PatientIdentifier> ids = new ArrayList<PatientIdentifier>();
		ids.add(ident);
		p.setIdentifiers(ids);

		p.setGivenName(reducedPatient.givenName);
		p.setMiddleName(reducedPatient.middleName);
		p.setFamilyName(reducedPatient.familyName);
		try {
			p.setBirthdate(MSFCommonUtils.DDMMYYYY_DATE_FORMATTER
					.parse(reducedPatient.birthDate));
		} catch (ParseException e2) {
			e2.printStackTrace();
		}
		p.setGender(Patient.Gender.valueOf(reducedPatient.gender));
		PersonAddress address = new PersonAddress();
		address.cityVillage = reducedPatient.city;
		p.setAddresses(Collections.singletonList(address));
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");	
		Calendar cal = Calendar.getInstance(); 
		int year = cal.get(Calendar.YEAR)-1900;
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		Date date = new Date(year,month,day);
		String newD = dateFormat.format(date);							
		try {
				date = dateFormat.parse(newD);
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
		
		p.setLocallyCreatedDate(date);
		
		//Fill encounter
		e.setEncounterDatetimeFromDDMMYYYYString(reducedEncounter.encounterDatetime);
		e.setEncounterType("8d5b27bc-c2cc-11de-8d13-0010c6dffd0f");
		e.setLocation("e343e91a-e058-4f28-9506-927c21e9cdd4");
		e.setForm("46c3e8c1-b532-437b-906a-317d39b20bfb");
		
		e.attachPatient(p);
		e.setProvider("78977a06-a7c4-45b1-be84-7413adeaa6a0");
		
		Observation newObs;
		for (ReducedObs rob : reducedObs) {
			if (rob.concept == null || rob.concept.isEmpty()) {
				Log.e("HtmlFormManager", "Concept null for answer: "
						+ rob.value + "/" + rob.valueCoded);
				continue;
			}

			newObs = new Observation();

			newObs.setConcept(rob.concept);
			Object val;
			if (rob.valueCoded != null && !rob.valueCoded.isEmpty()) {
				val = rob.valueCoded;
			} else {
				val = rob.value;
				//TODO: Handling dates lazily here, needs to be a separate field within the Ob rather than just RegEx recognition
				try {
					val = ddmmyyyy.parse(val.toString());
				} catch (ParseException ex) {
					//just don't change val! No handling needed.
				}
			}
			//Object val = (rob.valueCoded != null && !rob.valueCoded.isEmpty()) ? rob.valueCoded : rob.value;
			if (val == null || (val instanceof String && ((String)val).isEmpty())) {
				Log.e("HtmlFormManager", "Value empty/null for concept: "
						+ rob.concept);
				continue;
			}
			newObs.setValue(val);
			newObs.setObsDatetime(e.getEncounterDatetime());
			newObs.setPerson(e.getPatientUuid());
			newObs.setEncounter(e);

			obs.add(newObs);
		}
		
		//Save it all to the db
		try {
			ClinicAdapter ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
			
			Dao<Patient, Long> pDao = ca.getPatientDao();
			pDao.create(p);
			
			Dao<Encounter, Long> eDao = ca.getEncounterDao();
			eDao.create(e);
			
			Dao<Observation, Long> oDao = ca.getObservationDao();
			for(Observation o: obs) {
				oDao.create(o);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			ClinicAdapterManager.unlock();
		}
	}
}
