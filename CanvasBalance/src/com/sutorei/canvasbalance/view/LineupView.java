package com.sutorei.canvasbalance.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.sutorei.canvasbalance.domain.WeightedObject;

public class LineupView extends View{
	private Bitmap line;
	private WeightedObject mDraggedObject = null;
	private List<WeightedObject> objectsToSort;
	private boolean initialized = false, mDragOngoing = false;
	private List<Float> xAnchors;
	private float lineY, totalScaleRatio;
	private Paint mAntiAliasingPaint, mAlphaPaint, mCirclePaint;
	
	private int mViewWidth, mViewHeight;
	
	private boolean isInsideACircle(float x, float y, float centerX, float centerY, float radius){
		return (Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) < Math.pow(radius, 2));
	}
	
	public LineupView(Context context, List<WeightedObject> objects, File imageFolder){
		super (context);
		objectsToSort = objects;
		line = BitmapFactory.decodeFile(imageFolder
				+ File.separator + "line.png");
		
		mAntiAliasingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mAntiAliasingPaint.setFilterBitmap(true);
		mAntiAliasingPaint.setDither(true);

		mAlphaPaint = new Paint();
		mAlphaPaint.setAlpha(100);
		
		mCirclePaint = new Paint();
		mCirclePaint.setColor(line.getPixel(0, line.getHeight()/2));
		
		this.setOnTouchListener(new LineupOnTouchListener());
		shuffle();
		initCoordinatesAndScale();
		initialized = true;
		
	}
	
	private void shuffle(){
		Collections.shuffle(objectsToSort);
	}
	
	private void initCoordinatesAndScale(){
		lineY = line.getHeight()/2;
		float padding = 10;
		float anchorStep = (line.getWidth() - padding*2)/objectsToSort.size();
		float currentOffset = padding;
		for (WeightedObject wo: objectsToSort){
			float scaleHorizontal = (float)(anchorStep - padding*2)/wo.getWidth();
			float scaleVertical = (float)(line.getHeight())/wo.getHeight();
			wo.setScalingRatio(Math.min(1, Math.min(scaleHorizontal, scaleVertical)));
			wo.setX(currentOffset + anchorStep/2 - wo.getWidth()/2);
			xAnchors.add(currentOffset + anchorStep/2 - wo.getWidth()/2);
			wo.setY(lineY - wo.getHeight()/2);
			currentOffset += padding * 2 + anchorStep;
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		int desiredWidth = line.getWidth();
		int desiredHeight = line.getHeight();
		float proportion = (float) desiredWidth / desiredHeight;
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int mandatoryHeight = desiredHeight;
		int mandatoryWidth = desiredWidth;
		int screenWidth = widthSize;
		int screenHeight = heightSize;
		if (heightMode == MeasureSpec.EXACTLY
				|| heightMode == MeasureSpec.AT_MOST) {
			mandatoryHeight = heightSize;
		}
		if (widthMode == MeasureSpec.EXACTLY
				|| widthMode == MeasureSpec.AT_MOST) {
			mandatoryHeight = widthSize;
		}
		if (mandatoryHeight >= screenHeight) {
			mandatoryHeight = screenHeight;
		}
		if (mandatoryWidth >= screenWidth) {
			mandatoryWidth = screenWidth;
		}
		float derivedProportion = (float) mandatoryWidth / mandatoryHeight;
		if (derivedProportion >= proportion) {
			setMeasuredDimension(Math.round(proportion * mandatoryHeight),
					mandatoryHeight);
			this.mViewWidth = Math.round(proportion * mandatoryHeight);
			this.mViewHeight = mandatoryHeight;
		} else {
			setMeasuredDimension(mandatoryWidth,
					Math.round(mandatoryWidth / proportion));
			this.mViewWidth = mandatoryWidth;
			this.mViewHeight = Math.round(mandatoryWidth / proportion);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	
	RectF destRect = new RectF();
	
	@Override
	public void onDraw(Canvas canvas){
		if (!initialized){
			return ;
		}
		float scaleWidth = mViewWidth / (float) line.getWidth();
		float scaleHeight = mViewHeight / (float) line.getHeight();
		totalScaleRatio = Math.min(scaleWidth, scaleHeight);
		canvas.save();
		canvas.scale(totalScaleRatio, totalScaleRatio);
		//draw the line
		canvas.drawBitmap(line, 0, 0, mAntiAliasingPaint);
		for (float centerX: xAnchors){
			canvas.drawCircle(centerX, lineY, line.getHeight()/6, mCirclePaint);
		}
		for (WeightedObject wo: objectsToSort){
			destRect.set(wo.getX(), wo.getY(), wo.getX() + wo.getWidth(), wo.getY() + wo.getHeight());
			canvas.drawBitmap(wo.getBitmap(), null, destRect, mAntiAliasingPaint);
		}
		if(mDraggedObject != null){
			destRect.set(mDraggedObject.getX(), mDraggedObject.getY(),
					mDraggedObject.getX() + mDraggedObject.getWidth(),
					mDraggedObject.getY() + mDraggedObject.getHeight());
			canvas.drawBitmap(mDraggedObject.getBitmap(), null, destRect,
					mAlphaPaint);
		}
		canvas.restore();
	}
	

	private int mDraggedObjectIndex;
	private class LineupOnTouchListener implements View.OnTouchListener{

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			int eventX = (int) (event.getX() / totalScaleRatio);
			int eventY = (int) (event.getY() / totalScaleRatio);
			boolean eventHandled = false;
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				for (int i = 0; i < objectsToSort.size(); ++i){
					WeightedObject wo = objectsToSort.get(i);
					if (wo.isTouchedWithoutOpacity(eventX, eventY)){
						mDraggedObject = (WeightedObject) wo.copy();
						mDraggedObjectIndex = i;
						mDragOngoing = true;
						invalidate();
						eventHandled = true;
						break;
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mDragOngoing) {
					if (eventX + mDraggedObject.getBitmap().getWidth() / 2 <= line.getWidth()
							&& eventX - mDraggedObject.getBitmap().getWidth()
									/ 2 >= 0) {
						mDraggedObject.setX((int) Math.round(eventX
								- mDraggedObject.getBitmap().getWidth() / 2));
					}
					if (eventY + mDraggedObject.getBitmap().getHeight() / 2 <= line.getHeight()
							&& eventY - mDraggedObject.getBitmap().getHeight()
									/ 2 >= 0) {
						mDraggedObject.setY((int) Math.round(eventY
								- mDraggedObject.getBitmap().getHeight() / 2));
					}
					invalidate();
					eventHandled = true;
				}
				break;
			case MotionEvent.ACTION_UP:
				for (int i = 0; i < xAnchors.size(); ++i){
					if (isInsideACircle(eventX, eventY, xAnchors.get(i), lineY, line.getHeight()/4)){
						Collections.swap(objectsToSort, mDraggedObjectIndex, i);
					}
				}
				mDraggedObject = null;
				eventHandled = true;
				break;
			}
			return eventHandled;
		}
		
	}
}
