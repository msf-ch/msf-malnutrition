package org.msf.android.fragments.malnutrition;

import org.msf.android.R;
import org.msf.android.activities.malnutrition.MalnutritionWorkflowActivity;
import org.msf.android.app.MSFClinicApp;
import org.msf.android.fragments.AbstractLaunchScreenFragment;
import org.msf.android.tasks.ExportCSVDataMalnutrition;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MalnutritionHomeFragment extends AbstractLaunchScreenFragment implements View.OnClickListener {


	MalnutritionHomeButton registerHouseholdButton;
	String REGISTER_HOUSEHOLD_BUTTON_TAG = "REGISTER_HOUSEHOLD_BUTTON_TAG";
	
	MalnutritionHomeButton viewHouseholdsButton;
	String VIEW_HOUSEHOLD_BUTTON_TAG = "VIEW_HOUSEHOLD_BUTTON_TAG";
	
	MalnutritionHomeButton exportCSVButton;
	String EXPORT_CSV_TAG = "EXPORT_CSV_TAG";
	
	private Toast message;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.malnutrition_home, null);
		
		View msfLogo = viewGroup.findViewById(R.id.msf_logo);
		if (msfLogo != null) {
			msfLogo.setVisibility(View.GONE);
		}

		registerHouseholdButton = (MalnutritionHomeButton)viewGroup.findViewById(R.id.registerHouseholdButton);
		viewHouseholdsButton = (MalnutritionHomeButton)viewGroup.findViewById(R.id.viewHouseholdsButton);
		exportCSVButton = (MalnutritionHomeButton)viewGroup.findViewById(R.id.export_csv_button);
		
		registerHouseholdButton.setTag(REGISTER_HOUSEHOLD_BUTTON_TAG);
		viewHouseholdsButton.setTag(VIEW_HOUSEHOLD_BUTTON_TAG);
		exportCSVButton.setTag(EXPORT_CSV_TAG);
		
		registerHouseholdButton.setOnClickListener(this);
		viewHouseholdsButton.setOnClickListener(this);
		exportCSVButton.setOnClickListener(this);
		
		return viewGroup;
	}
	
	@Override
	public void onClick(View v) {
		Object tag = v.getTag();
		
		if (tag == null) {
			return;
		}
		
		if (tag.equals(REGISTER_HOUSEHOLD_BUTTON_TAG)) {
			startActivity(new Intent(getActivity(), MalnutritionWorkflowActivity.class));
		} else if (tag.equals(VIEW_HOUSEHOLD_BUTTON_TAG)) {
			
			if(message!=null)
				message.cancel();
			
			message = Toast.makeText(MSFClinicApp.getApplication(), getString(R.string.feature_unavailable), Toast.LENGTH_SHORT);
			message.show();
			
		} else if (tag.equals(EXPORT_CSV_TAG)) {
			ExportCSVDataMalnutrition task = new ExportCSVDataMalnutrition(
					getActivity());
			task.execute();
		}
	}
}
