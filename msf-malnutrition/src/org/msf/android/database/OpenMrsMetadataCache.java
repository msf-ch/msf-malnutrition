package org.msf.android.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.msf.android.openmrs.Concept;
import org.msf.android.openmrs.EncounterType;
import org.msf.android.openmrs.Form;
import org.msf.android.openmrs.Location;
import org.msf.android.openmrs.User;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

public class OpenMrsMetadataCache {
	private static List<Concept> concepts = new ArrayList<Concept>();
	private static List<Location> locations = new ArrayList<Location>();
	private static List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
	private static List<Form> forms = new ArrayList<Form>();
	private static List<User> users = new ArrayList<User>();

	public static void refreshConcepts(ClinicAdapter ca) throws SQLException {
		concepts = ca.getConceptDao().queryForAll();
	}

	public static List<Concept> getCachedConcepts(ClinicAdapter ca)
			throws SQLException {
		//maybe make this conditional
		refreshConcepts(ca);

		return concepts;
	}
	
	public static Concept getConcept(String uuid) {
		//look in the cache
		for (Concept c : concepts) {
			if (c.getUuid().equals(uuid)) {
				return c;
			}
		}
		
		return null;
	}
	
	public static Concept getConcept(ClinicAdapter ca, String uuid) throws SQLException {
		if (uuid == null) {
			return null;
		}
		
		//look in the cache
		Concept storedConcept = getConcept(uuid);
		if (storedConcept != null) {
			return storedConcept;
		}
		
		
		//look in the db
		Dao<Concept, Long> cDao = ca.getConceptDao();
		PreparedQuery<Concept> pq = cDao.queryBuilder().where().eq("uuid", uuid).prepare();
		List<Concept> searchedConcepts = cDao.query(pq);
		if (searchedConcepts.size() > 0) {
			Concept result = searchedConcepts.get(0);
			concepts.add(result);
			return result;
		}
		
		return null;
	}
	
	public static void buildLocationCache(ClinicAdapter ca) {
		locations.clear();
		try {
			locations.addAll(ca.getLocationDao().queryForAll());
		} catch (SQLException e) {
		}
	}
	
	public static Location getLocation(String uuid) {
		//look in the cache
		for (Location l : locations) {
			if (l.getUuid().equals(uuid)) {
				return l;
			}
		}
		
		return null;
	}
	
	public static Location getLocation(ClinicAdapter ca, String uuid) throws SQLException {
		if (uuid == null) {
			return null;
		}
		
		//look in the cache
		for (Location l : locations) {
			if (l.getUuid().equals(uuid)) {
				return l;
			}
		}
		
		//look in the db
		Dao<Location, Long> lDao = ca.getLocationDao();
		PreparedQuery<Location> pq = lDao.queryBuilder().where().eq("uuid", uuid).prepare();
		List<Location> searchedLocations = lDao.query(pq);
		if (searchedLocations.size() > 0) {
			Location result = searchedLocations.get(0);
			locations.add(result);
			return result;
		}
		
		return null;
	}
	
	public static void buildEncounterTypeCache(ClinicAdapter ca) {
		encounterTypes.clear();
		try {
			encounterTypes.addAll(ca.getEncounterTypeDao().queryForAll());
		} catch (SQLException e) {
		}
	}
	
	public static EncounterType getEncounterType(String uuid) {
		//look in the cache
		for (EncounterType c : encounterTypes) {
			if (c.getUuid().equals(uuid)) {
				return c;
			}
		}
		
		return null;
	}
	
	public static EncounterType getEncounterType(ClinicAdapter ca, String uuid) throws SQLException {
		if (uuid == null) {
			return null;
		}
		
		//look in the cache
		for (EncounterType et : encounterTypes) {
			if (et.getUuid().equals(uuid)) {
				return et;
			}
		}
		
		//look in the db
		Dao<EncounterType, Long> etDao = ca.getEncounterTypeDao();
		PreparedQuery<EncounterType> pq = etDao.queryBuilder().where().eq("uuid", uuid).prepare();
		List<EncounterType> searchedEncounterTypes = etDao.query(pq);
		if (searchedEncounterTypes.size() > 0) {
			EncounterType result = searchedEncounterTypes.get(0);
			encounterTypes.add(result);
			return result;
		}
		
		return null;
	}
	
	public static void buildFormsCache(ClinicAdapter ca) {
		forms.clear();
		try {
			forms.addAll(ca.getFormDao().queryForAll());
		} catch (SQLException e) {
		}
	}
	
	public static Form getForm(String uuid) {
		//look in the cache
		for (Form c : forms) {
			if (c.getUuid().equals(uuid)) {
				return c;
			}
		}
		
		return null;
	}
	
	public static Form getForm(ClinicAdapter ca, String uuid) throws SQLException {
		if (uuid == null) {
			return null;
		}
		
		//look in the cache
		for (Form f : forms) {
			if (f.getUuid().equals(uuid)) {
				return f;
			}
		}
		
		//look in the db
		Dao<Form, Long> cDao = ca.getFormDao();
		PreparedQuery<Form> pq = cDao.queryBuilder().where().eq("uuid", uuid).prepare();
		List<Form> searchedForms = cDao.query(pq);
		if (searchedForms.size() > 0) {
			Form result = searchedForms.get(0);
			forms.add(result);
			return result;
		}
		
		return null;
	}
	
	public static void buildUserCache(ClinicAdapter ca) {
		users.clear();
		try {
			users.addAll(ca.getUserDao().queryForAll());
		} catch (SQLException e) {
		}
	}
	
	public static User getUser(String uuid) {
		//look in the cache
		for (User c : users) {
			if (c.getUuid().equals(uuid)) {
				return c;
			}
		}
		
		return null;
	}
	
	public static User getUser(ClinicAdapter ca, String uuid) throws SQLException {
		if (uuid == null) {
			return null;
		}
		
		//look in the cache
		for (User u : users) {
			if (u.getUuid().equals(uuid)) {
				return u;
			}
		}
		
		//look in the db
		Dao<User, Long> uDao = ca.getUserDao();
		PreparedQuery<User> pq = uDao.queryBuilder().where().eq("uuid", uuid).or().eq("person", uuid).prepare();
		List<User> searchedUsers = uDao.query(pq);
		if (searchedUsers.size() > 0) {
			User result = searchedUsers.get(0);
			users.add(result);
			return result;
		}
		
		return null;
	}
}
