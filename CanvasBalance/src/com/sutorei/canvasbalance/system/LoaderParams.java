package com.sutorei.canvasbalance.system;

import android.annotation.SuppressLint;

@SuppressLint("SdCardPath")
public final class LoaderParams 
{
	public class BOOKS_TABLE {

	}
	/**
	 * Shared preferences section.
	 */
	public static final String SHARED_PREFS_NAME 				= "ManualPrefs";
	public static final String SHARED_PREFS_ACTIVE_USER_UUID 	= "Active user UUID";
	public static final String SHARED_PREFS_PAGE_NUMBER 		= "Last page of ";
	public static final String SHARED_PREFS_PAGE_ID		 		= "Page id";
	public static final String SHARED_PREFS_ACTIVE_USER_ID 		= "Active user id";
	
	/**
	 * Manual resources section.
	 */
	public static final String RESOURCES_ROOT_FOLDER 			= "/sdcard/Balance";
	public static final String RESOURCES_IMAGE_FOLDER 			= "image";
	public static final String RESOURCES_TEXT_FOLDER 			= "text";
	public static final String RESOURCES_TASKDATA_FOLDER 		= "data";
	public static final String RESOURCES_PAGES_FOLDER 			= RESOURCES_TASKDATA_FOLDER + "/pages";
	public static final String RESOURCES_TASKS_FOLDER 			= RESOURCES_TASKDATA_FOLDER + "/tasks";
	public static final String RESOURCES_VERSION_FILENAME		= "version";
	public static final String RESOURCES_MANUAL_FILENAME 		= "manual.json";
}
