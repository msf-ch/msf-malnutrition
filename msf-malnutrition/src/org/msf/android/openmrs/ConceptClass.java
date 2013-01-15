package org.msf.android.openmrs;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

//"uuid": "8d4907b2-c2cc-11de-8d13-0010c6dffd0f",
//"name": "Test",
//"description": "Acq. during patient encounter (vitals, labs, etc.)",
//"retired": false,
//"links": [{
//    "uri": "http://msfcholera.ocg.msf.org:8080/openmrs/ws/rest/v1/conceptclass/8d4907b2-c2cc-11de-8d13-0010c6dffd0f",
//    "rel": "self"
//}, {
//    "uri": "http://msfcholera.ocg.msf.org:8080/openmrs/ws/rest/v1/conceptclass/8d4907b2-c2cc-11de-8d13-0010c6dffd0f?v=full",
//    "rel": "full"
//}

@DatabaseTable(tableName = "conceptClasses")
public class ConceptClass extends OpenMRSObject {
	@DatabaseField
	private String name;
	@DatabaseField
	private String description;
	@DatabaseField
	private boolean retired;
	
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
	public boolean isRetired() {
		return retired;
	}
	public void setRetired(boolean retired) {
		this.retired = retired;
	}
}
