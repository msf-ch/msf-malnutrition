package org.msf.android.fragments.malnutrition;

import java.sql.SQLException;

import org.msf.android.R;
import org.msf.android.activities.malnutrition.MalnutritionWorkflowActivity;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.htmlforms.malnutrition.MalnutritionWorkflowManager;
import org.msf.android.openmrs.malnutrition.MalnutritionChild;
import org.msf.android.utilities.MSFCommonUtils;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;

import com.j256.ormlite.dao.CloseableWrappedIterable;

public class MalnutritionVerifySubmissionFragment extends Fragment implements
		OnCheckedChangeListener, OnClickListener {
	private MalnutritionWorkflowManager manager;

	private ViewGroup root;

	private ImageButton verifyCompleteButton;
	private ImageButton backButton;

	public static final String TAG_VERIFY_COMPLETE_BUTTON = "VERIFY_COMPLETE_BUTTON";
	public static final String TAG_BACK_BUTTON = "BACK_BUTTON";

	private CheckBox malesCheckBox;
	private CheckBox femalesCheckBox;

	public static final String TAG_MALES_CHECK_BOX = "MALES";
	public static final String TAG_FEMALES_CHECK_BOX = "FEMALES";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		manager = ((MalnutritionWorkflowActivity) activity).getManager();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = (ViewGroup) inflater.inflate(
				R.layout.malnutrition_verify_submission_fragment, null);
		root.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		malesCheckBox = (CheckBox) root
				.findViewById(R.id.malnutrition_verify_checkbox_males);
		femalesCheckBox = (CheckBox) root
				.findViewById(R.id.malnutrition_verify_checkbox_females);

		malesCheckBox.setTag(TAG_MALES_CHECK_BOX);
		femalesCheckBox.setTag(TAG_FEMALES_CHECK_BOX);

		malesCheckBox.setOnCheckedChangeListener(this);
		femalesCheckBox.setOnCheckedChangeListener(this);

		ClinicAdapter ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
		try {
			ca.getHouseholdDao().refresh(manager.getHousehold());
			CloseableWrappedIterable<MalnutritionChild> children = manager
					.getHousehold().children.getWrappedIterable();
			int males = 0;
			int females = 0;

			for (MalnutritionChild child : children) {
				if (MalnutritionChild.GENDER_FEMALE.equals(child.gender)) {
					females++;
				} else if (MalnutritionChild.GENDER_MALE.equals(child.gender)) {
					males++;
				}
			}

			malesCheckBox.setText(getString(R.string.malnutrition_verify_males)
					+ males);
			femalesCheckBox
					.setText(getString(R.string.malnutrition_verify_females)
							+ females);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ClinicAdapterManager.unlock();
		}

		malesCheckBox.setDuplicateParentStateEnabled(false);
		femalesCheckBox.setDuplicateParentStateEnabled(false);

		verifyCompleteButton = (ImageButton) root
				.findViewById(R.id.malnutrition_verify_complete_button);
		backButton = (ImageButton) root
				.findViewById(R.id.malnutrition_verify_back_button);

		verifyCompleteButton.setTag(TAG_VERIFY_COMPLETE_BUTTON);
		backButton.setTag(TAG_BACK_BUTTON);

		verifyCompleteButton.setOnClickListener(this);
		backButton.setOnClickListener(this);

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();

		checkSubmit();
	}

	public void checkSubmit() {
		if (malesCheckBox.isChecked() && femalesCheckBox.isChecked()) {
			verifyCompleteButton.setColorFilter(MSFCommonUtils.COLOR_FILTER);
			verifyCompleteButton.setClickable(true);
		} else {
			verifyCompleteButton
					.setColorFilter(MSFCommonUtils.GRAYSCALE_FILTER);
			verifyCompleteButton.setClickable(false);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		checkSubmit();
	}

	@Override
	public void onClick(View v) {
		Object tag = v.getTag();
		if (TAG_VERIFY_COMPLETE_BUTTON.equals(tag)) {
			manager.finishReview();
		} else if (TAG_BACK_BUTTON.equals(tag)) {
			manager.backFromReview();
		}
	}
}
