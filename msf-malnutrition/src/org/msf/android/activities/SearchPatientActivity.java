package org.msf.android.activities;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.msf.android.R;
import org.msf.android.adapters.PatientListAdapter;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.Patient;
import org.msf.android.tasks.DeleteDataEncounterTask;
import org.msf.android.tasks.DeleteDataPatientTask;
import org.msf.android.tasks.RetrievePatientsTask;

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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.dao.Dao;

public class SearchPatientActivity extends
		OrmLiteBaseListActivity<ClinicAdapter> {

	private ListView itemListView;
	private PatientListAdapter listAdapter;
	private Cursor currentCursor;
	private EditText searchField;
	private RetrievePatientsTask retrievePatientsTask;
	private boolean newEncounterExistingPatient = false;
	private	AlertDialog.Builder adb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_list_screen);
		
		Intent i = this.getIntent();
 
		newEncounterExistingPatient = i.getBooleanExtra("newEncounterExistingPatient", false);
		
		adb = new AlertDialog.Builder(SearchPatientActivity.this);
		
		itemListView = getListView();
		registerForContextMenu(itemListView);
		searchField = (EditText) findViewById(R.id.patientSearchField);
		registerListeners();
	}

	@Override
	protected void onDestroy() {
		if (currentCursor != null) {
			currentCursor.close();
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		search();
	}

	private void search() {
		if (retrievePatientsTask != null && !retrievePatientsTask.isCancelled()) {
			retrievePatientsTask.cancel(false);
		}
		retrievePatientsTask = new RetrievePatientsTask(getBaseContext(),
				currentCursor, itemListView, listAdapter);
		retrievePatientsTask.execute(new Object[] { getHelper(),
				searchField.getText().toString() });
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
					final int position, final long id) {

				if (newEncounterExistingPatient){
					getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					
					Intent intent = new Intent(SearchPatientActivity.this,
							HTMLFormEncounterActivity.class);
					intent.putExtra("_id", id);
					startActivity(intent);
				}
				else {
					AsyncTask<Void, Void, Void> loadPatientActivity = new AsyncTask<Void, Void, Void>() {
						ProgressDialog pd;
						
						protected void onPreExecute() {
							pd = ProgressDialog.show(SearchPatientActivity.this, "Loading patient", "Loading patient data...", true);
						};
						
						@Override
						protected Void doInBackground(Void... params) {
							Intent intent = new Intent(SearchPatientActivity.this,
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

				// LoadDataPatientTask task = new
				// LoadDataPatientTask(SearchPatientActivity.this,getBaseContext(),
				// getHelper());
				// task.execute(new Object[] {(Long)id});

				// new PostPatientsClass().execute(new Object[] {
				// (Long) id, (Integer) position });

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
		  Intent intent = new Intent(SearchPatientActivity.this,
					ViewPatientActivity.class);
			intent.putExtra("_id", info.id);
			startActivity(intent);
		  
		  return true;
		  
	  case R.id.edit_patient:
		  if(!mySelectedPatient.isSentToRemoteServer()){
		  Intent i = new Intent(getApplicationContext(),
					HTMLFormPatientActivity.class);
			i.putExtra("_id", mySelectedPatient.getDatabaseId());
			i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(i);
		  }
		  else {
      		Toast t = Toast.makeText(getApplicationContext(), "Can't edit this patient, he's already stored on the OpenMRS server.", Toast.LENGTH_LONG);
      		t.show();
      	}
		  return true;
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
									SearchPatientActivity.this, currentIteration, maxIteration, false);
								taskE.execute(new Long[] {(Long)encounter.getDatabaseId() });
							}
							
							DeleteDataPatientTask taskP = new DeleteDataPatientTask(
									SearchPatientActivity.this, false) {
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
	/*
	 * private class PostPatientsClass extends AsyncTask<Object, Void, Void> {
	 * 
	 * @Override protected void onPreExecute() { super.onPreExecute(); pd =
	 * ProgressDialog.show(SearchPatientActivity.this, "Submit patient",
	 * "Sending patient to OpenMRS server..", true); }
	 * 
	 * @Override protected Void doInBackground(Object... params) { long id =
	 * (Long) params[0]; int position = (Integer) params[1];
	 * 
	 * ClinicAdapter ca = null; String resultMessage; try { ca = new
	 * ClinicAdapter(getApplicationContext()); Dao<Patient, Long> pDao =
	 * ca.getPatientDao(); final Patient p = pDao.queryForId(id); if
	 * (!p.isSentToRemoteServer()) { ClinicRestClient restClient = new
	 * ClinicRestClient();
	 * 
	 * PatientRestUtilities patientUtilities = new PatientRestUtilities(
	 * restClient);
	 * 
	 * RestOperationResult<Patient> result = patientUtilities .postPatient(p);
	 * if (result.isSuccess()) { pDao.update(p);
	 * 
	 * resultMessage = "Created patient on server: " + p.toString(); } else {
	 * resultMessage = "ERROR sending patient to server: " + p.toString() +
	 * ". Exception: " + result.getExceptionThrown(); } } else { resultMessage =
	 * "'" + p.toString() + "' has already been sent to the server"; }
	 * 
	 * final String resultMessage2 = resultMessage; runOnUiThread(new Runnable()
	 * {
	 * 
	 * @Override public void run() { Toast.makeText(getBaseContext(),
	 * resultMessage2, Toast.LENGTH_LONG).show(); } });
	 * 
	 * } catch (SQLException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); runOnUiThread(new Runnable() {
	 * 
	 * @Override public void run() { Toast.makeText(getBaseContext(),
	 * "Failed to send patient to server", Toast.LENGTH_LONG).show(); } }); }
	 * finally { if (ca != null) { ca.close(); } } return null; }
	 * 
	 * @Override protected void onPostExecute(Void result) { // TODO
	 * Auto-generated method stub super.onPostExecute(result);
	 * listAdapter.getCursor().requery(); pd.dismiss(); } }
	 */

}

/*
 * extends ListActivity { private static PatientSearchAdapter adapter; private
 * SearchPatientTask searchPatientTask;
 * 
 * private EditText searchText; private ListView listView; private LinearLayout
 * searchingProgressLayout;
 * 
 * @Override protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState); if (adapter == null) { adapter = new
 * PatientSearchAdapter(getBaseContext()); } setListAdapter(adapter);
 * setContentView(R.layout.search_patients_window);
 * 
 * searchText = (EditText) findViewById(R.id.search_text);
 * searchText.setOnEditorActionListener(new OnEditorActionListener() {
 * 
 * @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent
 * event) { search(v.getText().toString()); return true; } });
 * searchingProgressLayout = (LinearLayout)
 * findViewById(R.id.searchingProgressLayout); listView = (ListView)
 * findViewById(android.R.id.list); }
 * 
 * @Override protected void onResume() { super.onResume();
 * 
 * }
 * 
 * @Override protected void onPause() { super.onPause(); }
 * 
 * @Override protected void onSaveInstanceState(Bundle outState) {
 * super.onSaveInstanceState(outState); }
 * 
 * @Override protected void onDestroy() { // clinicAdapter.close(); //
 * clinicAdapter = null; super.onDestroy(); }
 * 
 * private void search(String s) { if (searchPatientTask != null) { return; }
 * listView.setVisibility(View.VISIBLE);
 * searchingProgressLayout.setVisibility(View.GONE);
 * searchText.setEnabled(false); searchPatientTask = new SearchPatientTask();
 * searchPatientTask.addListener(new SearchListener());
 * searchPatientTask.execute(s);
 * 
 * showSearchingProgress(true);
 * 
 * InputMethodManager imm = (InputMethodManager)
 * getSystemService(Context.INPUT_METHOD_SERVICE);
 * imm.hideSoftInputFromWindow(searchText.getWindowToken(),
 * InputMethodManager.HIDE_NOT_ALWAYS); }
 * 
 * public void showSearchingProgress(boolean show) { Animation rotateAnim =
 * AnimationUtils.loadAnimation(this, R.anim.progress_rotate_anim); ImageView
 * progressLogo = (ImageView) findViewById(R.id.logoImageView); if (show) {
 * listView.setVisibility(View.GONE);
 * searchingProgressLayout.setVisibility(View.VISIBLE);
 * progressLogo.startAnimation(rotateAnim); } else {
 * progressLogo.clearAnimation(); listView.setVisibility(View.VISIBLE);
 * searchingProgressLayout.setVisibility(View.GONE); } }
 * 
 * @Override protected void onListItemClick(ListView l, View v, int position,
 * long id) { Ref r = adapter.getItem(position);
 * 
 * DownloadSinglePatientTask downloadPatientTask = new
 * DownloadSinglePatientTask();
 * downloadPatientTask.setDescription("Downloading patient: " + r.getDisplay());
 * downloadPatientTask.addListener(new DownloadPatientListener());
 * downloadPatientTask.execute(r.getUuid());
 * 
 * super.onListItemClick(l, v, position, id); }
 * 
 * class SearchListener implements GenericClinicTaskListener<SearchPatientTask>
 * {
 * 
 * @Override public void onStarted(SearchPatientTask task) { }
 * 
 * @Override public void onFinished(SearchPatientTask task) {
 * adapter.notifyDataSetChanged(); showSearchingProgress(false); try { List<Ref>
 * results = searchPatientTask.get(); adapter.clear(); for (Ref p : results) {
 * adapter.add(p); } } catch (Exception e) { return; } finally {
 * searchText.setEnabled(true); searchPatientTask = null;
 * searchText.clearFocus(); } }
 * 
 * @Override public void onProgressUpdate(SearchPatientTask task) { } }
 * 
 * class DownloadPatientListener implements
 * GenericClinicTaskListener<DownloadSinglePatientTask> {
 * 
 * @Override public void onStarted(DownloadSinglePatientTask task) { // TODO
 * Auto-generated method stub
 * 
 * }
 * 
 * @Override public void onFinished(DownloadSinglePatientTask task) { try {
 * Patient p = task.get();
 * MSFClinicApp.getStaticClinicAdapter().getPatientDao().createOrUpdate(p);
 * 
 * Intent i = new Intent(SearchPatientActivity.this,
 * ViewPatientActivityODK.class);
 * 
 * i.putExtra(Constants.KEY_PATIENT_ID, p.getUuid()); startActivity(i);
 * adapter.onClick(); } catch (Exception e) { Log.e("SearchPatientActivity",
 * e.getMessage(), e); } }
 * 
 * @Override public void onProgressUpdate(DownloadSinglePatientTask task) { //
 * TODO Auto-generated method stub
 * 
 * }
 * 
 * } }
 */
