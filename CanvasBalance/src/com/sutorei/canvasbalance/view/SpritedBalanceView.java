package com.sutorei.canvasbalance.view;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.sutorei.canvasbalance.domain.BalanceData;
import com.sutorei.canvasbalance.domain.WeightedObject;
import com.sutorei.canvasbalance.util.BalanceState;
import com.sutorei.canvasbalance.view.graphics.BalanceAnimationType;
import com.sutorei.canvasbalance.view.graphics.BalanceSpriteDrawer;
import com.sutorei.canvasbalance.view.graphics.SpriteContainer;

public class SpritedBalanceView extends View{
	
	private BalanceData balanceData;
	
	private List<WeightedObject> objectsOnLeftCup, objectsOnRightCup, avaliableObjects;
	
	private float weightOnLeftCup, weightOnRightCup, scaleFactor;
	
	private BalanceState currentState;
	
	private boolean fixed, interactive, taskInitialized, animationOngoing, dragOngoing;
	
	private Paint antiAliasingPaint, alphaPaint;
	
	private WeightedObject draggedObject;
	
	private int draggedObjectIndex;
	
	private DraggedObjectOrigin draggedObjectOrigin;
	
	private int viewWidth, viewHeight;
	
	private BalanceSpriteDrawer spriteDrawer;
	
	private Rect balanceRectangle;
	
	private int leftCupOffsetX, leftCupOffsetY, rightCupOffsetX, rightCupOffsetY, cupLength;
	
	private RectF destinationRect;
	
	SpritedBalanceView(Context context, BalanceData balanceData, SpriteContainer container){
		super(context);
		taskInitialized = false;
		spriteDrawer = new BalanceSpriteDrawer(container);
		
		weightOnLeftCup = 0;
		weightOnRightCup = 0;
		
		antiAliasingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		antiAliasingPaint.setFilterBitmap(true);
		antiAliasingPaint.setDither(true);
		alphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		alphaPaint.setAlpha(100);
		alphaPaint.setFilterBitmap(true);
		alphaPaint.setDither(true);
		
		balanceRectangle = new Rect(0, 0, 0, 0);
		destinationRect = new RectF(0, 0, 0, 0);
		loadAndStartTask(balanceData);
		initializeDrawing();
		this.setOnTouchListener(new BalanceOnTouchListener());
	}
	
	private void loadAndStartTask(BalanceData balanceData){
		cupLength = 260; //XXX
		this.objectsOnLeftCup = balanceData.getObjectsOnLeft();
		this.objectsOnRightCup = balanceData.getObjectsOnRight();
		this.avaliableObjects = balanceData.getAvaliableObjects();
		this.currentState = balanceData.getBalanceState();
		this.draggedObject = null;
		
		for (WeightedObject wo : objectsOnLeftCup) {
			weightOnLeftCup += wo.getWeight();
		}
		for (WeightedObject wo : objectsOnRightCup) {
			weightOnRightCup += wo.getWeight();
		}
		
		this.fixed = balanceData.isFixed();
		this.interactive = balanceData.isInteractive();
	}
	
	private void initializeDrawing(){
		balanceRectangle = spriteDrawer.fixedPositionRectangle(currentState);
		//TODO: read from config
		leftCupOffsetX = 5; rightCupOffsetY = spriteDrawer.getSpriteContainer().getFrameWidth() - leftCupOffsetX;
		switch (currentState){
		case EQUAL:
			leftCupOffsetY = rightCupOffsetY = spriteDrawer.getSpriteContainer().getFrameHeight() + 107;
			break;
		case LEFT_IS_HEAVIER:
			leftCupOffsetY =  spriteDrawer.getSpriteContainer().getFrameHeight() + 107 + 23;
			rightCupOffsetY = spriteDrawer.getSpriteContainer().getFrameHeight() + 107 - 23;
			break;
		case RIGHT_IS_HEAVIER:
			leftCupOffsetY =  spriteDrawer.getSpriteContainer().getFrameHeight() + 107 - 23;
			rightCupOffsetY = spriteDrawer.getSpriteContainer().getFrameHeight() + 107 + 23;
			break;
		}
		taskInitialized = true;
	}
	
