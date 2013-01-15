package org.msf.android.openmrs;

import java.io.IOException;
import java.util.Date;

import org.msf.android.rest.JsonOpenMRSObjectDeserializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "obs")
public class Observation extends OpenMRSObject {
	@DatabaseField
	@JsonDeserialize(contentUsing = JsonOpenMRSObjectDeserializer.class)
	private String person;
	@DatabaseField
	@JsonDeserialize(contentUsing = JsonOpenMRSObjectDeserializer.class)
	private Date obsDatetime;
	@DatabaseField
	@JsonDeserialize(contentUsing = JsonOpenMRSObjectDeserializer.class)
	private String concept;

	@JsonIgnore
	@DatabaseField(foreign = true)
	private Encounter encounter;
	@DatabaseField
	private Date encounterDate;
	@DatabaseField
	private String fieldName;
	@DatabaseField
	private boolean voided;

	// Internal value represenations
	@DatabaseField
	@JsonIgnore
	private String stringValue;
	@DatabaseField
	@JsonIgnore
	private Date dateValue;
	@DatabaseField
	@JsonIgnore
	private Integer intValue;
	@DatabaseField
	@JsonIgnore
	private Float floatValue;
	@DatabaseField
	@JsonIgnore
	private Boolean booleanValue;

	// not standard
	// @DatabaseField(dataType = DataType.ENUM_STRING)
	// @JsonSerialize(using = JsonDatatypeSerializer.class)
	// @JsonDeserialize(using = JsonDatatypeDeserializer.class)
	// private ConceptDataType conceptDataType;

	// public Integer getPatientId() {
	// return patientId;
	// }
	//
	// public void setPatientId(Integer patientId) {
	// this.patientId = patientId;
	// }
	//
	// public Float getValueNumeric() {
	// return Float.parseFloat(getValue());
	// }
	//
	// public void setValueNumeric(Float valueNumeric) {
	// this.value = Float.toString(valueNumeric);
	// }

	/**
	 * Generally it's best to use this method for general tasks, since it
	 * prioritizes which type to return. There shouldn't really be more than one
	 * value type in the db, but this is a safeguard just in case.
	 * 
	 * @return The value of the observation
	 */
	@JsonProperty(value = "value")
	public Object getValue() {
		// prioritize in order that you'd like the data to be used. So string
		// representation last.
		if (getDateValue() != null) {
			return getDateValue();
		} else if (getFloatValue() != null) {
			return getFloatValue();
		} else if (getIntValue() != null) {
			return getIntValue();
		} else if (getBooleanValue() != null) {
			return getBooleanValue();
		} else if (getStringValue() != null) {
			return getStringValue();
		}

		// null if all else fails
		return null;
	}

	/**
	 * @param value
	 *            The value you want to set. Currently date and string
	 *            supported.
	 */
	@JsonProperty(value = "value")
	@JsonDeserialize(using = ObsValueJsonDeserializer.class)
	public void setValue(Object value) {
		if (value instanceof Date) {
			dateValue = (Date) value;
		} else if (value instanceof Float) {
			floatValue = (Float) value;
		} else if (value instanceof Integer) {
			intValue = (Integer) value;
		} else if (value instanceof String) {
			stringValue = (String) value;
		} else if (value instanceof Boolean) {
			booleanValue = (Boolean) value;
		} else {
			stringValue = value.toString();
		}
	}

	public Date getEncounterDate() {
		return encounterDate;
	}

	public void setEncounterDate(Date encounterDate) {
		this.encounterDate = encounterDate;
	}

	//
	// public Integer getValueInt() {
	// return Integer.parseInt(getValue());
	// }
	//
	// public void setValueInt(Integer valueInt) {
	// this.value = Integer.toString(valueInt);
	// }

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean isVoided() {
		return voided;
	}

	public void setVoided(boolean voided) {
		this.voided = voided;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public Date getObsDatetime() {
		return obsDatetime;
	}

	public void setObsDatetime(Date obsDatetime) {
		this.obsDatetime = obsDatetime;
	}

	public String getPerson() {
		if (person != null && !person.isEmpty()) {
			return person;
		} else {
			return encounter.getPatientUuid();
		}
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	@JsonIgnore
	public String getStringValue() {
		return stringValue;
	}

	@JsonIgnore
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	@JsonIgnore
	public Date getDateValue() {
		return dateValue;
	}

	@JsonIgnore
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	@JsonIgnore
	public Integer getIntValue() {
		return intValue;
	}

	@JsonIgnore
	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}

	@JsonIgnore
	public Float getFloatValue() {
		return floatValue;
	}

	@JsonIgnore
	public void setFloatValue(Float floatValue) {
		this.floatValue = floatValue;
	}

	@JsonIgnore
	public Boolean getBooleanValue() {
		return booleanValue;
	}

	@JsonIgnore
	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public static class ObsValueJsonDeserializer extends
			StdDeserializer<Object> {

		protected ObsValueJsonDeserializer() {
			super(Object.class);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Object deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			if (jp.getCurrentToken() == JsonToken.VALUE_STRING) {
				// Dates are expressed as strings
				try {
					Date d = jp.readValueAs(Date.class);
					if (d != null)
						return d;
				} catch (Exception ex) {
				}
				return jp.getText();
			} else if (jp.getCurrentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
				return jp.getFloatValue();
			} else if (jp.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
				return jp.getIntValue();
			} else if (jp.getCurrentToken() == JsonToken.VALUE_TRUE
					|| jp.getCurrentToken() == JsonToken.VALUE_FALSE) {
				return jp.getBooleanValue();
			} else if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
				// Really shouldn't get here, but just in case...
				return null;
			} else if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
				// Not supported yet, not sure if it's even necessary
				throw new UnsupportedOperationException(
						"Receiving objects as obs value is not yet supported. Stick with raw types.");
			} else if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
				throw new UnsupportedOperationException(
						"Receiving arrays as obs value is not yet supported. Stick with raw types.");
			}

			throw new IllegalArgumentException(
					"Somehow a token was passed that was not readable by the program: "
							+ jp.getCurrentToken());
		}
	}
}
