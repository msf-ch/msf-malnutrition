package org.msf.android.rest;

import java.io.IOException;
import java.util.HashMap;

import org.msf.android.openmrs.OpenMRSObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;


/**
 * Need to use this class so that you can read either UUIDs or an object. Just 
 * take out the UUID if it's the full object
 * 
 * @author nwilkie
 */
public class JsonOpenMRSObjectDeserializer extends StdDeserializer<String> implements ContextualDeserializer {

	public JsonOpenMRSObjectDeserializer() {
		super(String.class);
	}

	@Override
	public String deserialize(JsonParser parser, DeserializationContext context)
			throws IOException, JsonProcessingException {
		String result = null;
		try {
			JsonToken currentToken = parser.getCurrentToken();
			if (currentToken == JsonToken.START_OBJECT) {
				HashMap map = parser.readValueAs(HashMap.class);
				result = (String)map.get(OpenMRSObject.UUID);
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
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
			BeanProperty property) throws JsonMappingException {
		System.out.println(property.getName());
		return new JsonOpenMRSObjectDeserializer();
	}

}
