package org.msf.android.rest;

import java.net.HttpURLConnection;

import org.msf.android.openmrs.OpenMRSObject;


public class RestOperationResult<T> {
	private T objectReceived;
	private Object objectSent;
	private boolean success;
	private int responseCode;
	private String message;
	private Exception exceptionThrown;
	private String errorString;
	private RestError error;

	public RestOperationResult() {
	}

	public RestOperationResult(RestOperationResult<?> prevResult) {
		if (prevResult.getObjectReceived() == null) {
			try {
				setObjectReceived((T) prevResult.getObjectReceived());
			} catch (ClassCastException ex) {
				setObjectReceived(null);
			}
		}
		setSuccess(prevResult.success);
		setResponseCode(prevResult.getResponseCode());
		setMessage(prevResult.getMessage());
		setExceptionThrown(prevResult.getExceptionThrown());
	}

	public T getObjectReceived() {
		return objectReceived;
	}

	public void setObjectReceived(T result) {
		this.objectReceived = result;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Exception getExceptionThrown() {
		return exceptionThrown;
	}

	public void setExceptionThrown(Exception exceptionThrown) {
		if (exceptionThrown != null) {
			success = false;
		}
		this.exceptionThrown = exceptionThrown;
	}

	public String getErrorString() {
		return errorString;
	}

	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}

	public RestError getError() {
		return error;
	}

	public void setError(RestError error) {
		this.error = error;
	}

	public Object getObjectSent() {
		return objectSent;
	}

	public void setObjectSent(Object objectSent) {
		this.objectSent = objectSent;
	}

	public T getSentObjectWithRetrievedUuid() {
		try {
			OpenMRSObject objectSent = (OpenMRSObject) getObjectSent();
			OpenMRSObject objectReceived = (OpenMRSObject) getObjectReceived();
			objectSent.setUuid(objectReceived.getUuid());
			objectSent.setETag(objectReceived.getETag());
			return (T) objectSent;
		} catch (ClassCastException ex) {
			return null;
		}
	}
	
	public boolean isNotModified() {
		return getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED;
	}
}
