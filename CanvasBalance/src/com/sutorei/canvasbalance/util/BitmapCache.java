package com.sutorei.canvasbalance.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapCache {
	private static HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

	private static final Pattern IMAGE_EXTENSION_PATTERN = Pattern.compile(
			".*(gif|png|bmp|jpg|jpeg)", Pattern.CASE_INSENSITIVE);

	private static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

		@Override
		public boolean accept(final File dir, final String name) {
			return IMAGE_EXTENSION_PATTERN.matcher(name).matches();
		}
	};

	public static Bitmap getBitmap(String filename) {
		filename = normalizePath(filename);

		if (!cache.containsKey(filename)) {
			preload(filename);
		}

		return cache.get(filename);
	}

	public static void preloadAll(File folder) {
		for (File file : folder.listFiles(IMAGE_FILTER)) {
			preload(file.getAbsolutePath());
		}
	}

	public static void purge() {
		for (Bitmap bitmap : cache.values()) {
			bitmap.recycle(); // TODO be careful with recycle(), bitmap may be
								// in use
		}

		cache.clear();
	}

	private static void preload(String filename) {
		filename = normalizePath(filename);

		cache.put(filename, BitmapFactory.decodeFile(filename));
	}

	private static String normalizePath(String path) {
		return new File(path).getAbsolutePath();
	}
}
