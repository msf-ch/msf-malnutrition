package org.msf.android.openmrs;

import org.msf.android.rest.JsonOpenMRSObjectDeserializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

//"uuid": "179f922d-29a2-4a6c-bd4b-017f74abfffe",
//"id": 15,
//"formUuid": "2472cfc0-11cd-4d20-bb7b-7707929251e5",
//"parentUuid": "3235ba28-f065-4b2d-87bd-6badc990a6fe",
//"fieldPart": "",
//"required": false,
//"sortWeight": 250.0,
//"fieldType": "8d5e7d7c-c2cc-11de-8d13-0010c6dffd0f",
//"fieldName": "PREGNANCY STATUS",
//"conceptUuid": "5272AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
//"tableName": "",
//"attributeName": "",
//"defaultValue": "",
//"selectMultiple": false

@DatabaseTable(tableName = "formfields")
public class FormField extends OpenMRSObject {
	@DatabaseField
	private Integer fieldNumber;
	@DatabaseField
	private String fieldPart; // string
	@DatabaseField
	private Integer pageNumber;
	@DatabaseField
	private Integer minOccurs;
	@DatabaseField
	private Integer maxOccurs;
	@DatabaseField
	private boolean required;
	@DatabaseField
	private Float sortWeight;

	// field values
	@DatabaseField
	@JsonIgnore
	@JsonDeserialize(using = JsonOpenMRSObjectDeserializer.class)
	private String fieldType;
	@DatabaseField
	private String fieldName;
	@DatabaseField
	private String conceptUuid;
	@DatabaseField
	private String tableName;
	@DatabaseField
	private String attributeName;
	@DatabaseField
	private String defaultValue;
	@DatabaseField
	private boolean selectMultiple; // boolean

	@JsonIgnore
	private Concept concept;

	/**
	 * Took this right from OpenMRS. Thank you OpenMRS devs! Sort order for the
	 * form fields in the schema. Attempts: 1) sortWeight 2) fieldNumber 3)
	 * fieldPart 4) fieldName
	 * 
	 * @param f
	 *            FormField to compare this object to
	 * @return -1, 0, or +1 depending on the difference between the FormFields
	 */
	@Override
	public int compareTo(OpenMRSObject formField) {
		FormField f = null;
		if (formField instanceof Field) {
			f = (FormField)formField;
		} else {
			return 0;
		}
		
		
		if (getSortWeight() != null || f.getSortWeight() != null) {
			if (getSortWeight() == null)
				return -1;
			if (f.getSortWeight() == null)
				return 1;
			int c = getSortWeight().compareTo(f.getSortWeight());
			if (c != 0)
				return c;
		}
		if (getPageNumber() != null || f.getPageNumber() != null) {
			if (getPageNumber() == null)
				return -1;
			if (f.getPageNumber() == null)
				return 1;
			int c = getPageNumber().compareTo(f.getPageNumber());
			if (c != 0)
				return c;
		}
		if (getFieldNumber() != null || f.getFieldNumber() != null) {
			if (getFieldNumber() == null)
				return -1;
			if (f.getFieldNumber() == null)
				return 1;
			int c = getFieldNumber().compareTo(f.getFieldNumber());
			if (c != 0)
				return c;
		}
		if (getFieldPart() != null || f.getFieldPart() != null) {
			if (getFieldPart() == null)
				return -1;
			if (f.getFieldPart() == null)
				return 1;
			int c = getFieldPart().compareTo(f.getFieldPart());
			if (c != 0)
				return c;
		}
		// if (getField() != null && f.getField() != null) {
		// int c = getField().getName().compareTo(f.getField().getName());
		// if (c != 0)
		// return c;
		// }
		if (getUuid() == null && f.getUuid() != null)
			return -1;
		if (getUuid() != null && f.getUuid() == null)
			return 1;
		if (getUuid() == null && f.getUuid() == null)
			return 1;

		return getUuid().compareTo(f.getUuid());

	}

	public Integer getFieldNumber() {
		return fieldNumber;
	}

	public void setFieldNumber(Integer fieldNumber) {
		this.fieldNumber = fieldNumber;
	}

	public String getFieldPart() {
		return fieldPart;
	}

	public void setFieldPart(String fieldPart) {
		this.fieldPart = fieldPart;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(Integer minOccurs) {
		this.minOccurs = minOccurs;
	}

	public Integer getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(Integer maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Float getSortWeight() {
		return sortWeight;
	}

	public void setSortWeight(Float sortWeight) {
		this.sortWeight = sortWeight;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isSelectMultiple() {
		return selectMultiple;
	}

	public void setSelectMultiple(boolean selectMultiple) {
		this.selectMultiple = selectMultiple;
	}

	public String getConceptUuid() {
		return conceptUuid;
	}

	public void setConceptUuid(String conceptUuid) {
		this.conceptUuid = conceptUuid;
	}

	public String toString() {
		return "FormField:: FieldName: " + getFieldName();
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}
}
