package org.msf.android.openmrs.malnutrition;

import org.msf.android.openmrs.OpenMRSObject;

import com.j256.ormlite.field.DatabaseField;

public class MalnutritionObject extends OpenMRSObject {
	@DatabaseField
	public boolean exportedToFile;
	
	@DatabaseField
	public boolean exportedToOpenMrs;
	
	@DatabaseField
	public boolean entryCompleted;
}
