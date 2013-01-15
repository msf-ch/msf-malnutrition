package org.msf.android.openmrs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.msf.android.rest.DefaultMapperFactory;
import org.msf.android.rest.JsonOpenMRSObjectDeserializer;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

//Basically a person
public class Patient extends OpenMRSObject {
	
	//Serialized stored fields
	@DatabaseField
	@JsonIgnore
	private String serializedIdentifiers;
	@DatabaseField
	@JsonIgnore
	private String serializedAddresses;
	
	//Shortcut stored values
	@DatabaseField
	@JsonIgnore
	private String preferredPatientIdentifier;
	
	//Person fields
	@JsonIgnore
	@DatabaseField
	private Gender gender;
	@JsonIgnore
	@DatabaseField
	private Date birthdate;
	@JsonIgnore
	@DatabaseField
	private boolean birthdateEstimated;
	@JsonIgnore
	@DatabaseField
	private boolean dead;
	@JsonIgnore
	@DatabaseField
	private Date deathDate;
	@JsonIgnore
	@DatabaseField
	private String givenName;
	@JsonIgnore
	@DatabaseField
	private String middleName;
	@JsonIgnore
	@DatabaseField
	private String familyName;
	@JsonIgnore
	@DatabaseField
	private String familyName2;
	@JsonIgnore
	@DatabaseField
	private String familyNameSuffix;
	
	@JsonIgnore
	@ForeignCollectionField(eager = false)
	private Collection<Encounter> encounters;
//	Removed for now because we don't want to take it IN. Just submit it when that's what we're putting in an age
//	@JsonIgnore
//	@DatabaseField
//	private int age;
	
	//In case this makes the db to slow...
//	@JsonIgnore
//	@ForeignCollectionField
//	private Collection<PersonAddress> addresses;

	// public Concept? causeOfDeath;
	/*
	 * @DatabaseField (foreign = true, foreignAutoRefresh = true) public Name
	 * preferredName;
	 * 
	 * @DatabaseField(foreign = true, foreignAutoRefresh = true) public Address
	 * preferredAddress;
	 * 
	 * @DatabaseField(foreign = true, foreignAutoRefresh = true) public
	 * Attributes attributes;
	 */

	public static class PatientIdentifier extends OpenMRSObject {
		public String identifier;
		@JsonDeserialize(using = JsonOpenMRSObjectDeserializer.class)
		public String identifierType;
		@JsonDeserialize(using = JsonOpenMRSObjectDeserializer.class)
		public String location;
		public boolean preferred;
		
		public PatientIdentifier() {
		}
	}

	public static class PersonName extends OpenMRSObject {
		public String givenName;
		public String middleName;
		public String familyName;
		public String familyName2;
		public boolean preferred;
		public String prefix;
		public String familyNamePrefix;
		public String familyNameSuffix;
		public String degree;
		
		public PersonName() {
		}
	}

	public static class PersonAddress extends OpenMRSObject {
		public boolean preferred;
		public String address1;
		public String address2;
		public String cityVillage;
		public String stateProvince;
		public String country;
		public String postalCode;
		public String countyDistrict;
		public String address3;
		public String address4;
		public String address5;
		public String address6;
		public Date startDate;
		public Date endDate;
		public String latitude;
		public String longitude;
		public boolean voided;
		
		public PersonAddress() {
		}
		
		@Override
		public String toString() {
			return cityVillage;
		}
	}
	
	@JsonGetter(value = "person")
	public String getPersonUuid() {
		return getUuid();
	}

	@JsonIgnore
	public Person getPerson() {
		Person result = new Person();

		result.addresses = getAddresses();
//		result.age = this.age;
		result.birthdate = this.birthdate;
		result.birthdateEstimated = this.birthdateEstimated;
		result.dead = this.dead;
		result.deathDate = this.deathDate;
		result.gender = this.gender;
		
		PersonName newName = new PersonName();
		newName.givenName = getGivenName();
		newName.middleName = getMiddleName();
		newName.familyName = getFamilyName();
		newName.familyName2 = getFamilyName2();
		newName.familyNameSuffix = getFamilyNameSuffix();
		
		result.names = new ArrayList<PersonName>();
		result.names.add(newName);

		return result;
	}

