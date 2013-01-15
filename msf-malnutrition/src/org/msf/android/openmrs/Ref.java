package org.msf.android.openmrs;

import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable(tableName = "refs")
public class Ref extends OpenMRSObject {
	public static final String DISPLAY = "display";
	
	private String display;
	
	public Ref() {
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
	
}
