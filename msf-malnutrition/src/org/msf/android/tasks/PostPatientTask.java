package org.msf.android.tasks;

import java.sql.SQLException;
import java.util.List;

import org.msf.android.database.ClinicAdapter;
import org.msf.android.network.CheckNetworkAvailable;
import org.msf.android.openmrs.Patient;
import org.msf.android.rest.ClinicRestClient;
import org.msf.android.rest.PatientRestUtilities;
import org.msf.android.rest.RestOperationResult;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

public class PostPatientTask extends AsyncTask<Object, Void, Void> {
	
	private Activity activity;
	private Context context;
	private ProgressDialog pd;
	
	public PostPatientTask(Activity activity, Context context){
		super();
		this.activity = activity;
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd = ProgressDialog.show(activity,
				"Submit patient", "Sending patient to OpenMRS server..",
				true);
	}

	@Override
	protected Void doInBackground(Object... params) {
		String identifier = (String) params[0];

		ClinicAdapter ca = null;
		String resultMessage;
		try {
			ca = new ClinicAdapter(context);
			Dao<Patient, Long> pDao = ca.getPatientDao();
			List<Patient> pList = pDao.queryForEq("preferredPatientIdentifier", identifier);
			final Patient p = pList.get(0);	
			
			if(new CheckNetworkAvailable(activity).isOnline()){
					if (!p.isSentToRemoteServer()) {
		
						ClinicRestClient restClient = new ClinicRestClient();
						PatientRestUtilities patientUtilities = new PatientRestUtilities(
								restClient);
		
						RestOperationResult<Patient> result = patientUtilities
								.postPatient(p);
						
						if (result.isSuccess()) {
							pDao.update(p);
		
							resultMessage = "Created patient on server: "
									+ p.toString();
						} else {
							resultMessage = "ERROR sending patient to server: "
									+ p.toString() + ". Exception: " + result.getExceptionThrown();
						}
					} else {
						resultMessage = "'" + p.toString()
								+ "' has already been sent to the server";
					}
			}
			else
				resultMessage = "No network connection availaible. Can't send the patient to the server. Check your settings !";
			final String resultMessage2 = resultMessage;
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(context, resultMessage2,
							Toast.LENGTH_LONG).show();
				}
			});

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(context,
							"Failed to send patient to server",
							Toast.LENGTH_LONG).show();
				}
			});
		} finally {
			if (ca != null) {
				ca.close();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		//adapterBasicDemo.getCursor().requery();
		pd.dismiss();
	}
}
