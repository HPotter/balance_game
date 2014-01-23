package com.sutorei.canvasbalance.view;

import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.sutorei.canvasbalance.domain.BalanceData;
import com.sutorei.canvasbalance.domain.BalanceState;
import com.sutorei.canvasbalance.domain.BalanceViewObject;
import com.sutorei.canvasbalance.domain.WeightedObject;
import com.sutorei.canvasbalance.util.BalanceBitmapCache;

@SuppressLint("ViewConstructor")
public class BalanceView extends View {

	private float mWeightOnLeft, mWeightOnRight;
	private BalanceState mPreviousState, mCurrentState;

	private BalanceViewObject mLeftCup, mRightCup, mBeam, mSupport,
			mPositivePopup, mNegativePopup, mBeamBent;
	// mBeamBent object is needed to store rotated states of beam getting an
	// undegraded sprite from mBeam
	// continious rotations of a sprite may lead to image degradation
	private boolean mDragOngoing, mTaskLoaded, mPositivePopupVisible,
			mNegativePopupVisible, mInteractive, mFixed, mAnimationOngoing;

	private int mViewWidth, mViewHeight;
	private float mTotalScaleRatio;

	private volatile float mDegree;
	// these are preallocated for drawing purposes
	private Matrix mRotationAnimation;
	private RectF destRect = new RectF();
	private int degreeClause, direction;

	private WeightedObject mDraggedObject = null;
	private int mDraggedObjectIndex, mDraggedObjectOrigin;
	// object origin -1 means left cup, 1 - right cup, 0 - available objects

	private Handler mInvalidatorHandler;
	private final int BASE_WIDTH, BASE_HEIGHT;

	private final float HORIZONTAL_CUP_OFFSET = 61;
	private int mLeftCupObjectOffset, mRightCupObjectOffset;
	private Paint mAntiAliasingPaint, mAlphaPaint;

	private Runnable invalidatorRunnable = new Runnable() {
		public void run() {
			invalidate();
		}
	};

