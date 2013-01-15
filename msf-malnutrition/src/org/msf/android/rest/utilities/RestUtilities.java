package org.msf.android.rest.utilities;

import org.msf.android.rest.ClinicRestClient;

public class RestUtilities {

	private ClinicRestClient restClient;

	public RestUtilities(ClinicRestClient restClient) {
		setRestClient(restClient);
	}

	public ClinicRestClient getRestClient() {
		return restClient;
	}

	public void setRestClient(ClinicRestClient restClient) {
		this.restClient = restClient;
	}
}
