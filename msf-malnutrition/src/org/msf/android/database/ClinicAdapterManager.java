package org.msf.android.database;

import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.msf.android.app.MSFClinicApp;

import com.j256.ormlite.dao.DaoManager;

public class ClinicAdapterManager {
	private static ClinicAdapter ca;
	private static Lock clinicLock;
	
	static {
		resetLock();
	}
	
	public static ClinicAdapter lockAndRetrieveClinicAdapter() {
		clinicLock.lock();
		
		if (ca == null || !ca.isOpen()) {
			setUpStaticAdapter();
		}
		return ca;
	}
	
	public static void unlock() {
		if (!ca.isOpen()) {
			setUpStaticAdapter();
		}
		clinicLock.unlock();
	}
	
	public static void reset() {
		setUpStaticAdapter();
		resetLock();
	}
	
	private static void setUpStaticAdapter() {
		releaseResources();
		try {
			DaoManager.clearDaoCache();
			ca = new ClinicAdapter(MSFClinicApp.getAppContext());
		} catch (SQLException e) {
			// TODO Handle failure somehow... fix db?
			e.printStackTrace();
		}
	}
	
	private static void releaseResources() {
		if (ca != null) {
			if (ca.isOpen()) {
				ca.close();
			}
			
			ca = null;
		}
		DaoManager.clearCache();
	}
	
	private static void resetLock() {
		clinicLock = new ReentrantLock();
	}
}
