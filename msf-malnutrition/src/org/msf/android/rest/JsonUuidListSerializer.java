package org.msf.android.rest;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonUuidListSerializer extends JsonSerializer<List<String>>{

	@Override
	public void serialize(List<String> value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		StringBuffer sb = new StringBuffer();
		for(String s : value) {
			sb.append(s);
			sb.append(JsonUuidListDeserializer.UUID_SEPARATOR);
		}
		jgen.writeString(sb.toString());
	}

}
