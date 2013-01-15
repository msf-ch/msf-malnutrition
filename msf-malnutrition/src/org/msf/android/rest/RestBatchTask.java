package org.msf.android.rest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.Patient;
import org.msf.android.rest.utilities.EncounterRestUtilities;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

public class RestBatchTask extends
		AsyncTask<Void, RestOperationProgress, List<RestOperationResult<?>>> {
	ProgressDialog progressDialog;

	public RestBatchTask(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialog.setTitle("Sending patient data...");
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Initializing...");

		progressDialog.show();
	}

	@Override
	protected List<RestOperationResult<?>> doInBackground(Void... v) {
		ClinicAdapter ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
		List<RestOperationResult<?>> result = new ArrayList<RestOperationResult<?>>();
		RestOperationProgress progress = new RestOperationProgress();

		try {
			Dao<Patient, Long> pDao = ca.getPatientDao();
			Dao<Encounter, Long> eDao = ca.getEncounterDao();

			ClinicRestClient restClient = new ClinicRestClient();
			PatientRestUtilities patientRestUtils = new PatientRestUtilities(
					restClient);
			EncounterRestUtilities encounterRestUtils = new EncounterRestUtilities(
					restClient);

			List<RestOperationResult<Patient>> patientResults = new ArrayList<RestOperationResult<Patient>>();
			List<RestOperationResult<Encounter>> encounterResults = new ArrayList<RestOperationResult<Encounter>>();

			PreparedQuery<Patient> ppq = pDao.queryBuilder().where()
					.isNull("uuid").prepare();
			PreparedQuery<Encounter> epq = eDao.queryBuilder().where()
					.isNull("uuid").prepare();

			// Iterate through and submit patients first
			// Otherwise some encounters might not be assigned patient uuids
			CloseableWrappedIterable<Patient> patients = pDao
					.getWrappedIterable(ppq);
			RestOperationResult<Patient> retrievedPatient;

			progress.currentTask = "Sending patients";
			progress.currentProgress = 0;
			for (Patient p : patients) {
				progress.currentProgress++;
				progress.currentItemDescription = p.toString();
				publishProgress(progress);

				retrievedPatient = patientRestUtils.postPatient(p);
				patientResults.add(retrievedPatient);
			}
			
			progress.currentTask = "Storing patients to database";
			progress.currentItemDescription = "";
			progress.currentProgress = 0;
			for (RestOperationResult<Patient> pResult : patientResults) {
				progress.currentProgress++;
				publishProgress(progress);
				
				if (pResult.isSuccess()) {
					pDao.update(pResult.getSentObjectWithRetrievedUuid());
				}
			}

			// NOW submit the encounters
			CloseableWrappedIterable<Encounter> encounters = eDao
					.getWrappedIterable(epq);
			RestOperationResult<Encounter> retrievedEncounter;

			progress.currentTask = "Sending encounters";
			progress.currentProgress = 0;
			for (Encounter e : encounters) {
				progress.currentProgress++;
				progress.currentItemDescription = e.getPatientName();
				publishProgress(progress);
				
				retrievedEncounter = encounterRestUtils.createOrUpdateEncounterOnOpenMRSServer(e);
				encounterResults.add(retrievedEncounter);
				
				if (retrievedEncounter.isSuccess()) {
					eDao.update(retrievedEncounter.getSentObjectWithRetrievedUuid());
				}
			}
			
			progress.currentTask = "Storing encounters to database";
			progress.currentItemDescription = "";
			progress.currentProgress = 0;
			for (RestOperationResult<Encounter> eResult : encounterResults) {
				progress.currentProgress++;
				publishProgress(progress);
				
				if (eResult.isSuccess()) {
					eDao.update(eResult.getSentObjectWithRetrievedUuid());
				}
			}


			result.addAll(patientResults);
			result.addAll(encounterResults);
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		} finally {
			ClinicAdapterManager.unlock();
		}

		return result;
	}

	@Override
	protected void onProgressUpdate(RestOperationProgress... values) {
		super.onProgressUpdate(values);

		progressDialog.setTitle(values[0].currentTask);
		progressDialog.setMessage("Sending " + values[0].currentItemDescription
				+ ", #" + values[0].currentProgress);
	}

	@Override
	protected void onPostExecute(List<RestOperationResult<?>> result) {
		super.onPostExecute(result);

		progressDialog.dismiss();
	}
}
