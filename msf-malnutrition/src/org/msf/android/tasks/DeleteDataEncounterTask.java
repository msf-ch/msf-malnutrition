package org.msf.android.tasks;

import java.sql.SQLException;

import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Encounter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

public class DeleteDataEncounterTask extends AsyncTask<Long, Void, String> {

		private ProgressDialog pd;
		private Activity activity;
		private int currentIteration;
		private int maxIteration;
		private boolean onDetails;
		
		public DeleteDataEncounterTask(Activity activity, int currentIteration, int maxIteration, boolean onDetails){
			super();
			this.activity = activity;
			this.currentIteration = currentIteration;
			this.maxIteration = maxIteration;
			this.onDetails = onDetails;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(activity,
					"Delete Encounter Data", "Deleting encounter data", true);
		}

		protected String doInBackground(Long... params) {
			ClinicAdapter ca = null;
			long id = params[0];
			try {
				
				ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
		
				Dao<Encounter, Long> dao = ca.getEncounterDao();
				DeleteBuilder<Encounter, Long> db = dao.deleteBuilder();
				
				db.where().eq("_id", id);
		        dao.delete(db.prepare());

		        //?
				Log.w("QUERY zouzou", db.prepare().toString());

			} catch (SQLException eSQL) {
				// TODO -- cleanup
				eSQL.printStackTrace();
				Log.e("DeleteDataEncounter", eSQL.getMessage());
			}
			finally{
				ClinicAdapterManager.unlock();
			}
			return null;
		}
		

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			Toast t = Toast.makeText(activity, "Encounter "+currentIteration+"/"+maxIteration+" for the patient deleted successfully!", 5);
			t.show();
			if(onDetails)
				activity.finish();
		}
	}