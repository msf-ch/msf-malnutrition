package org.msf.android.openmrs;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "addresses")
public class PersonAddress extends OpenMRSObject {
	@DatabaseField(foreign = true) 
	public Patient patient;
	@DatabaseField
	private boolean preferred;
	@DatabaseField
	private String address1;
	@DatabaseField
	private String address2;
	@DatabaseField
	private String cityVillage;
	@DatabaseField
	private String stateProvince;
	@DatabaseField
	private String country;
	@DatabaseField
	private String postalCode;
	@DatabaseField
	private String countyDistrict;
	@DatabaseField
	private String address3;
	@DatabaseField
	private String address4;
	@DatabaseField
	private String address5;
	@DatabaseField
	private String address6;
	@DatabaseField
	private Date startDate;
	@DatabaseField
	private Date endDate;
	@DatabaseField
	private String latitude;
	@DatabaseField
	private String longitude;
	@DatabaseField
	private boolean voided;
	
	
	
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public boolean isPreferred() {
		return preferred;
	}
	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCityVillage() {
		return cityVillage;
	}
	public void setCityVillage(String cityVillage) {
		this.cityVillage = cityVillage;
	}
	public String getStateProvince() {
		return stateProvince;
	}
	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCountyDistrict() {
		return countyDistrict;
	}
	public void setCountyDistrict(String countyDistrict) {
		this.countyDistrict = countyDistrict;
	}
	public String getAddress3() {
		return address3;
	}
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	public String getAddress4() {
		return address4;
	}
	public void setAddress4(String address4) {
		this.address4 = address4;
	}
	public String getAddress5() {
		return address5;
	}
	public void setAddress5(String address5) {
		this.address5 = address5;
	}
	public String getAddress6() {
		return address6;
	}
	public void setAddress6(String address6) {
		this.address6 = address6;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public boolean isVoided() {
		return voided;
	}
	public void setVoided(boolean voided) {
		this.voided = voided;
	}
}
