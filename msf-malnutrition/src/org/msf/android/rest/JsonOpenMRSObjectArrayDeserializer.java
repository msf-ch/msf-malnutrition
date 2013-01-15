package org.msf.android.rest;

import java.io.IOException;
import java.util.Iterator;

import org.msf.android.openmrs.OpenMRSObject;
import org.msf.android.utilities.MSFCommonUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JsonOpenMRSObjectArrayDeserializer extends StdDeserializer<String>
		implements ContextualDeserializer {

	public JsonOpenMRSObjectArrayDeserializer() {
		super(String.class);
	}

	@Override
	public String deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Iterator<OpenMRSObject> it = jp.readValuesAs(OpenMRSObject.class);
		StringBuffer sb = new StringBuffer();

		OpenMRSObject o;
		while (it.hasNext()) {
			o = it.next();
			String uuid = o.getUuid();
			sb.append(uuid);
			sb.append(UuidListTools.UUID_SEPARATOR);
		}
		//and just take off that last comma...
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	@Override
	public JsonDeserializer<String> createContextual(
			DeserializationContext ctxt, BeanProperty property)
			throws JsonMappingException {
		return this;
	}
}
