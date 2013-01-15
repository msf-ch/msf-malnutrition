package org.msf.android.database;

import java.sql.SQLException;
import java.util.List;

import org.msf.android.openmrs.Cohort;
import org.msf.android.openmrs.Concept;
import org.msf.android.openmrs.ConceptClass;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.EncounterType;
import org.msf.android.openmrs.Field;
import org.msf.android.openmrs.FieldType;
import org.msf.android.openmrs.Form;
import org.msf.android.openmrs.FormField;
import org.msf.android.openmrs.Location;
import org.msf.android.openmrs.LocationTag;
import org.msf.android.openmrs.MobileConfig;
import org.msf.android.openmrs.Observation;
import org.msf.android.openmrs.Patient;
import org.msf.android.openmrs.PersonAddress;
import org.msf.android.openmrs.User;
import org.msf.android.openmrs.malnutrition.MalnutritionChild;
import org.msf.android.openmrs.malnutrition.MalnutritionHousehold;
import org.msf.android.openmrs.malnutrition.MalnutritionObservation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class ClinicAdapter extends OrmLiteSqliteOpenHelper {
	public static final String DATABASE_NAME = "msfclinic.db";
	public static final int DATABASE_VERSION = 29;

	private Dao<Cohort, Long> cohortDao = null;
	private Dao<Observation, Long> observationDao = null;
	private Dao<Patient, Long> patientDao = null;
	private Dao<ConceptClass, Long> conceptClassDao = null;
	private Dao<Encounter, Long> encounterDao = null;
	private Dao<EncounterType, Long> encounterTypeDao = null;
	private Dao<Form, Long> formDao = null;
	private Dao<Location, Long> locationDao = null;
	private Dao<PersonAddress, Long> personAddressDao = null;
	private Dao<Concept, Long> conceptDao = null;
	private Dao<FormField, Long> formFieldDao = null;
	private Dao<FieldType, Long> fieldTypeDao = null;
	private Dao<Field, Long> fieldDao = null;
	private Dao<User, Long> userDao = null;
	private Dao<LocationTag, Long> locationTagDao = null;
	private Dao<MalnutritionHousehold, Long> householdDao = null;
	private Dao<MalnutritionChild, Long> childDao = null;

	private Dao<MalnutritionObservation, Long> malnutritionObservationDao = null;

	public ClinicAdapter(Context context) throws SQLException {
		this(context, DATABASE_NAME, DATABASE_VERSION);
	}
	
	public ClinicAdapter(Context context, String databaseName, int databaseVersion) throws SQLException {
		super(context, databaseName, null, databaseVersion, org.msf.android.R.raw.ormlite_config);
		
		cohortDao = getDao(Cohort.class);
		observationDao = getDao(Observation.class);
		patientDao = getDao(Patient.class);
		conceptClassDao = getDao(ConceptClass.class);
		encounterDao = getDao(Encounter.class);
		encounterTypeDao = getDao(EncounterType.class);
		formDao = getDao(Form.class);
		locationDao = getDao(Location.class);
		personAddressDao = getDao(PersonAddress.class);
		conceptDao = getDao(Concept.class);
		formFieldDao = getDao(FormField.class);
		fieldTypeDao = getDao(FieldType.class);
		fieldDao = getDao(Field.class);
		userDao = getDao(User.class);
		locationTagDao = getDao(LocationTag.class);
		householdDao = getDao(MalnutritionHousehold.class);
		childDao = getDao(MalnutritionChild.class);
		malnutritionObservationDao = getDao(MalnutritionObservation.class);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {
		try {
			Log.i(ClinicAdapter.class.getName(), "onCreate");
			DaoManager.clearCache();
			
			TableUtils.createTable(connectionSource, ConceptClass.class);
			TableUtils.createTable(connectionSource, Cohort.class);
			TableUtils.createTable(connectionSource, Encounter.class);
			TableUtils.createTable(connectionSource, Observation.class);
			TableUtils.createTable(connectionSource, Patient.class);
			TableUtils.createTable(connectionSource, EncounterType.class);
			TableUtils.createTable(connectionSource, Form.class);
			TableUtils.createTable(connectionSource, FormField.class);
			TableUtils.createTable(connectionSource, Location.class);
			TableUtils.createTable(connectionSource, PersonAddress.class);
			TableUtils.createTable(connectionSource, Concept.class);
			TableUtils.createTable(connectionSource, FieldType.class);
			TableUtils.createTable(connectionSource, Field.class);
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, LocationTag.class);
			TableUtils.createTable(connectionSource, MalnutritionHousehold.class);
			TableUtils.createTable(connectionSource, MalnutritionChild.class);
			TableUtils.createTable(connectionSource, MalnutritionObservation.class);
		} catch (SQLException e) {
			Log.e(ClinicAdapter.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}	

	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(ClinicAdapter.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, ConceptClass.class, true);
			TableUtils.dropTable(connectionSource, Cohort.class, true);
			TableUtils.dropTable(connectionSource, Observation.class, true);
			TableUtils.dropTable(connectionSource, Patient.class, true);
			TableUtils.dropTable(connectionSource, Encounter.class, true);
			TableUtils.dropTable(connectionSource, EncounterType.class, true);
			TableUtils.dropTable(connectionSource, Form.class, true);
			TableUtils.dropTable(connectionSource, FormField.class, true);
			TableUtils.dropTable(connectionSource, Location.class, true);
			TableUtils.dropTable(connectionSource, PersonAddress.class, true);
			TableUtils.dropTable(connectionSource, Concept.class, true);
			TableUtils.dropTable(connectionSource, FieldType.class, true);
			TableUtils.dropTable(connectionSource, Field.class, true);
			TableUtils.dropTable(connectionSource, User.class, true);
			TableUtils.dropTable(connectionSource, LocationTag.class, true);
			TableUtils.dropTable(connectionSource, MalnutritionHousehold.class, true);
			TableUtils.dropTable(connectionSource, MalnutritionChild.class, true);
			TableUtils.dropTable(connectionSource, MalnutritionObservation.class, true);
			
			// after we drop the old databases, we create the new ones
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			Log.e(ClinicAdapter.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}
	
	public void persistConfig(MobileConfig mc) throws SQLException {
		ConnectionSource cs = getConnectionSource();
		
		TableUtils.clearTable(cs, Location.class);
		TableUtils.clearTable(cs, Concept.class);
		TableUtils.clearTable(cs, ConceptClass.class);
		TableUtils.clearTable(cs, EncounterType.class);
		TableUtils.clearTable(cs, Form.class);
		TableUtils.clearTable(cs, FormField.class);
		TableUtils.clearTable(cs, FieldType.class);
		
		for (Location l : mc.getLocations()) {
			getLocationDao().create(l);
		}
		for (Concept c : mc.getConcepts()) {
			getConceptDao().create(c);
		}
		for (ConceptClass cc : mc.getConceptClasses()) {
			getConceptClassDao().create(cc);
		}
		for (EncounterType et : mc.getEncounterTypes()) {
			getEncounterTypeDao().create(et);
		}
		for (Form f : mc.getForms()) {
			getFormDao().create(f);
		}
		for (FormField ff : mc.getFormFields()) {
			getFormFieldDao().create(ff);
		}
		for (FieldType ft : mc.getFieldTypes()) {
			getFieldTypeDao().create(ft);
		}
	}
	
	public static void getAdapterTest(Context context) throws SQLException {
		ClinicAdapter ca = OpenHelperManager.getHelper(context, ClinicAdapter.class);
		Dao<Cohort, Long> dao = ca.getCohortDao();
		List<Cohort> cohorts = dao.queryForAll();
		System.out.println(cohorts);
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our Cohort class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Cohort, Long> getCohortDao()  {
		return cohortDao;
	}
	
	public Dao<Observation, Long> getObservationDao()  {
		return observationDao;
	}
	
	public Dao<Patient, Long> getPatientDao()  {
		return patientDao;
	}

	public Dao<ConceptClass, Long> getConceptClassDao() {
		return conceptClassDao;
	}

	public Dao<Encounter, Long> getEncounterDao() {
		return encounterDao;
	}

	public Dao<EncounterType, Long> getEncounterTypeDao() {
		return encounterTypeDao;
	}

	public Dao<Form, Long> getFormDao() {
		return formDao;
	}

	public Dao<Location, Long> getLocationDao() {
		return locationDao;
	}

	public Dao<PersonAddress, Long> getPersonAddressDao() {
		return personAddressDao;
	}

	public Dao<Concept, Long> getConceptDao() {
		return conceptDao;
	}

	public Dao<FormField, Long> getFormFieldDao() {
		return formFieldDao;
	}

	public Dao<FieldType, Long> getFieldTypeDao() {
		return fieldTypeDao;
	}

	public Dao<Field, Long> getFieldDao() {
		return fieldDao;
	}

	public Dao<User, Long> getUserDao() {
		return userDao;
	}

	public void setUserDao(Dao<User, Long> userDao) {
		this.userDao = userDao;
	}

	public Dao<LocationTag, Long> getLocationTagDao() {
		return locationTagDao;
	}

	public void setLocationTagDao(Dao<LocationTag, Long> locationTagDao) {
		this.locationTagDao = locationTagDao;
	}
	
	public Dao<MalnutritionHousehold, Long> getHouseholdDao() {
		return householdDao;
	}

	public void setHouseholdDao(Dao<MalnutritionHousehold, Long> householdDao) {
		this.householdDao = householdDao;
	}
	
	public Dao<MalnutritionChild, Long> getChildDao() {
		return childDao;
	}

	public void setChildDao(Dao<MalnutritionChild, Long> childDao) {
		this.childDao = childDao;
	}

	public Dao<MalnutritionObservation, Long> getMalnutritionObservationDao() {
		return malnutritionObservationDao;
	}

	public void setMalnutritionObservationDao(
			Dao<MalnutritionObservation, Long> malnutritionObservationDao) {
		this.malnutritionObservationDao = malnutritionObservationDao;
	}
}
