package org.msf.android.rest.utilities;

import org.msf.android.openmrs.Patient;
import org.msf.android.openmrs.Person;
import org.msf.android.rest.ClinicRestClient;
import org.msf.android.rest.RestOperationResult;

/**
 * Use this to send and retrieve patients rather than {@link ClinicRestClient}
 * 
 * @author nwilkie
 * 
 */
public class PatientRestUtilities extends RestUtilities {

	public PatientRestUtilities(ClinicRestClient restClient) {
		super(restClient);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param patient
	 *            The patient object to send for addition at the OpenMRS server
	 * 
	 * @return The patient object returned by the server
	 */
	public RestOperationResult<Patient> createOrUpdatePatientOnOpenMRSServer(Patient patient) {
		Person personSent = patient.getPerson();

		RestOperationResult<Person> personReceived = getRestClient()
				.createOrUpdateOnOpenMRSServer(Person.class,
						ClinicRestClient.PERSON_REST_URL, null, personSent);

		if (personReceived.isSuccess()) {
			personSent.setUuid(personReceived.getObjectReceived().getUuid());
			patient.setPerson(personSent, true);
		} else {
			return new RestOperationResult<Patient>(personReceived);
		}

		RestOperationResult<Patient> patientReceived = getRestClient()
				.createOrUpdateOnOpenMRSServer(Patient.class,
						ClinicRestClient.PATIENT_REST_URL, null, patient);
		if (patientReceived.isSuccess()) {
			((Patient)patientReceived.getObjectSent()).setUuid(patientReceived.getObjectReceived().getUuid());
		}
		return patientReceived;
	}
}
