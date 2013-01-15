package org.msf.android.activities;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.msf.android.R;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.database.OpenMrsMetadataCache;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.Patient;
import org.msf.android.openmrs.Patient.PersonAddress;
import org.msf.android.tasks.DeleteDataEncounterTask;
import org.msf.android.tasks.DeleteDataPatientTask;
import org.msf.android.tasks.LoadDataEncounterTask;
import org.msf.android.tasks.PostEncounterTask;
import org.msf.android.tasks.PostPatientTask;
import org.msf.android.utilities.MedicalTimeUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

public class ViewPatientActivity extends Activity {

	public static final int TAB_ICON_SQUARE_SIZE = 80;

	public static final String TAG_EXTRA = "tab_tag";

	public static final String PATIENT_TAB_TAG = "patientTab";
	public static final String ENCOUNTER_TAB_TAG = "encounterTab";
	
	private Long _id;
	private Patient mySelectedPatient;

	private ListView ListViewDetails;
	private ListView ListViewEncounters;

	private SimpleAdapter adapterBasicDemo;
	private SimpleAdapter adapterEncounter;

	private TextView patientNameField;
	private TextView birthdateField;
	private TextView ageField;
	private TextView identifierField;
	private TextView cityVillageField;

	private ImageView genderView;
	private ImageView submittedView;

	private TabHost tabs;

	private List<PersonAddress> adresses;
	private Collection<Encounter> encounters;

	private ArrayList<Long> databaseIdEncountersLoaded;
	
