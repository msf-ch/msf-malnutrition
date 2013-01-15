package org.msf.android.rest;

import java.io.IOException;

import org.msf.android.openmrs.ConceptDataType;
import org.msf.android.openmrs.OpenMRSObject;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonDatatypeDeserializer extends JsonDeserializer<ConceptDataType>{

	@Override
	public ConceptDataType deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		if (jp == null || jp.getText() == null || jp.getText().isEmpty()) {
			return null;
		} else {
			String uuid = (String)jp.readValueAs(java.util.Map.class).get(OpenMRSObject.UUID);
			
			return ConceptDataType.getType(uuid);
		}
	}

}
