package org.msf.android.managers.malnutrition;

import org.msf.android.R;
import org.msf.android.activities.malnutrition.MalnutritionWorkflowActivity;
import org.msf.android.fragments.malnutrition.MalnutritionChildSummaryFragment;
import org.msf.android.fragments.malnutrition.MalnutritionFormFragment;
import org.msf.android.fragments.malnutrition.MalnutritionVerifySubmissionFragment;
import org.msf.android.htmlforms.malnutrition.ChildBridge;
import org.msf.android.htmlforms.malnutrition.HouseholdBridge;
import org.msf.android.openmrs.malnutrition.MalnutritionHousehold;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.google.inject.Inject;

public class MalnutritionWorkflowManager implements LocationListener {

	public static String HOUSEHOLD_FORM_URL = "file:///android_asset/child_malnutrition/html/formpages_household.html";
	public static String CHILD_FORM_URL = "file:///android_asset/child_malnutrition/html/formpages_child.html";

	public static String TAG_WORKFLOW_CONTENT = "WORKFLOW_CONTENT";
	public static String TAG_HOUSEHOLD_ENTRY_FRAGMENT = "HOUSEHOLD_ENTRY_FRAGMENT";
	public static String TAG_CHILD_SUMMARY_FRAGMENT = "CHILD_SUMMARY_FRAGMENT";
	public static String TAG_CHILD_ENTRY_FRAGMENT = "CHILD_ENTRY_FRAGMENT";

	private MalnutritionWorkflowActivity workflowActivity;

	private HouseholdBridge householdBridge;
	private ChildBridge childBridge;

	private MalnutritionHousehold household;

	private LocationManager mLocationManager;

	private boolean householdFormStarted = false;
	private boolean householdFormFinished = false;
	private boolean childSummaryStarted = false;
	private boolean childSummaryFinished = false;

	private boolean fillingChildSummary = false;

	@Inject
	public MalnutritionWorkflowManager() {
	}
	
	public MalnutritionWorkflowManager(MalnutritionWorkflowActivity activity) {
		setWorkflowActivity(activity);
	}
	
	public void setWorkflowActivity(MalnutritionWorkflowActivity activity) {
		this.workflowActivity = activity;
	}

	public void initialize() {
		mLocationManager = (LocationManager) workflowActivity
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, this);

		startHouseholdForm();
	}

	public void startHouseholdForm() {
		if (getHouseholdBridge() == null) {
			setHouseholdBridge(new HouseholdBridge(this));
		}

		MalnutritionFormFragment formFragment = new MalnutritionFormFragment();
		FragmentTransaction transaction = workflowActivity
				.getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.malnutrition_workflow_content, formFragment,
				TAG_HOUSEHOLD_ENTRY_FRAGMENT);
		transaction.disallowAddToBackStack();
		transaction.commit();

		formFragment.initializeWebView(HOUSEHOLD_FORM_URL,
				getHouseholdBridge());

		householdFormStarted = true;
	}

	public void finishHouseholdForm() {
		if (getHouseholdBridge().getHousehold() != null) {
			setHousehold(getHouseholdBridge().getHousehold());
		}
		householdFormFinished = true;

		startChildSummary();
	}

	public void startChildSummary() {
		MalnutritionChildSummaryFragment frag = new MalnutritionChildSummaryFragment();
		FragmentTransaction transaction = workflowActivity
				.getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.malnutrition_workflow_content, frag, TAG_CHILD_SUMMARY_FRAGMENT);
		transaction.addToBackStack(null);

		transaction.commit();

		setChildSummaryStarted(true);
	}

	public void finishChildSummary() {
		setChildSummaryFinished(true);

		startReview();
		// finishReview();
	}

	public void startNewChildForm() {
		setChildBridge(new ChildBridge(this, getHouseholdBridge()
				.getHousehold()));
		MalnutritionFormFragment childFormFragment = new MalnutritionFormFragment();
		FragmentTransaction transaction = workflowActivity
				.getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.malnutrition_workflow_content,
				childFormFragment, TAG_CHILD_ENTRY_FRAGMENT);
		// transaction.disallowAddToBackStack();
		transaction.addToBackStack(null);

		transaction.commit();

		childFormFragment
				.initializeWebView(CHILD_FORM_URL, getChildBridge());
		setFillingChildSummary(true);
	}

	public void finishNewChildForm() {
		workflowActivity.getSupportFragmentManager().popBackStack();
		setFillingChildSummary(false);
	}

	public void startReview() {
		MalnutritionVerifySubmissionFragment verifyFragment = new MalnutritionVerifySubmissionFragment();

		FragmentTransaction transaction = workflowActivity
				.getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.malnutrition_workflow_content, verifyFragment, "");
		transaction.addToBackStack(null);

		transaction.commit();
	}

	public void finishReview() {
		workflowActivity.finish();

		Toast.makeText(
				workflowActivity,
				workflowActivity
						.getString(R.string.malnutrition_verify_form_saved),
				Toast.LENGTH_LONG).show();
	}

	public void onActivityDestroy() {
		if (mLocationManager != null) {
			mLocationManager.removeUpdates(this);
		}
	}

	public void backFromReview() {
		workflowActivity.getSupportFragmentManager().popBackStack();

		setChildSummaryFinished(false);
	}

	public MalnutritionHousehold getHousehold() {
		return household;
	}

	public void setHousehold(MalnutritionHousehold household) {
		this.household = household;
	}

	public boolean isHouseholdFormStarted() {
		return householdFormStarted;
	}

	public void setHouseholdFormStarted(boolean householdFormStarted) {
		this.householdFormStarted = householdFormStarted;
	}

	public boolean isHouseholdFormFinished() {
		return householdFormFinished;
	}

	public void setHouseholdFormFinished(boolean householdFormFinished) {
		this.householdFormFinished = householdFormFinished;
	}

	public boolean isChildSummaryStarted() {
		return childSummaryStarted;
	}

	public void setChildSummaryStarted(boolean childSummaryStarted) {
		this.childSummaryStarted = childSummaryStarted;
	}

	public boolean isChildSummaryFinished() {
		return childSummaryFinished;
	}

	public void setChildSummaryFinished(boolean childSummaryFinished) {
		this.childSummaryFinished = childSummaryFinished;
	}

	public HouseholdBridge getHouseholdBridge() {
		return householdBridge;
	}

	public void setHouseholdBridge(HouseholdBridge householdInterface) {
		this.householdBridge = householdInterface;
	}

	public ChildBridge getChildBridge() {
		return childBridge;
	}

	public void setChildBridge(ChildBridge childBridge) {
		this.childBridge = childBridge;
	}

	public boolean isFillingChildSummary() {
		return fillingChildSummary;
	}

	public void setFillingChildSummary(boolean fillingChildSummary) {
		this.fillingChildSummary = fillingChildSummary;
	}

	/* LocationListener functions */

	@Override
	public void onLocationChanged(Location location) {
		// Toast.makeText(workflowActivity,
		// "Longitude: " + location.getLongitude() + ", Latitude: "
		// + location.getLatitude(), Toast.LENGTH_LONG).show();
		try {

			getHouseholdBridge().setLatitude(location.getLatitude());
			getHouseholdBridge().setLongitude(location.getLongitude());

			mLocationManager.removeUpdates(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
