package org.msf.android.activities.malnutrition;

import java.util.ArrayList;
import java.util.List;

import org.msf.android.R;
import org.msf.android.htmlforms.ReducedFormData;
import org.msf.android.htmlforms.ReducedObs;
import org.msf.android.htmlforms.malnutrition.ChildInterface;
import org.msf.android.htmlforms.malnutrition.HouseholdInterface;
import org.msf.android.htmlforms.malnutrition.MalnutritionWorkflowManager;
import org.msf.android.openmrs.malnutrition.MalnutritionChild;
import org.msf.android.utilities.MSFCommonUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MalnutritionWorkflowActivity extends FragmentActivity implements
		OnGlobalLayoutListener {
	private MalnutritionWorkflowManager manager;

	private ViewGroup rootLayout;

	@Override
	protected void onCreate(Bundle savedBundle) {
		super.onCreate(savedBundle);
		setContentView(R.layout.malnutrition_workflow_activity);

		rootLayout = (ViewGroup) findViewById(R.id.rootLayout);
		rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);

		initializeWorkflow();
	}

	public void initializeWorkflow() {
		setManager(new MalnutritionWorkflowManager(this));
		getManager().initialize();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onGlobalLayout() {
		int activityHeight = rootLayout.getRootView().getHeight();
		int rootLayoutHeight = rootLayout.getHeight();

		int diff = activityHeight - rootLayoutHeight;

		if (diff > 100) {
			setKeyboardShown(true);
		} else {
			setKeyboardShown(false);
		}
	}

	private void setKeyboardShown(boolean isKeyboardShown) {
		if (isKeyboardShown) {
			findViewById(R.id.msf_logo).setVisibility(View.GONE);
		} else {
			findViewById(R.id.msf_logo).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (MSFCommonUtils.DEBUGGING_MODE) {
			if (keyCode == KeyEvent.KEYCODE_MENU) {
				if (!getManager().isHouseholdFormFinished()) {

					ReducedFormData testRfd = new ReducedFormData();
					testRfd.getObs().add(
							new ReducedObs(
									HouseholdInterface.ID_HOUSEHOLD_CHIEF,
									"Roberto Wilkie"));
					testRfd.getObs().add(
							new ReducedObs(HouseholdInterface.ID_HOUSEHOLD_ID,
									"2460"));
					testRfd.getObs().add(
							new ReducedObs(HouseholdInterface.ID_SURVEY_DATE,
									"04/28/2012"));
					testRfd.getObs().add(
							new ReducedObs(HouseholdInterface.ID_VILLAGE_NAME,
									"Mill Valley"));

					String testFormData;
					try {
						testFormData = new ObjectMapper()
								.writeValueAsString(testRfd);
						getManager().getHouseholdInterface().storeData(
								testFormData);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (!getManager().isChildSummaryFinished()) {
					List<ReducedFormData> testChildren = new ArrayList<ReducedFormData>();

					ReducedFormData testChild1 = new ReducedFormData();
					testChild1.getObs().add(
							new ReducedObs(ChildInterface.ID_AGE, "15"));
					testChild1.getObs().add(
							new ReducedObs(ChildInterface.ID_GIVEN_NAME,
									"Nicholas"));
					testChild1.getObs().add(
							new ReducedObs(ChildInterface.ID_FAMILY_NAME,
									"Wilkie"));
					testChild1.getObs().add(
							new ReducedObs(ChildInterface.ID_GENDER,
									MalnutritionChild.GENDER_MALE));
					testChild1.getObs()
							.add(new ReducedObs(ChildInterface.ID_ID_NUMBER,
									"24601"));
					testChildren.add(testChild1);

					ReducedFormData testChild2 = new ReducedFormData();
					testChild2.getObs().add(
							new ReducedObs(ChildInterface.ID_AGE, "13"));
					testChild2.getObs().add(
							new ReducedObs(ChildInterface.ID_GIVEN_NAME,
									"Elisabeth"));
					testChild2.getObs().add(
							new ReducedObs(ChildInterface.ID_FAMILY_NAME,
									"Wilkie"));
					testChild2.getObs().add(
							new ReducedObs(ChildInterface.ID_GENDER,
									MalnutritionChild.GENDER_FEMALE));
					testChild2.getObs()
							.add(new ReducedObs(ChildInterface.ID_ID_NUMBER,
									"24602"));
					testChildren.add(testChild2);

					ObjectMapper mapper = new ObjectMapper();
					try {
						String testChildSerialized1 = mapper
								.writeValueAsString(testChild1);
						String testChildSerialized2 = mapper
								.writeValueAsString(testChild2);

						getManager().startNewChildForm();
						getManager().getChildInterface().storeData(
								testChildSerialized1);

						Thread.sleep(1000);

						getManager().startNewChildForm();
						getManager().getChildInterface().storeData(
								testChildSerialized2);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return true;
			}
		}
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Fragment householdEntryFragment = getSupportFragmentManager().findFragmentByTag(MalnutritionWorkflowManager.TAG_HOUSEHOLD_ENTRY_FRAGMENT);
			Fragment childEntryFragment = getSupportFragmentManager().findFragmentByTag(MalnutritionWorkflowManager.TAG_CHILD_ENTRY_FRAGMENT);
			if ((householdEntryFragment != null && householdEntryFragment.isVisible()) || (childEntryFragment != null && childEntryFragment.isVisible())) {
				AlertDialog.Builder adb = new AlertDialog.Builder(
						MalnutritionWorkflowActivity.this);
				adb.setTitle(getString(R.string.exit_form));
				adb.setMessage(getString(R.string.quit_form));
				adb.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
									MalnutritionWorkflowActivity.this.finish();
								} else {
									getSupportFragmentManager().popBackStack();
								}
							}
						});
				adb.setNegativeButton(getString(R.string.no), null);
				adb.show();
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		getManager().onActivityDestroy();
		
		super.onDestroy();
	}

	public MalnutritionWorkflowManager getManager() {
		return manager;
	}

	public void setManager(MalnutritionWorkflowManager manager) {
		this.manager = manager;
	}
}
