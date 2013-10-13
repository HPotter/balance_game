package com.sutorei.balance.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BalanceViewObject{
	protected volatile int x, y;
	protected volatile Bitmap sprite;
	
	public BalanceViewObject(){
		x = 0; y = 0; sprite = null;
	}
	
	public BalanceViewObject copy(){
		BalanceViewObject copy = new BalanceViewObject(sprite);
		copy.setX(x);
		copy.setY(y);
		return copy;
	}
	public BalanceViewObject(int resourceId, Context context){
		x = 0; y = 0; 
		sprite = BitmapFactory.decodeResource(context.getResources(), resourceId);

	}
	
	public BalanceViewObject(Bitmap bm){
		sprite = bm;
		x = 0; y = 0;
	}
	public Bitmap getBitmap(){
		return sprite;
	}
	
	public void setBitmap(Bitmap bm){
		sprite.recycle();
		sprite = bm;
	}
	public void scaleBitmap(double scalingCoefficient){
		//TODO: implement scaling
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean isTouchedWithoutOpacity(float touchX, float touchY){
		if (touchX - x >= 0 && touchX - x <= sprite.getWidth()
				&& touchY - y>= 0 && touchY - y <= sprite.getHeight()){
			return true;
		}
		return false;
	}
	public boolean isTouched(float touchX, float touchY){
		if (touchX - x >= 0 && touchX - x <= sprite.getWidth()
				&& touchY - y>= 0 && touchY - y <= sprite.getHeight()){
			int color = sprite.getPixel((int)Math.round(touchX - x), 
										(int)Math.round(touchY - y));
			return !((color & 0xff000000) == 0x0);
		}
		return false;
	}
	
	public boolean isAbove(float touchX, float touchY){
		if (touchX - x >= 0 && touchX - x <= sprite.getWidth()
				&& touchY - y <= sprite.getHeight()){
			return true;
		}
		return false;
	}
}

