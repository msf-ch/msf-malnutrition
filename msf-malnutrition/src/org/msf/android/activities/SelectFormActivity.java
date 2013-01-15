package org.msf.android.activities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.msf.android.R;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.EncounterType;
import org.msf.android.openmrs.Form;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SelectFormActivity extends ExpandableListActivity {
	public static final int LIST_ITEM_LEFT_PADDING_GROUP = 60;
	public static final int LIST_ITEM_BOTTOM_PADDING_GROUP = 5;
	public static final int LIST_ITEM_RIGHT_PADDING_GROUP = 7;

	public static final int LIST_ITEM_LEFT_PADDING_CHILD = 20;
	public static final int LIST_ITEM_RIGHT_PADDING_CHILD = 7;
	public static final int LIST_ITEM_TOP_PADDING_CHILD = 3;
	public static final int LIST_ITEM_BOTTOM_PADDING_CHILD = 3;

	private ClinicAdapter ca;
	private List<EncounterType> encounterTypes;
	private List<Form> allForms;
	private List<List<Form>> groupedForms;
	private FormExpandableListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.select_form_screen);

		ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
		try {
			encounterTypes = ca.getEncounterTypeDao().queryForAll();
			allForms = ca.getFormDao().queryForAll();
			groupedForms = new ArrayList<List<Form>>();

			for (EncounterType et : encounterTypes) {
				List<Form> formsOfEncounterType = new ArrayList<Form>();
				for (Form f : allForms) {
					if (f.getEncounterType() != null && et.getUuid() != null
							&& f.getEncounterType().equals(et.getUuid())) {
						formsOfEncounterType.add(f);
					}
				}
				groupedForms.add(formsOfEncounterType);
			}
		} catch (SQLException e) {
			Toast.makeText(this, "Could not load EncounterType/Form data",
					Toast.LENGTH_LONG);
			finish();
		}

		mAdapter = new FormExpandableListAdapter();
		setListAdapter(mAdapter);

		getExpandableListView().expandGroup(0);
		
//		for (int i = 0; i < getExpandableListAdapter().getGroupCount(); i++) {
//			getExpandableListView().expandGroup(i);
//		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (ca == null) {
			ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ca = null;
		ClinicAdapterManager.unlock();
	}

	class FormExpandableListAdapter extends BaseExpandableListAdapter {

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				LayoutInflater inflater = ((LayoutInflater) SelectFormActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
				v = inflater.inflate(R.layout.form_list_item, null);
				v.setPadding(LIST_ITEM_LEFT_PADDING_CHILD,
						LIST_ITEM_TOP_PADDING_CHILD,
						LIST_ITEM_RIGHT_PADDING_CHILD,
						LIST_ITEM_BOTTOM_PADDING_CHILD);
			} else {
				v = convertView;
			}

			TextView formName = ((TextView) v.findViewById(R.id.formName));
			TextView formDescription = ((TextView) v
					.findViewById(R.id.formDescription));
			TextView formVersion = ((TextView) v.findViewById(R.id.formVersion));

			Form currentForm = groupedForms.get(groupPosition).get(
					childPosition);

			formName.setText(currentForm.getName());
			formDescription.setText(currentForm.getDescription());
			String formVersionText;
			if (currentForm.getVersion() != null
					&& !currentForm.getVersion().isEmpty()) {
				formVersionText = "v" + currentForm.getVersion();
			} else {
				formVersionText = "";
			}
			
			formVersion.setText(""); //not showing formVersion for right now, looks messier

			return v;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				LayoutInflater inflater = ((LayoutInflater) SelectFormActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
				v = inflater.inflate(R.layout.encountertype_list_item, null);
				v.setPadding(LIST_ITEM_LEFT_PADDING_GROUP, 0,
						LIST_ITEM_RIGHT_PADDING_GROUP,
						LIST_ITEM_BOTTOM_PADDING_GROUP);
			} else {
				v = convertView;
			}

			TextView encounterTypeName = ((TextView) v
					.findViewById(R.id.encounterTypeName));
			TextView encounterTypeDescription = ((TextView) v
					.findViewById(R.id.encounterTypeDescription));
			TextView numberOfForms = ((TextView) v
					.findViewById(R.id.numberOfForms));

			EncounterType currentEncounterType = encounterTypes
					.get(groupPosition);

			encounterTypeName.setText(currentEncounterType.getName());
			encounterTypeDescription.setText(currentEncounterType
					.getDescription());
			String numberOfFormsText = groupedForms.get(groupPosition).size()
					+ " form";
			if (groupedForms.get(groupPosition).size() != 1) {
				numberOfFormsText += "s";
			}
			numberOfForms.setText(numberOfFormsText);

			return v;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return groupedForms.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return groupedForms.get(groupPosition).get(childPosition)
					.getDatabaseId();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return groupedForms.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return encounterTypes.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return encounterTypes.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return encounterTypes.get(groupPosition).getDatabaseId();
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// ?
			return true;
		}

	}
}
