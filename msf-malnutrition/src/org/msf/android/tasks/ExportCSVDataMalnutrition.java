package org.msf.android.tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.malnutrition.MalnutritionChild;
import org.msf.android.openmrs.malnutrition.MalnutritionHousehold;
import org.msf.android.openmrs.malnutrition.MalnutritionObservation;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;

public class ExportCSVDataMalnutrition extends AsyncTask<Void, Void, Void> {

	public static final String OUTPUT_FILE_NAME_PREFIX = "malnutritionDataExport_";
	private static String directoryName = "OCGMalnutrition";
	public static final String HOUSEHOLD_FILE_NAME_PART = "_household";
	public static final String CHILD_FILE_NAME_PART = "_children";
	private File exportDir = new File(
			Environment.getExternalStorageDirectory(), directoryName);

	private static String[] EMPTY_STRING_ARRAY = {};

	private String timestamp;

	private ProgressDialog progressDialog;
	private Context context;

	private ArrayList<String> householdColumnNames;
	private ArrayList<String> childColumnNames;

	private ArrayList<String> columnValuesCurrent;
	private File householdFile;
	private File childFile;

	private boolean createdHouseholdFile;
	private boolean createdChildFile;

	public ExportCSVDataMalnutrition(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (context != null) {
			progressDialog = ProgressDialog.show(context, "Export CSV Data",
					"Exporting", true);
		}

		createdHouseholdFile = false;
		createdChildFile = false;
	}

	@Override
	protected Void doInBackground(Void... v) {
		Log.i("PATH", Environment.getExternalStorageDirectory() + "");
		if (!exportDir.exists()) {
			exportDir.mkdirs();
			exportDir.setReadable(true, false);
			exportDir.setWritable(true, false);
		}

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);

		timestamp = String.valueOf(year + "-" + month + "-" + day + "_" + hours
				+ "-" + minutes + "-" + seconds);

		try {
			ClinicAdapter ca = ClinicAdapterManager
					.lockAndRetrieveClinicAdapter();

			writeHouseholds(ca);
			writeChildren(ca);
			deleteOldFiles();

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ClinicAdapterManager.unlock();
		}

