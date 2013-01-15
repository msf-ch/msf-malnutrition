package org.msf.android.openmrs;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "cohorts")
public class Cohort extends OpenMRSObject {
	@DatabaseField
	private String name;
	@DatabaseField
	private String description;
	
    @Override
    public String toString() {
    	return name;
    }
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
