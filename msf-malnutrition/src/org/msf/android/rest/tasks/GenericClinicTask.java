package org.msf.android.rest.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.os.AsyncTask;

public abstract class GenericClinicTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {

	private boolean started = false;
	private boolean finished = false;

	private String description;

	private List<GenericClinicTaskListener<? extends GenericClinicTask>> listeners = new ArrayList<GenericClinicTaskListener<? extends GenericClinicTask>>();

	public GenericClinicTask() {
		this("");
	}

	public GenericClinicTask(String description) {
		super();
		setDescription(description);
	}

	@Override
	protected synchronized void onPreExecute() {
		started = true;
		for (GenericClinicTaskListener listener : listeners) {
			try {
				listener.onStarted(this);
			} catch (Exception e) {
				continue;
			}
		}
	}

	protected synchronized void onPostExecute(Result result) {
		finished = true;
		for (GenericClinicTaskListener listener : listeners) {
			try {
				listener.onFinished(this);
			} catch (Exception e) {
				continue;
			}
		}
	}

	protected synchronized void onProgressUpdate(Progress[] values) {
		for (GenericClinicTaskListener listener : listeners) {
			try {
				listener.onProgressUpdate(this);
			} catch (Exception e) {
				continue;
			}
		}
	}

	public synchronized boolean addListener(GenericClinicTaskListener listener) {
		return listeners.add(listener);
	}

	public synchronized boolean removeListener(
			GenericClinicTaskListener listener) {
		return listeners.remove(listener);
	}
	
	public synchronized void clearListeners() {
		listeners.clear();
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isFinished() {
		return finished;
	}

	public String getDescription() {
		return description;
	}

	public synchronized void setDescription(String description) {
			this.description = description;
	}
}
