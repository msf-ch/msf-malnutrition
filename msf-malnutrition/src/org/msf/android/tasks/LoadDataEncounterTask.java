package org.msf.android.tasks;
import java.sql.SQLException;

import org.msf.android.activities.ShowRecentEncountersActivity;
import org.msf.android.activities.ViewEncounterActivity;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.openmrs.Encounter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import com.j256.ormlite.dao.Dao;

public class LoadDataEncounterTask extends AsyncTask<Object, Void, Void> {
	
		private ProgressDialog pd;
		private Activity activity;
		private Context context;
		
		public LoadDataEncounterTask(Activity activity, Context context){
			super();
			this.activity = activity;
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		pd = ProgressDialog.show(activity,
					"Loading Encounter", "Loading encounter data",
					true);
		}
	
		@Override
		protected Void doInBackground(Object... params) {
			Long id = (Long) params[0];
			
			ClinicAdapter ca;
			try {
				ca = new ClinicAdapter(context);
				Dao<Encounter, Long> eDao = ca.getEncounterDao();
				Encounter e = eDao.queryForId(id);	
				Intent intent = new Intent(context, ViewEncounterActivity.class);
			    intent.putExtra("_id", e.getDatabaseId());
			    activity.startActivity(intent);
			} 
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
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