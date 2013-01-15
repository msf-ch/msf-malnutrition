package org.msf.android.activities;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.msf.android.R;
import org.msf.android.activities.malnutrition.HTMLFormChildActivity;
import org.msf.android.activities.malnutrition.HTMLFormHouseholdActivity;
import org.msf.android.activities.malnutrition.MalnutritionHomeActivity;
import org.msf.android.adapters.LaunchGridListAdapter;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.fragments.AbstractLaunchScreenFragment;
import org.msf.android.fragments.GridViewFragment;
import org.msf.android.fragments.malnutrition.MalnutritionHomeFragment;
import org.msf.android.managers.GPSManager;
import org.msf.android.network.CheckNetworkAvailable;
import org.msf.android.rest.RestBatchTask;
import org.msf.android.tasks.DownloadConfigTask;
import org.msf.android.tasks.ExportCSVDataMalnutrition;
import org.msf.android.utilities.DatabaseConfigUtil;
import org.msf.android.utilities.MSFCommonUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Scroller;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class LaunchScreenActivity extends FragmentActivity {

	public static final int AUTO_DOWNLOAD = 3;
	public static final int TAB_ICON_SQUARE_SIZE = 80;
	public static final int SCROLL_DURATION_TAB_SELECT = 300;

	private int[] tableIconsTab = new int[] { R.drawable.patient_tab_selector,
			/*R.drawable.openmrs_tab_selector,*/
			R.drawable.other_tasks_tab_selector };

	private Drawable[] tableIconsDrawable = new Drawable[3];

	private int[] tablePatientToolsString = new int[] {
			R.string.malnutrition_home_addnewhousehold,
			R.string.malnutrition_workflow_addnewchild,
			R.string.malnutrition_workflow_open_malnutrition_home,
			R.string.search_local_patients, R.string.show_recent_patients,
			R.string.search_local_encounters, R.string.show_recent_encounters,
			R.string.select_form };

	private int[] tablePatientToolsDrawable = new int[] {
			R.drawable.add_household_256, 
			R.drawable.add_baby256, R.drawable.family256,
			R.drawable.search_patients, R.drawable.recent_doc,
			R.drawable.search_encounters, R.drawable.encounter_icon,
			R.drawable.edit256};

	private int[] tableSearchToolsString = new int[] {
			R.string.send_new_entries, R.string.search_online_patients,
			R.string.search_online_encounters, R.string.search_online_cohorts }; 
//			R.string.malnutrition_home_addnewhousehold,
//			R.string.malnutrition_workflow_addnewchild,
//			R.string.malnutrition_workflow_open_malnutrition_home,
//			R.string.search_local_patients, R.string.show_recent_patients,
//			R.string.search_local_encounters, R.string.show_recent_encounters,
//			R.string.select_form };

	private int[] tableSearchToolsDrawable = new int[] {
			R.drawable.send_online_pe_icon_300,
			R.drawable.search_online_p_icon_300,
			R.drawable.search_online_e_icon_300,
			R.drawable.search_online_c_icon_300 };
//			R.drawable.add_household_256, 
//			R.drawable.add_baby256, R.drawable.family256,
//			R.drawable.search_patients, R.drawable.recent_doc,
//			R.drawable.search_encounters, R.drawable.encounter_icon,
//			R.drawable.edit256 };

	private int[] tableOtherTasksToolsString = new int[] {
			R.string.global_preferences_label,
			/*R.string.show_preferences_button_text, R.string.downloadConfig,*/
			R.string.resetDatabaseButton, R.string.report_problem,
			/*R.string.test_html_form_performance, R.string.test_gps_function,
			R.string.export_data*/ };

	private int[] tableOtherTasksToolsDrawable = new int[] {
			R.drawable.wheel256, /*R.drawable.configure256,
			R.drawable.download_online_config_icon_300,*/
			R.drawable.remove_from_database256, R.drawable.mailbox256,
			/*R.drawable.calculator256, R.drawable.mobile256, R.drawable.chart256*/ };

	private String[] tableTabSpec = new String[] { "patientTab", /*"openmrsTab",*/
			"otherTasksTab" };
	// private int[] tableTabSetContent = new int[] { R.id.listPatientTools,
	// R.id.listSearchTools, R.id.listOtherTasksTools };
	private String[] tableTabIndicator;

	private MalnutritionHomeFragment malnutritionHomeFragment;
	private GridViewFragment patientFragment;
	private GridViewFragment onlineFragment;
	private GridViewFragment otherTasksFragment;

	private HashMap<String, String> mapPatient;
	private HashMap<String, String> mapSearch;
	private HashMap<String, String> mapOtherTasks;

	private LaunchGridListAdapter adapterPatient;
	private LaunchGridListAdapter adapterSearch;
	private LaunchGridListAdapter adapterOtherTasks;

	private ViewPager viewPager;
	private TabHost tabHost;
	private TabsAdapter tabsAdapter;

	public LaunchScreenActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// We're using makeshift fragments that can't be reinstantiated, just
		// make new ones
		if (savedInstanceState != null) {
			savedInstanceState.remove("android:support:fragments");
		}
		super.onCreate(savedInstanceState);
		
		tableTabIndicator = new String[] { getString(R.string.malnutrition_home_tab_1), /*getString(R.string.malnutrition_home_tab_2),*/
				getString(R.string.malnutrition_home_tab_3)};

		setContentView(R.layout.launch_screen1);

		List<AbstractLaunchScreenFragment> fragmentsInOrder = new ArrayList<AbstractLaunchScreenFragment>(
				3);

		// Icons for tabs
		for (int i = 0; i < tableIconsTab.length; i++) {
			Drawable patientDrawable = getResources().getDrawable(
					tableIconsTab[i]);
			Bitmap patientBitmap = ((BitmapDrawable) patientDrawable
					.getCurrent()).getBitmap();
			tableIconsDrawable[i] = new BitmapDrawable(
					Bitmap.createScaledBitmap(patientBitmap,
							TAB_ICON_SQUARE_SIZE, TAB_ICON_SQUARE_SIZE, true));
		}

		viewPager = ((ViewPager) findViewById(R.id.pager));

		/**
		 * Fragments initialized in order of tabs, don't change the order of
		 * these!
		 **/
		initializeMalnutritionHomeTab(fragmentsInOrder);
		//initializePatientTab(fragmentsInOrder);
		//initializeOnlineTab(fragmentsInOrder);
		initializeAdminTab(fragmentsInOrder);

		// Tabs
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();

		for (int i = 0; i < tableTabSpec.length; i++) {
			TabHost.TabSpec spec = tabHost.newTabSpec(tableTabSpec[i]);
			// spec.setContent(tableTabSetContent[i]);
			spec.setContent(R.id.fakeLayout);
			spec.setIndicator(tableTabIndicator[i], tableIconsDrawable[i]);
			tabHost.addTab(spec);

			fragmentsInOrder.get(i).setTabTag(spec.getTag());
		}

		tabsAdapter = new TabsAdapter(fragmentsInOrder);

		tabHost.setOnTabChangedListener(tabsAdapter);
		viewPager.setOnPageChangeListener(tabsAdapter);
		viewPager.setAdapter(tabsAdapter);
		customizeViewPager();

		tabHost.setCurrentTab(0);
		resetTabAppearance(tabHost);
	}
	
	private void initializeMalnutritionHomeTab(List<AbstractLaunchScreenFragment> fragmentsInOrder) {
		malnutritionHomeFragment = new MalnutritionHomeFragment();
		fragmentsInOrder.add(malnutritionHomeFragment);
	}

	private void initializePatientTab(List<AbstractLaunchScreenFragment> fragmentsInOrder) {
		patientFragment = new GridViewFragment() {
			@Override
			public void setUpGridView(final GridView gv) {
				super.setUpGridView(gv);

				ArrayList<HashMap<String, String>> listPatientToolsStrings = new ArrayList<HashMap<String, String>>();

				// Initialize icons + text
				for (int i = 0; i < tablePatientToolsString.length; i++) {
					LaunchScreenActivity.this.mapPatient = new HashMap<String, String>();
					LaunchScreenActivity.this.mapPatient.put("description",
							getString(tablePatientToolsString[i]));
					LaunchScreenActivity.this.mapPatient.put("icon",
							String.valueOf(tablePatientToolsDrawable[i]));
					listPatientToolsStrings.add(mapPatient);
				}
				LaunchScreenActivity.this.adapterPatient = new LaunchGridListAdapter(
						getBaseContext(), listPatientToolsStrings,
						R.layout.launch_grid_list_item, new String[] { "icon",
								"description" }, new int[] { R.id.icon,
								R.id.description });

				gv.setAdapter(LaunchScreenActivity.this.adapterPatient);

				gv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> a, final View v,
							int position, long id) {
						HashMap<String, String> map = (HashMap<String, String>) gv
								.getItemAtPosition(position);
						if (map.get("description") == getString(R.string.search_local_patients)) {
							searchLocalPatients(v);
						}

						if (map.get("description") == getString(R.string.show_recent_patients)) {
							showRecentPatients(v);
						}

						if (map.get("description") == getString(R.string.search_local_encounters)) {
							searchLocalEncounters(v);
						}

						if (map.get("description") == getString(R.string.show_recent_encounters)) {
							showRecentEncounters(v);
						}

						if (map.get("description") == getString(R.string.select_form)) {
							showSelectForm(v);
						}

						if (map.get("description") == getString(R.string.malnutrition_home_addnewhousehold)) {
							webFormHouseholdClicked(v);
						}

						if (map.get("description") == getString(R.string.malnutrition_workflow_addnewchild)) {
							webFormChildClicked(v);
						}

						if (map.get("description") == getString(R.string.malnutrition_workflow_open_malnutrition_home)) {
							showMalnutritionWorkflow(v);
						}
					}
				});
			}
		};
		fragmentsInOrder.add(patientFragment);

	}

	private void initializeOnlineTab(List<AbstractLaunchScreenFragment> fragmentsInOrder) {

		onlineFragment = new GridViewFragment() {
			@Override
			public void setUpGridView(final GridView gv) {
				super.setUpGridView(gv);

				ArrayList<HashMap<String, String>> listSearchToolsStrings = new ArrayList<HashMap<String, String>>();

				// Initialize icons + text
				for (int i = 0; i < tableSearchToolsString.length; i++) {
					LaunchScreenActivity.this.mapSearch = new HashMap<String, String>();
					LaunchScreenActivity.this.mapSearch.put("description",
							getString(tableSearchToolsString[i]));
					LaunchScreenActivity.this.mapSearch.put("icon",
							String.valueOf(tableSearchToolsDrawable[i]));
					listSearchToolsStrings
							.add(LaunchScreenActivity.this.mapSearch);
				}

				LaunchScreenActivity.this.adapterSearch = new LaunchGridListAdapter(
						getBaseContext(), listSearchToolsStrings,
						R.layout.launch_grid_list_item, new String[] { "icon",
								"description" }, new int[] { R.id.icon,
								R.id.description });

				gv.setAdapter(LaunchScreenActivity.this.adapterSearch);

				gv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> a, final View v,
							int position, long id) {
						HashMap<String, String> map = (HashMap<String, String>) gv
								.getItemAtPosition(position);

						if (map.get("description") == getString(R.string.send_new_entries)) {
							AlertDialog.Builder adb = new AlertDialog.Builder(
									LaunchScreenActivity.this);
							adb.setTitle("Confirmation");
							adb.setMessage("Are you sure you want to send all the patients with their encounters to the server?");
							adb.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											uploadAllUnsent(v);
										}
									});
							adb.setNegativeButton("No", null);
							adb.show();

						}
						// implement

						if (map.get("description") == getString(R.string.search_online_patients)) {
						}
						// implement

						if (map.get("description") == getString(R.string.search_online_encounters)) {
						}
						// implement

						if (map.get("description") == getString(R.string.search_online_cohorts)) {
						}
						// implement

						if (map.get("description") == getString(R.string.search_local_patients)) {
							searchLocalPatients(v);
						}

						if (map.get("description") == getString(R.string.show_recent_patients)) {
							showRecentPatients(v);
						}

						if (map.get("description") == getString(R.string.search_local_encounters)) {
							searchLocalEncounters(v);
						}

						if (map.get("description") == getString(R.string.show_recent_encounters)) {
							showRecentEncounters(v);
						}

						if (map.get("description") == getString(R.string.select_form)) {
							showSelectForm(v);
						}

						if (map.get("description") == getString(R.string.malnutrition_home_addnewhousehold)) {
							webFormHouseholdClicked(v);
						}

						if (map.get("description") == getString(R.string.malnutrition_workflow_addnewchild)) {
							webFormChildClicked(v);
						}

						if (map.get("description") == getString(R.string.malnutrition_workflow_open_malnutrition_home)) {
							showMalnutritionWorkflow(v);
						}
					}
				});
			}
		};
		fragmentsInOrder.add(onlineFragment);
	}

	private void initializeAdminTab(List<AbstractLaunchScreenFragment> fragmentsInOrder) {
		otherTasksFragment = new GridViewFragment() {
			@Override
			public void setUpGridView(final GridView gv) {
				super.setUpGridView(gv);

				ArrayList<HashMap<String, String>> listOtherTasksToolsStrings = new ArrayList<HashMap<String, String>>();

				// Initialize icons + text
				for (int i = 0; i < tableOtherTasksToolsString.length; i++) {
					LaunchScreenActivity.this.mapOtherTasks = new HashMap<String, String>();
					LaunchScreenActivity.this.mapOtherTasks.put("description",
							getString(tableOtherTasksToolsString[i]));
					LaunchScreenActivity.this.mapOtherTasks.put("icon",
							String.valueOf(tableOtherTasksToolsDrawable[i]));
					listOtherTasksToolsStrings
							.add(LaunchScreenActivity.this.mapOtherTasks);
				}

				LaunchScreenActivity.this.adapterOtherTasks = new LaunchGridListAdapter(
						getBaseContext(), listOtherTasksToolsStrings,
						R.layout.launch_grid_list_item, new String[] { "icon",
								"description" }, new int[] { R.id.icon,
								R.id.description });

				gv.setAdapter(LaunchScreenActivity.this.adapterOtherTasks);

				gv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> a, final View v,
							int position, long id) {
						HashMap<String, String> map = (HashMap<String, String>) gv
								.getItemAtPosition(position);

						AlertDialog.Builder adb = new AlertDialog.Builder(
								LaunchScreenActivity.this);
						adb.setTitle("Confirmation");

						if (map.get("description") == getString(R.string.global_preferences_label)) {
							AlertDialog.Builder alert = new AlertDialog.Builder(LaunchScreenActivity.this);

							alert.setTitle(R.string.global_preferences_label);
							alert.setMessage(getString(R.string.password));

							// Set an EditText view to get user input 
							final EditText input = new EditText(LaunchScreenActivity.this);
							input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
							alert.setView(input);

							alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
								  String value = input.getText().toString(); 
								    if(value.equals(MSFCommonUtils.preferences_password))
										showPreferencesButtonClicked(v);
								    else
								    	Toast.makeText(getApplicationContext(), getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
								  }
								});
							alert.show();
						}

						if (map.get("description") == getString(R.string.downloadConfig)) {
							adb.setMessage("Are you sure you want to download the OpenMRS config?");
							adb.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											new DownloadConfigTask(
													LaunchScreenActivity.this)
													.execute(new Object[] { (View) v });
										}
									});
							adb.setNegativeButton("No", null);
							adb.show();
						}

						if (map.get("description") == getString(R.string.show_preferences_button_text)) {
							showPreferencesButtonClicked(v);
						}

						if (map.get("description") == getString(R.string.resetDatabaseButton)) {
							adb.setMessage(getString(R.string.reset_database));
							adb.setPositiveButton(getString(R.string.yes),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											AlertDialog.Builder alert = new AlertDialog.Builder(LaunchScreenActivity.this);

											alert.setTitle("Confirmation");
											alert.setMessage(getString(R.string.reset_database_confirmation));

											// Set an EditText view to get user input 
											final EditText input = new EditText(LaunchScreenActivity.this);
											input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
											alert.setView(input);

											alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int whichButton) {
												  String value = input.getText().toString();  
												    if(value.equals(DatabaseConfigUtil.database_password))
												    	resetDatabase(v);
												    else
												    	Toast.makeText(getApplicationContext(), getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
												  }
												});
											alert.setNegativeButton(getString(R.string.no), null);
											alert.show();
										}
									});
							adb.setNegativeButton(getString(R.string.no), null);
							adb.show();
						}

						if (map.get("description") == getString(R.string.report_problem)) {
							CheckNetworkAvailable checkConnection = new CheckNetworkAvailable(
									LaunchScreenActivity.this);
							if (checkConnection.isOnline()){
								String resultMessage = "There are no email clients installed.";
								Intent i = new Intent(Intent.ACTION_SEND);
								i.setType("message/rfc822");
								i.putExtra(Intent.EXTRA_EMAIL, new String[] {
										"alexis.cartier@gmail.com",
										"nwilkie@gmail.com" });
								i.putExtra(Intent.EXTRA_SUBJECT,
										"Malnutrition Application - Problem Reporting");
								i.putExtra(Intent.EXTRA_TEXT,
										"Problem/Suggestion here");
								try {
										startActivity(Intent.createChooser(i,
												"Send mail..."));
								} catch (android.content.ActivityNotFoundException ex) {
									Toast.makeText(LaunchScreenActivity.this,
											resultMessage, Toast.LENGTH_SHORT)
											.show();
								}
							}
							else
								Toast.makeText(LaunchScreenActivity.this,
										getString(R.string.no_network_connection), Toast.LENGTH_SHORT)
										.show();
							

						}

						if (map.get("description") == getString(R.string.test_html_form_performance)) {
							Intent lpaIntent = new Intent(
									LaunchScreenActivity.this,
									HTMLFormTestActivity.class);
							LaunchScreenActivity.this.startActivity(lpaIntent);
						}
						if (map.get("description") == getString(R.string.test_gps_function)) {
							GPSManager gps = new GPSManager(
									LaunchScreenActivity.this);
							gps.start();
						}
						if (map.get("description") == getString(R.string.malnutrition_home_export_data)) {
							ExportCSVDataMalnutrition task = new ExportCSVDataMalnutrition(
									LaunchScreenActivity.this);
							task.execute();
						}
					}
				});
			}
		};
		fragmentsInOrder.add(otherTasksFragment);
	}

	public void customizeViewPager() {
		try {
			Field mScroller;
			mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(
					viewPager.getContext());
			mScroller.set(viewPager, scroller);
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}

		viewPager.setPageMargin(5);
	}

	public class TabsAdapter extends FragmentPagerAdapter implements
			OnTabChangeListener, OnPageChangeListener {

		public List<AbstractLaunchScreenFragment> fragments;

		public TabsAdapter(List<AbstractLaunchScreenFragment> fragments) {
			super(getSupportFragmentManager());
			this.fragments = fragments;
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int page) {
			tabHost.setCurrentTab(page);
		}

		@Override
		public void onTabChanged(String tabId) {
			for (int i = 0; i < fragments.size(); i++) {
				AbstractLaunchScreenFragment fragment = fragments.get(i);
				if (tabId.equals(fragment.getTabTag())) {
					viewPager.setCurrentItem(i, true);
					break;
				}
			}

			resetTabAppearance(tabHost);
		}

		@Override
		public Fragment getItem(int item) {
			return fragments.get(item);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}

	public class FixedSpeedScroller extends Scroller {

		public FixedSpeedScroller(Context context) {
			super(context);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy,
				int duration) {
			// Ignore received duration, use fixed one instead, ref to our other
			// method below
			// Ignore received duration, use fixed one instead
			int pageSize = viewPager.getPageMargin() + viewPager.getWidth();

			int scrollDuration;
			if (dx % pageSize == 0) {
				scrollDuration = SCROLL_DURATION_TAB_SELECT;
			} else {
				scrollDuration = duration;
			}

			super.startScroll(startX, startY, dx, dy, scrollDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {

			super.startScroll(startX, startY, dx, dy);
		}
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

	public void newEncounterPatientButtonClicked(View view) {
		Intent ip = new Intent(getApplicationContext(),
				SearchPatientActivity.class);
		ip.putExtra("newEncounterExistingPatient", true);
		ip.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(ip);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void viewPatientsButtonClicked(View view) {
	}

	public void searchPatientButtonClicked(View view) {
	}

	public void searchLocalEncounters(View view) {
		Intent lpaIntent = new Intent(LaunchScreenActivity.this,
				SearchEncounterActivity.class);
		LaunchScreenActivity.this.startActivity(lpaIntent);
	}

	public void searchLocalPatients(View view) {
		Intent lpaIntent = new Intent(LaunchScreenActivity.this,
				SearchPatientActivity.class);
		LaunchScreenActivity.this.startActivity(lpaIntent);
	}

	public void showRecentPatients(View view) {
		Intent lpaIntent = new Intent(LaunchScreenActivity.this,
				ShowRecentPatientsActivity.class);
		LaunchScreenActivity.this.startActivity(lpaIntent);
	}

	public void showRecentEncounters(View view) {
		Intent lpaIntent = new Intent(LaunchScreenActivity.this,
				ShowRecentEncountersActivity.class);
		LaunchScreenActivity.this.startActivity(lpaIntent);
	}

	public void webFormButtonClicked(View view) {
		Intent lpaIntent = new Intent(LaunchScreenActivity.this,
				HTMLFormActivity.class);
		LaunchScreenActivity.this.startActivity(lpaIntent);
	}

	public void webFormPatientButtonClicked(View view) {
		Intent lpaIntent = new Intent(LaunchScreenActivity.this,
				HTMLFormPatientActivity.class);
		LaunchScreenActivity.this.startActivity(lpaIntent);
	}

	public void showSelectForm(View view) {
		Intent lpaIntent = new Intent(LaunchScreenActivity.this,
				SelectFormActivity.class);
		LaunchScreenActivity.this.startActivity(lpaIntent);
	}

	public void showPreferences(View view) {
		Intent lpaIntent = new Intent(LaunchScreenActivity.this,
				PreferencesActivity2.class);
		LaunchScreenActivity.this.startActivity(lpaIntent);
	}

	private void showMalnutritionWorkflow(View view) {
		Intent lpaIntent = new Intent(LaunchScreenActivity.this,
				MalnutritionHomeActivity.class);
		LaunchScreenActivity.this.startActivity(lpaIntent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void showPreferencesButtonClicked(View view) {
		Intent ip = new Intent(getApplicationContext(),
				PreferencesActivity2.class);
		startActivity(ip);
	}

	public void resetDatabase(View view) {
		File f = getBaseContext().getDatabasePath(ClinicAdapter.DATABASE_NAME);

		boolean deleted = f.delete();
		System.out.println("Deleted the database file: " + deleted);

		ClinicAdapterManager.reset();
		Toast t = Toast.makeText(LaunchScreenActivity.this,
				getString(R.string.all_datas_deleted), Toast.LENGTH_SHORT);
		t.show();
	}

	public void uploadAllUnsent(View v) {
		ProgressDialog progressDialog = new ProgressDialog(this);
		RestBatchTask batch = new RestBatchTask(progressDialog);

		batch.execute();
	}

	public void webFormHouseholdClicked(View view) {
		Intent lpaIntent = new Intent(LaunchScreenActivity.this,
				HTMLFormHouseholdActivity.class);
		LaunchScreenActivity.this.startActivity(lpaIntent);
	}
	
	public void webFormChildClicked(View view) {
		Intent lpaIntent = new Intent(LaunchScreenActivity.this,
				HTMLFormChildActivity.class);
		LaunchScreenActivity.this.startActivity(lpaIntent);
	}
}