	// important to outside checkout
	private List<WeightedObject> mObjectsOnLeft, mObjectsOnRight,
			mAvailableObjects;

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
		return mAvailableObjects;
	}

	public void setAvaliableObjects(List<WeightedObject> mAvaliableObjects) {
		this.mAvailableObjects = mAvaliableObjects;
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

	public BalanceView(Context context, BalanceData balanceData) {
		super(context);
		mTaskLoaded = false;
		mLeftCup = new BalanceViewObject(BalanceBitmapCache.getLeftCupBitmap());
		mRightCup = new BalanceViewObject(
				BalanceBitmapCache.getRightCupBitmap());
		mBeam = new BalanceViewObject(BalanceBitmapCache.getBeamBitmap());
		mSupport = new BalanceViewObject(BalanceBitmapCache.getSupportBitmap());

		mPositivePopupVisible = mNegativePopupVisible = false;

		mPositivePopup = new BalanceViewObject(
				BalanceBitmapCache.getFacePositiveBitmap());
		mNegativePopup = new BalanceViewObject(
				BalanceBitmapCache.getFaceNegativeBitmap());

		BASE_WIDTH = mLeftCup.getBitmap().getWidth() / 2
				+ mBeam.getBitmap().getWidth()
				+ mRightCup.getBitmap().getWidth() / 2;
		BASE_HEIGHT = mSupport.getBitmap().getHeight() * 4;
		mRotationAnimation = new Matrix();

		mWeightOnLeft = 0;
		mWeightOnRight = 0;

		mAnimationOngoing = false;
		mAntiAliasingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mAntiAliasingPaint.setFilterBitmap(true);
		mAntiAliasingPaint.setDither(true);

		mAlphaPaint = new Paint();
		mAlphaPaint.setAlpha(100);

		mInvalidatorHandler = new Handler();

		this.setOnTouchListener(new BalanceOnTouchListener());

		loadAndStartTask(new BalanceData(balanceData));
	}

	public void loadAndStartTask(BalanceData balanceData) {
		this.mObjectsOnLeft = balanceData.getObjectsOnLeft();
		this.mObjectsOnRight = balanceData.getObjectsOnRight();
		this.mAvailableObjects = balanceData.getAvaliableObjects();
		this.mFixed = balanceData.isFixed();
		this.mInteractive = balanceData.isInteractive();
		this.setCurrentState(balanceData.getBalanceState());

		float maxHeight = BASE_HEIGHT / 5;
		for (WeightedObject wo : mObjectsOnLeft) {
			mWeightOnLeft += wo.getWeight();
			if (wo.getBitmap().getHeight() > maxHeight) {
				wo.setScalingRatio(maxHeight / wo.getBitmap().getHeight());
			}
		}
		for (WeightedObject wo : mObjectsOnRight) {
			mWeightOnRight += wo.getWeight();
			if (wo.getBitmap().getHeight() > maxHeight) {
				wo.setScalingRatio(maxHeight / wo.getBitmap().getHeight());
			}
		}

		for (WeightedObject wo : mAvailableObjects) {
			if (wo.getBitmap().getHeight() > maxHeight) {
				wo.setScalingRatio(maxHeight / wo.getBitmap().getHeight());
			}
		}

		mTaskLoaded = true;

		mPreviousState = balanceData.getBalanceState();
		mAnimationOngoing = false;
		mDraggedObject = null;
		initCoordinates();
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
		if (heightMode == MeasureSpec.EXACTLY
				|| heightMode == MeasureSpec.AT_MOST) {
			mandatoryHeight = heightSize;
		}
		if (widthMode == MeasureSpec.EXACTLY
				|| widthMode == MeasureSpec.AT_MOST) {
			mandatoryHeight = widthSize;
		}
		mandatoryHeight = Math.min(mandatoryHeight, heightSize);
		mandatoryWidth = Math.min(mandatoryWidth, widthSize);

		float derivedProportion = (float) mandatoryWidth / mandatoryHeight;
		if (derivedProportion >= proportion) {
			this.mViewWidth = Math.round(proportion * mandatoryHeight);
			this.mViewHeight = mandatoryHeight;
		} else {
			this.mViewWidth = mandatoryWidth;
			this.mViewHeight = Math.round(mandatoryWidth / proportion);
		}

		setMeasuredDimension(this.mViewWidth, this.mViewHeight);
	}

	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	protected void initCoordinates() {
		mLeftCupObjectOffset = 0;
		mRightCupObjectOffset = 0;

		mRotationAnimation.reset();

		mPositivePopup.setX(10);
		mPositivePopup.setY(10);
		mNegativePopup.setX(10);
		mNegativePopup.setY(10);

		mSupport.setX(BASE_WIDTH / 2 - mSupport.getBitmap().getWidth() / 2);
		mSupport.setY(BASE_HEIGHT / 2);
		mBeam.setX(mSupport.getX() + mSupport.getBitmap().getWidth() / 2
				- mBeam.getBitmap().getWidth() / 2);
		mBeam.setY(mSupport.getY());
		mBeamBent = new BalanceViewObject(mBeam);
		Collections.sort(mObjectsOnLeft, new WeightedObject.HeightComparator());
		Collections
				.sort(mObjectsOnRight, new WeightedObject.HeightComparator());
		Collections.sort(mAvailableObjects,
				new WeightedObject.HeightComparator());
		switch (mCurrentState) {
		case EQUAL:
			mDegree = 0;
			break;
		case LEFT_IS_HEAVIER:
			mRotationAnimation.postRotate(-14,
					mBeam.getBitmap().getWidth() / 2, mBeam.getBitmap()
							.getHeight() / 2);
			mBeamBent.setX(mSupport.getX() + mSupport.getBitmap().getWidth()
					/ 2 - mBeamBent.getBitmap().getWidth() / 2);
			mBeamBent.setY(mSupport.getY() - mBeamBent.getBitmap().getHeight()
					/ 2 + mBeam.getBitmap().getHeight() / 2);
			mDegree = -14;
			break;
		case RIGHT_IS_HEAVIER:
			mRotationAnimation.postRotate(+14,
					mBeam.getBitmap().getWidth() / 2, mBeam.getBitmap()
							.getHeight() / 2);
			mBeamBent.setX(mSupport.getX() + mSupport.getBitmap().getWidth()
					/ 2 - mBeamBent.getBitmap().getWidth() / 2);
			mBeamBent.setY(mSupport.getY() - mBeamBent.getBitmap().getHeight()
					/ 2 + mBeam.getBitmap().getHeight() / 2);
			mDegree = 14;
			break;
		}
		mRotationAnimation.postTranslate(mBeamBent.getX(), mBeamBent.getY());
		float lever = mBeam.getBitmap().getWidth() / 2 - HORIZONTAL_CUP_OFFSET;
		mLeftCup.setX(mBeam.getX() - mLeftCup.getBitmap().getWidth() / 2
				+ HORIZONTAL_CUP_OFFSET + lever
				* (1 - (float) Math.cos(mDegree * Math.PI / 180f)));
		mLeftCup.setY(mSupport.getY() + mBeam.getBitmap().getHeight() / 2
				- mLeftCup.getBitmap().getHeight() + 5 - lever
				* (float) Math.sin(mDegree * Math.PI / 180f));
		mRightCup.setX(mBeam.getX() - mRightCup.getBitmap().getWidth() / 2
				+ mBeam.getBitmap().getWidth() - HORIZONTAL_CUP_OFFSET - lever
				* (1 - (float) Math.cos(mDegree * Math.PI / 180f)));
		mRightCup.setY(mSupport.getY() + mBeam.getBitmap().getHeight() / 2
				- mRightCup.getBitmap().getHeight() + 5 + lever
				* (float) Math.sin(mDegree * Math.PI / 180f));
		degreeClause = Math.round(mDegree);
		direction = 0;
	}

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

	@Override
	public void onDraw(Canvas canvas) {
		if (!mTaskLoaded) {
			return;
		}
		float scaleWidth = mViewWidth / (float) BASE_WIDTH;
		float scaleHeight = mViewHeight / (float) BASE_HEIGHT;
		mTotalScaleRatio = Math.max(scaleWidth, scaleHeight);
		canvas.save();
		canvas.scale(mTotalScaleRatio, mTotalScaleRatio);

		if (!mFixed) {
			mCurrentState = checkBalance();
			if (mCurrentState != mPreviousState) {
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
				mAnimationOngoing = true;
			}
			if (mAnimationOngoing) {
				balanceCoordinatesAdjustment();
			}
		}
		mLeftCupObjectOffset = 0;
		mRightCupObjectOffset = 0;
		float xOffset = 5;
		float yOffset = Math.round(BASE_HEIGHT * 19 / 20);
		objectCoordinatesAdjustment(xOffset, yOffset, canvas);

		canvas.drawBitmap(mLeftCup.getBitmap(), mLeftCup.getX(),
				mLeftCup.getY(), mAntiAliasingPaint);
		canvas.drawBitmap(mRightCup.getBitmap(), mRightCup.getX(),
				mRightCup.getY(), mAntiAliasingPaint);
		canvas.drawBitmap(mBeamBent.getBitmap(), mRotationAnimation,
				mAntiAliasingPaint);
		canvas.drawBitmap(mSupport.getBitmap(), mSupport.getX(),
				mSupport.getY(), mAntiAliasingPaint);

		if (mPositivePopupVisible) {
			canvas.drawBitmap(mPositivePopup.getBitmap(),
					mPositivePopup.getX(), mPositivePopup.getY(),
					mAntiAliasingPaint);
		}
		if (mNegativePopupVisible) {
			canvas.drawBitmap(mNegativePopup.getBitmap(),
					mNegativePopup.getX(), mNegativePopup.getY(),
					mAntiAliasingPaint);
		}
		canvas.restore();
		mInvalidatorHandler.postDelayed(invalidatorRunnable, 1000 / 60);
	}

	private void balanceCoordinatesAdjustment() {
		float startingSignum = Math.signum(mDegree - degreeClause);
		float lever = mBeam.getBitmap().getWidth() / 2 - HORIZONTAL_CUP_OFFSET;
		if ((mDegree - degreeClause) * startingSignum >= 1e-3) {
			mDegree += (float) direction * 32 / 100;
			if ((mDegree - degreeClause) * startingSignum < 1e-3) {
				mDegree = degreeClause;
			}
			mLeftCup.setX(mBeam.getX() - mLeftCup.getBitmap().getWidth() / 2
					+ HORIZONTAL_CUP_OFFSET + lever
					* (1 - (float) Math.cos(mDegree * Math.PI / 180f)));
			mLeftCup.setY(mSupport.getY() + mBeam.getBitmap().getHeight() / 2
					- mLeftCup.getBitmap().getHeight() + 5 - lever
					* (float) Math.sin(mDegree * Math.PI / 180f));
			mRightCup.setX(mBeam.getX() - mRightCup.getBitmap().getWidth() / 2
					+ mBeam.getBitmap().getWidth() - HORIZONTAL_CUP_OFFSET
					- lever * (1 - (float) Math.cos(mDegree * Math.PI / 180f)));
			mRightCup.setY(mSupport.getY() + mBeam.getBitmap().getHeight() / 2
					- mRightCup.getBitmap().getHeight() + 5 + lever
					* (float) Math.sin(mDegree * Math.PI / 180f));
			mRotationAnimation.reset();
			mBeamBent.setX(mSupport.getX() + mSupport.getBitmap().getWidth()
					/ 2 - mBeam.getBitmap().getWidth() / 2);
			mBeamBent.setY(mSupport.getY() - mBeamBent.getBitmap().getHeight()
					/ 2 + mBeam.getBitmap().getHeight() / 2);
			mRotationAnimation.postRotate(mDegree,
					mBeam.getBitmap().getWidth() / 2, mBeam.getBitmap()
							.getHeight() / 2);
			mRotationAnimation
					.postTranslate(mBeamBent.getX(), mBeamBent.getY());
			for (WeightedObject wo : mObjectsOnRight) {
				wo.setY(mRightCup.getY() + mRightCup.getBitmap().getHeight()
						* 3 / 4 - wo.getBitmap().getHeight());
			}
			for (WeightedObject wo : mObjectsOnLeft) {
				wo.setY(mLeftCup.getY() + mLeftCup.getBitmap().getHeight() * 3
						/ 4 - wo.getBitmap().getHeight());
			}
		} else {
			mAnimationOngoing = false;
		}
	}

	private void objectCoordinatesAdjustment(float xOffset, float yOffset,
			Canvas canvas) {
		for (WeightedObject wo : mAvailableObjects) {
			wo.setX(xOffset);
			wo.setY(yOffset - Math.round(wo.getHeight()));
			destRect.set(wo.getX(), wo.getY(), wo.getX() + wo.getWidth(),
					wo.getY() + wo.getHeight());
			canvas.drawBitmap(wo.getBitmap(), null, destRect,
					mAntiAliasingPaint);
			xOffset = wo.getX() + Math.round(wo.getWidth()) + 5;
		}
		for (WeightedObject wo : mObjectsOnRight) {
			wo.setX(mRightCup.getX() - mRightCupObjectOffset
					+ mRightCup.getBitmap().getWidth() * 86 / 100
					- Math.round(wo.getWidth()));
			wo.setY(mRightCup.getY() + mRightCup.getBitmap().getHeight() * 1
					/ 4 - Math.round(wo.getHeight()));
			destRect.set(wo.getX(), wo.getY(), wo.getX() + wo.getWidth(),
					wo.getY() + wo.getHeight());
			canvas.drawBitmap(wo.getBitmap(), null, destRect,
					mAntiAliasingPaint);
			mRightCupObjectOffset = (mRightCupObjectOffset + mRightCup
					.getBitmap().getWidth() / 4)
					% (mRightCup.getBitmap().getWidth() * 7 / 11);
		}
		for (WeightedObject wo : mObjectsOnLeft) {
			wo.setX(mLeftCup.getX() + mLeftCupObjectOffset
					+ mLeftCup.getBitmap().getWidth() / 7);
			wo.setY(mLeftCup.getY() + mLeftCup.getBitmap().getHeight() * 1 / 4
					- Math.round(wo.getHeight()));
			destRect.set(wo.getX(), wo.getY(), wo.getX() + wo.getWidth(),
					wo.getY() + wo.getHeight());
			canvas.drawBitmap(wo.getBitmap(), null, destRect,
					mAntiAliasingPaint);
			mLeftCupObjectOffset = (mLeftCupObjectOffset + mLeftCup.getBitmap()
					.getWidth() / 4)
					% (mRightCup.getBitmap().getWidth() * 7 / 11);

		}
		if (mDraggedObject != null) {
			destRect.set(mDraggedObject.getX(), mDraggedObject.getY(),
					mDraggedObject.getX() + mDraggedObject.getWidth(),
					mDraggedObject.getY() + mDraggedObject.getHeight());
			canvas.drawBitmap(mDraggedObject.getBitmap(), null, destRect,
					mAlphaPaint);
		}
	}

	public void setCorrectnessPopup(boolean isCorrect) {
		if (isCorrect) {
			mPositivePopupVisible = true;
			mNegativePopupVisible = false;
		} else {
			mPositivePopupVisible = false;
			mNegativePopupVisible = true;
		}

		invalidate();
	}

	public void dismissPopup() {
		mPositivePopupVisible = false;
		mNegativePopupVisible = false;

		invalidate();
	}

	private class BalanceOnTouchListener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			int eventX = (int) (event.getX() / mTotalScaleRatio);
			int eventY = (int) (event.getY() / mTotalScaleRatio);
			if (mAnimationOngoing)
				return false;
			if (!mInteractive)
				return false;
			switch (event.getAction()) {
			case (MotionEvent.ACTION_DOWN):
				if (eventY > BASE_HEIGHT / 2) {
					// that means we've likely got an object from free weights
					for (int i = 0; i < mAvailableObjects.size(); ++i) {
						WeightedObject wo = mAvailableObjects.get(i);
						if (wo.isTouchedWithoutOpacity(eventX, eventY)) {
							getParent()
									.requestDisallowInterceptTouchEvent(true);
							mDraggedObject = new WeightedObject(wo);
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
							mDraggedObject = new WeightedObject(
									mObjectsOnLeft.get(i));
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
							mDraggedObject = new WeightedObject(
									mObjectsOnRight.get(i));
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
							mAvailableObjects.remove(mDraggedObjectIndex);
							Collections.sort(mAvailableObjects,
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
							mAvailableObjects.remove(mDraggedObjectIndex);
							Collections.sort(mAvailableObjects,
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
							mAvailableObjects.remove(mDraggedObjectIndex);
						default:
							break;
						}
						mAvailableObjects.add(mDraggedObject);
						Collections.sort(mAvailableObjects,
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
		// TODO TODO!
		return null;
	}
}