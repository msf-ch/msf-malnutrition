package org.msf.android.rest.tasks;

import java.util.List;

import org.msf.android.managers.OpenMRSConnectionManager;
import org.msf.android.openmrs.Ref;
import org.msf.android.rest.ClinicRestClient;
import org.msf.android.rest.SearchResults;

public class SearchPatientTask extends
		GenericClinicTask<String, Integer, List<Ref>> {

	@Override
	protected List<Ref> doInBackground(String... params) {
		String search = params[0];
		List<Ref> result = null;
		try {
			ClinicRestClient client = new ClinicRestClient(
					OpenMRSConnectionManager
							.getDefaultOpenMRSConnectionManager());
			SearchResults<Ref> searchResults = client.getQuery(Ref.class,
					ClinicRestClient.MOBILE_PATIENT, search, null);
			result = searchResults.getResults();  /////DIDN'T ADAPT THIS
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return result;
	}
}
