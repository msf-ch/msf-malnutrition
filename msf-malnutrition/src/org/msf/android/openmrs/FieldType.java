package org.msf.android.openmrs;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "fieldtypes")
public class FieldType extends OpenMRSObject {
//	  "uuid": "8d5e8196-c2cc-11de-8d13-0010c6dffd0f",
//    "fieldTypeId": 2,
//    "name": "Database element",
//    "description": ""
	
	private String name;
	private String description;
	private boolean set;
	
	public boolean isSet() {
		return set;
	}
	public void setSet(boolean set) {
		this.set = set;
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
