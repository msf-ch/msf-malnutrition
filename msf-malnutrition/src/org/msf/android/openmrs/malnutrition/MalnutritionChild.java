package org.msf.android.openmrs.malnutrition;

import java.util.Date;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

public class MalnutritionChild extends MalnutritionObject {
	public static final String GENDER_MALE = "male";
	public static final String GENDER_FEMALE = "female";
	
	@DatabaseField
	public String givenName;
	
	@DatabaseField
	public String familyName;
	
	@DatabaseField
	public Date birthDate;
	
	@DatabaseField
	public String age;
	
	@DatabaseField
	public String idNumber;
	
	@DatabaseField
	public String gender;
	
	@DatabaseField(foreign = true)
	public MalnutritionHousehold household;
	
	@ForeignCollectionField
	public ForeignCollection<MalnutritionObservation> obs;
}
