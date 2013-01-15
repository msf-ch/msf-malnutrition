package org.msf.android.activities;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.msf.android.R;
import org.msf.android.adapters.EncounterListAdapter;
import org.msf.android.adapters.PatientListAdapter;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.Patient;
import org.msf.android.tasks.DeleteDataEncounterTask;
import org.msf.android.tasks.DeleteDataPatientTask;
import org.msf.android.tasks.LoadDataEncounterTask;
import org.msf.android.tasks.LoadDataPatientTask;
import org.msf.android.tasks.RetrieveEncountersFromDateTask;
import org.msf.android.tasks.RetrievePatientsFromDateTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.dao.Dao;

public class ShowRecentPatientsActivity extends
	OrmLiteBaseListActivity<ClinicAdapter> {

	public static final int MENU_CHANGE_PATIENTQUERY = Menu.FIRST+1;
	
	private ListView itemListView;
	private PatientListAdapter listAdapter;
	private Cursor currentCursor;
	private EditText searchField;
	
	private RetrievePatientsFromDateTask retrievePatientsFromDateTask;
	
	private String items[] = {"Today","Since last week","Since last month"};
	private String whichQuery = items[0];
	
	private String queryAsk = whichQuery;
	private boolean isQueryAsked = false;
	private boolean messageForQuery = false;
	
	private AlertDialog.Builder adb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.patient_list_screen);
	
	adb = new AlertDialog.Builder (ShowRecentPatientsActivity.this);
	itemListView = getListView();
	searchField = (EditText) findViewById(R.id.patientSearchField);
	registerListeners();
	registerForContextMenu(itemListView);
	
	search();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		search();
	}
	
	@Override
	protected void onDestroy() {
	if (currentCursor != null) {
		currentCursor.close();
	}
	super.onDestroy();
	}
	
	private void search() {
	if (retrievePatientsFromDateTask != null
			&& !retrievePatientsFromDateTask.isCancelled()) {
		retrievePatientsFromDateTask.cancel(false);
	}
	retrievePatientsFromDateTask = new RetrievePatientsFromDateTask(this, getBaseContext(), currentCursor,messageForQuery, items, listAdapter, itemListView);
	retrievePatientsFromDateTask.execute(new Object[] { getHelper(),
			searchField.getText().toString(), queryAsk, isQueryAsked});
	}
	
	private void registerListeners() {
	searchField.addTextChangedListener(new TextWatcher() {
	
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
	
		}
	
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
	
		}
	
		@Override
		public void afterTextChanged(Editable s) {
			search();
		}
	});
	
	itemListView.setOnItemClickListener(new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, final long id) {
			AsyncTask<Void, Void, Void> loadPatientActivity = new AsyncTask<Void, Void, Void>() {
				ProgressDialog pd;
				
				protected void onPreExecute() {
					pd = ProgressDialog.show(ShowRecentPatientsActivity.this, "Loading patient", "Loading patient data...", true);
				};
				
				@Override
				protected Void doInBackground(Void... params) {
					Intent intent = new Intent(ShowRecentPatientsActivity.this,
							ViewPatientActivity.class);
					intent.putExtra("_id", id);
					startActivity(intent);
					return null;
				}
				
				protected void onPostExecute(Void result) {
					pd.dismiss();
				};
			};
			
			loadPatientActivity.execute();
		}
	});
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {		
	      super.onCreateContextMenu(menu, v, menuInfo);
	      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		  final Patient mySelectedPatient = this.getPatient(info.id);
			
		  MenuInflater inflater = getMenuInflater();
		  inflater.inflate(R.menu.context_menu_patient, menu);
		  menu.setHeaderTitle(mySelectedPatient.getGivenName()+" "+mySelectedPatient.getFamilyName());
			
}


public boolean onContextItemSelected(MenuItem item) {
	  final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  final Patient mySelectedPatient = this.getPatient(info.id);
	  final Collection<Encounter> encounters = mySelectedPatient.getEncounters();
	  
	  switch(item.getItemId()) {
	  case R.id.view_patient:
		  Intent intent = new Intent(ShowRecentPatientsActivity.this,
					ViewPatientActivity.class);
			intent.putExtra("_id", info.id);
			startActivity(intent);
		  
		  return true;
		  
	  case R.id.edit_patient:
		  throw new UnsupportedOperationException("Cannot edit patients yet");
//		  Intent i = new Intent(getApplicationContext(),
//					HTMLFormPatientActivity.class);
//			i.putExtra("_id", info.id);
//			i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//			startActivity(i);
//		  return true;
		  
	  case R.id.delete_patient:
		  adb.setTitle("Confirmation");
			adb.setMessage("Are you sure you want to delete the ENTIRE patient (patient demographics and all encounters)?");
			adb.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							int maxIteration = encounters.size();
							int currentIteration = 0;
							for (Iterator<Encounter> iter = encounters.iterator(); iter
									.hasNext();) {
								currentIteration++;
								Encounter encounter = (Encounter) iter.next();
								DeleteDataEncounterTask taskE = new DeleteDataEncounterTask(
									ShowRecentPatientsActivity.this, currentIteration, maxIteration, false);
								taskE.execute(new Long[] {(Long)encounter.getDatabaseId() });
							}
							
							DeleteDataPatientTask taskP = new DeleteDataPatientTask(
									ShowRecentPatientsActivity.this, false) {
								@Override
								protected void onPostExecute(
										String result) {
									// Refresh the list
									super.onPostExecute(result);
									search();
								}
							};
							taskP.execute(new Object[] {(Long)mySelectedPatient.getDatabaseId() });
							
						}
						
					});
			adb.setNegativeButton("No", null);
			adb.show();
		  return true;
	  default:
	        return super.onContextItemSelected(item);
	  }
	  
	  
}

	@Override
	public boolean onCreateOptionsMenu (Menu menu){
		menu.add(Menu.NONE, MENU_CHANGE_PATIENTQUERY,Menu.NONE,"Change parameters for loading patients");
		return (super.onCreateOptionsMenu(menu));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch(item.getItemId()){
		case MENU_CHANGE_PATIENTQUERY:	
			
			whichQuery = items[0];	
			queryAsk = whichQuery;	
			
			AlertDialog.Builder adb = new AlertDialog.Builder(
					ShowRecentPatientsActivity.this);
			adb.setTitle("Loading new patients");
			adb.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
				public void onClick(
						DialogInterface dialog,
						int whichButton) {
					if(whichButton == 0)      {
						whichQuery = items[0];
						queryAsk = whichQuery;
					}
					else if(whichButton == 1) {
						whichQuery = items[1];
						queryAsk = whichQuery;
					}
					else if(whichButton == 2) {
						whichQuery = items[2];
						queryAsk = whichQuery;
					}
					
				}
			});
			adb.setPositiveButton("Load", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int choice) {
						isQueryAsked = true;
						searchField.setText("");
						retrievePatientsFromDateTask.doInBackground(new Object[] { getHelper(),
								searchField.getText().toString(), queryAsk, isQueryAsked});
					}
					});
			adb.setNegativeButton("Cancel", null);
			adb.show();
			break;
		}
		return (super.onOptionsItemSelected(item));
	}	

	private Patient getPatient(Long _id) {
		Patient patient = new Patient();
		ClinicAdapter ca = null;
		try {
			ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
			Dao<Patient, Long> pDao = ca.getPatientDao();
			patient = pDao.queryForId(_id);
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ClinicAdapterManager.unlock();
		}
		return patient;
	}
}
