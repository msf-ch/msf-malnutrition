package org.msf.android.rest.tasks;

import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;

public class OpenMRSComQueue {
	private static OpenMRSComQueue sharedOpenMRSComQueue;
	
	private static Queue<GenericClinicTask> queue = new LinkedList<GenericClinicTask>();
	private static GenericClinicTask activeTask;
	private boolean initialized = false;
	private boolean autoStartTopOfQueue = true;
	private Activity activity;
	
	public OpenMRSComQueue() {
		this.activity = activity;
		initialize();
	}
	
	private void initialize() {
		queue = new LinkedList<GenericClinicTask>();
		
		initialized = true;
	}
	
	public static OpenMRSComQueue getSharedOpenMRSQueue() {
		if (sharedOpenMRSComQueue != null) {
			sharedOpenMRSComQueue = new OpenMRSComQueue();
		}
		return sharedOpenMRSComQueue;
	}
	
	private Queue getQueue() {
		if (!initialized) {
			throw new IllegalStateException();
		}
		return queue;
	}
	
	
	public boolean addTask(GenericClinicTask task) {
		return getQueue().add(task);
	}

	public boolean isAutoStartTopOfQueue() {
		return autoStartTopOfQueue;
	}

	public void setAutoStartTopOfQueue(boolean autoStartTopOfQueue) {
		this.autoStartTopOfQueue = autoStartTopOfQueue;
	}
}
