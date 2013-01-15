package org.msf.android.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.Observation;
import org.msf.android.openmrs.Patient;
import org.msf.android.openmrs.Patient.PersonAddress;
import org.msf.android.utilities.MedicalTimeUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.CursorJoiner.Result;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

public class ExportCSVData extends AsyncTask<Void, Void, Result> {

	private static String directoryName = "MSFCholera-CSVFiles";
	private String nameFile;
	private ProgressDialog progressDialog;
	private Activity activity;
	private FileWriter writer;

	private String cityVillage;
	private List<PersonAddress> addresses;
	private Collection<Observation> observations;

	public ExportCSVData(Activity activity) {
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = ProgressDialog.show(activity, "Export CSV Datas",
				"Exporting", true);
	}

	@Override
	protected Result doInBackground(Void... v) {

		File exportDir = new File(Environment.getExternalStorageDirectory(),
				directoryName);
		Log.i("PATH", Environment.getExternalStorageDirectory() + "");
		if (!exportDir.exists())

		{

			exportDir.mkdirs();

		}

		String time = "";
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);

		time = String.valueOf(year + "-" + month + "-" + day + "_" + hours
				+ "-" + minutes + "-" + seconds);
		nameFile = "choleraDataExport_" + time + ".csv";
		File file = new File(exportDir, nameFile);
		try {
			file.createNewFile();
			writer = new FileWriter(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ClinicAdapter ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();

		try {
			Dao<Patient, Long> pDao = ca.getPatientDao();
			Dao<Encounter, Long> eDao = ca.getEncounterDao();

			PreparedQuery<Patient> ppq = pDao.queryBuilder().where()
					.isNull("uuid").prepare();
			PreparedQuery<Encounter> epq = eDao.queryBuilder().where()
					.isNull("uuid").prepare();

			CloseableWrappedIterable<Patient> patients = pDao
					.getWrappedIterable(ppq);
			try {
				writer.append("Family Name");
				writer.append(';');
				writer.append("Middle Name");
				writer.append(';');
				writer.append("Given Name");
				writer.append(';');
				writer.append("Age");
				writer.append(';');
				writer.append("Gender");
				writer.append(';');
				writer.append("City");
				writer.append("\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for (Patient p : patients) {
				try {
					writer.append(p.getFamilyName());
					writer.append(';');
					writer.append(p.getMiddleName());
					writer.append(';');
					writer.append(p.getGivenName());
					writer.append(';');
					writer.append(MedicalTimeUtils.getDefaultLongAge(p
							.getBirthdate()));
					writer.append(';');
					writer.append(p.getGender().getFullWord());
					writer.append(';');
					addresses = p.getAddresses();
					if (addresses.size() > 0) {
						// Should probably get preferred address here
						PersonAddress address = addresses.get(0);
						if (address.cityVillage != null
								&& !address.cityVillage.isEmpty()) {
							cityVillage = address.cityVillage;
						}
					}
					writer.append(cityVillage);
					writer.append("\n");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			CloseableWrappedIterable<Encounter> encounters = eDao
					.getWrappedIterable(epq);

			try {
				writer.append("\n");
				writer.append("Patient Name");
				writer.append(';');
				writer.append("Encounter Date Time");
				writer.append(';');
				writer.append("Encounter Type");
				writer.append(';');
				writer.append("Form");
				writer.append(';');
				for (Encounter e : encounters) {
					observations = e.getObs();
					for (Observation o : observations) {
						writer.append(o.getConcept());
						writer.append(';');
					}
				}
				writer.append("\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for (Encounter e : encounters) {
				try {
					writer.append(e.getPatientName());
					writer.append(';');
					writer.append(e.getEncounterDatetimeAsDDMMYYYYString());
					writer.append(';');
					writer.append(e.getEncounterType());
					writer.append(';');
					writer.append(e.getForm());
					writer.append(';');
					observations = e.getObs();
					for (Observation o : observations) {
						writer.append(o.getStringValue());
						writer.append(';');
					}
					writer.append("\n");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			ClinicAdapterManager.unlock();
		}

		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@Override
	protected void onPostExecute(Result result) {
		progressDialog.dismiss();
		Toast.makeText(
				activity.getApplicationContext(),
				"CSV file " + nameFile + " stocked in " + directoryName
						+ " directory.", Toast.LENGTH_LONG).show();
	}
}
