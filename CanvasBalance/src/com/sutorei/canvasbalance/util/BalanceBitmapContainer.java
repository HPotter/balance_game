package com.sutorei.canvasbalance.util;

import com.sutorei.canvasbalance.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BalanceBitmapContainer {
	private Bitmap leftCupBitmap, rightCupBitmap, beamBitmap, supportBitmap;
	
	public BalanceBitmapContainer(Context context){
		Resources r = context.getResources();
		leftCupBitmap = BitmapFactory.decodeResource(r, R.drawable.cup_left);
		rightCupBitmap = BitmapFactory.decodeResource(r, R.drawable.cup_right);
		beamBitmap = BitmapFactory.decodeResource(r, R.drawable.balance);
		supportBitmap = BitmapFactory.decodeResource(r, R.drawable.support);
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
