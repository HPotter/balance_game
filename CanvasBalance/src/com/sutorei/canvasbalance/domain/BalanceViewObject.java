package com.sutorei.canvasbalance.domain;

import android.graphics.Bitmap;

public class BalanceViewObject implements Cloneable {
	protected volatile float x = 0;
	protected volatile float y = 0;
	protected Bitmap bitmap = null;

	public BalanceViewObject() {
	}

	@Override
	public BalanceViewObject clone() {
		BalanceViewObject result = new BalanceViewObject();

		result.x = this.x;
		result.y = this.y;
		result.bitmap = this.bitmap;

		return result;
	}

	public BalanceViewObject(Bitmap bitmap) {
		this.bitmap = bitmap;
		x = 0;
		y = 0;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		bitmap.recycle(); // TODO clone bitmap, otherwise we can recycle useful
							// bitmap. Or remove this call
		this.bitmap = bitmap;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	// TODO rename
	public boolean isTouchedWithoutOpacity(float touchX, float touchY) {
		return (touchX - x >= 0 && touchX - x <= bitmap.getWidth()
				&& touchY - y >= 0 && touchY - y <= bitmap.getHeight());
	}

	public boolean isTouched(float touchX, float touchY) {
		if (isTouchedWithoutOpacity(touchX, touchY)) {
			int color = bitmap.getPixel((int) Math.ceil(touchX - x),
					(int) Math.ceil(touchY - y));

			return (color & 0xff000000) != 0x0;
		}

		return false;
	}

	// TODO rename
	public boolean isAbove(float touchX, float touchY) {
		return touchX - x >= 0 && touchX - x <= bitmap.getWidth() && touchY
				- y <= bitmap.getHeight();
	}
}
