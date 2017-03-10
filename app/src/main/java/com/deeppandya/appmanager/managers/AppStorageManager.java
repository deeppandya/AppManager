package com.deeppandya.appmanager.managers;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.deeppandya.appmanager.model.AppModel;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class AppStorageManager {
	private final static String TAG = AppStorageManager.class.getSimpleName();

	public static class StorageState {
		private long totalSize;
		private long freeSize;
		private long usedSize;

		public long getTotalSize() {
			return totalSize;
		}

		public void setTotalSize(long totalSize) {
			this.totalSize = totalSize;
		}

		public long getFreeSize() {
			return freeSize;
		}

		public void setFreeSize(long freeSize) {
			this.freeSize = freeSize;
		}

		public long getUsedSize() {
			return usedSize;
		}

		public void setUsedSize(long usedSize) {
			this.usedSize = usedSize;
		}
	}

	public static class StorageResult{
		public StorageState state;
		public Downloads downloads;
		public long selectedRecoverable = 0;
		public long totalRecoverable = 0;
		public List<AppModel> apps;
	}

	public static class Downloads{
		public long total;
		public long pictures;
		public long videos;
		public long music;
		public long apks;

		public long getOthersSize(){
			return total-pictures-videos-music-apks;
		}
	}

	private AppStorageManager() {
	}

	@SuppressWarnings("deprecation")
	public static StorageState getStorageState() {
		long freeStorage, totalStorage;

		StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());

		totalStorage = (long)stat.getBlockCount() * (long)stat.getBlockSize();
		freeStorage = (long)stat.getAvailableBlocks() * (long)stat.getBlockSize();
		long usedStorage = totalStorage - freeStorage;

		StorageState state = new StorageState();

		state.setFreeSize(freeStorage);
		state.setTotalSize(totalStorage);
		state.setUsedSize(usedStorage);

		return state;
	}

	public static boolean deleteRecursive(File fileOrDirectory) {
		if(fileOrDirectory==null)
			return false;

		if (fileOrDirectory.isDirectory()&&fileOrDirectory.listFiles()!=null&&fileOrDirectory.listFiles().length>0)
			for (File child : fileOrDirectory.listFiles())
				deleteRecursive(child);

		return fileOrDirectory.delete();
	}

	public static long getAPKsSizeIn(File dir) {
		return getSizeOf(getAPKsIn(dir));
	}

	/*public static long getAllAPKsSize() {
		return getAPKsSizeIn(Environment.getExternalStorageDirectory());
	}*/

	private static long getSizeOf(List<File> files){
		long totaleSize=0;

		for (File file : files) {
			totaleSize+=file.length();
		}

		return totaleSize;
	}

	private static long getSizeOfDir(File dir){
		long totaleSize=0;

		if(dir==null)
			return 0;

		final File[] listFiles = dir.listFiles();

		if(listFiles==null)
			return 0;

		for (File listFile : listFiles) {
			if (listFile.isDirectory())
				totaleSize += (listFile.length() + getSizeOfDir(listFile));
			else
				totaleSize += listFile.length();
		}

		return totaleSize;
	}

	public static List<File> getAPKsIn(File dir){
		return getFilesWithExtension(dir, "apk");
	}

	public static List<File> getDownloadFiles(){
		return getAllFilesInDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
	}

	public static long getDownloadFilesSize(){
		return getSizeOf(getDownloadFiles());
	}

	public static List<File> getAllFilesInDir(File dir){
		return getFilesWithExtension(dir, null);
	}

	public static List<File> getFilesWithExtension(File dir, String ext) {
		List<File> extFiles = new ArrayList<>();

		if(dir==null)
			return extFiles;

		File[] listFile = dir.listFiles();

		if (listFile != null) {
			for (File aListFile : listFile) {

				if (aListFile.isDirectory()) {
					getFilesWithExtension(aListFile, ext);
				} else {
					if (ext == null || aListFile.getName().endsWith(ext)) {
						extFiles.add(aListFile);
					}
				}
			}
		}

		return extFiles;
	}

	public static List<File> getFilesWithType(File dir, String type) {
		List<File> typeFiles = new ArrayList<>();

		File[] listFile = dir.listFiles();

		if (listFile != null) {
			for (File aListFile : listFile) {

				if (aListFile.isDirectory()) {
					getFilesWithExtension(aListFile, type);
				} else {
					final String absolutePath = aListFile.getAbsolutePath();
					if (absolutePath != null && absolutePath.length() > 0) {
						String mimeType = "";
						try {
							mimeType = URLConnection.guessContentTypeFromName(absolutePath);
						} catch (Exception ignored) {
						}

						if (mimeType != null && mimeType.contains(type)) {
							typeFiles.add(aListFile);
						}
					}
				}
			}
		}

		return typeFiles;
	}

	public static List<File> getOthersTypeFiles(File dir) {
		List<File> typeFiles = new ArrayList<>();

		File[] listFile = dir.listFiles();

		if (listFile != null) {
			for (File aListFile : listFile) {

				if (aListFile.isDirectory()) {
					getOthersTypeFiles(aListFile);
				} else {
					final String absolutePath = aListFile.getAbsolutePath();
					if (absolutePath != null && absolutePath.length() > 0) {
						String mimeType = "";
						try {
							mimeType = URLConnection.guessContentTypeFromName(absolutePath);
						} catch (Exception ignored) {
						}
						if (mimeType == null || (mimeType != null && !mimeType.contains("image") && !mimeType.contains("video") && !mimeType.contains("audio"))) {
							typeFiles.add(aListFile);
						}
					}
				}
			}
		}

		return typeFiles;
	}

	private static long getSizeOfPictures() {
		return getSizeOf(getFilesWithType(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "image"));
	}

	private static long getSizeOfMusic() {
		return getSizeOf(getFilesWithType(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "audio"));
	}

	private static long getSizeOfAPKs() {
		//return getSizeOf(getFilesWithType(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "audio"));
		return getAPKsSizeIn(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
	}

	private static long getSizeOfVideos() {
		return getSizeOf(getFilesWithType(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "video"));
	}

	public static Downloads getDownloads(){
		Downloads downloads = new Downloads();

		downloads.total = getDownloadFilesSize();
		downloads.pictures = getSizeOfPictures();
		downloads.videos = getSizeOfVideos();
		downloads.music = getSizeOfMusic();
		downloads.apks = getSizeOfAPKs();

		return downloads;
	}

	public static void deleteAllAPKs() {

		final List<File> apKs = getAPKsIn(Environment.getExternalStorageDirectory());

		for (File file : apKs) {
			final boolean delete = file.delete();
			Log.i(TAG, "Delete APK: "+file.getAbsolutePath()+"="+delete);
		}
	}

	public static void deleteDownloadsFiles(Context context){
		File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

		final File[] downloadedFiles = file.listFiles();

		if(downloadedFiles!=null&&downloadedFiles.length>0)
			for (File f : downloadedFiles) {
				boolean delete = f.delete();
				Log.i(TAG, "Delete Downloaded file: "+f.getAbsolutePath()+"="+delete);
			}
	}

	public static long getTotalUsedStorage(long size, long def) {
		if(size<=1024*1024)
			return 1024*1024+2048;
		else if(size<=1024*1024*100)
			return 1024*1024*120;
		else if(size<=1024*1024*500)
			return 1024*1024*580;
		else if(size<=1024*1024*1024)
			return 1024*1024*1124;
		else if(size<=1024*1024*1024*5)
			return (long) (1024*1024*1024*5.8);
		else
			return def;
	}
}