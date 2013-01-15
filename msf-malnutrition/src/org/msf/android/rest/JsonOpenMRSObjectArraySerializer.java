package org.msf.android.rest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

public class JsonOpenMRSObjectArraySerializer extends JsonSerializer<String> implements ContextualSerializer {

	public JsonOpenMRSObjectArraySerializer() {
	}

	@Override
	public void serialize(String value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
	}

	@Override
	public JsonSerializer<String> createContextual(SerializerProvider prov,
			BeanProperty property) throws JsonMappingException {
		return null;
	}
}
