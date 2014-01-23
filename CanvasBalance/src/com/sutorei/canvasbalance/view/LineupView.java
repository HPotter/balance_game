package com.sutorei.canvasbalance.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.sutorei.canvasbalance.domain.WeightedObject;
import com.sutorei.canvasbalance.util.BalanceBitmapCache;

@SuppressLint("ViewConstructor")
public class LineupView extends View {
	private Bitmap mLine;
	private WeightedObject mDraggedObject = null;
	private List<WeightedObject> mObjectsToSort;
	private boolean mInitialized = false, mDragOngoing = false;
	private List<Float> mAnchors;
	private float lineY, mTotalScaleRatio, mAnchorStep;
	private Paint mAntiAliasingPaint, mAlphaPaint;

	private int mViewWidth, mViewHeight;

	private RectF destRect = new RectF();
	private int mDraggedObjectIndex;

	public List<WeightedObject> getObjectsToSort() {
		return mObjectsToSort;
	}

	public LineupView(Context context, List<WeightedObject> objects) {
		super(context);
		mObjectsToSort = objects;
		mLine = BalanceBitmapCache.getLineBitmap();

		mAnchors = new ArrayList<Float>();
		mAntiAliasingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mAntiAliasingPaint.setFilterBitmap(true);
		mAntiAliasingPaint.setDither(true);

		mAlphaPaint = new Paint();
		mAlphaPaint.setAlpha(100);

		this.setOnTouchListener(new LineupOnTouchListener());
		shuffle();
		initCoordinatesAndScale();
		mInitialized = true;

	}

	private void shuffle() {
		Collections.shuffle(mObjectsToSort);
	}

	private void initCoordinatesAndScale() {
		lineY = mLine.getHeight() / 2;
		float padding = 5;
		mAnchorStep = (mLine.getWidth() - padding * 2)
				/ (mObjectsToSort.size() + 0);
		float currentOffset = padding;
		for (WeightedObject wo : mObjectsToSort) {
			float scaleHorizontal = (float) (mAnchorStep - padding * 2)
					/ wo.getWidth();
			float scaleVertical = (float) (mLine.getHeight()) / wo.getHeight();
			wo.setScalingRatio(Math.min(1,
					Math.min(scaleHorizontal, scaleVertical)));
			wo.setX(currentOffset + mAnchorStep / 2 - wo.getWidth() / 2);
			mAnchors.add(currentOffset + mAnchorStep / 2);
			wo.setY(lineY - wo.getHeight() / 2);
			currentOffset += padding * 2 + mAnchorStep;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int desiredWidth = mLine.getWidth();
		int desiredHeight = mLine.getHeight();
		float proportion = (float) desiredWidth / desiredHeight;
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int mandatoryHeight = desiredHeight;
		int mandatoryWidth = desiredWidth;
		if (heightMode == MeasureSpec.EXACTLY
				|| heightMode == MeasureSpec.AT_MOST) {
			mandatoryHeight = heightSize;
		}
		if (widthMode == MeasureSpec.EXACTLY
				|| widthMode == MeasureSpec.AT_MOST) {
			mandatoryHeight = widthSize;
		}
		if (mandatoryHeight >= heightSize) {
			mandatoryHeight = heightSize;
		}
		if (mandatoryWidth >= widthSize) {
			mandatoryWidth = widthSize;
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

	public boolean check() {
		boolean answer = true;
		for (int i = 0; i < mObjectsToSort.size() - 1; ++i) {
			if (mObjectsToSort.get(i).getWeight() > mObjectsToSort.get(i + 1)
					.getWeight()) {
				answer = false;
				break;
			}
		}
		return answer;
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!mInitialized) {
			return;
		}
		float scaleWidth = mViewWidth / (float) mLine.getWidth();
		float scaleHeight = mViewHeight / (float) mLine.getHeight();
		mTotalScaleRatio = Math.min(scaleWidth, scaleHeight);
		canvas.save();
		canvas.scale(mTotalScaleRatio, mTotalScaleRatio);
		// draw the line
		canvas.drawBitmap(mLine, 0, 0, mAntiAliasingPaint);
		for (WeightedObject wo : mObjectsToSort) {
			destRect.set(wo.getX(), wo.getY(), wo.getX() + wo.getWidth(),
					wo.getY() + wo.getHeight());
			canvas.drawBitmap(wo.getBitmap(), null, destRect,
					mAntiAliasingPaint);
		}
		if (mDraggedObject != null) {
			destRect.set(mDraggedObject.getX(), mDraggedObject.getY(),
					mDraggedObject.getX() + mDraggedObject.getWidth(),
					mDraggedObject.getY() + mDraggedObject.getHeight());
			canvas.drawBitmap(mDraggedObject.getBitmap(), null, destRect,
					mAlphaPaint);
		}
		canvas.restore();
	}

	private class LineupOnTouchListener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			int eventX = (int) (event.getX() / mTotalScaleRatio);
			int eventY = (int) (event.getY() / mTotalScaleRatio);
			boolean eventHandled = false;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				for (int i = 0; i < mObjectsToSort.size(); ++i) {
					WeightedObject wo = mObjectsToSort.get(i);
					if (wo.isTouchedWithoutOpacity(eventX, eventY)) {
						mDraggedObject = new WeightedObject(wo);
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
					if (eventX + mDraggedObject.getWidth() / 2 <= mLine
							.getWidth()
							&& eventX - mDraggedObject.getWidth() / 2 >= 0) {
						mDraggedObject.setX((int) Math.round(eventX
								- mDraggedObject.getWidth() / 2));
					}
					if (eventY + mDraggedObject.getHeight() / 2 <= mLine
							.getHeight()
							&& eventY - mDraggedObject.getHeight() / 2 >= 0) {
						mDraggedObject.setY((int) Math.round(eventY
								- mDraggedObject.getHeight() / 2));
					}
					for (int i = 0; i < mAnchors.size(); ++i) {
						if (eventX >= mAnchors.get(i) - mAnchorStep / 2
								&& eventX <= mAnchors.get(i) + mAnchorStep / 2) {
							if (i == mDraggedObjectIndex) {
								break;
							}
							// swap coordinates
							mObjectsToSort.get(i).setX(
									mAnchors.get(mDraggedObjectIndex)
											- mObjectsToSort.get(i).getWidth()
											/ 2);
							mObjectsToSort.get(mDraggedObjectIndex).setX(
									mAnchors.get(i)
											- mObjectsToSort.get(
													mDraggedObjectIndex)
													.getWidth() / 2);
							Collections.swap(mObjectsToSort, i,
									mDraggedObjectIndex);
							mDraggedObjectIndex = i;
							break;
						}
					}
					invalidate();
					eventHandled = true;
				}
				break;
			case MotionEvent.ACTION_UP:
				mDraggedObject = null;
				eventHandled = true;
				invalidate();
				break;
			}
			return eventHandled;
		}

	}
}
