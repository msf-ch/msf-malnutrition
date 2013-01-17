package org.msf.android.htmlforms;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.msf.android.R;
import org.msf.android.app.MSFClinicApp;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.Location;
import org.msf.android.openmrs.Patient;
import org.msf.android.rest.DefaultMapperFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.dao.Dao;

public class HtmlFormInterface {
	private ObjectMapper mapper;

	private Patient patient;
	private Encounter encounter;

	private ReducedFormData formData;
	private Activity parentActivity;

	public HtmlFormInterface(Activity parentActivity) {
		this(parentActivity, new Patient(), new Encounter());
	}

	public HtmlFormInterface(Activity parentActivity, Patient patient,
			Encounter encounter) {
		mapper = DefaultMapperFactory.getDefaultMapper();

		setFormData(new ReducedFormData());

		this.patient = patient;
		this.encounter = encounter;
		this.parentActivity = parentActivity;
	}

	public void storeData(String data) throws JsonParseException,
			JsonMappingException, IOException {
		try {
			System.out.println("Form result string: " + data);
			ReducedFormData result = mapper.readValue(data,
					new TypeReference<ReducedFormData>() {
					});
			setFormData(result);

			new HtmlFormManager().submitCholeraForm(getFormData());

			Context context = MSFClinicApp.getApplication();
			CharSequence text = "Patient " + result.getPatient().familyName
					+ ", " + result.getPatient().givenName
					+ " stored on the phone!";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} catch (Exception ex) {
			ex.printStackTrace();

			Context context = MSFClinicApp.getApplication();
			CharSequence text = "Error: " + ex.getMessage();
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		parentActivity.finish();
	}

	public String getLocations() {
		ClinicAdapter ca2 = null;
		List<Location> locs;
		try {
			ca2 = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
			Dao<Location, Long> locDao = ca2.getLocationDao();
			locs = locDao.queryForAll();
		} catch (SQLException ex) {
			ex.printStackTrace();
			locs = new ArrayList<Location>();
			Location errorLoc = new Location();
			errorLoc.setName("ERROR RETRIEVING LOCATIONS");
			locs.add(errorLoc);
		} finally {
			if (ca2 != null)
				ClinicAdapterManager.unlock();
		}

		String result;
		try {
			result = DefaultMapperFactory.getDefaultMapper()
					.writeValueAsString(locs);
			return result;
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public String serializeData() throws JsonGenerationException,
			JsonMappingException, IOException {
		return mapper.writeValueAsString(getFormData());
	}

	public ReducedFormData getFormData() {
		return formData;
	}

	private void setFormData(ReducedFormData formData) {
		this.formData = formData;
	}
}
