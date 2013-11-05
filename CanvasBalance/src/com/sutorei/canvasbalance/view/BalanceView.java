package com.sutorei.canvasbalance.view;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sutorei.canvasbalance.domain.BalanceData;
import com.sutorei.canvasbalance.domain.BalanceViewObject;
import com.sutorei.canvasbalance.domain.WeightedObject;
import com.sutorei.canvasbalance.util.BalanceBitmapContainer;
import com.sutorei.canvasbalance.util.BalanceState;

public class BalanceView extends View {

	// important to outside checkout
	private List<WeightedObject> mObjectsOnLeft, mObjectsOnRight,
			mAvaliableObjects;

	public List<WeightedObject> getObjectsOnLeft() {
		return mObjectsOnLeft;
	}

	public void setObjectsOnLeft(List<WeightedObject> mObjectsOnLeft) {
		this.mObjectsOnLeft = mObjectsOnLeft;
	}

	public List<WeightedObject> getObjectsOnRight() {
		return mObjectsOnRight;
	}

	public void setObjectsOnRight(List<WeightedObject> mObjectsOnRight) {
		this.mObjectsOnRight = mObjectsOnRight;
	}

	public List<WeightedObject> getAvaliableObjects() {
		return mAvaliableObjects;
	}

	public void setAvaliableObjects(List<WeightedObject> mAvaliableObjects) {
		this.mAvaliableObjects = mAvaliableObjects;
	}

	public float getWeightOnLeft() {
		return mWeightOnLeft;
	}

	public void setWeightOnLeft(float mWeightOnLeft) {
		this.mWeightOnLeft = mWeightOnLeft;
	}

	public float getWeightOnRight() {
		return mWeightOnRight;
	}

	public void setWeightOnRight(float mWeightOnRight) {
		this.mWeightOnRight = mWeightOnRight;
	}

	public BalanceState getPreviousState() {
		return mPreviousState;
	}

	public void setPreviousState(BalanceState mPreviousState) {
		this.mPreviousState = mPreviousState;
	}

	public BalanceState getCurrentState() {
		return mCurrentState;
	}

	public void setCurrentState(BalanceState mCurrentState) {
		this.mCurrentState = mCurrentState;
	}

	private float mWeightOnLeft, mWeightOnRight;
	private BalanceState mPreviousState, mCurrentState;

	private static final int BASE_WIDTH = 800; // XXX XXX XXX
	private static final int BASE_HEIGHT = 600; // XXX XXX XXX

	// inner logic
	private BalanceViewObject mLeftCup, mRightCup, mBeam, mSupport;
	private volatile BalanceViewObject mBeamBent;
	private boolean mInitPlacementFinished, mDragOngoing, taskLoaded;
	private volatile boolean mAnimationOngoing;
	private Paint mAntiAliasingPaint, mAlphaPaint;
	int mLeftCupXAtBalance, mLeftCupYAtBalance;
	private WeightedObject mDraggedObject = null;
	private float totalScaleRatio, objectScaleRatio;
	private int mDraggedObjectIndex, mDraggedObjectOrigin;
	// object origin -1 means left cup, 1 - right cup, 0 - avaliable objects
	Matrix mRotationAnimation, mRotationPredisposition;
	private volatile float mDegree;
	private boolean interactive, fixed;
	int leftCupObjectOffset;
	int rightCupObjectOffset;

	private int mViewWidth, mViewHeight;

	private BalanceState checkBalance() {
		float eps = (float) 1e-4;
		if (mWeightOnLeft - mWeightOnRight > eps) {
			return BalanceState.LEFT_IS_HEAVIER;
		} else if (mWeightOnLeft - mWeightOnRight < -eps) {
			return BalanceState.RIGHT_IS_HEAVIER;
		} else {
			return BalanceState.EQUAL;
		}
	}

