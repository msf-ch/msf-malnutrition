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

public class HtmlFormChildManager {

	SimpleDateFormat ddmmyyyy = new SimpleDateFormat("dd-MM-yyyy");
	
	public Patient storePatientFromForm(ReducedPatient reducedPatient,Patient patient, boolean isNew) {
		Patient p;
		if(isNew)
			p = new Patient();
		else
			p = patient;
		
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
		

		try {
			ClinicAdapter ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
			
			Dao<Patient, Long> pDao = ca.getPatientDao();
			if(isNew)
				pDao.create(p);
			else
				pDao.update(p);
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		} finally {
			ClinicAdapterManager.unlock();
		}
		
		return p;
	}
}
