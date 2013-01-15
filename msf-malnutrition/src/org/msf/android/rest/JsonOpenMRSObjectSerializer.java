package org.msf.android.rest;

import java.io.IOException;

import org.msf.android.openmrs.OpenMRSObject;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

public class JsonOpenMRSObjectSerializer extends JsonSerializer<OpenMRSObject> implements ContextualSerializer {

	public JsonOpenMRSObjectSerializer() {	}
	
	@Override
	public void serialize(OpenMRSObject value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeString(value.getUuid());
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov,
			BeanProperty property) throws JsonMappingException {
		return new JsonOpenMRSObjectSerializer();
	}
	
	
}