	private void checkBalance() {
		BalanceState updatedState;
		float eps = (float) 1e-4;
		if (weightOnLeftCup - weightOnRightCup > eps) {
			updatedState = BalanceState.LEFT_IS_HEAVIER;
		} else if (weightOnLeftCup - weightOnRightCup < -eps) {
			 updatedState = BalanceState.RIGHT_IS_HEAVIER;
		} else {
			updatedState = BalanceState.EQUAL;
		}
		if (updatedState != currentState && !fixed){
			AnimationUpdateThread at;
			if (currentState == BalanceState.LEFT_IS_HEAVIER && updatedState == BalanceState.EQUAL){
				at = new AnimationUpdateThread(BalanceAnimationType.LEFT_TO_CENTER);
			} else if (currentState == BalanceState.LEFT_IS_HEAVIER && updatedState == BalanceState.RIGHT_IS_HEAVIER){
				at = new AnimationUpdateThread(BalanceAnimationType.LEFT_TO_RIGHT);
			} else if (currentState == BalanceState.EQUAL && updatedState == BalanceState.LEFT_IS_HEAVIER){
				at = new AnimationUpdateThread(BalanceAnimationType.CENTER_TO_LEFT);
			} else if (currentState == BalanceState.EQUAL && updatedState == BalanceState.RIGHT_IS_HEAVIER){
				at = new AnimationUpdateThread(BalanceAnimationType.CENTER_TO_RIGHT);
			} else if (currentState == BalanceState.RIGHT_IS_HEAVIER && updatedState == BalanceState.EQUAL){
				at = new AnimationUpdateThread(BalanceAnimationType.RIGHT_TO_CENTER);
			} else {
				at = new AnimationUpdateThread(BalanceAnimationType.RIGHT_TO_LEFT);
			}
			Thread t = new Thread(at);
			t.start();
		}
	}
	
