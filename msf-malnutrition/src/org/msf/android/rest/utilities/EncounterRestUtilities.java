package org.msf.android.rest.utilities;

import org.msf.android.openmrs.Encounter;
import org.msf.android.rest.ClinicRestClient;
import org.msf.android.rest.RestOperationResult;

public class EncounterRestUtilities extends RestUtilities {
	public EncounterRestUtilities(ClinicRestClient restClient) {
		super(restClient);
	}

	public RestOperationResult<Encounter> createOrUpdateEncounterOnOpenMRSServer(
			Encounter encounter) {
		RestOperationResult<Encounter> encounterResult = getRestClient()
				.createOnOpenMRSServer(Encounter.class,
						ClinicRestClient.ENCOUNTER_REST_URL, null, encounter);

		if (encounterResult.isSuccess()) {
			((Encounter) encounterResult.getObjectSent())
					.setUuid(encounterResult.getObjectReceived().getUuid());
		}

		return encounterResult;
	}
}
