package org.msf.android.rest.tasks;

import org.msf.android.openmrs.Patient;
import org.msf.android.rest.ClinicRestClient;

import android.util.Log;

public class DownloadSinglePatientTask extends
		GenericClinicTask<String, Integer, Patient> {

	@Override
	protected Patient doInBackground(String... params) {
		ClinicRestClient client;
		try {
			client = new ClinicRestClient();
			Patient p = client.getResource(Patient.class, ClinicRestClient.PATIENT_REST_URL, params[0], null, null).getObjectReceived();
			return p;
		} catch (Exception e) {
			Log.e("DownloadSinglePatient","couldn't get the patient", e);
			return null;
		}
	}

}
