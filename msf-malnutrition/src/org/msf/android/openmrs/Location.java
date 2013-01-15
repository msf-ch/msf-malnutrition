package org.msf.android.openmrs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.msf.android.app.MSFClinicApp;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.rest.JsonOpenMRSObjectArrayDeserializer;
import org.msf.android.rest.JsonOpenMRSObjectDeserializer;
import org.msf.android.rest.UuidListTools;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.j256.ormlite.field.DatabaseField;

//"uuid": "8d6c993e-c2cc-11de-8d13-0010c6dffd0f",
//"name": "Unknown Location",
//"description": null,
//"address1": "",
//"address2": "",
//"cityVillage": "",
//"stateProvince": "",
//"country": "",
//"postalCode": "",
//"latitude": null,
//"longitude": null,
//"countyDistrict": null,
//"address3": null,
//"address4": null,
//"address5": null,
//"address6": null,
//"tags": [],
//"parentLocation": null,
//"childLocations": [],
//"retired": false,
//"links": [{
//    "uri": "http://msfcholera.ocg.msf.org:8080/openmrs/ws/rest/v1/location/8d6c993e-c2cc-11de-8d13-0010c6dffd0f",
//    "rel": "self"
//}, {
//    "uri": "http://msfcholera.ocg.msf.org:8080/openmrs/ws/rest/v1/location/8d6c993e-c2cc-11de-8d13-0010c6dffd0f?v=full",
//    "rel": "full"
//}]

public class Location extends OpenMRSObject {
	@DatabaseField
	private String name;
	@DatabaseField
	private String description;
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
	private String latitude;
	@DatabaseField
	private String longitude;
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
	@JsonIgnore
	private String tags;
	@DatabaseField
	private String parentLocation;
	@DatabaseField
	@JsonIgnore
	private String childLocations;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	@JsonIgnore
	public List<String> getTags() {
		return UuidListTools.deserializeUuidList(this.tags);
	}

	@JsonIgnore
	public void setTags(List<String> tags) {
		this.tags = UuidListTools.serializeUuidList(tags);
	}

	@JsonSetter(value = "tags")
	@JsonDeserialize(using = JsonOpenMRSObjectArrayDeserializer.class)
	public void setTagsString(String tags) {
		this.tags = tags;
	}

	@JsonGetter(value = "tags")
	@JsonDeserialize(using = JsonOpenMRSObjectArrayDeserializer.class)
	public List<LocationTag> getTagsRefs() {
		List<LocationTag> result = new ArrayList<LocationTag>();
		ClinicAdapter ca = null;
		try {
			ca = new ClinicAdapter(MSFClinicApp.getAppContext());
			for (String tagUuid : getTags()) {
				List<LocationTag> tagsFromDb = ca.getLocationTagDao()
						.queryBuilder().selectColumns("uuid", "name").where()
						.eq("uuid", tagUuid).query();

				result.addAll(tagsFromDb);
			}
		} catch (SQLException e) {
			System.out.println("Couldn't get tag due to SQLException: "
					+ e.getMessage());
		} finally {
			if (ca != null) {
				ca.close();
			}
		}

		return result;
	}

	public String getParentLocation() {
		return parentLocation;
	}

	@JsonDeserialize(using = JsonOpenMRSObjectDeserializer.class)
	public void setParentLocation(String parentLocation) {
		this.parentLocation = parentLocation;
	}

	@JsonIgnore
	public List<String> getChildLocations() {
		return UuidListTools.deserializeUuidList(childLocations);
	}

	@JsonIgnore
	public void setChildLocations(List<String> childLocations) {
		this.childLocations = UuidListTools.serializeUuidList(childLocations);
	}

	@JsonProperty(value = "childLocations")
	@JsonDeserialize(using = JsonOpenMRSObjectArrayDeserializer.class)
	public void setChildLocationsString(String childLocations) {
		this.childLocations = childLocations;
	}
}
