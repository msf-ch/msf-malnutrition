package org.msf.android.fragments.malnutrition;

import java.sql.SQLException;

import org.msf.android.R;
import org.msf.android.activities.malnutrition.MalnutritionWorkflowActivity;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.htmlforms.malnutrition.MalnutritionWorkflowManager;
import org.msf.android.openmrs.malnutrition.MalnutritionChild;
import org.msf.android.openmrs.malnutrition.MalnutritionHousehold;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

public class MalnutritionChildSummaryFragment extends Fragment implements
		OnClickListener {
	public static final String ADD_CHILD_BUTTON_TAG = "ADD_CHILD_BUTTON";
	public static final String CONTINUE_BUTTON_TAG = "CONTINUE_BUTTON";

	public static final ColorDrawable LIST_GRAY = new ColorDrawable(0xFFF0F0F0);
	public static final ColorDrawable LIST_WHITE = new ColorDrawable(0xFFFFFFFF);

	public static final int ICON_SIZE = 40;

	ViewGroup rootView;

	MalnutritionWorkflowManager manager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		manager = ((MalnutritionWorkflowActivity) activity).getManager();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = (ViewGroup) inflater.inflate(
				R.layout.malnutrition_child_entry_fragment, null);

		// drawable for add child button
		Button addChildButton = (Button) rootView
				.findViewById(R.id.malnutrition_child_entry_form_add_child);
		addChildButton.setTag(ADD_CHILD_BUTTON_TAG);
		addChildButton.setOnClickListener(this);
		Drawable addChildButtonDrawable = getResources().getDrawable(
				R.drawable.add_baby256);
		float addChildButtonDrawableScale = ((float) ICON_SIZE)
				/ (float) addChildButtonDrawable.getIntrinsicHeight();
		ScaleDrawable addChildButtonDrawableScaled = new ScaleDrawable(
				addChildButtonDrawable, Gravity.CENTER,
				addChildButtonDrawableScale, addChildButtonDrawableScale);
		addChildButtonDrawableScaled.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
		addChildButton.setCompoundDrawables(addChildButtonDrawableScaled, null,
				null, null);

		// drawable for continue button
		Button continueButton = (Button) rootView
				.findViewById(R.id.malnutrition_child_entry_form_continue);
		continueButton.setTag(CONTINUE_BUTTON_TAG);
		continueButton.setOnClickListener(this);
		Drawable continueButtonDrawable = getResources().getDrawable(
				R.drawable.navigate_right256);
		float continueButtonDrawableScale = ((float) ICON_SIZE)
				/ (float) continueButtonDrawable.getIntrinsicHeight();
		ScaleDrawable continueButtonDrawableScaled = new ScaleDrawable(
				continueButtonDrawable, Gravity.CENTER,
				continueButtonDrawableScale, continueButtonDrawableScale);
		continueButtonDrawableScaled.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
		continueButton.setCompoundDrawables(continueButtonDrawableScaled, null,
				null, null);

		TextView householdChiefTextView = (TextView) rootView
				.findViewById(R.id.malnutrition_child_entry_form_household_chief);
		TextView householdIdTextView = (TextView) rootView
				.findViewById(R.id.malnutrition_child_entry_form_household_id);
		TextView villageTextView = (TextView) rootView
				.findViewById(R.id.malnutrition_child_entry_form_village);

		MalnutritionHousehold household = manager.getHousehold();
		householdChiefTextView.setText(household.householdChief);
		householdIdTextView.setText(household.householdId);
		villageTextView.setText(household.village);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		rebuildList();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		if (v.getTag().equals(ADD_CHILD_BUTTON_TAG)) {
			manager.startNewChildForm();
		} else if (v.getTag().equals(CONTINUE_BUTTON_TAG)) {
			manager.finishChildSummary();
		}
	}

	public void rebuildList() {
		ViewGroup childList = (ViewGroup) rootView
				.findViewById(R.id.malnutrition_child_summary_form_list);

		// we're starting over
		childList.removeAllViews();

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (manager.getHousehold() != null) {
			try {
				ClinicAdapter ca = ClinicAdapterManager
						.lockAndRetrieveClinicAdapter();

				Dao<MalnutritionHousehold, Long> hDao = ca.getHouseholdDao();
				hDao.refresh(manager.getHousehold());

				MalnutritionChild[] children = new MalnutritionChild[0];
				children = manager.getHousehold().children.toArray(children);

				View squareForChild;
				boolean alternatingGray = false;
				for (MalnutritionChild child : children) {
					squareForChild = inflater.inflate(
							R.layout.malnutrition_child_summary_square, null);

					// Given name, family name
					((TextView) squareForChild
							.findViewById(R.id.malnutrition_child_summary_square_child_name))
							.setText(child.familyName + ", " + child.givenName);

					((TextView) squareForChild
							.findViewById(R.id.malnutrition_child_summary_square_child_age))
							.setText(child.age
									+ " "
									+ getResources().getString(
											R.string.malnutrition_months_old));

					// Id number
					((TextView) squareForChild
							.findViewById(R.id.malnutrition_child_summary_square_identifier_text))
							.setText(child.idNumber);

					// Gender
					if (MalnutritionChild.GENDER_FEMALE.equals(child.gender)) {
						((ImageView) squareForChild
								.findViewById(R.id.malnutrition_child_summary_square_gender_image))
								.setImageResource(R.drawable.female128);
					} else {
						((ImageView) squareForChild
								.findViewById(R.id.malnutrition_child_summary_square_gender_image))
								.setImageResource(R.drawable.male128);
					}

					if (alternatingGray) {
						squareForChild.setBackgroundDrawable(LIST_GRAY);
					}
					alternatingGray = !alternatingGray;

					ImageButton deleteButton = (ImageButton) squareForChild
							.findViewById(R.id.deleteChildButton);
					final Long databaseId = child.getDatabaseId();
					deleteButton.setTag(databaseId);
					squareForChild.setTag(databaseId);

					deleteButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							AlertDialog.Builder adb = new AlertDialog.Builder(
									getActivity());
							adb.setTitle(getResources().getString(R.string.malnutrition_verify_confirmation_title));
							adb.setMessage(getResources().getString(R.string.malnutrition_verify_confirmation_message));
							adb.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											try {
												deleteChild(databaseId);
											} catch (SQLException e) {
												e.printStackTrace();
											}
										}
									});
							adb.setNegativeButton(getResources().getString(R.string.no), null);
							adb.show();
						}
					});

					childList.addView(squareForChild);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} finally {
				ClinicAdapterManager.unlock();
			}
		}
	}

	public void deleteChild(Long databaseId) throws SQLException {
		try {
			ClinicAdapter ca = ClinicAdapterManager
					.lockAndRetrieveClinicAdapter();
			ca.getChildDao().deleteById(databaseId);
		} finally {
			ClinicAdapterManager.unlock();
		}

		rebuildList();
	}
}
