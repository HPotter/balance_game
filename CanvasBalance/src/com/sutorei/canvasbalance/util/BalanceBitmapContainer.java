package com.sutorei.canvasbalance.util;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//TODO(optional) ordinary singleton?
public class BalanceBitmapContainer {
	private static Bitmap leftCupBitmap = null;
	private static Bitmap rightCupBitmap = null;
	private static Bitmap beamBitmap = null;
	private static Bitmap supportBitmap = null;
	private static Bitmap facePositive = null;
	private static Bitmap faceNegative = null;

	private BalanceBitmapContainer() {
	}

	public static void loadBitmaps(File imageFolder) {
		if (leftCupBitmap == null) {
			leftCupBitmap = BitmapFactory.decodeFile(imageFolder
					+ File.separator + "cup_left.png");
		}
		if (rightCupBitmap == null) {
			rightCupBitmap = BitmapFactory.decodeFile(imageFolder
					+ File.separator + "cup_right.png");
		}
		if (beamBitmap == null) {
			beamBitmap = BitmapFactory.decodeFile(imageFolder + File.separator
					+ "balance.png");
		}
		if (supportBitmap == null) {
			supportBitmap = BitmapFactory.decodeFile(imageFolder
					+ File.separator + "support.png");
		}
		if (facePositive == null) {
			facePositive = BitmapFactory.decodeFile(imageFolder
					+ File.separator + "sticker_ok_big.png");
		}
		if (faceNegative == null) {
			faceNegative = BitmapFactory.decodeFile(imageFolder
					+ File.separator + "sticker_false_big.png");
		}
	}

	public static Bitmap getLeftCupBitmap() {
		return leftCupBitmap;
	}

	public static Bitmap getRightCupBitmap() {
		return rightCupBitmap;
	}

	public static Bitmap getBeamBitmap() {
		return beamBitmap;
	}

	public static Bitmap getSupportBitmap() {
		return supportBitmap;
	}

	public static Bitmap getFacePositiveBitmap(){
		return facePositive;
	}
	
	public static Bitmap getFaceNegativeBitmap(){
		return faceNegative;
	}
}
