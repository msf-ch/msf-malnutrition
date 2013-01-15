package org.msf.android.openmrs;

import java.util.List;

import org.msf.android.rest.JsonOpenMRSObjectDeserializer;
import org.msf.android.rest.UuidListTools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

//"uuid": "2472cfc0-11cd-4d20-bb7b-7707929251e5",
//"id": 2,
//"formId": 2,
//"name": "Cholera form 2",
//"description": "Test cholera form",
//"version": "1",
//"published": false,
//"encounterType": "8d5b27bc-c2cc-11de-8d13-0010c6dffd0f",
//"formFields": "be1bfea9-3b80-41b8-b921-9ced616f1c09,0197a867-0c1d-4c4d-b9ca-9035e1172ae0,3235ba28-f065-4b2d-87bd-6badc990a6fe,179f922d-29a2-4a6c-bd4b-017f74abfffe,fe5bfbee-4b5f-4d00-838f-03b7760ad6c7,71d28901-41dd-4447-8502-6361a2da21f8,1274eeed-b821-4e2e-8a8a-e20732e3ca4d,c5649057-0689-46ca-bc47-324b8e9444ee,88388c4f-7958-49a7-a066-92bbac383dd3,1c343fab-5e1a-4fbd-bd4c-c7bce5f8d6b2"

@DatabaseTable(tableName = "forms")
public class Form extends OpenMRSObject {
	@DatabaseField
	public String name;
	@DatabaseField
	public String description;
	@DatabaseField
	public String version;
	@DatabaseField
	public Boolean published;
	@DatabaseField
	@JsonDeserialize(as = String.class, using = JsonOpenMRSObjectDeserializer.class)
	public String encounterType;
	@DatabaseField
	@JsonIgnore
	public String formFields;

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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public String getEncounterType() {
		return encounterType;
	}

	public void setEncounterType(String encounterType) {
		this.encounterType = encounterType;
	}

	public List<String> getFormFields() {
		return UuidListTools.deserializeUuidList(formFields);
	}

	public void setFormFields(List<String> formFields) {
		this.formFields = UuidListTools.serializeUuidList(formFields);
	}
	
	@JsonSetter(value = "formFields")
	public void setFormFieldsFromObjects(List<OpenMRSObject> formFields) {
		this.formFields = UuidListTools.serializeUuidListFromObjects(formFields);
	}
}
