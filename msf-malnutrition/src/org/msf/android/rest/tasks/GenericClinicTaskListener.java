package org.msf.android.rest.tasks;

public abstract interface GenericClinicTaskListener <T extends GenericClinicTask> {
	public void onStarted(T task);
	
	public void onFinished(T task);
	
	public void onProgressUpdate(T task);
}
