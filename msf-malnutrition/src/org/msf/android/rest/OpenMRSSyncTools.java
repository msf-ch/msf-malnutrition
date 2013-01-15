package org.msf.android.rest;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.openmrs.Concept;
import org.msf.android.openmrs.ConceptClass;
import org.msf.android.openmrs.ConceptDataType;
import org.msf.android.openmrs.EncounterType;
import org.msf.android.openmrs.Field;
import org.msf.android.openmrs.Form;
import org.msf.android.openmrs.Location;
import org.msf.android.openmrs.LocationTag;
import org.msf.android.openmrs.OpenMRSObject;
import org.msf.android.openmrs.Ref;

import android.util.Log;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

public class OpenMRSSyncTools {
	ClinicRestClient restClient;
	ClinicAdapter adapter;

	public OpenMRSSyncTools() {
	}

	public void syncMetaData() throws SQLException, URISyntaxException {
		try {
			initialize();

			syncLocationTags();
			syncLocations();
			syncConceptClasses();
			syncEncounterTypes();
			syncForms();
			syncFields();

			syncConceptsFromFields();
		} finally {
			close();
		}
	}

	private void initialize() throws SQLException {
		restClient = new ClinicRestClient();
		adapter = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
	}

	private void close() {
		restClient = null;

		if (adapter != null)
			ClinicAdapterManager.unlock();
	}