		return null;

	}

	private void writeHouseholds(ClinicAdapter ca) throws SQLException,
			IOException {
		String fileNameRoot = OUTPUT_FILE_NAME_PREFIX + timestamp
				+ HOUSEHOLD_FILE_NAME_PART;
		File tempHouseholdFile = new File(exportDir, fileNameRoot + "_temp.csv");
		File householdFile = new File(exportDir, fileNameRoot + ".csv");

		householdColumnNames = new ArrayList<String>();
		columnValuesCurrent = new ArrayList<String>();
		// AlexisTranslate
		householdColumnNames.add("Internal_Database_ID");
		columnValuesCurrent.add(null);

		householdColumnNames.add("Number_Of_Recorded_Children");
		columnValuesCurrent.add(null);

		householdColumnNames.add("Longitude");
		columnValuesCurrent.add(null);

		householdColumnNames.add("Latitude");
		columnValuesCurrent.add(null);

		Dao<MalnutritionHousehold, Long> hDao = ca.getHouseholdDao();
		CloseableWrappedIterable<MalnutritionHousehold> hIterable = hDao
				.getWrappedIterable();

		String longitude;
		String latitude;
		try {
			tempHouseholdFile.delete();
			tempHouseholdFile.createNewFile();
			CSVWriter csvTempWriter = createConfiguredCsvWriter(tempHouseholdFile);
			for (MalnutritionHousehold household : hIterable) {
				// InternalDatabaseID
				columnValuesCurrent.set(0,
						Long.toString(household.getDatabaseId()));
				// NumberOfRecordedChildren
				columnValuesCurrent.set(1,
						Integer.toString(household.children.size()));

				if (household.longitude != Double.MAX_VALUE
						&& household.longitude != 0) {
					longitude = Double.toString(household.longitude);
				} else {
					longitude = "NOT_MEASURED";
				}
				columnValuesCurrent.set(2, longitude);

				if (household.latitude != Double.MAX_VALUE
						&& household.latitude != 0) {
					latitude = Double.toString(household.latitude);
				} else {
					latitude = "NOT_MEASURED";
				}
				columnValuesCurrent.set(3, latitude);

				CloseableWrappedIterable<MalnutritionObservation> obsIterator = household.obs
						.getWrappedIterable();
				writeTempObservations(csvTempWriter, obsIterator,
						householdColumnNames);
			}
			csvTempWriter.close();

			householdFile.delete();
			householdFile.createNewFile();
			writeTableFromTemp(householdColumnNames, tempHouseholdFile,
					householdFile);

			createdHouseholdFile = true;
			this.householdFile = householdFile;
			this.householdFile.setReadable(true, false);
			this.householdFile.setWritable(true, false);
		} catch (IllegalStateException ex) {
			if (ex.getCause() instanceof SQLException) {
				throw (SQLException) ex.getCause();
			}
		}
	}

	private void writeChildren(ClinicAdapter ca) throws SQLException,
			IOException {
		String fileNameRoot = OUTPUT_FILE_NAME_PREFIX + timestamp
				+ CHILD_FILE_NAME_PART;
		File tempChildrenFile = new File(exportDir, fileNameRoot + "_temp.csv");
		File childrenFile = new File(exportDir, fileNameRoot + ".csv");

		childColumnNames = new ArrayList<String>();
		columnValuesCurrent = new ArrayList<String>();

		// AlexisTranslate
		childColumnNames.add("Internal_Database_ID");
		columnValuesCurrent.add(null);
		childColumnNames.add("Household_ID");
		columnValuesCurrent.add(null);
		childColumnNames.add("Full Child ID");
		columnValuesCurrent.add(null);

		Dao<MalnutritionChild, Long> cDao = ca.getChildDao();
		CloseableWrappedIterable<MalnutritionChild> cIterable = cDao
				.getWrappedIterable();

		try {
			tempChildrenFile.delete();
			tempChildrenFile.createNewFile();
			CSVWriter csvTempWriter = createConfiguredCsvWriter(tempChildrenFile);
			for (MalnutritionChild child : cIterable) {
				// InternalDatabaseID
				columnValuesCurrent
						.set(0, Long.toString(child.getDatabaseId()));
				// Household_ID
				if (child.household != null) {
					columnValuesCurrent.set(1,
							Long.toString(child.household.getDatabaseId()));
				}

				if (child.household != null) {
					columnValuesCurrent.set(2, child.idNumber + "-"
							+ child.household.householdId);
				}

				CloseableWrappedIterable<MalnutritionObservation> obsIterator = child.obs
						.getWrappedIterable();
				writeTempObservations(csvTempWriter, obsIterator,
						childColumnNames);
			}
			csvTempWriter.close();

			childrenFile.delete();
			childrenFile.createNewFile();

			writeTableFromTemp(childColumnNames, tempChildrenFile, childrenFile);

			createdChildFile = true;
			childFile = childrenFile;
			childrenFile.setReadable(true, false);
			childrenFile.setWritable(true, false);
		} catch (IllegalStateException ex) {
			if (ex.getCause() instanceof SQLException) {
				throw (SQLException) ex.getCause();
			}
		}
	}

	private void writeTempObservations(CSVWriter csvWriter,
			Iterable<MalnutritionObservation> obsIteratable,
			ArrayList<String> columnNames) throws IOException {

		int indexOfColumn;
		for (MalnutritionObservation ob : obsIteratable) {
			if (ob.concept == null) {
				System.out.println("NULL OBS CONCEPT ON EXPORT!!!");
				continue;
			} else if (ob.value == null) {
				ob.value = "";
			}

			if (!columnNames.contains(ob.concept)) {
				columnNames.add(ob.concept);
				columnValuesCurrent.add(ob.value);
			} else {
				indexOfColumn = columnNames.indexOf(ob.concept);
				columnValuesCurrent.set(indexOfColumn, ob.value);
			}
		}
		csvWriter.writeNext(columnValuesCurrent.toArray(EMPTY_STRING_ARRAY));
		Collections.fill(columnValuesCurrent, "");
	}

	private void writeTableFromTemp(List<String> columnValues, File tempFile,
			File outputFile) throws IOException {
		FileWriter writer = new FileWriter(outputFile);

		CSVWriter csvWriter = createConfiguredCsvWriter(writer);
		csvWriter.writeNext(columnValues.toArray(EMPTY_STRING_ARRAY));
		csvWriter.writeNext(EMPTY_STRING_ARRAY);
		csvWriter.flush();

		FileReader reader = new FileReader(tempFile);
		IOUtils.copy(reader, writer);

		reader.close();
		writer.close();

		tempFile.delete();
	}

	private void deleteOldFiles() {
		File[] files = exportDir.listFiles();
		for (File f : files) {
			if (createdHouseholdFile
					&& f.getName().contains(HOUSEHOLD_FILE_NAME_PART)
					&& !f.getName().equals(householdFile.getName())) {
				f.delete();
			}

			if (createdChildFile && f.getName().contains(CHILD_FILE_NAME_PART)
					&& !f.getName().equals(childFile.getName())) {
				f.delete();
			}
		}
	}

	@Override
	protected void onPostExecute(Void v) {
		if (progressDialog != null && context != null) {
			progressDialog.dismiss();
			Toast.makeText(
					context,
					"CSV files " + " stocked in " + directoryName
							+ " directory.", Toast.LENGTH_LONG).show();
		}
	}

	public static CSVWriter createConfiguredCsvWriter(File file)
			throws IOException {
		return createConfiguredCsvWriter(new FileWriter(file));
	}

	public static CSVWriter createConfiguredCsvWriter(FileWriter fileWriter)
			throws IOException {
		CSVWriter csvWriter = new CSVWriter(new BufferedWriter(fileWriter));

		return csvWriter;
	}

}
