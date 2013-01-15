package org.msf.android.openmrs;

import java.util.Date;
import java.util.List;

import org.msf.android.openmrs.Patient.Gender;
import org.msf.android.openmrs.Patient.PersonAddress;
import org.msf.android.openmrs.Patient.PersonName;
import org.msf.android.rest.JsonOpenMRSObjectDeserializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

//uuid
//display
//gender
//age
//birthdate
//birthdateEstimated
//dead
//deathDate
//causeOfDeath
//preferredName
//preferredAddress
//names
//addresses
//attributes
//voided
//auditInfo
//links

public class Person extends OpenMRSObject {
	public Gender gender;
//	public int age;
	public Date birthdate;
	public boolean birthdateEstimated;
	public boolean dead;
	public Date deathDate;
	@JsonInclude(Include.NON_EMPTY)
	@JsonDeserialize(using = JsonOpenMRSObjectDeserializer.class)
	public String causeOfDeath;

	public List<PersonName> names;
	public List<PersonAddress> addresses;

	public Person() {
	}
}
