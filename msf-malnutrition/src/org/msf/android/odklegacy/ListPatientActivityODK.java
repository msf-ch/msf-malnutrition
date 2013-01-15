package org.msf.android.odklegacy;


// TODO Display patient view data
// TODO Display ages instead of dates
// TODO Optimize download patient task

//From ODK Collect: Keeping for reference

public class ListPatientActivityODK { //extends ListActivity implements UploadFormListener {
//
//	// Menu ID's
//	private static final int MENU_DOWNLOAD = Menu.FIRST;
//	private static final int MENU_UPLOAD = MENU_DOWNLOAD + 1;
//	private static final int MENU_NEWPATIENT = MENU_DOWNLOAD + 2;
//	private static final int MENU_PREFERENCES = MENU_DOWNLOAD + 3;
//
//	// Request codes
//	public static final int DOWNLOAD_PATIENT = 1;
//	public static final int BARCODE_CAPTURE = 2;
//	public static final int AUTO_DOWNLOAD = 3;
//
//	private static final String DOWNLOAD_PATIENT_CANCELED_KEY = "downloadPatientCanceled";
//
//	private ImageButton mBarcodeButton;
//	private EditText mSearchText;
//	private TextWatcher mFilterTextWatcher;
//
//	private ArrayAdapter<Patient> mPatientAdapter;
//	private ArrayList<Patient> mPatients = new ArrayList<Patient>();
//	private boolean mDownloadPatientCanceled = false;
//
//	private UploadInstanceTask mUploadFormTask;
//	private static final int UPLOAD_DIALOG = 1;
//	private AlertDialog mAlertDialog;
//	private ProgressDialog mProgressDialog;
//	private Integer totalCount = -1;
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * 
//	 * @see android.app.Activity#onCreate(android.os.Bundle)
//	 */
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		// NICKDEBUG
//		// SharedPreferences settings =
//		// PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//		// SharedPreferences.Editor editor = settings.edit();
//		// editor.putBoolean(PreferencesActivity.KEY_FIRST_RUN, true);
//		// editor.commit();
//
//		if (savedInstanceState != null) {
//			if (savedInstanceState.containsKey(DOWNLOAD_PATIENT_CANCELED_KEY)) {
//				mDownloadPatientCanceled = savedInstanceState
//						.getBoolean(DOWNLOAD_PATIENT_CANCELED_KEY);
//			}
//		}
//
//		setContentView(R.layout.find_patient);
//		setTitle(getString(R.string.app_name) + " > "
//				+ getString(R.string.find_patient));
//
//		if (!FileUtils.storageReady()) {
//			showCustomToast(getString(R.string.error, R.string.storage_error));
//			finish();
//		}
//
//		mFilterTextWatcher = new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				if (mPatientAdapter != null) {
//					mPatientAdapter.getFilter().filter(s);
//				}
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//
//			}
//		};
//
//		mSearchText = (EditText) findViewById(R.id.search_text);
//		mSearchText.addTextChangedListener(mFilterTextWatcher);
//
//		mBarcodeButton = (ImageButton) findViewById(R.id.barcode_button);
//		mBarcodeButton.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				Intent i = new Intent("com.google.zxing.client.android.SCAN");
//				try {
//					startActivityForResult(i, BARCODE_CAPTURE);
//				} catch (ActivityNotFoundException e) {
//					Toast t = Toast.makeText(getApplicationContext(),
//							getString(R.string.error, R.string.barcode_error),
//							Toast.LENGTH_LONG);
//					t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//					t.show();
//				}
//			}
//		});
//
//	}
//
//	@Override
//	protected void onListItemClick(ListView listView, View view, int position,
//			long id) {
//		// Get selected patient
//		Patient p = (Patient) getListAdapter().getItem(position);
//		String patientIdStr = p.getId().toString();
//
//		Intent ip = new Intent(getApplicationContext(),
//				ViewPatientActivity.class);
//		ip.putExtra(Constants.KEY_PATIENT_ID, patientIdStr);
//		startActivity(ip);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//		menu.add(0, MENU_DOWNLOAD, 0, getString(R.string.download_patients))
//				.setIcon(R.drawable.ic_menu_allfriends);
//		menu.add(0, MENU_UPLOAD, 0, getString(R.string.upload_patients))
//				.setIcon(android.R.drawable.ic_menu_upload);
//		menu.add(0, MENU_NEWPATIENT, 0, getString(R.string.new_patient))
//				.setIcon(R.drawable.ic_menu_invite);
//		menu.add(0, MENU_PREFERENCES, 0, getString(R.string.server_preferences))
//				.setIcon(android.R.drawable.ic_menu_preferences);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case MENU_DOWNLOAD:
//			Intent id = new Intent(getApplicationContext(),
//					DownloadPatientActivity.class);
//			startActivityForResult(id, DOWNLOAD_PATIENT);
//			return true;
//		case MENU_UPLOAD:
//			uploadFormInstances();
//			return true;
//		case MENU_NEWPATIENT: // TODO: Make this work
//			Intent in = new Intent(getApplicationContext(),
//					ListFormActivity.class);
//			// in.putExtra(Constants.KEY_PATIENT_ID, "0");
//			startActivity(in);
//			return true;
//		case MENU_PREFERENCES:
//			Intent ip = new Intent(getApplicationContext(),
//					PreferencesActivity.class);
//			startActivity(ip);
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode,
//			Intent intent) {
//
//		if (resultCode == RESULT_CANCELED) {
//			if (requestCode == DOWNLOAD_PATIENT) {
//				mDownloadPatientCanceled = true;
//			}
//			return;
//		}
//
//		if (requestCode == BARCODE_CAPTURE && intent != null) {
//			String sb = intent.getStringExtra("SCAN_RESULT");
//			if (sb != null && sb.length() > 0) {
//				mSearchText.setText(sb);
//			}
//		}
//
//		if (requestCode == DOWNLOAD_PATIENT || requestCode == AUTO_DOWNLOAD) {
//			try {
//				getPatients();
//			} catch (SQLException ex) {
//				showCustomToast("SQL ERROR: " + ex.getMessage());
//			}
//		}
//
//		super.onActivityResult(requestCode, resultCode, intent);
//
//	}
//
//	private void getPatients() throws SQLException {
//		getPatients(null);
//	}
//
//	private void getPatients(String searchStr) throws SQLException {
//
//		// ClinicAdapter ca = new ClinicAdapter();
//
//		// ca.open();
//		// Cursor c = null;
//		// DateFormat mdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//		if (searchStr == null || searchStr.length() == 0) {
//			ClinicAdapter2 ca = MSFClinicApp.createClinicAdapter();
//			Dao<Patient, String> pdao = ca.getPatientDao();
//			List<Patient> allPatients = pdao.queryForAll();
//			mPatients.clear();
//			mPatients.addAll(allPatients);
//			ca.close();
//			refreshView();
//			return;
//		}
//
//		searchStr = searchStr.replaceAll("\\*", "");
//		searchStr = searchStr.replaceAll("%", "");
//		searchStr = searchStr.replaceAll("_", "");
//		searchStr = searchStr.replaceAll("  ", " ");
//		searchStr = searchStr.replace(", ", " ");
//
//		String[] tokens = searchStr.split(" ");
//
//		ClinicAdapter2 ca = MSFClinicApp.createClinicAdapter();
//		Dao<Patient, String> pdao = ca.getPatientDao();
//		Where<Patient, String> tokenWhere;
//		List<Where<Patient, String>> tokenWhereList = new ArrayList<Where<Patient, String>>();
//		for (int i = 0; i < tokens.length; i++) {
//			String token = tokens[i];
//			if (token.length() > 0) {
//				tokenWhere = pdao.queryBuilder().where()
//						.like("givenName", token);
//				tokenWhere = tokenWhere.or().like("middleName", token);
//				tokenWhere = tokenWhere.or().like("middleName", token);
//				tokenWhereList.add(tokenWhere);
//			}
//		}
//
//		Where<Patient, String> queryWhere;
//		if (tokenWhereList.size() == 1) {
//			queryWhere = tokenWhereList.get(0);
//		} else {
//			Where<Patient, String> first = tokenWhereList.remove(0);
//			Where<Patient, String> second = tokenWhereList.remove(0);
//			Where<Patient, String>[] others = tokenWhereList
//					.toArray(new Where[tokenWhereList.size()]);
//
//			queryWhere = pdao.queryBuilder().where().and(first, second, others);
//		}
//
//		PreparedQuery<Patient> prepQuery = queryWhere.prepare();
//		List<Patient> result = pdao.query(prepQuery);
//
//		mPatients.clear();
//		mPatients.addAll(result);
//
//		ca.close();
//
//		refreshView();
//		/*
//		 * if (searchStr != null) { c = ca.fetchPatients(searchStr, searchStr);
//		 * } else { c = ca.fetchAllPatients(); }
//		 * 
//		 * if (c != null && c.getCount() >= 0) {
//		 * 
//		 * mPatients.clear();
//		 * 
//		 * int patientIdIndex = c .getColumnIndex(ClinicAdapter.KEY_PATIENT_ID);
//		 * int identifierIndex = c
//		 * .getColumnIndex(ClinicAdapter.KEY_IDENTIFIER); int givenNameIndex = c
//		 * .getColumnIndex(ClinicAdapter.KEY_GIVEN_NAME); int familyNameIndex =
//		 * c .getColumnIndex(ClinicAdapter.KEY_FAMILY_NAME); int middleNameIndex
//		 * = c .getColumnIndex(ClinicAdapter.KEY_MIDDLE_NAME); int
//		 * birthDateIndex = c .getColumnIndex(ClinicAdapter.KEY_BIRTH_DATE); int
//		 * genderIndex = c.getColumnIndex(ClinicAdapter.KEY_GENDER);
//		 * 
//		 * if (c.getCount() > 0) {
//		 * 
//		 * Patient p; do { p = new Patient(); p.setId(c.getInt(patientIdIndex));
//		 * p.setIdentifier(c.getString(identifierIndex));
//		 * p.getPerson().getPreferredName
//		 * ().setGivenName(c.getString(givenNameIndex));
//		 * p.getPerson().getPreferredName
//		 * ().setFamilyName(c.getString(familyNameIndex));
//		 * p.getPerson().getPreferredName
//		 * ().setMiddleName(c.getString(middleNameIndex)); try {
//		 * p.getPerson().setBirthDate(mdf.parse(c.getString(birthDateIndex))); }
//		 * catch (ParseException e) { e.printStackTrace(); }
//		 * p.getPerson().setGender(Gender.valueOf(c.getString(genderIndex)));
//		 * mPatients.add(p); } while (c.moveToNext()); }
//		 * 
//		 * }
//		 */
//
//		// if (c != null) {
//		// c.close();
//		// }
//		// ca.close();
//	}
//
//	private void refreshView() {
//
//		mPatientAdapter = new PatientAdapter(this, R.layout.patient_list_item,
//				mPatients);
//		setListAdapter(mPatientAdapter);
//
//	}
//
//	@Override
//	protected void onDestroy() {
//		if (mUploadFormTask != null) {
//			mUploadFormTask.setUploadListener(null);
//			if (mUploadFormTask.getStatus() == AsyncTask.Status.FINISHED) {
//				mUploadFormTask.cancel(true);
//			}
//		}
//
//		super.onDestroy();
//		mSearchText.removeTextChangedListener(mFilterTextWatcher);
//
//	}
//
//	@Override
//	protected void onResume() {
//		if (mUploadFormTask != null) {
//			mUploadFormTask.setUploadListener(this);
//		}
//		super.onResume();
//
//		SharedPreferences settings = PreferenceManager
//				.getDefaultSharedPreferences(getBaseContext());
//		boolean firstRun = settings.getBoolean(
//				PreferencesActivity.KEY_FIRST_RUN, true);
//
//		/*
//		 * if (firstRun) { // Save first run status /*
//		 * 
//		 * SharedPreferences.Editor editor = settings.edit();
//		 * editor.putBoolean(PreferencesActivity.KEY_FIRST_RUN, false);
//		 * editor.commit();
//		 * 
//		 * // Start preferences workflowActivity Intent ip = new
//		 * Intent(getApplicationContext(), PreferencesActivity.class);
//		 * startActivity(ip);
//		 */
//
//		/*
//		 * SharedPreferences.Editor editor = settings.edit();
//		 * editor.putBoolean(PreferencesActivity.KEY_AUTO_DOWNLOAD, true);
//		 * editor.commit();
//		 * 
//		 * Intent id = new Intent(getApplicationContext(),
//		 * AutoDownloadActivity.class); startActivityForResult(id,
//		 * AUTO_DOWNLOAD);
//		 */
//		// startActivity(id);
//
//		/*
//		 * getPatients(); } else { getPatients();
//		 * 
//		 * if (mPatients.isEmpty() && !mDownloadPatientCanceled) { // Download
//		 * patients if no patients are in db Intent id = new
//		 * Intent(getApplicationContext(), DownloadPatientActivity.class);
//		 * startActivityForResult(id, DOWNLOAD_PATIENT); } else { // hack to
//		 * trigger refilter
//		 * mSearchText.setText(mSearchText.getText().toString()); }
//		 */
//
//		try {
//			getPatients();
//		} catch (SQLException ex) {
//			showCustomToast("SQL ERROR: " + ex.getMessage());
//		}
//
//		if (mPatients.isEmpty() && !mDownloadPatientCanceled) {
//			// Download patients if no patients are in db
//			Intent id = new Intent(getApplicationContext(),
//					DownloadPatientActivity.class);
//			startActivityForResult(id, DOWNLOAD_PATIENT);
//		} else {
//			// hack to trigger refilter
//			mSearchText.setText(mSearchText.getText().toString());
//		}
//	}
//
//	@Override
//	protected void onPause() {
//		dismissDialogs();
//		super.onPause();
//
//	}
//
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//
//		outState.putBoolean(DOWNLOAD_PATIENT_CANCELED_KEY,
//				mDownloadPatientCanceled);
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
//
//	// FROM HERE IS SAMS ADDITIONS
//	private ArrayList<String> getUploadFormsInstances() {
//		ClinicAdapter cla = new ClinicAdapter();
//		cla.open();
//		Cursor c = cla
//				.fetchFormInstancesByStatus(ClinicAdapter.STATUS_INITIALIZED);
//		startManagingCursor(c);
//		ArrayList<String> allInstances = new ArrayList<String>();
//		while (!c.isAfterLast()) {
//			String s = c.getString(c
//					.getColumnIndex(ClinicAdapter.KEY_FORMINSTANCE_PATH));
//			allInstances.add(s);
//			c.moveToNext();
//		}
//		return allInstances;
//	}
//
//	private void uploadFormInstances() {
//		SharedPreferences settings = PreferenceManager
//				.getDefaultSharedPreferences(getBaseContext());
//		StringBuilder url = new StringBuilder(settings.getString(
//				PreferencesActivity.KEY_SERVER,
//				getString(R.string.default_server)));
//		url.append(Constants.INSTANCE_UPLOAD_URL);
//		url.append("?uname=");
//		url.append(settings.getString(PreferencesActivity.KEY_USERNAME,
//				getString(R.string.default_username)));
//		url.append("&pw=");
//		url.append(settings.getString(PreferencesActivity.KEY_PASSWORD,
//				getString(R.string.default_password)));
//		ArrayList<String> instances = getUploadFormsInstances();
//		totalCount = instances.size();
//
//		if (totalCount < 1) {
//			Toast.makeText(getApplicationContext(), "No Patients to Upload",
//					Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		// convert array list to an array
//		String[] sa = instances.toArray(new String[totalCount]);
//		mUploadFormTask = new UploadInstanceTask();
//		mUploadFormTask.setUploadListener(this);
//		mUploadFormTask.setUploadServer(url.toString());
//		mUploadFormTask.execute(sa);
//		showDialog(UPLOAD_DIALOG);
//	}
//
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch (id) {
//		case UPLOAD_DIALOG:
//			mProgressDialog = new ProgressDialog(this);
//			DialogInterface.OnClickListener uploadButtonListener = new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//					mUploadFormTask.setUploadListener(null);
//					mUploadFormTask.cancel(true);
//				}
//			};
//			mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
//			mProgressDialog.setTitle(getString(R.string.upload_patients));
//			mProgressDialog.setMessage(getString(R.string.uploading_patients));
//			mProgressDialog.setIndeterminate(true);
//			mProgressDialog.setCancelable(false);
//			mProgressDialog.setButton(getString(R.string.cancel),
//					uploadButtonListener);
//			return mProgressDialog;
//		}
//		return null;
//	}
//
//	private void dismissDialogs() {
//		if (mAlertDialog != null && mAlertDialog.isShowing()) {
//			mAlertDialog.dismiss();
//		}
//	}
//
//	@Override
//	public void progressUpdate(String message, int progress, int max) {
//		mProgressDialog.setMax(max);
//		mProgressDialog.setProgress(progress);
//	}
//
//	@Override
//	public void uploadComplete(ArrayList<String> result) {
//		dismissDialog(UPLOAD_DIALOG);
//		int resultSize = result.size();
//		if (resultSize == totalCount) {
//			Toast.makeText(this,
//					getString(R.string.upload_all_successful, totalCount),
//					Toast.LENGTH_SHORT).show();
//
//		} else {
//			String s = totalCount - resultSize + " of " + totalCount;
//			Toast.makeText(this, getString(R.string.upload_some_failed, s),
//					Toast.LENGTH_LONG).show();
//		}
//
//		// for all successful update status
//		ClinicAdapter cla = new ClinicAdapter();
//		cla.open();
//		for (int i = 0; i < resultSize; i++) {
//			Cursor c = cla.fetchFormInstancesByPath(result.get(i));
//			if (c != null) {
//				cla.updateFormInstance(result.get(i),
//						ClinicAdapter.STATUS_SUBMITTED);
//			}
//		}
//		cla.close();
//
//	}
//
//	@Override
//	public Object onRetainNonConfigurationInstance() {
//		if (mUploadFormTask != null
//				&& mUploadFormTask.getStatus() != AsyncTask.Status.FINISHED)
//			return mUploadFormTask;
//
//		return null;
//	}

}
