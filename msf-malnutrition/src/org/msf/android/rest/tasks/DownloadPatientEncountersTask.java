package org.msf.android.rest.tasks;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.msf.android.openmrs.Encounter;
import org.msf.android.rest.ClinicRestClient;
import org.msf.android.rest.SearchResults;

public class DownloadPatientEncountersTask extends
		GenericClinicTask<String, Integer, SearchResults<Encounter>> {

	@Override
	protected SearchResults<Encounter> doInBackground(String... args) {
		ClinicRestClient client = new ClinicRestClient();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("patient", args[0]);
		SearchResults<Encounter> results = null;
		try {
			results = client.getQuery(Encounter.class,
					ClinicRestClient.MOBILE_ENCOUNTER, map);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return results;
	}

}
