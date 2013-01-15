package org.msf.android.activities.malnutrition;

import org.msf.android.R;
import org.msf.android.fragments.malnutrition.MalnutritionHomeButton;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class MalnutritionHomeActivity extends FragmentActivity implements View.OnClickListener {

	private MalnutritionHomeButton registerHouseholdButton;
	public static final String REGISTER_HOUSEHOLD_BUTTON_TAG = "REGISTER_HOUSEHOLD_BUTTON_TAG";
	
	private MalnutritionHomeButton viewHouseholdsButton;
	public static final String VIEW_HOUSEHOLD_BUTTON_TAG = "VIEW_HOUSEHOLD_BUTTON_TAG";
	
	private MalnutritionHomeButton exportCsvButton;
	public static final String EXPORT_CSV_TAG = "EXPORT_CSV_TAG";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.malnutrition_home);
		
		registerHouseholdButton = (MalnutritionHomeButton)findViewById(R.id.registerHouseholdButton);
		viewHouseholdsButton = (MalnutritionHomeButton)findViewById(R.id.viewHouseholdsButton);
		exportCsvButton = (MalnutritionHomeButton)findViewById(R.id.export_csv_button);
		
		registerHouseholdButton.setTag(REGISTER_HOUSEHOLD_BUTTON_TAG);
		viewHouseholdsButton.setTag(VIEW_HOUSEHOLD_BUTTON_TAG);
		exportCsvButton.setTag(EXPORT_CSV_TAG);
		
		registerHouseholdButton.setOnClickListener(this);
		viewHouseholdsButton.setOnClickListener(this);
		exportCsvButton.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		Object tag = v.getTag();
		
		if (tag == null) {
			return;
		}
		
		if (tag.equals(REGISTER_HOUSEHOLD_BUTTON_TAG)) {
			startActivity(new Intent(this, MalnutritionWorkflowActivity.class));
		} else if (tag.equals(VIEW_HOUSEHOLD_BUTTON_TAG)) {
			
		} else if (tag.equals(EXPORT_CSV_TAG)) {
			
		}
	}
}
