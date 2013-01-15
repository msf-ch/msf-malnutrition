package org.msf.android.rest;

import org.msf.android.openmrs.Patient;
import org.msf.android.openmrs.Person;

/**
 * Use this to send and retrieve patients rather than {@link ClinicRestClient}
 * 
 * @author nwilkie
 * 
 */
public class PatientRestUtilities {
	private ClinicRestClient restClient;

	public PatientRestUtilities(ClinicRestClient restClient) {
		setRestClient(restClient);
	}

	/**
	 * @param patient
	 *            The patient object to send for addition at the OpenMRS server
	 * 
	 * @return The patient object returned by the server
	 */
	public RestOperationResult<Patient> postPatient(Patient patient) {
		Person personSent = patient.getPerson();

		RestOperationResult<Person> personReceived = getRestClient().createOnOpenMRSServer(
				Person.class, ClinicRestClient.PERSON_REST_URL, null,
				personSent);
		
		if(!personReceived.isSuccess()) {
			return new RestOperationResult<Patient>(personReceived);
		}
		
		personSent.setUuid(personReceived.getObjectReceived().getUuid());
		patient.setPerson(personSent, true);

		
		
		RestOperationResult<Patient> patientReceived = getRestClient().createOnOpenMRSServer(
				Patient.class, ClinicRestClient.PATIENT_REST_URL, null, patient);
		
		if (!patientReceived.isSuccess()) {
			return patientReceived;
		}
		
		patientReceived.getObjectReceived().setDatabaseId(patient.getDatabaseId());
		patient.setUuid(patientReceived.getObjectReceived().getUuid());
		
		return patientReceived;
	}

	public ClinicRestClient getRestClient() {
		return restClient;
	}

	public void setRestClient(ClinicRestClient restClient) {
		this.restClient = restClient;
	}
}
