package org.msf.android.openmrs.malnutrition;

import org.msf.android.openmrs.OpenMRSObject;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;


public class MalnutritionHousehold extends MalnutritionObject {
	@DatabaseField
	public String householdChief;
	@DatabaseField
	public String village;
	@DatabaseField
	public String surveyDate;
	@DatabaseField
	public String householdId;
	
	@DatabaseField
	public double longitude;
	@DatabaseField
	public double latitude;
	
	@ForeignCollectionField
	public ForeignCollection<MalnutritionChild> children;
	
	@ForeignCollectionField
	public ForeignCollection<MalnutritionObservation> obs;
}
