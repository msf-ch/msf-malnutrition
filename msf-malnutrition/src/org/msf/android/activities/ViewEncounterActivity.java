package org.msf.android.activities;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.msf.android.R;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.fragments.ViewEncounterFragment;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.Observation;
import org.msf.android.openmrs.Patient;
import org.msf.android.tasks.DeleteDataEncounterTask;
import org.msf.android.tasks.PostEncounterTask;
import org.msf.android.utilities.MSFCommonUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

public class ViewEncounterActivity extends FragmentActivity implements OnPageChangeListener {

	public static final int MENU_ADD_ENCOUNTERDATA = Menu.FIRST + 1;
	public static final int MENU_DELETE_ENCOUNTER = Menu.FIRST + 2;
	
	public static final int ENCOUNTER_FRAGMENT_PADDING = 15;

	// private ListView ListViewEncounterDetails;
	private ListView ListViewEncounterObservations;
	private SimpleAdapter observationAdapter;

	private TextView patientNameField;
	private TextView birthdateField;
	private TextView identifierField;
	private TextView encounterDateField;
	//private TextView encounterTimeField;
	private TextView secondLineField;

	private ImageView genderView;
	private ImageView submittedView;
	
	private ImageButton rightButton;
	private ImageButton leftButton;

	private SlidingDrawer slidingDrawer;

	private ViewPager pager;
	private EncounterPagerAdapter pagerAdapter;

	private Patient patient;
	private List<Encounter> encounters;
	private Collection<Observation> observations;

