package org.msf.android.rest;

import java.util.Arrays;
import java.util.List;

import org.msf.android.openmrs.OpenMRSObject;

public class UuidListTools {
	public static final String UUID_SEPARATOR = ",";

	public static String serializeUuidList(List<String> list) {
		StringBuffer sb = new StringBuffer();

		for (String s : list) {
			sb.append(s);
			sb.append(UUID_SEPARATOR);
		}
		
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String serializeUuidListFromObjects(List<OpenMRSObject> list) {
		StringBuffer sb = new StringBuffer();

		for (OpenMRSObject o : list) {
			if (o != null) {
				sb.append(o.getUuid());
				sb.append(UUID_SEPARATOR);
			}
		}

		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		
		return sb.toString();
	}

	public static List<String> deserializeUuidList(String serializedUuids) {
		if (serializedUuids == null) {
			return null;
		} else {
			return Arrays.asList(serializedUuids.split(UUID_SEPARATOR));
		}
	}
}
