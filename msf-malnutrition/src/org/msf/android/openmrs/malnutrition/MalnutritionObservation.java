package org.msf.android.openmrs.malnutrition;

import org.msf.android.openmrs.OpenMRSObject;

import com.j256.ormlite.field.DatabaseField;

public class MalnutritionObservation extends MalnutritionObject {
	@DatabaseField
	public String concept;
	
	@DatabaseField
	public String value;
	
	@DatabaseField(foreign = true)
	public MalnutritionChild child;

	@DatabaseField(foreign = true)
	public MalnutritionHousehold household;
}