	private void adjustObjectScaling(){
		int maxHeight = spriteDrawer.getSpriteContainer().getFrameHeight();
		for (WeightedObject wo : objectsOnLeftCup) {
			if (wo.getBitmap().getHeight() > maxHeight) {
				maxHeight = wo.getBitmap().getHeight();
			}
		}
		for (WeightedObject wo : objectsOnRightCup) {
			if (wo.getBitmap().getHeight() > maxHeight) {
				maxHeight = wo.getBitmap().getHeight();
			}
		}
		for (WeightedObject wo : avaliableObjects) {
			if (wo.getBitmap().getHeight() > maxHeight) {
				maxHeight = wo.getBitmap().getHeight();
			}
		}
		float objectScaleRatio = ((float)maxHeight)/ (viewHeight/3);
		
		for (WeightedObject wo : objectsOnLeftCup) {
			wo.setScalingRatio(objectScaleRatio);
		}
		for (WeightedObject wo : objectsOnRightCup) {
			wo.setScalingRatio(objectScaleRatio);
		}
		for (WeightedObject wo : avaliableObjects) {
			wo.setScalingRatio(objectScaleRatio);
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int desiredWidth = spriteDrawer.getSpriteContainer().getFrameWidth();
		int desiredHeight = spriteDrawer.getSpriteContainer().getFrameHeight() * 3;
		float proportion = ((float)desiredWidth)/desiredHeight;
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int mandatoryHeight = desiredHeight;
		int mandatoryWidth = desiredWidth;
		
		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		
		if (heightMode == MeasureSpec.EXACTLY
				|| heightMode == MeasureSpec.AT_MOST) {
			mandatoryHeight = heightSize;
		}
		if (widthMode == MeasureSpec.EXACTLY
				|| widthMode == MeasureSpec.AT_MOST) {
			mandatoryHeight = widthSize;
		}
		if (mandatoryHeight >= metrics.heightPixels){
			mandatoryHeight = metrics.heightPixels;
		}
		if (mandatoryWidth >= metrics.widthPixels){
			mandatoryWidth = metrics.widthPixels;
		}
		
		float derivedProportion = (float) mandatoryWidth / mandatoryHeight;
		if (derivedProportion >= proportion) {
			setMeasuredDimension(Math.round(proportion * mandatoryHeight),
					mandatoryHeight);
			this.viewWidth = Math.round(proportion * mandatoryHeight);
			this.viewHeight = mandatoryHeight;
		} else {
			setMeasuredDimension(mandatoryWidth,
					Math.round(mandatoryWidth / proportion));
			this.viewWidth = mandatoryWidth;
			this.viewHeight = Math.round(mandatoryWidth / proportion);
		}
		adjustObjectScaling();

		scaleFactor = Math.min((float)this.viewWidth/(float)spriteDrawer.getSpriteContainer().getFrameWidth(),
								(float)this.viewHeight/(float)spriteDrawer.getSpriteContainer().getFrameHeight());
	}
		
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private void scaleRectF(RectF rect, float factor){
		rect.top *= factor;
		rect.bottom *= factor;
		rect.left *= factor;
		rect.right *= factor;
	}
	
	public void onDraw(Canvas canvas){
		//draw balance
		destinationRect.set(0, 
							spriteDrawer.getSpriteContainer().getFrameHeight(),
							spriteDrawer.getSpriteContainer().getFrameWidth(),
							spriteDrawer.getSpriteContainer().getFrameHeight() * 2);
		scaleRectF(destinationRect, scaleFactor);
		Log.d("DEST_RECT_BALANCE", destinationRect.toString());
		Log.d("SOURCE_RECT_BALANCE", balanceRectangle.toString());
		canvas.drawBitmap(spriteDrawer.getSpriteContainer().getSpriteSheet(), balanceRectangle, destinationRect, antiAliasingPaint);
		//draw avaliableObjects
		int xOffset = 0;
		for (WeightedObject wo : avaliableObjects){
			destinationRect.set(xOffset, 
								(spriteDrawer.getSpriteContainer().getFrameHeight() * 3 - wo.getHeight()),
								(xOffset + wo.getWidth()),
								spriteDrawer.getSpriteContainer().getFrameHeight() * 3);
			xOffset += wo.getWidth() + 5;
			scaleRectF(destinationRect, scaleFactor);
			Log.d("DEST_RECT_AVALIABLE", destinationRect.toString());
			canvas.drawBitmap(wo.getBitmap(), null, destinationRect, antiAliasingPaint);
		}
		//draw objects on left cup
		xOffset = leftCupOffsetX;
		for (WeightedObject wo : objectsOnLeftCup){
			destinationRect.set(xOffset, 
					leftCupOffsetY - wo.getHeight(),
					xOffset + wo.getWidth(),
					leftCupOffsetY);
			scaleRectF(destinationRect, scaleFactor);
			Log.d("DEST_RECT_LEFT", destinationRect.toString());
			xOffset = leftCupOffsetX + (xOffset - leftCupOffsetX + Math.round(wo.getWidth()*scaleFactor))%cupLength; //FIXME
			canvas.drawBitmap(wo.getBitmap(), null, destinationRect, antiAliasingPaint);
		}
		
		//draw objects on right cup
		xOffset = rightCupOffsetX;
		for (WeightedObject wo : objectsOnRightCup){
			destinationRect.set(xOffset - wo.getWidth(), 
					rightCupOffsetY - wo.getHeight(),
					xOffset,
					rightCupOffsetY);
			scaleRectF(destinationRect, scaleFactor);
			Log.d("DEST_RECT_RIGHT", destinationRect.toString());
			xOffset = rightCupOffsetX - ( - xOffset + rightCupOffsetX - Math.round(wo.getWidth()*scaleFactor))%cupLength; //FIXME
			canvas.drawBitmap(wo.getBitmap(), null, destinationRect, antiAliasingPaint);
		}
		
		//draw the dragged object
		if (draggedObject != null){
			destinationRect.set(draggedObject.getX() - draggedObject.getWidth()*scaleFactor,
					draggedObject.getY() - draggedObject.getHeight()*scaleFactor,
					draggedObject.getX() + draggedObject.getWidth()*scaleFactor,
					draggedObject.getY() + draggedObject.getHeight()*scaleFactor);
			Log.d("DEST_RECT_AVALIABLE", destinationRect.toString());
			canvas.drawBitmap(draggedObject.getBitmap(), null, destinationRect, alphaPaint);
		}
	}
	
	private enum DraggedObjectOrigin {
		FREE_OBJECTS, OBJECTS_ON_LEFT, OBJECTS_ON_RIGHT;
	}
	
	private class BalanceOnTouchListener implements View.OnTouchListener {
		
		private void afterDrop(){
			switch (draggedObjectOrigin){
			case OBJECTS_ON_LEFT:
				objectsOnLeftCup.remove(draggedObjectIndex);
				Collections.sort(objectsOnLeftCup, new WeightedObject.HeightComparator());
				weightOnLeftCup -= draggedObject.getWeight();
				break;
			case FREE_OBJECTS:
				avaliableObjects.remove(draggedObjectIndex);
				Collections.sort(avaliableObjects, new WeightedObject.HeightComparator());
				break;
			case OBJECTS_ON_RIGHT:
				objectsOnRightCup.remove(draggedObjectIndex);
				Collections.sort(objectsOnRightCup, new WeightedObject.HeightComparator());
				weightOnRightCup -= draggedObject.getWeight();
			}
		}

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			if (!interactive){
				return false;
			}
			int eventX = (int) (event.getX() / scaleFactor);
			int eventY = (int) (event.getY() / scaleFactor);
			boolean eventHandled = false;
			switch (event.getAction()) {
			case (MotionEvent.ACTION_DOWN):
				if (eventY >= spriteDrawer.getSpriteContainer().getFrameHeight()*2){
					for (int i = 0; i < avaliableObjects.size(); ++i) {
						WeightedObject wo = avaliableObjects.get(i);
						if (wo.isTouchedWithoutOpacity(eventX, eventY)) {
							getParent()
									.requestDisallowInterceptTouchEvent(true);
							draggedObject = (WeightedObject) wo.copy();
							draggedObjectIndex = i;
							draggedObjectOrigin = DraggedObjectOrigin.FREE_OBJECTS;
							dragOngoing = true;
							invalidate();
							eventHandled = true;
						}
					}
				} else {
					// check objects on cups
					for (int i = objectsOnLeftCup.size() - 1; i >= 0; --i) {
						// objects sorted from farthest to closest to user
						// so we'll check them in backwards order
						if (objectsOnLeftCup.get(i).isTouched(eventX, eventY)) {
							getParent()
									.requestDisallowInterceptTouchEvent(true);
							draggedObject = (WeightedObject) objectsOnLeftCup.get(i).copy();
							draggedObjectIndex = i;
							draggedObjectOrigin = DraggedObjectOrigin.OBJECTS_ON_LEFT;
							dragOngoing = true;
							invalidate();
							eventHandled = true;
						}
					}
					for (int i = objectsOnRightCup.size() - 1; i >= 0; --i) {
						if (objectsOnRightCup.get(i).isTouched(eventX, eventY)) {
							getParent()
									.requestDisallowInterceptTouchEvent(true);
							draggedObject = objectsOnRightCup.get(i).copy();
							draggedObjectIndex = i;
							draggedObjectOrigin = DraggedObjectOrigin.OBJECTS_ON_RIGHT;
							dragOngoing = true;
							invalidate();
							eventHandled = true;
						}
					}
				}
				break;
			case (MotionEvent.ACTION_MOVE):
				if (dragOngoing) {
					//TODO: highlight zones
					if (eventX + draggedObject.getBitmap().getWidth() / 2 <= viewWidth/scaleFactor
							&& eventX - draggedObject.getBitmap().getWidth()
									/ 2 >= 0) {
						draggedObject.setX((int) Math.round(eventX
								- draggedObject.getBitmap().getWidth() / 2));
					}
					if (eventY + draggedObject.getBitmap().getHeight() / 2 <= viewHeight/scaleFactor
							&& eventY - draggedObject.getBitmap().getHeight()
									/ 2 >= 0) {
						draggedObject.setY((int) Math.round(eventY
								- draggedObject.getBitmap().getHeight() / 2));
					}
					invalidate();
					eventHandled = true;
				}
				break;
			case (MotionEvent.ACTION_UP):
				if (dragOngoing){
					//check if dropped above the left cup; to simplify things cup widths are considered halves of the views
					if (eventY <= viewHeight/scaleFactor * (2/3) && eventX <= viewWidth/scaleFactor/2){
						//that means the left cup
						afterDrop();
						objectsOnLeftCup.add(draggedObject);
						weightOnLeftCup += draggedObject.getWeight();
						Collections.sort(objectsOnLeftCup, new WeightedObject.HeightComparator());
					} else if (eventY <= viewHeight/scaleFactor * (2/3) && eventX > viewWidth/scaleFactor/2){
						//that means the right cup
						afterDrop();
						objectsOnRightCup.add(draggedObject);
						weightOnRightCup += draggedObject.getWeight();
						Collections.sort(objectsOnRightCup, new WeightedObject.HeightComparator());
					} else {
						//then object goes to the pool of avaliable
						afterDrop();
						avaliableObjects.add(draggedObject);
						Collections.sort(avaliableObjects, new WeightedObject.HeightComparator());
					}
					draggedObject = null;
					dragOngoing = false;
					eventHandled = true;
					checkBalance();
					invalidate();
				}
				break;
			}
			return eventHandled;
		}
	}
	
	private class AnimationUpdateThread implements Runnable{
		private int finalFrameNumber;
		private BalanceAnimationType type;

		public AnimationUpdateThread(BalanceAnimationType t){
			t = type;
			finalFrameNumber = spriteDrawer.framesInAnimation(type);
		}
		@Override
		public void run() {
			animationOngoing = true;
			int frameNumber = 0;
			while (frameNumber < finalFrameNumber){
				balanceRectangle = spriteDrawer.animationFrameRectangle(type, frameNumber);
				switch (type){
				case LEFT_TO_RIGHT:
				case LEFT_TO_CENTER:
				case CENTER_TO_RIGHT:
					leftCupOffsetY -= 2;
					rightCupOffsetY += 2;
					break;
				case RIGHT_TO_LEFT:
				case RIGHT_TO_CENTER:
				case CENTER_TO_LEFT:
					leftCupOffsetY += 2;
					rightCupOffsetY -= 2;
					break;
				}
				frameNumber++;
				try {
					Thread.sleep(1000/24);
				} catch (InterruptedException e) {

				}
			}
			animationOngoing = false;
		}
		
	}
}