	public BalanceView(Context context, File balanceResourceFolder,
			BalanceData balanceData) {
		super(context);
		taskLoaded = false;
		Bitmap b = Bitmap.createBitmap(33600, 200, Config.ARGB_8888);
		BalanceBitmapContainer mBalanceBitmaps = new BalanceBitmapContainer(
				balanceResourceFolder);
		mLeftCup = new BalanceViewObject(mBalanceBitmaps.getLeftCupBitmap());
		mRightCup = new BalanceViewObject(mBalanceBitmaps.getRightCupBitmap());
		mBeam = new BalanceViewObject(mBalanceBitmaps.getBeamBitmap());
		mSupport = new BalanceViewObject(mBalanceBitmaps.getSupportBitmap());

		mRotationAnimation = new Matrix();
		mRotationPredisposition = new Matrix();

		mWeightOnLeft = 0;
		mWeightOnRight = 0;

		mAnimationOngoing = false;
		mAntiAliasingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mAntiAliasingPaint.setFilterBitmap(true);
		mAntiAliasingPaint.setDither(true);
		mInitPlacementFinished = false;

		mAlphaPaint = new Paint();
		mAlphaPaint.setAlpha(100);
		
		Log.e("#REKT", Boolean.toString(balanceData.isInteractive()));
		
		this.setOnTouchListener(new BalanceOnTouchListener());

		loadAndStartTask(balanceData);
	}

	public void loadAndStartTask(BalanceData balanceData) {
		this.mObjectsOnLeft = balanceData.getObjectsOnLeft();
		this.mObjectsOnRight = balanceData.getObjectsOnRight();
		this.mAvaliableObjects = balanceData.getAvaliableObjects();
		this.fixed = balanceData.isFixed();
		Log.e("#REKT", Boolean.toString(balanceData.isInteractive()));
		Log.e("#REKT", Boolean.toString(balanceData.isFixed()));
		this.interactive = balanceData.isInteractive();
		this.setCurrentState(balanceData.getBalanceState());

		int maxHeight = BASE_HEIGHT / 4;
		for (WeightedObject wo : mObjectsOnLeft) {
			mWeightOnLeft += wo.getWeight();
			if (wo.getBitmap().getHeight() > maxHeight) {
				maxHeight = wo.getBitmap().getHeight();
			}
		}
		for (WeightedObject wo : mObjectsOnRight) {
			mWeightOnRight += wo.getWeight();
			if (wo.getBitmap().getHeight() > maxHeight) {
				maxHeight = wo.getBitmap().getHeight();
			}
		}

		for (WeightedObject wo : mAvaliableObjects) {
			if (wo.getBitmap().getHeight() > maxHeight) {
				maxHeight = wo.getBitmap().getHeight();
			}
		}
		objectScaleRatio = ((float) BASE_HEIGHT / 4) / maxHeight;

		for (WeightedObject wo : mObjectsOnLeft) {
			wo.setScalingRatio(objectScaleRatio);
		}
		for (WeightedObject wo : mObjectsOnRight) {
			wo.setScalingRatio(objectScaleRatio);
		}
		for (WeightedObject wo : mAvaliableObjects) {
			wo.setScalingRatio(objectScaleRatio);
		}

		taskLoaded = true;

		mPreviousState = balanceData.getBalanceState();
		mInitPlacementFinished = false;
		mAnimationOngoing = false;
		mDraggedObject = null;
		invalidate();
	}

