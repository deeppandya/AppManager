package com.deeppandya.appmanager.managers;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.deeppandya.appmanager.model.AppModel;

import java.util.List;


public class AppMemoryManager {

	private final static String TAG = "AppMemoryManager";

	public static class MemoryState {
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

	public static class MemoryResult{
		public AppModel apps;
		public MemoryState state;
		public long selectedRecoverable;
		public long totalRecoverable;
		public int runningServices;
	}

	private AppMemoryManager(){}

	public static MemoryState getMemoryState(Context context){
		long totalMemory = AppMemoryManager.getTotalMemory(context);

		final long availableMemory = AppMemoryManager.getAvailableMemory(context);
		final long usedMemory = totalMemory-availableMemory;

		MemoryState state = new MemoryState();

		state.setTotalSize(totalMemory);
		state.setUsedSize(usedMemory);
		state.setFreeSize(availableMemory);

		return state;
	}

	public static long getAvailableMemory(Context context){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		Log.i(TAG, "memoryInfo.availMem=" + memoryInfo.availMem);
		return memoryInfo.availMem;
	}

	public static long getTotalMemory(Context context){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		Log.i(TAG, "memoryInfo.totalMem=" + memoryInfo.totalMem);
		return memoryInfo.totalMem;
	}

	public static void killApp(Context context, String pkgName) {
		if(pkgName==null)
			return;
		
		ActivityManager actvityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		actvityManager.killBackgroundProcesses(pkgName);
	}

	public static void killApps(Context context, List<AppModel> apps) {
		if(apps==null)
			return;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		for (AppModel app : apps) {
			if(prefs.getBoolean(app.getAppName()+app.getPackageName(), true)) {
				killApp(context, app.getPackageName());
				Log.d(TAG, "killApps() returned: " + app.getPackageName());
			}
		}
	}
	
	public static int getNumberOfRunningServicesPkgs(Context context){
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> listOfProcesses = activityManager.getRunningServices(1000);
		if(listOfProcesses!=null)
			return listOfProcesses.size();
		
		return 0;
	}
	
	public static List<ResolveInfo> getInstalledApps(Context context){
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		return context.getPackageManager().queryIntentActivities( mainIntent, 0);
	}
	
	public static boolean hasAppWithUri(Context context, String uri){
		return hasAppWithPackageName(context, Uri.parse(uri).getQueryParameter("id"));
	}
	
	public static boolean hasAppWithPackageName(Context context, String packageName){
		if(packageName==null||"".equals(packageName))
			return false;
		
		List<ResolveInfo> apps = getInstalledApps(context);
		for (ResolveInfo resolveInfo : apps) {
			if(packageName.equals(resolveInfo.activityInfo.packageName))
				return true;
		}
		
		return false;
	}
}
