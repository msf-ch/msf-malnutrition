package org.msf.android.openmrs;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.j256.ormlite.field.DatabaseField;

public class OpenMRSObject implements Comparable<OpenMRSObject>{
	public static final String UUID = "uuid";
	public static final String VOIDED = "voided";
	public static final String LINKS = "links";
	
	
	@DatabaseField(generatedId = true, columnName="_id")
	@JsonIgnore
	private Long databaseId;
	@DatabaseField
	@JsonIgnore
	private String uuid;
	@DatabaseField(canBeNull = true)
	@JsonIgnore
	private String eTag;
	@DatabaseField
	@JsonIgnore
	private Date lastSyncedDate;
	@DatabaseField
	@JsonIgnore
	private Date locallyCreatedDate;
	@DatabaseField
	@JsonIgnore
	private String display;
	
	public OpenMRSObject() {
	}
	
	@JsonIgnore
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	
	@JsonIgnore
	public String getETag() {
		return eTag;
	}

	public void setETag(String eTag) {
		this.eTag = eTag;
	}
	
	@JsonIgnore
	public String getUuid() {
		return uuid;
	}
	@JsonSetter(value = "uuid")
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@JsonIgnore
	public Date getLastSyncedDate() {
		return lastSyncedDate;
	}
	@JsonIgnore
	public void setLastSyncedDate(Date lastSyncedDate) {
		this.lastSyncedDate = lastSyncedDate;
	}

	@JsonIgnore
	public Long getDatabaseId() {
		return databaseId;
	}

	@JsonIgnore
	public void setDatabaseId(Long databaseId) {
		this.databaseId = databaseId;
	}
	
	@JsonIgnore
	public boolean isSentToRemoteServer() {
		return getUuid() != null;
	}

	@Override
	public int compareTo(OpenMRSObject another) {
		if (this.getUuid() == null) {
			if (another.getUuid() == null) {
				return 0;
			} else {
				return -1;
			}
		}
		
		return this.getUuid().compareTo(another.getUuid());
	}

	@JsonIgnore
	public Date getLocallyCreatedDate() {
		return locallyCreatedDate;
	}

	@JsonIgnore
	public void setLocallyCreatedDate(Date locallyCreatedDate) {
		this.locallyCreatedDate = locallyCreatedDate;
	}
}