	@JsonSetter(value = "person")
	public void setPerson(Person person) {
		setPerson(person, true);
	}
	
	public void setPerson(Person person, boolean transferUuidFromPersonToPatient) {
//		setAge(person.age);
		setBirthdate(person.birthdate);
		setBirthdateEstimated(person.birthdateEstimated);
		setDead(person.dead);
		setDeathDate(person.deathDate);
		setGender(person.gender);
		setAddresses(person.addresses);

		PersonName name = Patient.getPreferredPersonNameFromList(person.names);
		if (name != null) {
			setFamilyName(name.familyName);
			setFamilyName2(name.familyName2);
			setMiddleName(name.middleName);
			setGivenName(name.givenName);
			setFamilyNameSuffix(name.familyNameSuffix);
		}
		
		if (transferUuidFromPersonToPatient) {
			setUuid(person.getUuid());
		}
	}

	@JsonGetter(value = "identifiers")
	public List<PatientIdentifier> getIdentifiers() {
		List<PatientIdentifier> result = null;
		String serializedIdents = getSerializedIdentifiers();
		try {
			result = DefaultMapperFactory.getDefaultMapper().readValue(
					serializedIdents,
					new TypeReference<List<PatientIdentifier>>() {
					});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (NullPointerException e) {
		}

		return result;
	}

	@JsonSetter(value = "identifiers")
	public void setIdentifiers(List<PatientIdentifier> identifiers) {

		String result = null;
		try {
			result = DefaultMapperFactory.getDefaultMapper()
					.writeValueAsString(identifiers);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setSerializedIdentifiers(result);

		PatientIdentifier ident = Patient
				.getPreferredPatientIdentifierFromList(identifiers);
		if (ident != null) {
			setPreferredPatientIdentifier(ident.identifier);
		}
	}
	
	@JsonIgnore
	public List<PersonAddress> getAddresses() {
		List<PersonAddress> result = null;
		try {
			if (getSerializedAddresses() != null )
			result = DefaultMapperFactory.getDefaultMapper().readValue(
					getSerializedAddresses(),
					new TypeReference<List<PersonAddress>>() {
					});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
		}

		return result;
	}

	@JsonIgnore
	public void setAddresses(List<PersonAddress> addresses) {

		String result = null;
		try {
			result = DefaultMapperFactory.getDefaultMapper()
					.writeValueAsString(addresses);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setSerializedAddresses(result);

//		PersonAddress address = Patient
//				.getPreferredPersonAddressFromList(addresses);
		
		//maybe store city in at the patient level?
	}

	public Patient() {
	}

//	public void createOrUpdatePatient(ClinicAdapter2 ca) throws SQLException {
//		Dao<Patient, String> pDao = ca.getPatientDao();
//		Dao<PersonAddress, String> paDao = ca.getPersonAddressDao();
//
//		if (getAddresses() != null) {
//			for (PersonAddress pa : getAddresses()) {
//				pa.setPatient(this);
//				paDao.createOrUpdate(pa);
//			}
//		}
//
//		pDao.createOrUpdate(this);
//	}

	private static PersonName getPreferredPersonNameFromList(
			List<PersonName> personNames) {
		PersonName result;
		if (personNames.size() == 0) {
			return null;
		} else {
			result = personNames.get(0);
		}
		if (personNames.size() > 1) {
			for (int i = 1; i < personNames.size(); i++) {
				PersonName temp = personNames.get(i);
				if (temp != null && temp.preferred) {
					result = temp;
					break;
				}
			}
		}
		return result;
	}

	private static PatientIdentifier getPreferredPatientIdentifierFromList(
			List<PatientIdentifier> patientIdentifiers) {
		PatientIdentifier result;
		if (patientIdentifiers.size() == 0) {
			return null;
		} else {
			result = patientIdentifiers.get(0);
		}
		if (patientIdentifiers.size() > 1) {
			for (int i = 1; i < patientIdentifiers.size(); i++) {
				PatientIdentifier temp = patientIdentifiers.get(i);
				if (temp != null && temp.preferred) {
					result = temp;
					break;
				}
			}
		}
		return result;
	}

	private static PersonAddress getPreferredPersonAddressFromList(
			List<PersonAddress> personAddresses) {
		PersonAddress result;
		if (personAddresses.size() == 0) {
			return null;
		} else {
			result = personAddresses.get(0);
		}
		if (personAddresses.size() > 1) {
			for (int i = 1; i < personAddresses.size(); i++) {
				PersonAddress temp = personAddresses.get(i);
				if (temp != null && temp.preferred) {
					result = temp;
					break;
				}
			}
		}
		return result;
	}

	public enum Gender {
		M("MALE"), F("FEMALE");
		private String fullWord;

		private Gender(String fullWord) {
			this.fullWord = fullWord;
		}

		public String getFullWord() {
			return fullWord;
		}
	}
	
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

//	public int getAge() { // Should probably calculate from the birthdate if
//							// this is missing...
//		return age;
//	}
//
//	public void setAge(int age) {
//		this.age = age;
//	}

	@JsonIgnore
	public String getFullName() {
		StringBuffer buffer = new StringBuffer();
		if (getGivenName() != null && getGivenName().length() > 0) {
			buffer.append(getGivenName());
			buffer.append(" ");
		}

		if (getMiddleName() != null && getMiddleName().length() > 0) {
			buffer.append(getMiddleName());
			buffer.append(" ");
		}

		if (getFamilyName() != null && getFamilyName().length() > 0) {
			buffer.append(getFamilyName());
			buffer.append(" ");
		}

		if (getFamilyName2() != null && getFamilyName2().length() > 0) {
			buffer.append(getFamilyName2());
		}

		return buffer.toString();
	}
	
	@Override
	public String toString() {
		return getFullName();
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	
	public Date getBirthdate() {
		return birthdate;

		// try {
		// return ServerDateFormat.parse(getString(BIRTHDATE));
		// } catch (ParseException e) {
		// return null;
		// }
	}

	public boolean isBirthdateEstimated() {
		return birthdateEstimated;
	}

	public void setBirthdateEstimated(boolean birthdateEstimated) {
		this.birthdateEstimated = birthdateEstimated;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getFamilyName2() {
		return familyName2;
	}

	public void setFamilyName2(String familyName2) {
		this.familyName2 = familyName2;
	}

	private String getSerializedIdentifiers() {
		return serializedIdentifiers;
	}

	private void setSerializedIdentifiers(String serializedIdentifiers) {
		this.serializedIdentifiers = serializedIdentifiers;
	}

	public String getPreferredPatientIdentifier() {
		return preferredPatientIdentifier;
	}

	public void setPreferredPatientIdentifier(String preferredPatientIdentifier) {
		this.preferredPatientIdentifier = preferredPatientIdentifier;
	}

	public String getSerializedAddresses() {
		return serializedAddresses;
	}

	public void setSerializedAddresses(String serializedAddresses) {
		this.serializedAddresses = serializedAddresses;
	}

	public String getFamilyNameSuffix() {
		return familyNameSuffix;
	}

	public void setFamilyNameSuffix(String familyNameSuffix) {
		this.familyNameSuffix = familyNameSuffix;
	}

	@JsonIgnore
	public Collection<Encounter> getEncounters() {
		return encounters;
	}

	@JsonIgnore
	public void setEncounters(Collection<Encounter> encounters) {
		this.encounters = encounters;
	}

}
