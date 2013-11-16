package com.sutorei.canvasbalance.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class BalanceObjectBitmapCache {
	private static HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

	static final String[] IMAGE_EXTENSIONS = new String[] { "gif", "png",
			"bmp", "jpg", "jpeg" };

	static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

		@Override
		public boolean accept(final File dir, final String name) {
			for (final String ext : IMAGE_EXTENSIONS) {
				if (name.toLowerCase(Locale.ENGLISH).endsWith("." + ext)) {
					return true;
				}
			}
			return false;
		}
	};

	public static Bitmap getBitmap(String filename) {
		filename = normalizePath(filename);

		if (!cache.containsKey(filename)) {
			Log.d(BalanceObjectBitmapCache.class.toString(), "hot preload");
			preload(filename);
		}

		return cache.get(filename);
	}

	public static void preloadAll(File taskFolder) {
		Log.d("preloadAll", "");
		for (File file : taskFolder.listFiles(IMAGE_FILTER)) {
			preload(file.getAbsolutePath());
		}
	}

	public static void purge() {
		for (Bitmap bitmap : cache.values()) {
			bitmap.recycle();
		}

		cache.clear();
	}

	private static void preload(String filename) {
		filename = normalizePath(filename);

		Log.d(BalanceObjectBitmapCache.class.toString(), "preload " + filename);
		cache.put(filename, BitmapFactory.decodeFile(filename));
	}

	private static String normalizePath(String path) {
		return new File(path).getAbsolutePath();
	}
}
