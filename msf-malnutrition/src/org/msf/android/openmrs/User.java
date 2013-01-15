package org.msf.android.openmrs;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User extends OpenMRSObject {

	@DatabaseField
	public String username;
	@DatabaseField
	public String systemId;
	//userProperties, contains loginattempts?
	@DatabaseField
	public String person;
	//privileges lots of these
	//roles lots of these too
	//allRoles too many
	//proficientLocales don't know what this is
	@DatabaseField
	public String secretQuestion;
	@DatabaseField
	public boolean retired;
	
}
