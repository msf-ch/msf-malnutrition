package org.msf.android.tasks;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.msf.android.database.ClinicAdapter;
import org.msf.android.network.CheckNetworkAvailable;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.Observation;
import org.msf.android.openmrs.Patient;
import org.msf.android.rest.ClinicRestClient;
import org.msf.android.rest.RestOperationResult;
import org.msf.android.rest.utilities.EncounterRestUtilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

public class PostEncounterTask extends AsyncTask<Object, Void, Void> {
	
	private Activity activity;
	private Context context;
	private ProgressDialog pd;
	
	public PostEncounterTask(Activity activity, Context context){
		super();
		this.activity = activity;
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd = ProgressDialog.show(activity,
				"Submit encounter", "Sending encounter to OpenMRS server..",
				true);
	}

	@Override
	protected Void doInBackground(Object... params) {
		Long identifier = (Long) params[0];

		String resultMessage;
		ClinicAdapter ca2 = null;

		try {
			if(new CheckNetworkAvailable(activity).isOnline()){
							ca2 = new ClinicAdapter(context);
							Dao<Patient, Long> pDao = ca2.getPatientDao();
							Dao<Encounter, Long> eDao = ca2.getEncounterDao();
							Dao<Observation, Long> oDao = ca2.getObservationDao();
							Encounter e = eDao.queryForId(identifier);
							Patient p = pDao.queryForId(identifier);
				
							Collection<Observation> obs = e.getObs();
							Iterator<Observation> obsIt = obs.iterator();
				
							Observation o;
							while (obsIt.hasNext()) {
								o = obsIt.next();
								o.setPerson(e.getPatientUuid());
								oDao.update(o);
							}
				

							
							if(!p.isSentToRemoteServer()){
								resultMessage = "Can't create encounter on server. Patient "+p.getFullName()+" is not created on the server";
							}
							
							else {
								ClinicRestClient restClient = new ClinicRestClient();
					
								EncounterRestUtilities encounterUtilities = new EncounterRestUtilities(
										restClient);
					
								RestOperationResult<Encounter> result = encounterUtilities
										.createOrUpdateEncounterOnOpenMRSServer(e);
								
								if (result.isSuccess()) {
									eDao.update(e);
					
									resultMessage = "Created encounter on server: "
													+ e.toString();
								} else {
					
									resultMessage = 
											"ERROR sending encounter to server. Exception: "
													+ result.getMessage();
								}
							}
			}
			else
				resultMessage = "No network connection availaible. Can't send the encounter to the server. Check your settings !";
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
							"Failed to send encounter to server due to SQLException",
							Toast.LENGTH_LONG).show();
				}
			});
		} finally {
			if (ca2 != null) {
				ca2.close();
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
