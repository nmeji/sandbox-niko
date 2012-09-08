package com.ndroid.util;

import android.content.Context;
import android.os.Environment;

public class FileStorage {
	private boolean externalStorageAvailable;
	private boolean externalStorageWritable;
	
	public FileStorage() {
		String externalStorageState = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
			externalStorageAvailable = externalStorageWritable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState)) {
			externalStorageAvailable = true;
			externalStorageWritable = false;
		} else {
			externalStorageAvailable = externalStorageWritable = false;
		}
	}

	public boolean isExternalStorageAvailable() {
		return externalStorageAvailable;
	}

	public boolean isExternalStorageWritable() {
		return externalStorageWritable;
	}
	
	private String getCacheDir(boolean needsWritePermission, Context context) {
		if ((needsWritePermission && externalStorageWritable) || externalStorageAvailable) {
			return context.getExternalCacheDir().getAbsolutePath();
		} else {
			return context.getCacheDir().getAbsolutePath();
		}
	}
	
	public String getCacheDir(Context context) {
		return getCacheDir(false, context);
	}
	
	public String getWriteableCacheDir(Context context) {
		return getCacheDir(true, context);
	}
}
