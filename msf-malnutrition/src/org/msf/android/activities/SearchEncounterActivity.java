package org.msf.android.activities;

import org.msf.android.R;
import org.msf.android.adapters.EncounterListAdapter;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.tasks.DeleteDataEncounterTask;
import org.msf.android.tasks.LoadDataEncounterTask;
import org.msf.android.tasks.RetrieveEncountersTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
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

public class SearchEncounterActivity extends
		OrmLiteBaseListActivity<ClinicAdapter> {

	private ListView itemListView;
	private EncounterListAdapter listAdapter;
	private Cursor currentCursor;
	private EditText searchField;
	private RetrieveEncountersTask retrieveEncountersTask;
	private AlertDialog.Builder adb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encounter_list_screen);

		itemListView = getListView();
		adb = new AlertDialog.Builder(SearchEncounterActivity.this);
		searchField = (EditText) findViewById(R.id.encounterSearchField);
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
		if (retrieveEncountersTask != null
				&& !retrieveEncountersTask.isCancelled()) {
			retrieveEncountersTask.cancel(false);
		}
		retrieveEncountersTask = new RetrieveEncountersTask(getBaseContext(), currentCursor, itemListView, listAdapter);
		retrieveEncountersTask.execute(new Object[] { getHelper(),
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
					int position, long id) {
				
				LoadDataEncounterTask task = new LoadDataEncounterTask(SearchEncounterActivity.this,getBaseContext());
				task.execute(new Object[] {(Long)id});
				
			}
		});
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {		
		super.onCreateContextMenu(menu, v, menuInfo);
			
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_encounter, menu);
		menu.setHeaderTitle("Encounter");
		
	}
	
	
	public boolean onContextItemSelected(MenuItem item) {
		  final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		  switch(item.getItemId()) {
		  case R.id.view_encounter:
			  Long idE = info.id;
				LoadDataEncounterTask task = new LoadDataEncounterTask(
						SearchEncounterActivity.this, getBaseContext());
				task.execute(new Object[] { (Long) idE });			  
			  return true;
		  case R.id.edit_encounter:
		        
			  
			  return true;
		  case R.id.delete_encounter:
			  adb.setTitle("Confirmation");
				adb.setMessage("Are you sure you want to delete this encounter?");
				adb.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Long idE = info.id;
								DeleteDataEncounterTask taskE = new DeleteDataEncounterTask(
										SearchEncounterActivity.this, 1,1, false) {
									@Override
									protected void onPostExecute(
											String result) {
										super.onPostExecute(result);
										search();
										
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
}