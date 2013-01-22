/**
 * 
 */
package org.msf.android.test;

import java.io.File;

import android.content.Context;

/**
 * @author Nicholas Wilkie
 *
 */
public class TestConstants {
	public static String getDebugOutputDirectory(Context context) {
		File f = context.getDir("DEBUG", Context.MODE_WORLD_WRITEABLE);
		return f.getPath();
	}
}
