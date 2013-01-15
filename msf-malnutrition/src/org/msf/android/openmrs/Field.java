package org.msf.android.openmrs;

import java.util.List;

import org.msf.android.rest.JsonOpenMRSObjectArrayDeserializer;
import org.msf.android.rest.JsonOpenMRSObjectDeserializer;
import org.msf.android.rest.UuidListTools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/*
 * uuid
display
name
description
fieldType
concept
tableName
attributeName
defaultValue
selectMultiple
answers
retired
auditInfo
links	
 */


@DatabaseTable(tableName = "fields")
public class Field extends OpenMRSObject{
	@DatabaseField
	public String name;
	@DatabaseField
	public String description;
	@DatabaseField
	@JsonDeserialize(using = JsonOpenMRSObjectDeserializer.class)
	public String fieldType;
	@DatabaseField
	@JsonDeserialize(using = JsonOpenMRSObjectDeserializer.class)
	public String concept;
	@DatabaseField
	public String tableName;
	@DatabaseField
	public String attributeName;
	@DatabaseField
	public String defaultValue;
	@DatabaseField
	public boolean selectMultiple;
	@DatabaseField
	@JsonIgnore
	public String answers;
	
	@JsonSetter(value = "answers")
	@JsonDeserialize(using = JsonOpenMRSObjectArrayDeserializer.class)
	public void setAnswers(String answers) {
		this.answers = answers;
	}
}
