package org.msf.android.rest;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.commons.io.IOUtils;
import org.msf.android.net.OpenMRSConnectionManager;
import org.msf.android.openmrs.Concept;
import org.msf.android.openmrs.ConceptClass;
import org.msf.android.openmrs.Encounter;
import org.msf.android.openmrs.EncounterType;
import org.msf.android.openmrs.Field;
import org.msf.android.openmrs.FieldType;
import org.msf.android.openmrs.Form;
import org.msf.android.openmrs.Location;
import org.msf.android.openmrs.LocationTag;
import org.msf.android.openmrs.MobileConfig;
import org.msf.android.openmrs.OpenMRSObject;
import org.msf.android.openmrs.Patient;

import android.util.Base64;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ClinicRestClient {
	public static final Map<String, String> QUERY_REP_FULL = getURLArgumentsMap(new String[] { "v", "full" });
	public static final Map<String, String> QUERY_REP_REF = getURLArgumentsMap(new String[] { "v", "ref" });
	
	/*Rest V1 URLs*/
	public static final String REST_V1_PATH = "ws/rest/v1/";
	public static final String PATIENT_REST_URL = REST_V1_PATH + "patient";
	public static final String USER_REST_URL = REST_V1_PATH + "user";
	public static final String PERSON_REST_URL = REST_V1_PATH + "person";
	public static final String ENCOUNTER_REST_URL = REST_V1_PATH + "encounter";
	public static final String ENCOUNTER_TYPE_REST_URL = REST_V1_PATH
			+ "encountertype";
	public static final String LOCATION_REST_URL = REST_V1_PATH + "location";
	public static final String CONCEPT_REST_URL = REST_V1_PATH + "concept";
	public static final String CONCEPT_CLASS_REST_URL = REST_V1_PATH
			+ "conceptclass";
	public static final String CONCEPT_DATATYPE_REST_URL = REST_V1_PATH
			+ "conceptdatatype";
	public static final String FORM_REST_URL = REST_V1_PATH + "form";
	public static final String FIELD_TYPE_REST_URL = REST_V1_PATH + "fieldtype";
	public static final String FIELD_REST_URL = REST_V1_PATH + "field";
	public static final String LOCATION_TAG_REST_URL = REST_V1_PATH + "locationtag";

	/*MSF Connector URLs*/
	public static final String MSF_CONNECTOR_PATH = "ws/rest/msfconnector/";
	public static final String MOBILE_CONFIG = MSF_CONNECTOR_PATH + "mconfig";
	public static final String MOBILE_PATIENT = MSF_CONNECTOR_PATH + "mpatient";
	public static final String MOBILE_ENCOUNTER = MSF_CONNECTOR_PATH
			+ "mencounter";

	private static Map<Class<?>, String> pathsForClasses;
	static {
		LinkedHashMap<Class<?>, String> pfc = new LinkedHashMap<Class<?>, String>();
		pfc.put(Patient.class, PATIENT_REST_URL);
		// pfc.put(User.class, USER_REST_URL); MUST IMPLEMENT USER
		// pfc.put(Person.class, PERSON_REST_URL); PROBABLY WON'T USE PERSON
		pfc.put(Encounter.class, ENCOUNTER_REST_URL);
		pfc.put(EncounterType.class, ENCOUNTER_TYPE_REST_URL);
		pfc.put(Location.class, LOCATION_REST_URL);
		pfc.put(Patient.class, PATIENT_REST_URL);
		pfc.put(Concept.class, CONCEPT_REST_URL);
		pfc.put(ConceptClass.class, CONCEPT_CLASS_REST_URL);
		// pfc.put(ConceptDatatype.class, CONCEPT_DATATYPE_REST_URL);
		pfc.put(Form.class, FORM_REST_URL);
		pfc.put(FieldType.class, FIELD_TYPE_REST_URL);
		pfc.put(Field.class, FIELD_REST_URL);
		pfc.put(LocationTag.class, LOCATION_TAG_REST_URL);

		pfc.put(MobileConfig.class, MOBILE_CONFIG);
		pathsForClasses = pfc;
	}

	private OpenMRSConnectionManager connectionManager;

	private ObjectMapper mapper;

	public enum REQUEST_TYPE {
		GET, POST, DELETE
	};

	public ClinicRestClient() {
		this(OpenMRSConnectionManager.getDefaultOpenMRSConnectionManager());
	}

	public ClinicRestClient(OpenMRSConnectionManager cm) {
		connectionManager = cm;
		initialize();
	}

	private void initialize() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
	}

	public <T extends OpenMRSObject> RestOperationResult<T> getSubResource(
			Class<T> clazzToGet, List<String> parentResources,
			String uuidToGet, Map<String, String> urlQueries) {
		List<String> uuidsToGet = Collections.singletonList(uuidToGet);
		List<RestOperationResult<T>> result = getSubResourcesList(clazzToGet,
				parentResources, uuidsToGet, urlQueries);
		return result.get(0);
	}

	public <T extends OpenMRSObject> List<RestOperationResult<T>> getSubResourcesList(
			Class<T> clazzToGet, List<String> parentResources,
			List<String> uuidsToGet, Map<String, String> urlQueries) {
		StringBuilder sb = new StringBuilder();
		List<RestOperationResult<T>> results = new ArrayList<RestOperationResult<T>>();
		if (parentResources != null) {
			for (String s : parentResources) {
				sb.append(s);
				sb.append("/");
			}
		}
		String prefix = sb.toString();
		String path;
		for (String uuidToGet : uuidsToGet) {
			if (uuidToGet != null) {
				path = prefix + uuidToGet;
				RestOperationResult<T> temp = getFromOpenMRSServer(clazzToGet,
						path, urlQueries, null);
				results.add(temp);
			}
		}
		return results;
	}

	public <T extends OpenMRSObject> SearchResults<T> getQuery(Class<T> clazz,
			String path, String query, String rep) throws URISyntaxException {
		Map<String, String> queryMap = new HashMap<String, String>();
		if (query != null || rep != null) {
			if (query != null) {
				queryMap.put("q", query);
			}
			if (rep != null) {
				queryMap.put("v", rep);
			}
		}

		return getQuery(clazz, path, queryMap);
	}

	public <T extends OpenMRSObject> SearchResults<T> getQuery(Class<T> clazz,
			String path, Map<String, String> urlQueries)
			throws URISyntaxException {
		RestOperationResult<LinkedHashMap> ror = getFromOpenMRSServer(
				LinkedHashMap.class, path, urlQueries, null);
		List rawResults = (List) ror.getObjectReceived().get(
				SearchResults.RESULTS);
		List<T> results = new ArrayList<T>();
		for (Object o : rawResults) {
			results.add(mapper.convertValue(o, clazz));
		}

		SearchResults<T> searchResults = new SearchResults<T>();
		searchResults.setResults(results);
		return searchResults;
	}

	public <T extends OpenMRSObject> List<RestOperationResult<T>> getOpenMRSObjectsBatch(
			Class<T> clazz, String path, Map<String, String> urlQueries,
			List<String> uuids) {
		// TODO: MAKE THIS WORK

		return null;
	}

	public <T extends OpenMRSObject> RestOperationResult<T> getResource(
			Class<T> clazz, String path, String uuid,
			Map<String, String> urlQueries, String eTag) {
		String path2 = path + "/" + uuid;
		return getFromOpenMRSServer(clazz, path2, urlQueries, eTag);
	}

	public <T> RestOperationResult<T> getFromOpenMRSServer(Class<T> clazz,
			String path, Map<String, String> urlQueries, String eTag) {
		return executeRestConnection(clazz, path, urlQueries, null, "GET", eTag);
	}

	public <T> RestOperationResult<T> createOrUpdateOnOpenMRSServer(
			Class<T> clazz, String path, Map<String, String> urlQueries,
			Object body) {
		if (body instanceof OpenMRSObject
				&& ((OpenMRSObject) body).getUuid() != null
				&& !((OpenMRSObject) body).getUuid().isEmpty()) {
			// if we have an OpenMRSObject with a non-empty UUID, update it
			return updateOnOpenMRSServer(clazz, path, urlQueries, (OpenMRSObject) body);
		} else {
			// otherwise create it!
			return createOnOpenMRSServer(clazz, path, urlQueries, body);
		}
	}

	public <T> RestOperationResult<T> createOnOpenMRSServer(Class<T> clazz,
			String path, Map<String, String> urlQueries, Object body) {
		return executeRestConnection(clazz, path, urlQueries, body, "POST", null);
	}

	public <T> RestOperationResult<T> updateOnOpenMRSServer(Class<T> clazz,
			String path, Map<String, String> urlQueries, OpenMRSObject body) {
		if (body.getUuid() == null || body.getUuid().isEmpty()) {
			throw new IllegalArgumentException(
					"No UUID defined for OpenMRSObject: " + body);
		}
		String subResourcePath = path + "/" + body.getUuid();
		return executeRestConnection(clazz, subResourcePath, urlQueries, body,
				"POST", body.getETag());
	}

	public <T> RestOperationResult<T> executeRestConnection(Class<T> clazz,
			String path, Map<String, String> urlQueries, Object body,
			String method, String eTag) {
		boolean first = true;
		StringBuilder sb = new StringBuilder(connectionManager.getBaseURL());
		sb.append(path);
		if (urlQueries != null && !urlQueries.isEmpty()) {
			sb.append("?");
			for (Entry<String, String> e : urlQueries.entrySet()) {
				if (e.getKey() == null || e.getValue() == null)
					continue;

				if (first) {
					first = false;
				} else {
					sb.append("&");
				}

				sb.append(URLEncoder.encode(e.getKey()));
				sb.append("=");
				sb.append(URLEncoder.encode(e.getValue()));
			}
		}

		String bodyToString = "";
		HttpURLConnection conn = null;
		RestOperationResult<T> result = new RestOperationResult<T>();
		result.setObjectSent(body);

		try {
			if (body != null) {
				bodyToString = mapper.writeValueAsString(body);
			} else {
				bodyToString = null;
			}

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();

			// get BASIC auth string
			String loginPass = connectionManager.getLogin() + ":"
					+ String.valueOf(connectionManager.getPassword());
			String base64loginPass = Base64.encodeToString(
					loginPass.getBytes(), Base64.DEFAULT).replaceAll("\\n", "");
			String authorizationString = "Basic " + base64loginPass;
			conn.setRequestProperty("Authorization", authorizationString);

			conn.setRequestMethod(method);
			conn.setConnectTimeout(2000);
			conn.setDoInput(true);
			conn.setDoOutput(bodyToString != null);

			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			if (eTag == null && body != null && body instanceof OpenMRSObject && ((OpenMRSObject)body).getETag() != null) {
				//No explicit eTag but the request entity has one
				eTag = ((OpenMRSObject)body).getETag();
			}
			
			if (eTag != null) {
				conn.setRequestProperty("If-None-Match", eTag);
			}
			// if (body instanceof OpenMRSObject) {
			// conn.addRequestProperty("If-None-Match",
			// ((OpenMRSObject)body).getETag());
			// }

			conn.connect();

			if (bodyToString != null) {
				OutputStreamWriter ow = new OutputStreamWriter(
						conn.getOutputStream());
				ow.write(bodyToString);
				ow.flush();

				System.out.println(bodyToString);
			}

			System.out.println(url.toString());
			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);
			result.setResponseCode(responseCode);

			String responseMessage = conn.getResponseMessage();
			System.out.println("Response message: " + responseMessage);
			result.setMessage(responseMessage);

			System.out.println("Response header fields: "
					+ conn.getHeaderFields());

			if (responseCode == HttpURLConnection.HTTP_ACCEPTED
					|| responseCode == HttpURLConnection.HTTP_CREATED
					|| responseCode == HttpURLConnection.HTTP_RESET
					|| responseCode == HttpURLConnection.HTTP_OK) {

				InputStream inputStream;
				String encoding = conn.getContentEncoding();
				if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
					inputStream = new GZIPInputStream(conn.getInputStream());
				} else if (encoding != null
						&& encoding.equalsIgnoreCase("deflate")) {
					inputStream = new InflaterInputStream(
							conn.getInputStream(), new Inflater(true));
				} else {
					inputStream = conn.getInputStream();
				}

				String wholeResponse = IOUtils.toString(inputStream, "UTF-8");
				System.out.println("Whole response: " + wholeResponse);
				//this is good for debugging but costs more resources
				//than passing the inputStream to the mapper.
				//change for deployment?

				T openMRSObject = mapper.readValue(wholeResponse, clazz);
				if (result != null) {
					System.out.println("Object received: " + result.toString());
				}
				if (openMRSObject instanceof OpenMRSObject) {
					String eTagFromHeader = conn
							.getHeaderField("ETag");
					((OpenMRSObject) openMRSObject).setETag(eTagFromHeader);
				}
				result.setObjectReceived(openMRSObject);
				result.setSuccess(true);
			} else if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
				result.setSuccess(true);
				System.out.println("No change via ETag: " + sb.toString());
			} else {
				// you've got an error
				result.setSuccess(false);

				String errorResponse = IOUtils.toString(
						(InputStream) conn.getErrorStream(), "UTF-8");
				System.out.println("Rest error: " + errorResponse);
				result.setErrorString(errorResponse);

				RestError errorObject = mapper.readValue(errorResponse,
						RestError.class);
				result.setError(errorObject);
			}

			return result;
		} catch (Exception e) {
			result.setExceptionThrown(e);
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return result;
	}

	public ObjectMapper getMapper() {
		return mapper;
	}

	public static Map<String, String> getURLArgumentsMap(String... strings) {
		Map<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < strings.length; i += 2) {
			result.put(strings[i], strings[i + 1] != null ? strings[i + 1]
					: null);
		}
		return result;
	}

	public static String getDefaultRestLocation(Class<?> clazz) {
		return pathsForClasses.get(clazz);
	}
}
