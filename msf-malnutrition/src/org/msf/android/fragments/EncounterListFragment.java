package org.msf.android.fragments;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.msf.android.adapters.EncounterListAdapter;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Encounter;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.stmt.Where;

public class EncounterListFragment extends ListFragment {
	private Cursor currentCursor;
	private EncounterListAdapter listAdapter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	public void searchPatientName(String search) {
		search(Arrays.asList(search.split(" ")), Arrays.asList(new String[] {"patientName"}));
	}
	
	public void searchAll() {
		search(Collections.<String>emptyList(), Collections.<String>emptyList());
	}
	
	public void search(List<String> searchQueries, List<String> searchFields) {
		new RetrieveEncountersTask().execute(null, searchQueries, searchFields);
	}

	private class RetrieveEncountersTask extends
			AsyncTask<Object, Void, Cursor> {

		// can use UI thread here
		protected void onPreExecute() {
			currentCursor = null;
		}

		@Override
		protected Cursor doInBackground(Object... params) {
			Cursor c = null;
			try {
				ClinicAdapter ca = (ClinicAdapter) params[0];
				List<String> searchQueries = (List<String>)params[1];
				List<String> searchFields =  (List<String>)params[2];
//				List<String> searchFields = Arrays.asList(new String[] {
//						"patientName", "encounterType" });
				

				while (searchQueries.remove("")) {
				} // get rid of any empty strings from double spaces or whatnot

				Dao<Encounter, Long> dao = ca.getEncounterDao();
				Where<Encounter, Long> where = dao.queryBuilder().where();
				if (searchQueries.size() == 0) {
					where = where.isNotNull("_id");
				} else {
					int andCount = 0;
					int orCount;
					for (int i = 0; i < searchQueries.size(); i++) {
						orCount = 0;
						for (int j = 0; j < searchFields.size(); j++) {
							if (!searchQueries.get(i).isEmpty()) {
								where = where.like(searchFields.get(j), "%"
										+ searchQueries.get(i) + "%");
								orCount++;
							}
						}
						if (orCount > 0) {
							where = where.or(orCount);
							andCount++;
						}
					}
					where = where.and(andCount);
				}

				if (RetrieveEncountersTask.this.isCancelled()) {
					return null;
				}

				AndroidCompiledStatement compiledStatement = (AndroidCompiledStatement) where
						.prepare().compile(
								ca.getConnectionSource()
										.getReadOnlyConnection(),
								StatementType.SELECT);
				c = compiledStatement.getCursor();
			} catch (SQLException e) {
				// TODO -- cleanup
				e.printStackTrace();
				Log.e("PatientSearchActivity", e.getMessage());
			}
			System.out.println("Number of entries:" + c.getCount());
			return c;
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(Cursor c) {
			if (!RetrieveEncountersTask.this.isCancelled()) {
				listAdapter = new EncounterListAdapter(getActivity().getApplicationContext(),
						c, false, ClinicAdapterManager.lockAndRetrieveClinicAdapter());
				ClinicAdapterManager.unlock();
				setListAdapter(listAdapter);
				// TODO -- none of this works
				getListView().invalidateViews();
				listAdapter.notifyDataSetChanged();
				Cursor oldCursor = currentCursor;
				currentCursor = c;
				if (oldCursor != null) {
					oldCursor.close();
				}
			}
		}
	}
}
