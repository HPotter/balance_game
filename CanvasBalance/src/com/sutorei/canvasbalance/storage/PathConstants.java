package com.sutorei.canvasbalance.storage;

import android.os.Environment;

// XXX
public interface PathConstants {
	public static final String UPDATE_SERVER_ROOT = "http://hprouter.campus.mipt.ru/balance/";
	public static final String UPDATE_SERVER_RELATIVE_DB_PATH = "balance.sqlite";
	public static final String UPDATE_SERVER_RELATIVE_ITEM_IMAGES_PATH = "/images/items/";
	public static final String UPDATE_SERVER_RELATIVE_LEVEL_PREVIEW_IMAGES_PATH = "/images/level_previews/";
	public static final String LOCAL_IMAGES_RESOURCE_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/libra/res/images/";
	public static final String LOCAL_ITEM_IMAGES_RELATIVE_RESOURCE_PATH = "/items/";
	public static final String LOCAL_LEVEL_PREVIEW_IMAGES_RELATIVE_RESOURCE_PATH ="/level_previews/";
	public static final String SHARED_PROGRESS_NAME = "shared_progress";
}
