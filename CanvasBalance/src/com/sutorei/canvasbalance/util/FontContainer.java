package com.sutorei.canvasbalance.util;

import java.io.File;

import android.graphics.Typeface;

public class FontContainer {
	private static Typeface typeface = null;
	
	public static void setTypeface(File typefaceFile) {
		typeface = Typeface.createFromFile(typefaceFile);
	}
	
	public static void unsetTypeface() {
		typeface = null;
	}
	
	public static Typeface getTypeface() {
		return typeface;
	}
	
	public static boolean isTypefaceSet() {
		return (typeface != null);
	}
}
