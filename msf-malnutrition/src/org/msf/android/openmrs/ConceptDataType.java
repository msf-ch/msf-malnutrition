package org.msf.android.openmrs;

import java.util.HashMap;
import java.util.Map;

public enum ConceptDataType {
	BOOLEAN("8d4a5cca-c2cc-11de-8d13-0010c6dffd0f", "BIT"), CODED(
			"8d4a48b6-c2cc-11de-8d13-0010c6dffd0f", "CWE"), DATE(
			"8d4a505e-c2cc-11de-8d13-0010c6dffd0f", "DT"), DATETIME(
			"8d4a5af4-c2cc-11de-8d13-0010c6dffd0f", "TS"), DOCUMENT(
			"8d4a4e74-c2cc-11de-8d13-0010c6dffd0f", "RP"), NUMERIC(
			"8d4a4488-c2cc-11de-8d13-0010c6dffd0f", "NM"), TEXT(
			"8d4a4ab4-c2cc-11de-8d13-0010c6dffd0f", "ST"), TIME(
			"8d4a591e-c2cc-11de-8d13-0010c6dffd0f", "TM"), NA(
			"8d4a4c94-c2cc-11de-8d13-0010c6dffd0f", ""), RULE(
			"8d4a5e96-c2cc-11de-8d13-0010c6dffd0f", ""), STRUCTURED_NUMERIC(
			"8d4a606c-c2cc-11de-8d13-0010c6dffd0f", ""), COMPLEX(
			"8d4a6242-c2cc-11de-8d13-0010c6dffd0f", "");

	private String openmrsCode;
	private String hl7_code;

	private static Map<String, ConceptDataType> fieldTypeLookupMap;

	ConceptDataType(String openmrsCode, String hl7_code) {
		this.openmrsCode = openmrsCode;
		this.hl7_code = hl7_code;
	}

	public static ConceptDataType getType(String query) {
		if (fieldTypeLookupMap == null) {
			fieldTypeLookupMap = new HashMap<String, ConceptDataType>();
			ConceptDataType[] values = values();
			for (ConceptDataType f : values) {
				fieldTypeLookupMap.put(f.getOpenmrsCode(), f);
			}
		}
		return fieldTypeLookupMap.get(query);
	}

	public String getOpenmrsCode() {
		return openmrsCode;
	}

	public String getHl7_code() {
		return hl7_code;
	}
}
