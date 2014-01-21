package com.sutorei.canvasbalance.util;

import java.io.File;

import android.graphics.Bitmap;

public class BalanceBitmapCache extends BitmapCache {
	private static String BALANCE_BITMAP_FOLDER = null;
	
	// TODO rename bitmaps
	public static void loadBitmaps(File imageFolder) {
		BALANCE_BITMAP_FOLDER = imageFolder + File.separator;
		
		preloadAll(imageFolder);
	}

	public static Bitmap getBitmap(String filename) {
		return BitmapCache.getBitmap(BALANCE_BITMAP_FOLDER + filename);
	}
	
	public static Bitmap getLeftCupBitmap() {
		return getBitmap("cup_left.png");
	}

	public static Bitmap getRightCupBitmap() {
		return getBitmap("cup_right.png");
	}

	public static Bitmap getBeamBitmap() {
		return getBitmap("balance.png");
	}

	public static Bitmap getSupportBitmap() {
		return getBitmap("support.png");
	}

	public static Bitmap getFacePositiveBitmap() {
		return getBitmap("sticker_ok_big.png");
	}

	public static Bitmap getFaceNegativeBitmap() {
		return getBitmap("sticker_false_big.png");
	}

	public static Bitmap getLineBitmap() {
		return getBitmap("line.png");
	}
}
