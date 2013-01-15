package org.msf.android.openmrs;

import java.util.List;

public class MobileConfig extends OpenMRSObject {
	private List<Location> locations;
	private List<Concept> concepts;
	private List<ConceptClass> conceptClasses;
	private List<EncounterType> encounterTypes;
	private List<Form> forms;
	private List<FormField> formFields;
	private List<FieldType> fieldTypes;
	
	public List<Location> getLocations() {
		return locations;
	}
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}
	public List<Concept> getConcepts() {
		return concepts;
	}
	public void setConcepts(List<Concept> concepts) {
		this.concepts = concepts;
	}
	public List<ConceptClass> getConceptClasses() {
		return conceptClasses;
	}
	public void setConceptClasses(List<ConceptClass> conceptClasses) {
		this.conceptClasses = conceptClasses;
	}
	public List<EncounterType> getEncounterTypes() {
		return encounterTypes;
	}
	public void setEncounterTypes(List<EncounterType> encounterTypes) {
		this.encounterTypes = encounterTypes;
	}
	public List<Form> getForms() {
		return forms;
	}
	public void setForms(List<Form> forms) {
		this.forms = forms;
	}
	public List<FormField> getFormFields() {
		return formFields;
	}
	public void setFormFields(List<FormField> formFields) {
		this.formFields = formFields;
	}
	public List<FieldType> getFieldTypes() {
		return fieldTypes;
	}
	public void setFieldTypes(List<FieldType> fieldTypes) {
		this.fieldTypes = fieldTypes;
	}
}