	private <T extends OpenMRSObject> void syncMetaData(Class<T> clazz,
			boolean clearLocalEntries, boolean updateLocalEntries,
			boolean createNewLocalEntriesFromServer,
			boolean updateServerEntries, boolean postNewEntriesToServer)
			throws SQLException, URISyntaxException {
		Dao<T, String> dao = adapter.getDao(clazz);

		// queryBuilder just retrieves the fields we want for these purposes, we
		// don't need the whole object
		QueryBuilder<T, String> queryBuilder = dao.queryBuilder();
		queryBuilder = queryBuilder.selectColumns("_id", "uuid", "eTag");

		// completely drop the table, faster than clear()
		if (clearLocalEntries) {
			TableUtils.dropTable(adapter.getConnectionSource(), clazz, true);
			TableUtils.createTable(adapter.getConnectionSource(), clazz);
		}

		// Updating local database from server
		if ((updateLocalEntries && !clearLocalEntries)
				|| createNewLocalEntriesFromServer) { // Either you're only
														// making new
			// entries or you're only
			// updating entries and didn't
			// clear the database (in which
			// case there are no entries to
			// update
			if (clearLocalEntries) {
				// it's faster to just get everything all at once because there
				// will never be any ETag matching if you cleared the database
				SearchResults<T> results = restClient.getQuery(
						clazz,
						ClinicRestClient.getDefaultRestLocation(clazz),
						ClinicRestClient.getURLArgumentsMap(new String[] { "v",
								"full" }));

				List<T> resultsList = results.getResults();
				for (T item : resultsList) {
					dao.create(item);
				}
			} else {
				// Get refs from the server so that we can collect the UUIDs
				SearchResults<Ref> refs = restClient.getQuery(Ref.class,
						ClinicRestClient.getDefaultRestLocation(clazz), null);
				List<String> uuidsFromServer = new ArrayList<String>();
				List<String> uuidsFromLocalDb = new ArrayList<String>();
				for (Ref r : refs.getResults()) {
					uuidsFromServer.add(r.getUuid());
				}

				// get objects that have been sent
				queryBuilder.setWhere(queryBuilder.where().isNotNull("uuid"));
				PreparedQuery<T> nonNullUuidQuery = queryBuilder.prepare();
				CloseableWrappedIterable<T> nonNullIterable = dao
						.getWrappedIterable(nonNullUuidQuery);
				RestOperationResult<T> result;

				try {
					for (T localEntry : nonNullIterable) {
						// get the local uuids no matter what
						uuidsFromLocalDb.add(localEntry.getUuid());
						if (updateLocalEntries) {
							// get the resource from the server
							result = restClient.getResource(clazz,
									ClinicRestClient
											.getDefaultRestLocation(clazz),
									localEntry.getUuid(),
									ClinicRestClient.QUERY_REP_FULL, localEntry
											.getETag());
							// update if necessary
							if (result.isSuccess() && !result.isNotModified()
									&& result.getObjectReceived() != null) {
								// make sure the new object has it's database ID
								// to store
								T objectReceived = result.getObjectReceived();
								objectReceived.setDatabaseId(localEntry
										.getDatabaseId());
								dao.update(result.getObjectReceived());
							} else if (result.isNotModified()) {
								System.out
										.println("HTTP_NOT_MODIFIED acknowledged ("
												+ clazz.getName() + ")");
							}
						}
					}
				} finally {
					nonNullIterable.close();
				}

				if (createNewLocalEntriesFromServer) {
					List<String> uuidsOnServerNotLocal = new ArrayList<String>(
							uuidsFromServer);
					uuidsOnServerNotLocal.removeAll(uuidsFromLocalDb);

					for (String serverUuid : uuidsOnServerNotLocal) {
						// retrieve the server resource
						result = restClient.getResource(clazz,
								ClinicRestClient.getDefaultRestLocation(clazz),
								serverUuid, ClinicRestClient.QUERY_REP_FULL,
								null);

						if (result.isSuccess()
								&& result.getObjectReceived() != null) {
							dao.create(result.getObjectReceived());
						} else {
							Log.e(this.getClass().getName(),
									"Create new local entry failed: "
											+ clazz.getName());
						}
					}
				}
			}
		}

		// send new entries/changes to server
		if ((updateServerEntries || postNewEntriesToServer)
				&& !clearLocalEntries) {

			List<T> entriesToPost;
			if (updateServerEntries && postNewEntriesToServer) {
				// Post everything
				entriesToPost = dao.queryForAll();
			} else {
				CloseableWrappedIterable<T> localItems = dao
						.getWrappedIterable();
				entriesToPost = new ArrayList<T>();
				try {
					for (T localItem : localItems) {
						boolean existsOnServer = (localItem.getUuid() != null);
						if (updateServerEntries && existsOnServer) { // could be
																		// one
																		// clause,
																		// just
																		// clearer
																		// like
																		// this
							// only add existing
							entriesToPost.add(localItem);
						} else if (postNewEntriesToServer && !existsOnServer) {
							// only add those that didn't exist
							entriesToPost.add(localItem);
						}
					}
				} finally {
					localItems.close();
				}
			}

			// ...and send the ones that should be sent. REALLY need to handle
			// errors here, track which worked
			RestOperationResult<T> result;
			for (T entryToPost : entriesToPost) {
				result = restClient.createOrUpdateOnOpenMRSServer(clazz,
						ClinicRestClient.getDefaultRestLocation(clazz), null,
						entryToPost);
				if (result.isSuccess() && !result.isNotModified()) {
					entryToPost.setETag(result.getObjectReceived().getETag());
					entryToPost.setUuid(result.getObjectReceived().getUuid());
				}
				dao.update(entryToPost);
			}
		}
	}

	public void syncLocationTags() throws SQLException, URISyntaxException {
		syncMetaData(LocationTag.class, false, true, true, false, false);
	}

	public void syncLocations() throws SQLException, URISyntaxException {
		// don't delete database and send the new locations!
		syncMetaData(Location.class, false, true, true, false, true);
	}

	public void syncConceptClasses() throws SQLException, URISyntaxException {
		syncMetaData(ConceptClass.class, false, true, true, false, false);
	}

	public void syncEncounterTypes() throws SQLException, URISyntaxException {
		syncMetaData(EncounterType.class, false, true, true, false, false);
	}

	public void syncForms() throws SQLException, URISyntaxException {
		syncMetaData(Form.class, false, true, true, false, false);
	}

	public void syncFields() throws SQLException, URISyntaxException {
		syncMetaData(Field.class, false, true, true, false, false);
	}

