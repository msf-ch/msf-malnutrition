package org.msf.android.tasks;

import java.sql.SQLException;

import org.msf.android.activities.ViewPatientActivity;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.openmrs.Patient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.j256.ormlite.dao.Dao;

public class LoadDataPatientTask extends AsyncTask<Object, Void, Patient> {

		private ProgressDialog pd;
		private Activity activity;
		private Context context;
		private ClinicAdapter ca;
		
		public LoadDataPatientTask(Activity activity, Context context, ClinicAdapter ca){
			super();
			this.ca = ca;
			this.activity = activity;
			this.context = context;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(activity,
					"Load Patient Data", "Retrieving patient data", true);
		}

		@Override
		protected Patient doInBackground(Object... params) {

			long id = (Long) params[0];

			try {
				Dao<Patient, Long> pDao = ca.getPatientDao();
				Patient p = pDao.queryForId(id);
				return p;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Patient result) {
			pd.dismiss();
		}
	}