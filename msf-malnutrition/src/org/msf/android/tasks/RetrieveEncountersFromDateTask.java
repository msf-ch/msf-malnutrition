package org.msf.android.tasks;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.msf.android.adapters.EncounterListAdapter;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Encounter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;

public class RetrieveEncountersFromDateTask extends AsyncTask<Object, Void, Cursor> {
		

		private Activity activity;
		private Context context;
		private Cursor currentCursor;
		private boolean messageForQuery;
		private String [] items;
		private EncounterListAdapter listAdapter;
		private ListView itemListView;
		
		public RetrieveEncountersFromDateTask(Activity activity, Context context, Cursor currentCursor, 
				boolean messageForQuery, String [] items, EncounterListAdapter listAdapter, ListView itemlistView){
			this.activity = activity;
			this.context = context;
			this.currentCursor = currentCursor;
			this. messageForQuery = messageForQuery;
			this.items = items;
			this.listAdapter = listAdapter;
			this.itemListView = itemlistView;
		}
		
		// can use UI thread here
		protected void onPreExecute() {
			currentCursor = null;
		}
		
		@Override
		public Cursor doInBackground(Object... params) {
			Cursor c = null;
			String queryAsk = (String)params[2];
			boolean isQueryAsked = (Boolean)params[3];
				
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");	
			Calendar cal = Calendar.getInstance(); 
			int year = cal.get(Calendar.YEAR)-1900;
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			
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
					messageForQuery = true;
					//where = where.isNotNull("_id");
					if(queryAsk != null && isQueryAsked){
						if(queryAsk.equals(items[0])){				
							Date date = new Date(year,month,day);
							String newD = dateFormat.format(date);							
							try {
									date =dateFormat.parse(newD);
								} catch (ParseException e) {
									e.printStackTrace();
								}
							where.eq("encounterDatetime", date);
						}
						if(queryAsk.equals(items[1])){
							Date date = new Date(year,month,day-7);
							String newD = dateFormat.format(date);
							try {
									date =dateFormat.parse(newD);
								} catch (ParseException e) {
									e.printStackTrace();
								}
							where.ge("encounterDatetime", date);
						}
						if(queryAsk.equals(items[2])){
							Date date = new Date(year,month-1,day);
							String newD = dateFormat.format(date);
							
							try {
									date =dateFormat.parse(newD);
								} catch (ParseException e) {
									e.printStackTrace();
								}
							where.gt("encounterDatetime", date);
							Log.w("DATE FOR QUERY",where.toString());
						}
					}
					else {	
						Date date = new Date(year,month,day);
						String newD = dateFormat.format(date);
						
						try {
								date =dateFormat.parse(newD);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						where.eq("encounterDatetime", date);
					}
					
				} else {
					messageForQuery = false;
					
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
		
				if (this.isCancelled()) {
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
			if (!this.isCancelled()) {
				
				listAdapter = new EncounterListAdapter(context,
						c, false, ClinicAdapterManager.lockAndRetrieveClinicAdapter());
				ClinicAdapterManager.unlock();
				itemListView.setAdapter(listAdapter);
				itemListView.invalidateViews();
				listAdapter.notifyDataSetChanged();
				Cursor oldCursor = currentCursor;
				currentCursor = c;
				if (oldCursor != null) {
					oldCursor.close();
				}
				if(messageForQuery){
					if (c.getCount()==0){
						Toast t = Toast.makeText(activity, " No encounters downloaded, you can change the parameters to download other encounters by pressing the menu button", Toast.LENGTH_SHORT);
						t.show();						
					}
					else {
						String StringNbEncounters = "encounter";
						if(c.getCount()>1)
							StringNbEncounters = "encounters";
						Toast t = Toast.makeText(activity, c.getCount()+" "+StringNbEncounters+" downloaded successfully !", 50);
						t.show();
					}
				}
			}
		}
}