	public void syncConceptsFromFields() throws SQLException,
			URISyntaxException {
		final String conceptPath = ClinicRestClient
				.getDefaultRestLocation(Concept.class);

		Dao<Field, Long> fieldDao = adapter.getFieldDao();
		Dao<Concept, Long> conceptDao = adapter.getConceptDao();
		List<Field> fields = fieldDao.queryForAll();

		List<String> conceptUuidsFromFields = new ArrayList<String>();
		List<String> conceptAnswerUuids = new ArrayList<String>();

		for (Field f : fields) {
			if (f.concept != null && !f.concept.isEmpty()
					&& !conceptUuidsFromFields.contains(f.concept)) {
				conceptUuidsFromFields.add(f.concept);
			}
		}

		List<RestOperationResult<Concept>> conceptsFromServer = new ArrayList<RestOperationResult<Concept>>();

		Concept existingConcept;
		RestOperationResult<Concept> retrievedConcept = null;
		for (String conceptUuid : conceptUuidsFromFields) {
			List<Concept> dbConcept = conceptDao
					.queryForEq("uuid", conceptUuid);
			if (dbConcept.size() > 0) {
				// see if we need to update the local db
				existingConcept = dbConcept.get(0);
				retrievedConcept = restClient.getResource(Concept.class,
						conceptPath, conceptUuid,
						ClinicRestClient.QUERY_REP_FULL,
						existingConcept.getETag());
			} else {
				existingConcept = null;
				retrievedConcept = restClient.getResource(Concept.class,
						conceptPath, conceptUuid,
						ClinicRestClient.QUERY_REP_FULL, null);
			}

			if (!retrievedConcept.isSuccess()) {
				System.out.println("Failure to retrieve concept: "
						+ conceptUuid);
				continue;
			}

			if (retrievedConcept.isNotModified()) {
				// no update needed, just debug message acknowledging that
				System.out.println("Concept not changed: "
						+ (existingConcept != null ? existingConcept.getName()
								: conceptUuid));
				continue;
			}

			if (existingConcept != null) {
				// update the concept in the database
				retrievedConcept.getObjectReceived().setDatabaseId(
						existingConcept.getDatabaseId());
			}

			conceptsFromServer.add(retrievedConcept);
		}

		// build a list of uuids that aren't from fields already, avoiding
		// redundant uuids within the list
		for (RestOperationResult<Concept> ror : conceptsFromServer) {
			try {
				if (ror.isSuccess()) {
					for (String uuid : ror.getObjectReceived().getAnswers()) {
						if (!conceptAnswerUuids.contains(uuid)
								&& !conceptUuidsFromFields.contains(uuid)) {
							conceptAnswerUuids.add(uuid);
						}
					}
				}
			} catch (NullPointerException ex) {
			}
		}

		for (String conceptAnswerUuid : conceptAnswerUuids) {
			List<Concept> dbConcept = conceptDao.queryForEq("uuid",
					conceptAnswerUuid);
			if (dbConcept.size() > 0) {
				// see if we need to update the local db
				existingConcept = dbConcept.get(0);
				retrievedConcept = restClient.getResource(Concept.class,
						conceptPath, conceptAnswerUuid,
						ClinicRestClient.QUERY_REP_FULL,
						existingConcept.getETag());
			} else {
				existingConcept = null;
				retrievedConcept = restClient.getResource(Concept.class,
						conceptPath, conceptAnswerUuid,
						ClinicRestClient.QUERY_REP_FULL, null);
			}

			if (!retrievedConcept.isSuccess()) {
				System.out.println("Failure to retrieve concept: "
						+ conceptAnswerUuid);
				continue;
			}

			if (retrievedConcept.isNotModified()) {
				// no update needed, just debug message acknowledging that
				System.out.println("Concept not changed: "
						+ (existingConcept != null ? existingConcept.getName()
								: conceptAnswerUuid));
				continue;
			}

			if (existingConcept != null) {
				// update the concept in the database
				retrievedConcept.getObjectReceived().setDatabaseId(
						existingConcept.getDatabaseId());
			}

			conceptsFromServer.add(retrievedConcept);
		}

		Concept conceptForDb;
		for (RestOperationResult<Concept> ror : conceptsFromServer) {
			conceptForDb = ror.getObjectReceived();
			if (conceptForDb.getDatabaseId() != null) {
				conceptDao.update(conceptForDb);
			} else {
				conceptDao.create(conceptForDb);
			}
		}
	}

	public void submitUnsentPatientsAndEncounters(ClinicAdapter adapter) {

	}
}
