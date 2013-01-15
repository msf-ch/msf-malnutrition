package org.msf.android.odklegacy;


//From ODK Collect: Keeping for reference
public class ObservationChartActivityODK {// extends Activity {

//	private Patient mPatient;
//	private String mObservationFieldName;
//
//	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
//	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
//
//	private GraphicalView mChartView;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		setContentView(R.layout.observation_chart);
//		
//		if (!FileUtils.storageReady()) {
//			showCustomToast(getString(R.string.error, R.string.storage_error));
//			finish();
//		}
//
//		// TODO Check for invalid patient IDs
//		String patientIdStr = getIntent().getStringExtra(Constants.KEY_PATIENT_ID);
//		Integer patientId = Integer.valueOf(patientIdStr);
//		try {
//			mPatient = getPatient(patientId);
//		} catch (SQLException ex) {
//			showCustomToast("SQL ERROR: " + ex.getMessage());
//			finish();
//		}
//		
//		mObservationFieldName = getIntent().getStringExtra(Constants.KEY_OBSERVATION_FIELD_NAME);
//		
//		setTitle(getString(R.string.app_name) + " > "
//				+ getString(R.string.view_patient_detail));
//		
//		TextView textView = (TextView) findViewById(R.id.title_text);
//		if (textView != null) {
//			textView.setText(mObservationFieldName);
//		}
//		
//		XYSeriesRenderer r = new XYSeriesRenderer();
//		r.setLineWidth(3.0f);
//		r.setColor(getResources().getColor(R.color.chart_red));
//		r.setPointStyle(PointStyle.CIRCLE);
//		r.setFillPoints(true);
//		
//		mRenderer.addSeriesRenderer(r);
//		mRenderer.setShowLegend(false);
//		//mRenderer.setXTitle("Encounter Date");
//		//mRenderer.setAxisTitleTextSize(18.0f);
//		mRenderer.setLabelsTextSize(11.0f);
//		//mRenderer.setXLabels(10);
//		mRenderer.setShowGrid(true);
//		mRenderer.setLabelsColor(getResources().getColor(android.R.color.black));
//	}
//	
//	private Patient getPatient(Integer patientId) throws SQLException {
//
//		ClinicAdapter2 ca = MSFClinicApp.createClinicAdapter();
//		Dao<Patient, String> pdao = ca.getPatientDao();
//		List<Patient> results = pdao.queryForEq("id", patientId);
//		Patient result;
//		if (results.size() == 0) {
//			return null;
//		} else {
//			result = results.get(0);
//		}
//		ca.close();
//		return result;
//		
////		Patient p = null;
////		ClinicAdapter ca = new ClinicAdapter();
////		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////
////		ca.open();
////		Cursor c = ca.fetchPatient(patientId);
////
////		if (c != null && c.getCount() > 0) {
////			int patientIdIndex = c
////					.getColumnIndex(ClinicAdapter.KEY_PATIENT_ID);
////			int identifierIndex = c
////					.getColumnIndex(ClinicAdapter.KEY_IDENTIFIER);
////			int givenNameIndex = c
////					.getColumnIndex(ClinicAdapter.KEY_GIVEN_NAME);
////			int familyNameIndex = c
////					.getColumnIndex(ClinicAdapter.KEY_FAMILY_NAME);
////			int middleNameIndex = c
////					.getColumnIndex(ClinicAdapter.KEY_MIDDLE_NAME);
////			int birthDateIndex = c
////					.getColumnIndex(ClinicAdapter.KEY_BIRTH_DATE);
////			int genderIndex = c.getColumnIndex(ClinicAdapter.KEY_GENDER);
////			
////			p = new Patient();
////			p.setId(c.getInt(patientIdIndex));
////			p.makeDefaultIdentifier(c.getString(identifierIndex));
////			p.getPerson().getPreferredName().setGivenName(c.getString(givenNameIndex));
////			p.getPerson().getPreferredName().setFamilyName(c.getString(familyNameIndex));
////			p.getPerson().getPreferredName().setMiddleName(c.getString(middleNameIndex));
////			try {
////				p.getPerson().setBirthDate(df.parse(c.getString(birthDateIndex)));
////			} catch (ParseException e) {
////				e.printStackTrace();
////			}
////			p.getPerson().setGender(Gender.valueOf(c.getString(genderIndex)));
////		}
////
////		if (c != null) {
////			c.close();
////		}
////		ca.close();
////
////		return p;
//	}
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
//			XYSeries series;
//			if (mDataset.getSeriesCount() > 0) {
//				series = mDataset.getSeriesAt(0);
//				series.clear();
//			} else {
//				series = new XYSeries(fieldName);
//				mDataset.addSeries(series);
//			}
//
//			int valueIntIndex = c.getColumnIndex(ClinicAdapter.KEY_VALUE_INT);
//			int valueNumericIndex = c.getColumnIndex(ClinicAdapter.KEY_VALUE_NUMERIC);
//			int encounterDateIndex = c.getColumnIndex(ClinicAdapter.KEY_ENCOUNTER_DATE);
//			int dataTypeIndex = c.getColumnIndex(ClinicAdapter.KEY_DATA_TYPE);
//
//			do {
//				try {
//					Date encounterDate = df.parse(c.getString(encounterDateIndex));
//					int dataType = c.getInt(dataTypeIndex);
//					
//					double value;
//					if (dataType == Constants.TYPE_INT) {
//						value = c.getInt(valueIntIndex);
//						series.add(encounterDate.getTime(), value);
//					} else if (dataType == Constants.TYPE_FLOAT) {
//						value = c.getFloat(valueNumericIndex);
//						series.add(encounterDate.getTime(), value);
//					}
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//
//			} while(c.moveToNext());
//		}
//
//		if (c != null) {
//			c.close();
//		}
//		ca.close();
//	}
//	
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		if (mPatient != null && mObservationFieldName != null) {
//			getObservations(mPatient.getId(), mObservationFieldName);
//		}
//		
//		if (mChartView == null) {
//			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
//			mChartView = ChartFactory.getTimeChartView(this, mDataset,
//					mRenderer, null);
//			layout.addView(mChartView, new LayoutParams(
//					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//		} else {
//			mChartView.repaint();
//		}
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
