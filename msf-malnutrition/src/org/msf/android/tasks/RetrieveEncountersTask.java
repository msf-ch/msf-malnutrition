package org.msf.android.tasks;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.msf.android.adapters.EncounterListAdapter;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Encounter;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;

public class RetrieveEncountersTask extends
			AsyncTask<Object, Void, Cursor> {

		private Context context;
		private ListView itemListView;
		private Cursor currentCursor;
		private EncounterListAdapter listAdapter;
		
		public RetrieveEncountersTask (Context context, Cursor currentCursor, ListView itemListView, EncounterListAdapter listAdapter){
			super();
			this.context = context;
			this.currentCursor = currentCursor;
			this.itemListView = itemListView;
			this.listAdapter = listAdapter;
		}
		// can use UI thread here
		protected void onPreExecute() {
			currentCursor = null;
		}

		@Override
		protected Cursor doInBackground(Object... params) {
			Cursor c = null;
			try {
				ClinicAdapter ca = (ClinicAdapter) params[0];
				String searchQuery = (String) params[1];
				List<String> searchQueries = new ArrayList<String>(
						Arrays.asList(searchQuery.split(" ")));

				while (searchQueries.remove("")) {
				} // get rid of any empty strings from double spaces or whatnot

				List<String> searchFields = Arrays.asList(new String[] {
						"patientName", "encounterType" });

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
				listAdapter = new EncounterListAdapter(context,
						c, false, ClinicAdapterManager.lockAndRetrieveClinicAdapter());
				ClinicAdapterManager.unlock();
				itemListView.setAdapter(listAdapter);
				// TODO -- none of this works
				itemListView.invalidateViews();
				listAdapter.notifyDataSetChanged();
				Cursor oldCursor = currentCursor;
				currentCursor = c;
				if (oldCursor != null) {
					oldCursor.close();
				}
			}
		}
	}