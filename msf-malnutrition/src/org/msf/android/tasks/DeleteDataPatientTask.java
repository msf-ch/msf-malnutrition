package org.msf.android.tasks;

import java.sql.SQLException;

import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Patient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

public class DeleteDataPatientTask extends AsyncTask<Object, Void, String> {

	private ProgressDialog pd;
	private Activity activity;
	private Boolean onDetails = false;
	private Patient p = null;

	public DeleteDataPatientTask(Activity activity,
			boolean onDetails) {
		super();
		this.activity = activity;
		this.onDetails = onDetails;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd = ProgressDialog.show(activity, "Delete Patient Data",
				"Deleting patient data", true);
	}

	protected String doInBackground(Object... params) {
		ClinicAdapter ca = null;
		long id = (Long) params[0];
		String resultMessage = "";
		try {

			ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();

			Dao<Patient, Long> dao = ca.getPatientDao();
			p = dao.queryForId(id);
				DeleteBuilder<Patient, Long> db = dao.deleteBuilder();

				db.where().eq("_id", id);
				int rowsDeleted = dao.delete(db.prepare());
			if (!p.isSentToRemoteServer()) {
				if (rowsDeleted > 0) {
					resultMessage = "Patient " + p.getGivenName() + " "
							+ p.getFamilyName() + " successfully deleted!";
				} else {
					resultMessage = "Patient " + p.getGivenName() + " "
							+ p.getFamilyName()
							+ " not deleted due to undefined database error";
				}
			} else {
				resultMessage = "Patient deleted from local database but NOT from OpenMRS server.";
			}
		} catch (SQLException e) {
			// TODO -- cleanup
			e.printStackTrace();
			Log.e("DeleteDataPatient", e.getMessage());

			resultMessage = "Patient " + p.getGivenName() + " "
					+ p.getFamilyName() + " not deleted due to SQLException: "
					+ e.getMessage();
		} finally {
			ClinicAdapterManager.unlock();
		}
		return resultMessage;
	}

	@Override
	protected void onPostExecute(String resultMessage) {
		pd.dismiss();
		Toast t = Toast.makeText(activity, resultMessage, 5);
		t.show();
		if (onDetails == true)
			activity.finish();
	}
}