package org.msf.android.openmrs;

import com.j256.ormlite.field.DatabaseField;


public class LocationTag extends OpenMRSObject {
	@DatabaseField
	private String name;
	@DatabaseField
	private String description;
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
