package com.ndroid.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;

import android.util.Log;

public class DirectoryManager {
	private static final String TAG = DirectoryManager.class.getSimpleName();
	
	/**
	 * Forks a polling thread that reduces the total size to 80% of the specified max size when the size exceeded.
	 * 
	 * @param directory
	 * @param maxFileSize
	 * @param pollInterval
	 * @param threadName
	 */
	public static void startMonitoring(File directory, long maxFileSize, long pollInterval, String threadName) {
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("File is not a directory.");
		}
		
		// TODO: max file size validation
		
		Thread maintenance = new Thread(new MemoryMaintenance(directory, maxFileSize, pollInterval), threadName);
		maintenance.setDaemon(true);
		maintenance.start();
	}
	
	private static class MemoryMaintenance implements Runnable {
		private final File directory;
		private final long maxFileSize;
		private final long pollInterval;
		
		MemoryMaintenance(File directory, long maxFileSize, long pollInterval) {
			this.directory = directory;
			this.maxFileSize = maxFileSize;
			this.pollInterval = pollInterval;
		}
		
		private long getFileSize(File file) throws IOException {
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			return raf.length();
		}
		
		private File[] sortByLastModified(File directory) {
			File[] files = directory.listFiles();
			Arrays.sort(files, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
				}
			});
			return files;
		}
		
		private void cleanup(long currentSize) {
			try {
				long sizeToFreeUp = currentSize - (long)(maxFileSize*0.8);
				
				File[] files = sortByLastModified(directory);
				int i = 0;
				for(long freedMemory=0; freedMemory<sizeToFreeUp && i<files.length; i++) {
					long fileSize = getFileSize(files[i]);
					if(files[i].delete()) {
						freedMemory += fileSize;
					}
				}
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
			}
		}
		
		public void run() {
			while(true) {
				try {
					long currentSize = getFileSize(directory);
					if (currentSize > maxFileSize) {
						cleanup(currentSize);
					}
				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				}
				
				try {
					Thread.sleep(pollInterval);
				} catch(InterruptedException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				}
			}
		}
	}
}