	public static final SimpleDateFormat OBS_DATE_VALUE_DISPLAY = new SimpleDateFormat(
			"dd MMMMM yyyy");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encounter_view_details_list);

		Intent intent = getIntent();
		// getting the identifier of the patient selected
		Long _id = intent.getLongExtra("_id", Long.MIN_VALUE);

		patientNameField = (TextView) findViewById(R.id.name_text);
		birthdateField = (TextView) findViewById(R.id.birthdate_text);
		identifierField = (TextView) findViewById(R.id.identifier_text);
		encounterDateField = (TextView) findViewById(R.id.encounterDate);
		//encounterTimeField = (TextView) findViewById(R.id.encounterTime);
		secondLineField = (TextView) findViewById(R.id.secondLine);

		genderView = (ImageView) findViewById(R.id.gender_image);
		submittedView = (ImageView) findViewById(R.id.leftIcon);
		
		rightButton = (ImageButton) findViewById(R.id.navigateRightButton);
		leftButton = (ImageButton) findViewById(R.id.navigateLeftButton);
		

		pager = ((ViewPager) findViewById(R.id.pager));
		
		try {
			ClinicAdapter ca = ClinicAdapterManager
					.lockAndRetrieveClinicAdapter();
			Encounter initialSelectedEncounter = getEncounter(_id);
			encounters = ca.getEncounterDao().queryBuilder()
					.orderBy("encounterDatetime", true).where()
					.eq("dbPatient_id", initialSelectedEncounter.getDbPatient())
					.query();
			

			/** Fill patient data **/
			Patient mySelectedPatient = initialSelectedEncounter.getDbPatient();

			// Fill patient fields
			patientNameField.setText(mySelectedPatient.getFullName());
			identifierField.setText(mySelectedPatient
					.getPreferredPatientIdentifier());
			// patientNameField.setShadowLayer(1, 0, 2, R.color.solid_red);
			if (mySelectedPatient.getGender().getFullWord().equals("FEMALE")) {
				genderView.setImageResource(R.drawable.female24);
			} else {
				genderView.setImageResource(R.drawable.male24);
			}

			if (mySelectedPatient.getUuid() == null) {
				submittedView
						.setColorFilter(MSFCommonUtils.GRAYSCALE_FILTER);
				submittedView.setImageDrawable(getResources().getDrawable(
						R.drawable.memorycard24));
			} else {
				submittedView.setColorFilter(MSFCommonUtils.COLOR_FILTER);
				submittedView.setImageDrawable(getResources().getDrawable(
						R.drawable.openmrs_icon_24));
			}

			LocalDate birthdate = new LocalDate(
					mySelectedPatient.getBirthdate());
			LocalDate now = new LocalDate();
			int age = Years.yearsBetween(birthdate, now).getYears();
			birthdateField.setText(Html.fromHtml((new SimpleDateFormat(
					"dd-MMM-yyyy").format(mySelectedPatient.getBirthdate()))
					+ " (" + age + "yo)"));

			/** Set up ViewPager with encounter details **/
			pager.setPageMargin(0);
			pagerAdapter = new EncounterPagerAdapter(
					getSupportFragmentManager());
			pager.setAdapter(pagerAdapter);
			pager.setOnPageChangeListener(this);

			int currentEncounterIdx = -1;
			Encounter e = null;
			for (int i = 0; i < encounters.size(); i++) {
				e = encounters.get(i);
				if (e.getDatabaseId() == _id) {
					currentEncounterIdx = i;
					pager.setCurrentItem(currentEncounterIdx);
					break;
				}
			}
			if (currentEncounterIdx < 0) {
				throw new RuntimeException("Selected encounter not found... "
						+ _id);
			}
			
			setEncounterHeaders();
			setButtonVisibility();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ClinicAdapterManager.unlock();
		}
	}

	private Encounter getEncounter(Long _id) {
		ClinicAdapter ca;
		Encounter encounter = new Encounter();
		try {
			ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
			Dao<Encounter, Long> eDao = ca.getEncounterDao();
			encounter = eDao.queryForId(_id);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ClinicAdapterManager.unlock();
		}
		return encounter;
	}

	public Encounter getCurrentEncounter() {
		int itemIdx = pager.getCurrentItem();
		return encounters.get(itemIdx);
	}
	
	public void setEncounterHeaders() {
		Encounter currentEncounter = getCurrentEncounter();
		
		encounterDateField.setText(MSFCommonUtils.DDMMMMMYYYY_DATE_FORMATTER.format(currentEncounter.getEncounterDatetime()));
		secondLineField.setText((pager.getCurrentItem() + 1) + "/" + pagerAdapter.getCount());
		//encounterTimeField.setText(new SimpleDateFormat("kk:mm").format(currentEncounter.getEncounterDatetime()));
	}
	
	public void setButtonVisibility() {
		if (pager.getCurrentItem() == pagerAdapter.getCount() - 1) {
			rightButton.setVisibility(View.INVISIBLE);
//			rightButton.setColorFilter(MSFCommonUtils.GRAYSCALE_FILTER);
			rightButton.setEnabled(false);
		} else {
			rightButton.setVisibility(View.VISIBLE);
//			rightButton.setColorFilter(MSFCommonUtils.COLOR_FILTER);
			rightButton.setEnabled(true);
		}
		
		if (pager.getCurrentItem() == 0) {
			leftButton.setVisibility(View.INVISIBLE);
//			leftButton.setColorFilter(MSFCommonUtils.GRAYSCALE_FILTER);
			leftButton.setEnabled(false);
		} else {
			leftButton.setVisibility(View.VISIBLE);
//			leftButton.setColorFilter(MSFCommonUtils.COLOR_FILTER);
			leftButton.setEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ADD_ENCOUNTERDATA, Menu.NONE,
				"Send this encounter").setIcon(R.drawable.add_encounter_24);
		menu.add(Menu.NONE, MENU_DELETE_ENCOUNTER, Menu.NONE,
				"Delete this encounter").setIcon(
				R.drawable.icon_delete_encounter24);
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AlertDialog.Builder adb = new AlertDialog.Builder(
				ViewEncounterActivity.this);
		switch (item.getItemId()) {
		case MENU_ADD_ENCOUNTERDATA:
			adb.setTitle("Confirmation");
			adb.setMessage("Do you want to send this encounter of this patient?");
			adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					/*
					 * int oldPosition = currentCursor.getPosition();
					 * currentCursor.moveToPosition(position); long itemId =
					 * currentCursor.getLong(currentCursor
					 * .getColumnIndex("_id"));
					 * currentCursor.moveToPosition(oldPosition);
					 */
					PostEncounterTask task = new PostEncounterTask(
							ViewEncounterActivity.this, getBaseContext());
					task.execute(new Object[] { (Long) getCurrentEncounter()
							.getDatabaseId() });
				}
			});
			adb.setNegativeButton("No", null);

			adb.show();
			break;
		case MENU_DELETE_ENCOUNTER:
			adb.setTitle("Confirmation");
			adb.setMessage("Are you sure you want to delete this encounter?");
			adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					DeleteDataEncounterTask taskE = new DeleteDataEncounterTask(
							ViewEncounterActivity.this, 1, 1, true);
					taskE.execute(new Long[] { (Long) getCurrentEncounter()
							.getDatabaseId() });
				}
			});
			adb.setNegativeButton("No", null);

			adb.show();
			break;
		}
		return (super.onOptionsItemSelected(item));
	}

	public class EncounterPagerAdapter extends FragmentStatePagerAdapter {
		public EncounterPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int idx) {
			ViewEncounterFragment result = null;
			try {
				ClinicAdapter ca = ClinicAdapterManager
						.lockAndRetrieveClinicAdapter();
				result = new ViewEncounterFragment(encounters.get(idx), ca);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				ClinicAdapterManager.unlock();
			}

			return result;
		}

		@Override
		public int getCount() {
			return encounters.size();
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int page) {
		setEncounterHeaders();
		setButtonVisibility();
	}
	
	public void rightButtonClicked(View v) {
		if (pager.getCurrentItem() < pagerAdapter.getCount() - 1) {
			pager.setCurrentItem(pager.getCurrentItem() + 1, true);
		}
	}
	
	public void leftButtonClicked(View v) {
		if (pager.getCurrentItem() > 0) {
			pager.setCurrentItem(pager.getCurrentItem() - 1, true);
		}
	}
}
