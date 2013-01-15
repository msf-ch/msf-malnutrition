package org.msf.android.openmrs;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

import org.msf.android.rest.JsonOpenMRSObjectDeserializer;
import org.msf.android.utilities.MSFCommonUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/* Create and update
 * encounterDatetime (x)
patient (need to fix from patientid)
encounterType (x)
location (x)
form (x)
provider (x)
orders (NEED TO MAKE ORDERS)
obs (x)
 * 
 */

@DatabaseTable(tableName = "encounters")
public class Encounter extends OpenMRSObject {
	@DatabaseField
	private Date encounterDatetime;
	@DatabaseField
	@JsonDeserialize(as = String.class, using = JsonOpenMRSObjectDeserializer.class)
	private String encounterType;
	@DatabaseField
	@JsonIgnore
	private String patientUuid;
	@JsonIgnore
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Patient dbPatient;
	
	@DatabaseField
	@JsonDeserialize(as = String.class, using = JsonOpenMRSObjectDeserializer.class)
	private String location;
	@DatabaseField
	@JsonDeserialize(as = String.class, using = JsonOpenMRSObjectDeserializer.class)
	private String form;
	@DatabaseField
	@JsonDeserialize(as = String.class, using = JsonOpenMRSObjectDeserializer.class)
	private String provider;
	
	//For display optimization
	@DatabaseField
	@JsonIgnore
	private String patientName;
	
	@ForeignCollectionField //must collect to get obs, not loaded with Encounter!!
	private Collection<Observation> obs;
	
	@JsonIgnore
	public String getEncounterDatetimeAsDDMMYYYYString() {
		String result = MSFCommonUtils.DDMMYYYY_DATE_FORMATTER.format(encounterDatetime);
		return result;
	}

	@JsonIgnore
	public void setEncounterDatetimeFromDDMMYYYYString(String encounterDatetime) {
		try {
			//USE JAXB for timestamps!!! ISO8601 http://stackoverflow.com/questions/2201925/converting-iso8601-compliant-string-to-java-util-date
			this.encounterDatetime = MSFCommonUtils.DDMMYYYY_DATE_FORMATTER.parse(encounterDatetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	
	public Date getEncounterDatetime() {
		return encounterDatetime;
	}
	
	public void setEncounterDatetime(Date datetime) {
		this.encounterDatetime = datetime;
	}
	
	@JsonProperty(value = "patient")
	@JsonSerialize(as = String.class)
	public String getPatientUuid() {
		if ((patientUuid == null || patientUuid.isEmpty()) && dbPatient != null) {
			return dbPatient.getUuid();
		}
		return patientUuid;
	}

	@JsonProperty(value = "patient")
	@JsonDeserialize(using = JsonOpenMRSObjectDeserializer.class)
	public void setPatientUuid(String patient) {
		this.patientUuid = patient;
	}
	
	public void attachPatient(Patient p) {
		setPatientUuid(p.getUuid());
		setDbPatient(p);
		setPatientName(p.getFamilyName() + ", " + p.getGivenName());
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getEncounterType() {
		return encounterType;
	}

	public void setEncounterType(String encounterType) {
		this.encounterType = encounterType;
	}

	public Collection<Observation> getObs() {
		return obs;
	}

	public void setObs(Collection<Observation> obs) {
		this.obs = obs;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	@JsonIgnore
	public String getPatientName() {
		return patientName;
	}

	@JsonIgnore
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	
	@JsonIgnore
	public Patient getDbPatient() {
		return dbPatient;
	}
	
	@JsonIgnore
	public void setDbPatient(Patient dbPatient) {
		this.dbPatient = dbPatient;
	}
}
