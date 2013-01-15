package org.msf.android.utilities;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public final class FileUtils {
	private final static String tag = "FileUtils";

	// Storage paths

	public static final boolean deleteFolder(String path) {
		// not recursive
		if (path != null && storageReady()) {
			File dir = new File(path);
			if (dir.exists() && dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (File file : files) {
					if (!file.delete()) {
						Log.i(tag, "Failed to delete " + file);
					}
				}
			}
			return dir.delete();
		} else {
			return false;
		}
	}

	// Deal with exceptions?
	public static final void deleteFolderRecursively(File fileOrDirectory)
			throws IOException {
		if (fileOrDirectory != null && storageReady()) {
			if (fileOrDirectory.isDirectory())
				for (File child : fileOrDirectory.listFiles())
					deleteFolderRecursively(child);
			fileOrDirectory.delete();
		}
	}

	public static final void deleteFolderContents(File directory)
			throws IOException {
		if (directory.exists() && directory.isDirectory()) {
			for (File child : directory.listFiles()) {
				if (child.isDirectory()) {
					deleteFolderRecursively(child);
				} else {
					child.delete();
				}
			}
		}
	}

	public static final boolean createFolder(String path) {
		if (storageReady()) {
			boolean made = true;
			File dir = new File(path);
			if (!dir.exists()) {
				made = dir.mkdirs();
			}
			return made;
		} else {
			return false;
		}
	}

	public static final boolean deleteFile(String path) {
		if (storageReady()) {
			File f = new File(path);
			return f.delete();
		} else {
			return false;
		}
	}

	public static boolean storageReady() {
		String cardstatus = Environment.getExternalStorageState();
		if (cardstatus.equals(Environment.MEDIA_REMOVED)
				|| cardstatus.equals(Environment.MEDIA_UNMOUNTABLE)
				|| cardstatus.equals(Environment.MEDIA_UNMOUNTED)
				|| cardstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			return false;
		} else {
			return true;
		}
	}
}
