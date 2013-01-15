package org.msf.android.services;

import java.io.File;
import java.util.ArrayList;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;

public class MSFBackupService extends IntentService {

	public static final String DEFAULT_SERVICE_NAME = "BACKUP_SERVICE";

	public static final String OUTPUT_FILE_NAME_PREFIX = "malnutritionDataExport_";
	private static String directoryName = "OCGMalnutrition";
	private File exportDir = new File(
			Environment.getExternalStorageDirectory(), directoryName);

	private static String[] EMPTY_STRING_ARRAY = {};

	private String timestamp;

	private ProgressDialog progressDialog;

	private ArrayList<String> householdColumnNames;
	private ArrayList<String> childColumnNames;

	private ArrayList<String> columnValuesCurrent;

	public MSFBackupService() {
		this(DEFAULT_SERVICE_NAME);
	}

	public MSFBackupService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
	}

}
