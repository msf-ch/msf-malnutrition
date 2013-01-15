package org.msf.android.utilities;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    public static final String database_password = "Geneva9090";
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starting....");
		writeConfigFile("ormlite_config.txt");
		System.out.println("Ended...");
	}
}
