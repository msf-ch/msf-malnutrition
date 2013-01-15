package org.msf.android.rest;

import java.io.IOException;

import org.msf.android.openmrs.ConceptDataType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonDatatypeSerializer extends JsonSerializer<ConceptDataType> {

	@Override
	public void serialize(ConceptDataType value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeString(value.getOpenmrsCode());
	}

}