	public void loadAndStartTask(Context context,
			List<WeightedObject> _objectsOnLeft,
			List<WeightedObject> _objectsOnRight,
			List<WeightedObject> _avaliableObjects, boolean _isTouchable,
			boolean _isFixed, BalanceState bs) {
		int maxHeight = BASE_HEIGHT / 4;
		mObjectsOnLeft = _objectsOnLeft;
		for (WeightedObject wo : mObjectsOnLeft) {
			mWeightOnLeft += wo.getWeight();
			if (wo.getBitmap().getHeight() > maxHeight) {
				maxHeight = wo.getBitmap().getHeight();
			}
		}
		mObjectsOnRight = _objectsOnRight;
		for (WeightedObject wo : mObjectsOnRight) {
			mWeightOnRight += wo.getWeight();
			if (wo.getBitmap().getHeight() > maxHeight) {
				maxHeight = wo.getBitmap().getHeight();
			}
		}

		mAvaliableObjects = _avaliableObjects;

		for (WeightedObject wo : mAvaliableObjects) {
			if (wo.getBitmap().getHeight() > maxHeight) {
				maxHeight = wo.getBitmap().getHeight();
			}
		}

		objectScaleRatio = ((float) BASE_HEIGHT / 4) / maxHeight;

		for (WeightedObject wo : mObjectsOnLeft) {
			wo.setScalingRatio(objectScaleRatio);
		}
		for (WeightedObject wo : mObjectsOnRight) {
			wo.setScalingRatio(objectScaleRatio);
		}
		for (WeightedObject wo : mAvaliableObjects) {
			wo.setScalingRatio(objectScaleRatio);
		}

		interactive = _isTouchable;
		fixed = _isFixed;
		taskLoaded = true;

		mCurrentState = bs;
		mPreviousState = bs;
		mInitPlacementFinished = false;
		mAnimationOngoing = false;
		mDraggedObject = null;
		invalidate();
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int desiredWidth = BASE_WIDTH;
		int desiredHeight = BASE_HEIGHT;
		float proportion = (float) desiredWidth / desiredHeight;
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int mandatoryHeight = desiredHeight;
		int mandatoryWidth = desiredWidth;

		// Measure Height
		if (heightMode == MeasureSpec.EXACTLY
				|| heightMode == MeasureSpec.AT_MOST) {
			mandatoryHeight = heightSize;
		}
		if (widthMode == MeasureSpec.EXACTLY
				|| widthMode == MeasureSpec.AT_MOST) {
			mandatoryHeight = widthSize;
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

	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		drawOnCanvas(canvas);
	}

	public void drawOnCanvas(Canvas canvas) {
		if (!taskLoaded) {
			return;
		}
		float scaleWidth = mViewWidth / (float) BASE_WIDTH;
		float scaleHeight = mViewHeight / (float) BASE_HEIGHT;

		totalScaleRatio = Math.max(scaleWidth, scaleHeight);
		canvas.scale(totalScaleRatio, totalScaleRatio);
		if (!fixed) {
			mCurrentState = checkBalance();
			if (mCurrentState != mPreviousState) {
				int degreeClause, direction;
				switch (mCurrentState) {
				case LEFT_IS_HEAVIER:
					degreeClause = -14;
					direction = -1;
					break;
				case RIGHT_IS_HEAVIER:
					degreeClause = 14;
					direction = 1;
					break;
				case EQUAL:
					if (mPreviousState == BalanceState.LEFT_IS_HEAVIER) {
						degreeClause = 0;
						direction = 1;
					} else {
						degreeClause = 0;
						direction = -1;
					}
					break;
				default:
					throw new IllegalArgumentException(
							"Unhandled state of balance");
				}
				mPreviousState = mCurrentState;
				AnimationUpdateThread at = new AnimationUpdateThread(
						degreeClause, direction);
				Thread t = new Thread(at);
				t.start();
			}
		}
		if (!mInitPlacementFinished) {
			leftCupObjectOffset = 0;
			rightCupObjectOffset = 0;
			mLeftCupXAtBalance = BASE_WIDTH / 2 - mBeam.getBitmap().getWidth()
					/ 2 - mRightCup.getBitmap().getWidth() / 4;
			mLeftCupYAtBalance = BASE_HEIGHT / 5;

			mRotationAnimation.reset();

			mSupport.setX(BASE_WIDTH / 2 - mSupport.getBitmap().getWidth() / 2);
			mSupport.setY(BASE_HEIGHT / 2);
			mBeam.setX(mSupport.getX() + mSupport.getBitmap().getWidth() / 2
					- mBeam.getBitmap().getWidth() / 2);
			mBeam.setY(mSupport.getY());
			Collections.sort(mObjectsOnLeft,
					new WeightedObject.HeightComparator());
			Collections.sort(mObjectsOnRight,
					new WeightedObject.HeightComparator());
			Collections.sort(mAvaliableObjects,
					new WeightedObject.HeightComparator());
			switch (mCurrentState) {
			case EQUAL:
				mBeamBent = mBeam.copy();
				mLeftCup.setX(mBeam.getX() - mLeftCup.getBitmap().getWidth()
						/ 2 + 40);
				mLeftCup.setY(mSupport.getY() + mBeam.getBitmap().getHeight()
						/ 2 - mLeftCup.getBitmap().getHeight() + 5);
				mRightCup.setX(mBeam.getX() - mRightCup.getBitmap().getWidth()
						/ 2 + mBeam.getBitmap().getWidth() - 41);
				mRightCup.setY(mSupport.getY() + mBeam.getBitmap().getHeight()
						/ 2 - mRightCup.getBitmap().getHeight() + 5);
				mDegree = 0;
				mRotationAnimation.postTranslate(mBeamBent.getX(),
						mBeamBent.getY());
				break;
			case LEFT_IS_HEAVIER:
				mRotationAnimation.postRotate(-14,
						mBeam.getBitmap().getWidth() / 2, mBeam.getBitmap()
								.getHeight() / 2);
				mBeamBent = mBeam.copy();
				mBeamBent.setX(mSupport.getX()
						+ mSupport.getBitmap().getWidth() / 2
						- mBeamBent.getBitmap().getWidth() / 2);
				mBeamBent.setY(mSupport.getY()
						- mBeamBent.getBitmap().getHeight() / 2
						+ mBeam.getBitmap().getHeight() / 2);
				mLeftCup.setX(mBeam.getX() - mLeftCup.getBitmap().getWidth()
						/ 2 + 41);
				mLeftCup.setY(mSupport.getY() + mBeam.getBitmap().getHeight()
						/ 2 - mLeftCup.getBitmap().getHeight() + 49);
				mRightCup.setX(mBeam.getX() + mBeam.getBitmap().getWidth()
						- mRightCup.getBitmap().getWidth() / 2 - 41);
				mRightCup.setY(mSupport.getY() + mBeam.getBitmap().getHeight()
						/ 2 - mRightCup.getBitmap().getHeight() - 39);
				mRotationAnimation.postTranslate(mBeamBent.getX(),
						mBeamBent.getY());
				mDegree = -14;
				break;
			case RIGHT_IS_HEAVIER:
				mRotationAnimation.postRotate(+14,
						mBeam.getBitmap().getWidth() / 2, mBeam.getBitmap()
								.getHeight() / 2);
				mBeamBent = mBeam.copy();
				mBeamBent.setX(mSupport.getX()
						+ mSupport.getBitmap().getWidth() / 2
						- mBeamBent.getBitmap().getWidth() / 2);
				mBeamBent.setY(mSupport.getY()
						- mBeamBent.getBitmap().getHeight() / 2
						+ mBeam.getBitmap().getHeight() / 2);
				mLeftCup.setX(mBeam.getX() - mLeftCup.getBitmap().getWidth()
						/ 2 + 41);
				mLeftCup.setY(mSupport.getY() + mBeam.getBitmap().getHeight()
						/ 2 - mLeftCup.getBitmap().getHeight() - 39);
				mRightCup.setX(mBeam.getX() + mBeam.getBitmap().getWidth()
						- mRightCup.getBitmap().getWidth() / 2 - 41);
				mRightCup.setY(mSupport.getY() + mBeam.getBitmap().getHeight()
						/ 2 - mRightCup.getBitmap().getHeight() + 49);
				mRotationAnimation.postTranslate(mBeamBent.getX(),
						mBeamBent.getY());
				mDegree = 14;
				break;
			}
			mInitPlacementFinished = true;
		} else {
			mPreviousState = mCurrentState;
			mCurrentState = checkBalance();
		}
		leftCupObjectOffset = 0;
		rightCupObjectOffset = 0;
		int xOffset = 5;
		int yOffset = Math.round(BASE_HEIGHT * 19 / 20);
		for (WeightedObject wo : mAvaliableObjects) {
			wo.setX(xOffset);
			wo.setY(yOffset - Math.round(wo.getHeight()));
			canvas.drawBitmap(wo.getBitmap(), null,
					new RectF(wo.getX(), wo.getY(), wo.getX() + wo.getWidth(),
							wo.getY() + wo.getHeight()), mAntiAliasingPaint);
			xOffset += 5 + wo.getBitmap().getWidth();
		}
		for (WeightedObject wo : mObjectsOnRight) {
			wo.setX(mRightCup.getX() - rightCupObjectOffset
					+ mRightCup.getBitmap().getWidth() * 8 / 9
					- Math.round(wo.getWidth()));
			wo.setY(mRightCup.getY() + mRightCup.getBitmap().getHeight() * 1
					/ 4 - Math.round(wo.getHeight()));
			canvas.drawBitmap(wo.getBitmap(), null,
					new RectF(wo.getX(), wo.getY(), wo.getX() + wo.getWidth(),
							wo.getY() + wo.getHeight()), mAntiAliasingPaint);
			rightCupObjectOffset = (rightCupObjectOffset + mRightCup
					.getBitmap().getWidth() / 4)
					% (mRightCup.getBitmap().getWidth() * 7 / 11);
		}
		for (WeightedObject wo : mObjectsOnLeft) {
			wo.setX(mLeftCup.getX() + leftCupObjectOffset
					+ mLeftCup.getBitmap().getWidth() / 7);
			wo.setY(mLeftCup.getY() + mLeftCup.getBitmap().getHeight() * 1 / 4
					- Math.round(wo.getHeight()));
			canvas.drawBitmap(wo.getBitmap(), null,
					new RectF(wo.getX(), wo.getY(), wo.getX() + wo.getWidth(),
							wo.getY() + wo.getHeight()), mAntiAliasingPaint);
			leftCupObjectOffset = (leftCupObjectOffset + mLeftCup.getBitmap()
					.getWidth() / 4)
					% (mRightCup.getBitmap().getWidth() * 7 / 11);

		}
		if (mDraggedObject != null) {
			canvas.drawBitmap(mDraggedObject.getBitmap(), null, new RectF(
					mDraggedObject.getX(), mDraggedObject.getY(),
					mDraggedObject.getX() + mDraggedObject.getWidth(),
					mDraggedObject.getY() + mDraggedObject.getHeight()),
					mAlphaPaint);
		}
		canvas.drawBitmap(mLeftCup.getBitmap(), mLeftCup.getX(),
				mLeftCup.getY(), mAntiAliasingPaint);
		canvas.drawBitmap(mRightCup.getBitmap(), mRightCup.getX(),
				mRightCup.getY(), mAntiAliasingPaint);
		canvas.drawBitmap(mBeamBent.getBitmap(), mRotationAnimation,
				mAntiAliasingPaint);
		canvas.drawBitmap(mSupport.getBitmap(), mSupport.getX(),
				mSupport.getY(), mAntiAliasingPaint);
	}

	private class AnimationUpdateThread implements Runnable {
		private int degreeClause;
		// 1 is clockwise rotation, -1 is ccw rotation
		private int direction;

		public AnimationUpdateThread(int degreeClause, int direction) {
			this.direction = direction;
			this.degreeClause = degreeClause;
		}

		@Override
		public void run() {
			mAnimationOngoing = true;
			float startingSignum = Math.signum(mDegree - degreeClause);
			// while(startingSignum == smoothenedSignum(mDegree)){
			while ((mDegree - degreeClause) * startingSignum >= 1e-3) {
				mDegree += (float) direction * 32 / 100;
				if ((mDegree - degreeClause) * startingSignum < 1e-3) {
					mDegree = degreeClause;
				}
				mLeftCup.setY(mLeftCup.getY() - direction);
				mRightCup.setY(mRightCup.getY() + direction);
				mRotationAnimation.reset();
				mBeamBent.setX(mSupport.getX()
						+ mSupport.getBitmap().getWidth() / 2
						- mBeam.getBitmap().getWidth() / 2);
				mBeamBent.setY(mSupport.getY()
						- mBeamBent.getBitmap().getHeight() / 2
						+ mBeam.getBitmap().getHeight() / 2);
				mRotationAnimation.postRotate(mDegree, mBeam.getBitmap()
						.getWidth() / 2, mBeam.getBitmap().getHeight() / 2);
				mRotationAnimation.postTranslate(mBeamBent.getX(),
						mBeamBent.getY());
				for (WeightedObject wo : mObjectsOnRight) {
					wo.setY(mRightCup.getY()
							+ mRightCup.getBitmap().getHeight() * 3 / 4
							- wo.getBitmap().getHeight());
				}
				for (WeightedObject wo : mObjectsOnLeft) {
					wo.setY(mLeftCup.getY() + mLeftCup.getBitmap().getHeight()
							* 3 / 4 - wo.getBitmap().getHeight());
				}
				BalanceView.this.postInvalidate();

				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {

				}
			}
			mDegree = degreeClause;
			BalanceView.this.postInvalidate();
			mAnimationOngoing = false;
		}

	}

	@Override
	public Parcelable onSaveInstanceState() {

		Bundle bundle = new Bundle();
		ObjectMapper mapper = new ObjectMapper();
		bundle.putParcelable("instanceState", super.onSaveInstanceState());
		BalanceData bd = new BalanceData();
		bd.setAvaliableObjects(this.mAvaliableObjects);
		bd.setBalanceState(this.getCurrentState());
		bd.setFixed(this.fixed);
		bd.setInteractive(this.interactive);
		bd.setObjectsOnLeft(this.mObjectsOnLeft);
		bd.setObjectsOnRight(this.mObjectsOnRight);
		try {
			bundle.putString("stateToSave", mapper.writeValueAsString(bd));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			Log.e("EXCEPTION",
					"Failed to save instance state " + e.getMessage());
		}

		return bundle;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {

		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			ObjectMapper mapper = new ObjectMapper();
			super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
			try {
				BalanceData bd = mapper.readValue(
						bundle.getString("stateToSave"), BalanceData.class);
				taskLoaded = false;
				loadAndStartTask(bd);
			} catch (JsonParseException e) {
				Log.d("EXCEPTION",
						"Failed to load instance state " + e.getMessage());
			} catch (JsonMappingException e) {
				Log.d("EXCEPTION",
						"Failed to load instance state " + e.getMessage());
			} catch (IOException e) {
				Log.d("EXCEPTION",
						"Failed to load instance state " + e.getMessage());
			}
			return;
		}

		super.onRestoreInstanceState(state);
	}

	private class BalanceOnTouchListener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			int eventX = (int) (event.getX() / totalScaleRatio);
			int eventY = (int) (event.getY() / totalScaleRatio);
			Log.d("#REKT", "Entered onTouch");
			if (mAnimationOngoing)
				return false;
			if (!interactive)
				return false;
			switch (event.getAction()) {
			case (MotionEvent.ACTION_DOWN):
				if (eventY > BASE_HEIGHT / 2) {
					// that means we've likely got an object from free weights
					for (int i = 0; i < mAvaliableObjects.size(); ++i) {
						WeightedObject wo = mAvaliableObjects.get(i);
						if (wo.isTouchedWithoutOpacity(eventX, eventY)) {
							getParent()
									.requestDisallowInterceptTouchEvent(true);
							mDraggedObject = (WeightedObject) wo.copy();
							mDraggedObjectIndex = i;
							mDraggedObjectOrigin = 0;
							mDragOngoing = true;
							invalidate();
							return true;
						}
					}
				} else {
					// check objects on cups
					for (int i = mObjectsOnLeft.size() - 1; i >= 0; --i) {
						// objects sorted from farthest to closest to user
						// so we'll check them in backwards order
						if (mObjectsOnLeft.get(i).isTouched(eventX, eventY)) {
							getParent()
									.requestDisallowInterceptTouchEvent(true);
							mDraggedObject = (WeightedObject) mObjectsOnLeft
									.get(i).copy();
							mDraggedObjectIndex = i;
							mDraggedObjectOrigin = -1;
							mDragOngoing = true;
							invalidate();
							return true;
						}
					}
					for (int i = mObjectsOnRight.size() - 1; i >= 0; --i) {
						if (mObjectsOnRight.get(i).isTouched(eventX, eventY)) {
							getParent()
									.requestDisallowInterceptTouchEvent(true);
							mDraggedObject = mObjectsOnRight.get(i).copy();
							mDraggedObjectIndex = i;
							mDraggedObjectOrigin = 1;
							mDragOngoing = true;
							invalidate();
							return true;
						}
					}
				}
				break;
			case (MotionEvent.ACTION_MOVE):
				if (mDragOngoing) {
					if (eventX + mDraggedObject.getBitmap().getWidth() / 2 <= BASE_WIDTH
							&& eventX - mDraggedObject.getBitmap().getWidth()
									/ 2 >= 0) {
						mDraggedObject.setX((int) Math.round(eventX
								- mDraggedObject.getBitmap().getWidth() / 2));
					}
					if (eventY + mDraggedObject.getBitmap().getHeight() / 2 <= BASE_HEIGHT
							&& eventY - mDraggedObject.getBitmap().getHeight()
									/ 2 >= 0) {
						mDraggedObject.setY((int) Math.round(eventY
								- mDraggedObject.getBitmap().getHeight() / 2));
					}
					invalidate();
				}
				break;
			case (MotionEvent.ACTION_UP):
				if (mDragOngoing) {
					if (mLeftCup.isAbove(eventX, eventY)) {
						mObjectsOnLeft.add(mDraggedObject);
						mWeightOnLeft += mDraggedObject.getWeight();
						switch (mDraggedObjectOrigin) {
						case -1:
							mObjectsOnLeft.remove(mDraggedObjectIndex);
							Collections.sort(mObjectsOnLeft,
									new WeightedObject.HeightComparator());
							mWeightOnLeft -= mDraggedObject.getWeight();
							break;
						case 0:
							mAvaliableObjects.remove(mDraggedObjectIndex);
							Collections.sort(mAvaliableObjects,
									new WeightedObject.HeightComparator());
							break;
						case 1:
							mObjectsOnRight.remove(mDraggedObjectIndex);
							Collections.sort(mObjectsOnRight,
									new WeightedObject.HeightComparator());
							mWeightOnRight -= mDraggedObject.getWeight();
							break;
						default:
							break;
						}
						Collections.sort(mObjectsOnLeft,
								new WeightedObject.HeightComparator());
						mDraggedObject = null;
						mDragOngoing = false;
						invalidate();
					} else if (mRightCup.isAbove(eventX, eventY)) {
						mObjectsOnRight.add(mDraggedObject);
						mWeightOnRight += mDraggedObject.getWeight();
						switch (mDraggedObjectOrigin) {
						case 0:
							mAvaliableObjects.remove(mDraggedObjectIndex);
							Collections.sort(mAvaliableObjects,
									new WeightedObject.HeightComparator());
							break;
						case -1:
							mObjectsOnLeft.remove(mDraggedObjectIndex);
							Collections.sort(mObjectsOnLeft,
									new WeightedObject.HeightComparator());
							mWeightOnLeft -= mDraggedObject.getWeight();
							break;
						case 1:
							mObjectsOnRight.remove(mDraggedObjectIndex);
							Collections.sort(mObjectsOnRight,
									new WeightedObject.HeightComparator());
							mWeightOnRight -= mDraggedObject.getWeight();
							break;
						default:
							break;
						}
						Collections.sort(mObjectsOnRight,
								new WeightedObject.HeightComparator());
						mDraggedObject = null;
						mDragOngoing = false;
						invalidate();
					} else {
						switch (mDraggedObjectOrigin) {
						case 1:
							mObjectsOnRight.remove(mDraggedObjectIndex);
							Collections.sort(mObjectsOnRight,
									new WeightedObject.HeightComparator());
							mWeightOnRight -= mDraggedObject.getWeight();
							break;
						case -1:
							mObjectsOnLeft.remove(mDraggedObjectIndex);
							Collections.sort(mObjectsOnLeft,
									new WeightedObject.HeightComparator());
							mWeightOnLeft -= mDraggedObject.getWeight();
							break;
						case 0:
							mAvaliableObjects.remove(mDraggedObjectIndex);
						default:
							break;
						}
						mAvaliableObjects.add(mDraggedObject);
						Collections.sort(mAvaliableObjects,
								new WeightedObject.HeightComparator());
						mDraggedObject = null;
						mDragOngoing = false;
						invalidate();
					}
				}
				break;
			default:
				break;
			}
			return true;
		}
	}

	public static Bitmap generatePreview(BalanceData balanceData) {
		// TODO
		return null;
	}
}
