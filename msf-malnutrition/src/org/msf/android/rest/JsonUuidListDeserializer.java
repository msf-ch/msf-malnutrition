package org.msf.android.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonUuidListDeserializer extends JsonDeserializer<List<String>>{

	public static final String UUID_SEPARATOR = ",";
	
	@Override
	public List<String> deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		String text = jp.getText();
		List<String> result = Arrays.asList(text.split(UUID_SEPARATOR));
		return result;
	}

}
