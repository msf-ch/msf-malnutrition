package org.msf.android.openmrs;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.msf.android.rest.JsonDatatypeDeserializer;
import org.msf.android.rest.JsonDatatypeSerializer;
import org.msf.android.rest.JsonOpenMRSObjectArrayDeserializer;
import org.msf.android.rest.JsonOpenMRSObjectDeserializer;
import org.msf.android.rest.UuidListTools;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

//"uuid": "300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
//"conceptId": 300,
//"name": "BLOOD TYPING",
//"datatype": "8d4a48b6-c2cc-11de-8d13-0010c6dffd0f",
//"conceptClass": "8d4907b2-c2cc-11de-8d13-0010c6dffd0f",
//"set": false,
//"version": "",
//"answerConcepts": "690AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,694AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,692AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,699AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,696AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,1230AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,701AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,1231AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
//"retired": false

@DatabaseTable(tableName = "concepts")
public class Concept extends OpenMRSObject {
	@DatabaseField
	@JsonDeserialize(using = ConceptNameDeserializer.class)
	private String name;
	@DatabaseField(dataType = DataType.ENUM_STRING)
	@JsonSerialize(using = JsonDatatypeSerializer.class)
	@JsonDeserialize(using = JsonDatatypeDeserializer.class)
	private ConceptDataType datatype;
	@DatabaseField
	@JsonDeserialize(using = JsonOpenMRSObjectDeserializer.class)
	private String conceptClass;
	@DatabaseField
	private boolean set;
	@DatabaseField
	private String version;
	@DatabaseField
	private boolean retired;
	@DatabaseField
	@JsonDeserialize(using = JsonOpenMRSObjectArrayDeserializer.class)
	private String answers;

	public List<String> getAnswers() {
		if (answers == null || answers.isEmpty()) {
			return Collections.emptyList();
		} else {
			return UuidListTools.deserializeUuidList(answers);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonGetter(value = "datatype")
	public ConceptDataType getDatatype() {
		return datatype;
	}

	@JsonSetter(value = "datatype")
	public void setDatatype(ConceptDataType datatype) {
		this.datatype = datatype;
	}

	public String getConceptClass() {
		return conceptClass;
	}

	public void setConceptClass(String conceptClass) {
		this.conceptClass = conceptClass;
	}

	public boolean isSet() {
		return set;
	}

	public void setSet(boolean set) {
		this.set = set;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isRetired() {
		return retired;
	}

	public void setRetired(boolean retired) {
		this.retired = retired;
	}

	@Override
	public String toString() {
		return "Concept:: Name: " + getName() + ", Datatype: " + getDatatype();
	}

	public static class ConceptNameDeserializer extends StdDeserializer<String>
			implements ContextualDeserializer {

		public ConceptNameDeserializer() {
			super(String.class);
		}

		@Override
		public String deserialize(JsonParser parser,
				DeserializationContext context) throws IOException,
				JsonProcessingException {
			String result = null;
			try {
				JsonToken currentToken = parser.getCurrentToken();
				if (currentToken == JsonToken.START_OBJECT) {
					HashMap map = parser.readValueAs(HashMap.class);
					result = (String) map.get("name");
				} else if (currentToken == JsonToken.VALUE_STRING) {
					result = parser.getText();
				}
			} catch (JsonParseException e) {
				e.printStackTrace();
				result = null;
			} catch (JsonMappingException ex) {
				ex.printStackTrace();
				result = null;
			} catch (Exception e) {
				e.printStackTrace();
				result = null;
			}
			return result;
		}

		@Override
		public JsonDeserializer<?> createContextual(
				DeserializationContext ctxt, BeanProperty property)
				throws JsonMappingException {
			return this;
		}

	}
}