	private	AlertDialog.Builder adb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_patient_details_list);

		Intent i = this.getIntent();
		// getting the identifier of the patient selected
		_id = i.getLongExtra("_id", Long.MIN_VALUE);

		// Define fields
		adb = new AlertDialog.Builder(ViewPatientActivity.this);
		 
		// Icons for tabs
		// I think this might be inefficient... can't see why though
		Drawable patientInfoDrawable = getResources().getDrawable(
				R.drawable.edit_or_view_user_256);
		Bitmap patientInfoBitmap = ((BitmapDrawable) patientInfoDrawable
				.getCurrent()).getBitmap();
		Drawable patientInfoBitmapScaled = new BitmapDrawable(
				Bitmap.createScaledBitmap(patientInfoBitmap,
						TAB_ICON_SQUARE_SIZE, TAB_ICON_SQUARE_SIZE, true));
		Drawable encounterInfoDrawable = getResources().getDrawable(
				R.drawable.icon_list_encounter_256);
		Bitmap encounterInfoBitmap = ((BitmapDrawable) encounterInfoDrawable
				.getCurrent()).getBitmap();
		Drawable encounterInfoBitmapScaled = new BitmapDrawable(
				Bitmap.createScaledBitmap(encounterInfoBitmap,
						TAB_ICON_SQUARE_SIZE, TAB_ICON_SQUARE_SIZE, true));

		// Tabs
		tabs = (TabHost) findViewById(android.R.id.tabhost);
		tabs.setup();

		TabHost.TabSpec spec = tabs.newTabSpec(ENCOUNTER_TAB_TAG);
		spec.setContent(R.id.listViewEncounterPatient);
		spec.setIndicator("Encounters", encounterInfoBitmapScaled);
		tabs.addTab(spec);
		
		spec = tabs.newTabSpec(PATIENT_TAB_TAG);
		spec.setContent(R.id.listViewDetailsPatient);
		spec.setIndicator("Basic Demographics", patientInfoBitmapScaled);
		tabs.addTab(spec);

		tabs.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

				resetTabAppearance(tabHost);
			}
		});

		tabs.setCurrentTab(0);
		resetTabAppearance(tabs);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		_id = intent.getLongExtra("_id", Long.MIN_VALUE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		definePatient();

		setupPatientDemoList();
		setupEncounterList();

		String tagTabToSet = getIntent().getExtras().getString(TAG_EXTRA);
		if (tagTabToSet != null && !tagTabToSet.isEmpty()) {
			tabs.setCurrentTabByTag(tagTabToSet);
		}
	}

	public void definePatient() {
		mySelectedPatient = this.getPatient(_id);

		patientNameField = (TextView) findViewById(R.id.name_text);
		birthdateField = (TextView) findViewById(R.id.birthdate_text);
		identifierField = (TextView) findViewById(R.id.identifier_text);
		cityVillageField = (TextView) findViewById(R.id.cityVillage_text);
		ageField = (TextView) findViewById(R.id.age_text);
		
		genderView = (ImageView) findViewById(R.id.gender_image);
//		submittedView = (ImageView) findViewById(R.id.leftIcon);

		// Fill fields
		patientNameField.setText(mySelectedPatient.getFullName());
		identifierField.setText(mySelectedPatient
				.getPreferredPatientIdentifier());
		// patientNameField.setShadowLayer(1, 0, 2, R.color.solid_red);
		if (mySelectedPatient.getGender().getFullWord().equals("FEMALE")) {
			genderView.setImageResource(R.drawable.female24);
		} else {
			genderView.setImageResource(R.drawable.male24);
		}
		
		String cityVillage = "No address on file";
		List<PersonAddress> addresses = mySelectedPatient.getAddresses();
		if (addresses.size() > 0) {
			//Should probably get preferred address here
			PersonAddress address = addresses.get(0);
			if (address.cityVillage != null && !address.cityVillage.isEmpty()) {
				cityVillage = address.cityVillage;
			}
		}
		cityVillageField.setText(cityVillage);
		
		

//		if (mySelectedPatient.getUuid() == null) {
//			submittedView.setColorFilter(PatientListAdapter.GRAYSCALE_FILTER);
//			submittedView.setImageDrawable(getResources().getDrawable(
//					R.drawable.memorycard24));
//		} else {
//			submittedView.setColorFilter(PatientListAdapter.COLOR_FILTER);
//			submittedView.setImageDrawable(getResources().getDrawable(
//					R.drawable.openmrs_icon_24));
//		}


		birthdateField.setText(new SimpleDateFormat(
				"dd MMM yyyy").format(mySelectedPatient.getBirthdate()));
		String age = MedicalTimeUtils.getDefaultLongAge(mySelectedPatient.getBirthdate());
		ageField.setText("(" + age + " old)");
	}

	public void setupPatientDemoList() {
		ListViewDetails = (ListView) findViewById(R.id.listViewDetailsPatient);

		ArrayList<HashMap<String, String>> listItemBasicDemo = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> map;
		// Identifier
		String identifier = mySelectedPatient.getPreferredPatientIdentifier();

		if (identifier == null || identifier == "")
			identifier = "No identifier for this patient";

		map = new HashMap<String, String>();
		map.put("title", "Identifier");
		map.put("description", identifier);
		map.put("icon", null);
		listItemBasicDemo.add(map);

		// Name
		map = new HashMap<String, String>();
		map.put("title", "Given name");
		map.put("description", mySelectedPatient.getGivenName());
		map.put("icon", null);
		listItemBasicDemo.add(map);

		// Middle name
		map = new HashMap<String, String>();
		map.put("title", "Middle name");
		map.put("description", mySelectedPatient.getMiddleName());
		map.put("icon", null);
		listItemBasicDemo.add(map);

		// Family name
		map = new HashMap<String, String>();
		map.put("title", "Family name");
		map.put("description", mySelectedPatient.getFamilyName());
		map.put("icon", null);
		listItemBasicDemo.add(map);

		// Adresses
		adresses = mySelectedPatient.getAddresses();
		if (adresses == null) {
			map = new HashMap<String, String>();
			map.put("title", "Address");
			map.put("description", "No address for this patient");
			map.put("icon", null);
			listItemBasicDemo.add(map);
		} else {
			for (Iterator<PersonAddress> iter = adresses.iterator(); iter
					.hasNext();) {
				PersonAddress pe = (PersonAddress) iter.next();
				map = new HashMap<String, String>();
				map.put("title", "Adress");
				map.put("description", pe.toString());
				map.put("icon", null);
				listItemBasicDemo.add(map);

			}
		}

		adapterBasicDemo = new SimpleAdapter(this.getBaseContext(),
				listItemBasicDemo, R.layout.listview_detailspatient_layout,
				new String[] { "title", "description" }, new int[] {
						R.id.title, R.id.description });

		ListViewDetails.setAdapter(adapterBasicDemo);
	}

	public void setupEncounterList() {
		/** List Encounters **/
		HashMap<String, String> map = new HashMap<String, String>();
		ListViewEncounters = (ListView) findViewById(R.id.listViewEncounterPatient);
		
		registerForContextMenu(ListViewEncounters);
		
		ArrayList<HashMap<String, String>> listItemEncounter = new ArrayList<HashMap<String, String>>();

		try {
			ClinicAdapter ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
			OpenMrsMetadataCache.buildEncounterTypeCache(ca);
			OpenMrsMetadataCache.buildFormsCache(ca);
			
			//Most recent encounters at top, so descending
			encounters = ca.getEncounterDao().queryBuilder()
					.orderBy("encounterDatetime", false).where()
					.eq("dbPatient_id", mySelectedPatient.getDatabaseId())
					.query();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ClinicAdapterManager.unlock();			
		}

		databaseIdEncountersLoaded = new ArrayList<Long>();
		
		if (encounters == null) {
			map = new HashMap<String, String>();
			map.put("formName", "Encounter");
			map.put("encounterType", "No encounters for this patient");
			map.put("encounterDate", "");
			listItemEncounter.add(map);
		} else {
			for (Iterator<Encounter> iter = encounters.iterator(); iter
					.hasNext();) {
				Encounter encounter = (Encounter) iter.next();
				databaseIdEncountersLoaded.add(encounter.getDatabaseId());
				
				map = new HashMap<String, String>();
				
				String formName = "";
				//formName = OpenMrsMetadataCache.getForm(encounter.getForm()).getName();
				
				String encounterType = "";
				//encounterType = OpenMrsMetadataCache.getEncounterType(encounter.getEncounterType()).getName();
				
				
				map.put("formName",formName);
				map.put("encounterType", encounterType);
				map.put("encounterDate", encounter.getEncounterDatetimeAsDDMMYYYYString());
				listItemEncounter.add(map);

			}
		}

		adapterEncounter = new SimpleAdapter(
				this.getBaseContext(), listItemEncounter,
				R.layout.encounter_list_item2, new String[] {
						"formName", "encounterDate", "encounterType" }, new int[] { R.id.formNameField,
						R.id.encounter_date_field, R.id.encounter_name_field });

		ListViewEncounters.setAdapter(adapterEncounter);

		ListViewEncounters.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, final long id) {
				Long idE = databaseIdEncountersLoaded.get(position);
				LoadDataEncounterTask task = new LoadDataEncounterTask(
						ViewPatientActivity.this, getBaseContext());
				task.execute(new Object[] { (Long) idE });
			}
		});
	}

	public void resetTabAppearance(TabHost tabHost) {
		TabWidget tw = tabHost.getTabWidget();
		for (int i = 0; i < tw.getChildCount(); i++) {
			View v = tw.getChildAt(i);
			v.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.tab_gradient));
		}
		View selectedTab = tw.getChildAt(tabHost.getCurrentTab());
		selectedTab.setBackgroundResource(R.drawable.tab_gradient_selected);
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent event) {
		if (keycode == KeyEvent.KEYCODE_MENU) {
			this.registerForContextMenu(ViewPatientActivity.this
					.getCurrentFocus());

		}
		return super.onKeyDown(keycode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_patient, menu);
 
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AlertDialog.Builder adb = new AlertDialog.Builder(
				ViewPatientActivity.this);
		switch (item.getItemId()) {
        case R.id.add_patient:
        	adb.setTitle("Confirmation");
			adb.setMessage("Do you want to send the data of "
					+ mySelectedPatient.getGivenName() + " "
					+ mySelectedPatient.getFamilyName() + " to the server ?");
			adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					PostPatientTask task = new PostPatientTask(
							ViewPatientActivity.this, getBaseContext()) {
						@Override
						protected void onPostExecute(Void result) {
							super.onPostExecute(result);
							definePatient();
						}
					};
					task.execute(new Object[] { (String) mySelectedPatient
							.getPreferredPatientIdentifier() });
				}
			});
			adb.setNegativeButton("No", null);
			adb.show();
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
								Encounter encounter = (Encounter) iter.next();
								currentIteration++;
								DeleteDataEncounterTask taskE = new DeleteDataEncounterTask(
										ViewPatientActivity.this, currentIteration, maxIteration, false);
								taskE.execute(new Long[] {(Long)encounter.getDatabaseId() });
							}
							
							DeleteDataPatientTask taskP = new DeleteDataPatientTask(
									ViewPatientActivity.this, true);
							taskP.execute(new Object[] {(Long)mySelectedPatient.getDatabaseId() });
						}
					});
			adb.setNegativeButton("No", null);
			adb.show();
           return true;
        
		case R.id.create_encounter:
			Intent intent = new Intent(getApplicationContext(),
					HTMLFormEncounterActivity.class);
			intent.putExtra("_id", mySelectedPatient.getDatabaseId());
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
			return true;
		case R.id.send_encounter:

        	adb.setTitle("Confirmation");
			adb.setMessage("Do you want to send all the encounters for this patient to the server?");
			adb.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							for (Iterator<Encounter> iter = encounters.iterator(); iter
									.hasNext();) {
								Encounter encounter = (Encounter) iter.next();
								PostEncounterTask task = new PostEncounterTask(
										ViewPatientActivity.this, getBaseContext());
								task.execute(new Object[] { (Long) encounter.getDatabaseId() });
							}
						}
					});
			adb.setNegativeButton("No", null);
			adb.show();

            return true;

			
		}
		return (super.onOptionsItemSelected(item));
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {		
		super.onCreateContextMenu(menu, v, menuInfo);
			
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_encounter, menu);
		menu.setHeaderTitle("Encounter");
		
	}
	
	
	public boolean onContextItemSelected(MenuItem item) {
		  final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		  Long idE = databaseIdEncountersLoaded.get(info.position);
		  
		  switch(item.getItemId()) {
		  case R.id.view_encounter:
				LoadDataEncounterTask task = new LoadDataEncounterTask(
						ViewPatientActivity.this, getBaseContext());
				task.execute(new Object[] { (Long) idE });			  
			  return true;
		  case R.id.edit_encounter:
			  Intent intent = new Intent(getApplicationContext(),
						HTMLFormEncounterActivity.class);
				intent.putExtra("_id", mySelectedPatient.getDatabaseId());
				intent.putExtra("_idE", idE);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
			  
			  return true;
		  case R.id.delete_encounter:
			  adb.setTitle("Confirmation");
				adb.setMessage("Are you sure you want to delete this encounter?");
				adb.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Long idE = databaseIdEncountersLoaded.get(info.position);
								DeleteDataEncounterTask taskE = new DeleteDataEncounterTask(
										ViewPatientActivity.this, 1,1, false) {
									@Override
									protected void onPostExecute(
											String result) {
										super.onPostExecute(result);
										
										//Refresh the encounters list
										setupEncounterList();
									}
								};
								taskE.execute(new Long[] {(Long)idE });
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
	 * ProgressDialog.show(ViewPatientActivity.this, "Submit patient",
	 * "Sending patient to OpenMRS server..", true); }
	 * 
	 * @Override protected Void doInBackground(Object... params) { String
	 * identifier = (String) params[0];
	 * 
	 * ClinicAdapter ca = null; String resultMessage; try { ca = new
	 * ClinicAdapter(getApplicationContext()); Dao<Patient, Long> pDao =
	 * ca.getPatientDao(); List<Patient> pList =
	 * pDao.queryForEq("preferredPatientIdentifier", identifier); final Patient
	 * p = pList.get(0); if (!p.isSentToRemoteServer()) { ClinicRestClient
	 * restClient = new ClinicRestClient();
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
	 * //adapterBasicDemo.getCursor().requery(); pd.dismiss(); } }
	 */
}
