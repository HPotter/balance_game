package com.sutorei.balance.util;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BalanceBitmapContainer {
	
	private static final String LEFT_CUP_FILENAME = "left_cup.png";
	private static final String RIGHT_CUP_FILENAME = "right_cup.png";
	private static final String BEAM_FILENAME = "beam.png";
	private static final String SUPPORT_FILENAME = "support.png";
	
	private Bitmap leftCupBitmap, rightCupBitmap, beamBitmap, supportBitmap;
	
/*	public BalanceBitmapContainer(Context context){
		Resources r = context.getResources();
		leftCupBitmap = BitmapFactory.decodeResource(r, R.drawable.cup_left);
		rightCupBitmap = BitmapFactory.decodeResource(r, R.drawable.cup_right);
		beamBitmap = BitmapFactory.decodeResource(r, R.drawable.balance);
		supportBitmap = BitmapFactory.decodeResource(r, R.drawable.support);
	} */
	
	public BalanceBitmapContainer(File resourceFolder) throws IOException{
		leftCupBitmap = BitmapFactory.decodeFile(resourceFolder.getAbsolutePath()+LEFT_CUP_FILENAME);
		rightCupBitmap = BitmapFactory.decodeFile(resourceFolder.getAbsolutePath()+RIGHT_CUP_FILENAME);
		beamBitmap = BitmapFactory.decodeFile(resourceFolder.getAbsolutePath() + BEAM_FILENAME);
		supportBitmap = BitmapFactory.decodeFile(resourceFolder.getAbsolutePath() + SUPPORT_FILENAME);
		if (	leftCupBitmap == null 	||
				rightCupBitmap == null 	||
				beamBitmap == null 		||
				supportBitmap == null){
			throw new IOException("Failed to read a resource file");
		}
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
