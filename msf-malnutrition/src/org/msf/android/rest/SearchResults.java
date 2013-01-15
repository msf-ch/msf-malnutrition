package org.msf.android.rest;

import java.util.List;
import java.util.Map;

import org.msf.android.openmrs.OpenMRSObject;

public class SearchResults<T> {
	public static final String RESULTS = "results";

	private List<T> results;
	private String ETag;
	
	public SearchResults() {
	}
	
	public List<T> getResults() {
		//return getList(RESULTS);
		return results;
	}
	
	public void setResults(List<T> results) {
		this.results = results;
	}

	public String getETag() {
		return ETag;
	}

	public void setETag(String eTag) {
		ETag = eTag;
	}
	
	/*
	public List<Ref> getRefs() {
		List<Object> rawResults = getResults();
		List<Ref> results = new ArrayList<Ref>();
		Map<String, Object> temp;
		Ref newRef;
		for (Object o : rawResults) {
			temp = (Map<String, Object>)o;
			newRef = new Ref(temp);
			results.add(newRef);
		}
		return results;
	}

	public <T extends OpenMRSObject> List<T> getResults(Class<T> clazz)
			throws Exception {
		List<Object> rawResults = getResults();
		List<T> results = new ArrayList<T>();
		for (Object o : rawResults) {
			Constructor<T> c = clazz.getConstructor(Map.class);
			T instance = c.newInstance((Map) o);
			results.add(instance);
		}
		return results;
	}*/
}
