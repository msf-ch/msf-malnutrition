package org.msf.android.odklegacy;


//From ODK Collect: Keeping for reference
public class ObservationTimelineActivityODK {//extends ListActivity {

//	private Patient mPatient;
//	private String mObservationFieldName;
//
//	private ArrayAdapter<Observation> mEncounterAdapter;
//	private ArrayList<Observation> mEncounters = new ArrayList<Observation>();
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		setContentView(R.layout.observation_timeline);
//
//		if (!FileUtils.storageReady()) {
//			showCustomToast(getString(R.string.error, R.string.storage_error));
//			finish();
//		}
//
//		// TODO Check for invalid patient IDs
//		String patientIdStr = getIntent().getStringExtra(
//				Constants.KEY_PATIENT_ID);
//		Integer patientId = Integer.valueOf(patientIdStr);
//		try {
//			ClinicAdapter2 ca = MSFClinicApp.createClinicAdapter();
//			Dao<Patient, String> pdao = ca.getPatientDao();
//			List<Patient> results = pdao.queryForEq("id", patientId);
//			if (results.size() == 0) {
//				mPatient = null;
//			} else {
//				mPatient = results.get(0);
//			}
//			
//			ca.close();
//		} catch (SQLException ex) {
//			showCustomToast("SQL ERROR: " + ex.getMessage());
//		}
//
//		mObservationFieldName = getIntent().getStringExtra(
//				Constants.KEY_OBSERVATION_FIELD_NAME);
//
//		setTitle(getString(R.string.app_name) + " > "
//				+ getString(R.string.view_patient_detail));
//
//		TextView textView = (TextView) findViewById(R.id.title_text);
//		if (textView != null) {
//			textView.setText(mObservationFieldName);
//		}
//	}
//
//	//
//	// private Patient getPatient(Integer patientId) {
//	//
//	// Patient p = null;
//	// ClinicAdapter ca = new ClinicAdapter();
//	// DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	//
//	// ca.open();
//	// Cursor c = ca.fetchPatient(patientId);
//	//
//	// if (c != null && c.getCount() > 0) {
//	// int patientIdIndex = c
//	// .getColumnIndex(ClinicAdapter.KEY_PATIENT_ID);
//	// int identifierIndex = c
//	// .getColumnIndex(ClinicAdapter.KEY_IDENTIFIER);
//	// int givenNameIndex = c
//	// .getColumnIndex(ClinicAdapter.KEY_GIVEN_NAME);
//	// int familyNameIndex = c
//	// .getColumnIndex(ClinicAdapter.KEY_FAMILY_NAME);
//	// int middleNameIndex = c
//	// .getColumnIndex(ClinicAdapter.KEY_MIDDLE_NAME);
//	// int birthDateIndex = c
//	// .getColumnIndex(ClinicAdapter.KEY_BIRTH_DATE);
//	// int genderIndex = c.getColumnIndex(ClinicAdapter.KEY_GENDER);
//	//
//	// p = new Patient();
//	// p.setId(c.getInt(patientIdIndex));
//	// p.makeDefaultIdentifier(c.getString(identifierIndex));
//	// p.getPerson().getPreferredName().setGivenName(c.getString(givenNameIndex));
//	// p.getPerson().getPreferredName().setFamilyName(c.getString(familyNameIndex));
//	// p.getPerson().getPreferredName().setMiddleName(c.getString(middleNameIndex));
//	// try {
//	// p.getPerson().setBirthDate(df.parse(c.getString(birthDateIndex)));
//	// } catch (ParseException e) {
//	// e.printStackTrace();
//	// }
//	// p.getPerson().setGender(Gender.valueOf(c.getString(genderIndex)));
//	// }
//	//
//	// if (c != null) {
//	// c.close();
//	// }
//	// ca.close();
//	//
//	// return p;
//	// }
//
//	private void getObservations(Integer patientId, String fieldName) {
//
//		ClinicAdapter ca = new ClinicAdapter();
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//		ca.open();
//		Cursor c = ca.fetchPatientObservation(patientId, fieldName);
//
//		if (c != null && c.getCount() >= 0) {
//
//			mEncounters.clear();
//
//			int valueTextIndex = c.getColumnIndex(ClinicAdapter.KEY_VALUE_TEXT);
//			int valueIntIndex = c.getColumnIndex(ClinicAdapter.KEY_VALUE_INT);
//			int valueDateIndex = c.getColumnIndex(ClinicAdapter.KEY_VALUE_DATE);
//			int valueNumericIndex = c
//					.getColumnIndex(ClinicAdapter.KEY_VALUE_NUMERIC);
//			int encounterDateIndex = c
//					.getColumnIndex(ClinicAdapter.KEY_ENCOUNTER_DATE);
//			int dataTypeIndex = c.getColumnIndex(ClinicAdapter.KEY_DATA_TYPE);
//
//			Observation obs;
//			do {
//				obs = new Observation();
//				obs.setFieldName(fieldName);
//				try {
//					obs.setEncounterDate(df.parse(c
//							.getString(encounterDateIndex)));
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//
//				int dataType = c.getInt(dataTypeIndex);
//				switch (dataType) {
//				case Constants.TYPE_INT:
//					obs.setConceptDataType(ConceptDataType.NUMERIC);
//					//obs.setValueInt(c.getInt(valueIntIndex));
//					break;
//				case Constants.TYPE_FLOAT:
//					obs.setConceptDataType(ConceptDataType.NUMERIC);
////					obs.setValueNumeric(c.getFloat(valueNumericIndex));
//					break;
//				case Constants.TYPE_DATE:
//					obs.setConceptDataType(ConceptDataType.DATE);
//					try {
//						obs.setValueDate(df.parse(c.getString(valueDateIndex)));
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//					break;
//				default:
//					obs.setConceptDataType(ConceptDataType.TEXT);
//					obs.setValue(c.getString(valueTextIndex));
//				}
//
//				mEncounters.add(obs);
//
//			} while (c.moveToNext());
//		}
//
//		refreshView();
//
//		if (c != null) {
//			c.close();
//		}
//		ca.close();
//	}
//
//	private void refreshView() {
//
//		mEncounterAdapter = new EncounterAdapter(this,
//				R.layout.encounter_list_item, mEncounters);
//		setListAdapter(mEncounterAdapter);
//
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		if (mPatient != null && mObservationFieldName != null) {
//			getObservations(mPatient.getId(), mObservationFieldName);
//		}
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//
//	}
//
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//	}
//
//	private void showCustomToast(String message) {
//		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View view = inflater.inflate(R.layout.toast_view, null);
//
//		// set the text in the view
//		TextView tv = (TextView) view.findViewById(R.id.message);
//		tv.setText(message);
//
//		Toast t = new Toast(this);
//		t.setView(view);
//		t.setDuration(Toast.LENGTH_LONG);
//		t.setGravity(Gravity.CENTER, 0, 0);
//		t.show();
//	}
}
