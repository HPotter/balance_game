package com.sutorei.canvasbalance.util;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

//TODO singleton
public class BalanceBitmapContainer {
	private Bitmap leftCupBitmap, rightCupBitmap, beamBitmap, supportBitmap;
	
	public BalanceBitmapContainer(File imageFolder){
		leftCupBitmap = BitmapFactory.decodeFile(imageFolder + File.separator + "cup_left.png");
		rightCupBitmap = BitmapFactory.decodeFile(imageFolder + File.separator + "cup_right.png");
		beamBitmap = BitmapFactory.decodeFile(imageFolder + File.separator + "balance.png");
		Log.e("", imageFolder + File.separator + "balance.png");
		supportBitmap = BitmapFactory.decodeFile(imageFolder + File.separator + "support.png");
	}

	public Bitmap getLeftCupBitmap() {
		return leftCupBitmap;
	}

	public Bitmap getRightCupBitmap() {
		return rightCupBitmap;
	}

	public Bitmap getBeamBitmap() {
		return beamBitmap;
	}

	public Bitmap getSupportBitmap() {
		return supportBitmap;
	}
	
	
}
