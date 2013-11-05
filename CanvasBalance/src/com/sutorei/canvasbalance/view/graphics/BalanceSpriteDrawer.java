package com.sutorei.canvasbalance.view.graphics;

import com.sutorei.canvasbalance.util.BalanceState;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class BalanceSpriteDrawer {

	private final SpriteContainer container;
	
	private RectF destinationRect;
	
	private Rect sourceRect;
	
	private Rect tempRect;
		
	public BalanceSpriteDrawer(SpriteContainer balanceSpriteContainer) {
		this.container = balanceSpriteContainer;
		sourceRect = new Rect(0, 0, balanceSpriteContainer.getFrameWidth(), balanceSpriteContainer.getFrameHeight());
		destinationRect = new RectF(0, 0, balanceSpriteContainer.getFrameWidth(), balanceSpriteContainer.getFrameHeight());
		tempRect = new Rect(0, 0, 0, 0);
	}

	public int framesInAnimation(BalanceAnimationType type){
		int result = -1488;
		switch(type){
		case CENTER_TO_LEFT:
		case LEFT_TO_CENTER:
			result = container.getFrameNumberInRow(0);
		case CENTER_TO_RIGHT:
		case RIGHT_TO_CENTER:
			result = container.getFrameNumberInRow(1);
		case LEFT_TO_RIGHT:
		case RIGHT_TO_LEFT:
			result = container.getFrameNumberInRow(0) +
					container.getFrameNumberInRow(1);
		}
		return result;
	}
	
	public Rect fixedPositionRectangle(BalanceState state){
		switch(state){
		case LEFT_IS_HEAVIER:
			tempRect.set(0, container.getFrameHeight(), container.getFrameWidth(), container.getFrameHeight()*2);
			break;
		case EQUAL:
			tempRect.set(0, 0, container.getFrameWidth(), container.getFrameHeight());
			break;
		case RIGHT_IS_HEAVIER:
			tempRect.set(container.getFrameNumberInRow(0) * (container.getFrameWidth() + container.getFramePadding()), 0, 
					container.getFrameNumberInRow(0) * (container.getFrameWidth() + container.getFramePadding()) + container.getFrameWidth(), container.getFrameHeight());
			break;
		}
		return tempRect;
	}
	
	public void drawFixedPosition(Canvas canvas, float left, float top, Paint paint, BalanceState state){
		destinationRect.set(left, top, container.getFrameWidth(), container.getFrameHeight());
		sourceRect = fixedPositionRectangle(state);
		canvas.drawBitmap(container.getSpriteSheet(), sourceRect, destinationRect, paint);
	}
	
	public Rect animationFrameRectangle(BalanceAnimationType type, int frameNumber){
		int frameLeft = 0, frameTop = 0;
		switch (type){
		case CENTER_TO_LEFT:
			frameLeft = frameNumber * (container.getFrameWidth() + container.getFramePadding());
			frameTop = 0;
			break;
		case LEFT_TO_CENTER:
			frameLeft = (container.getFrameNumberInRow(0) - frameNumber) * (container.getFrameWidth()  + container.getFramePadding());
			frameTop = 0;
			break;
		case CENTER_TO_RIGHT:
			frameLeft = (container.getFrameNumberInRow(0) - frameNumber) * (container.getFrameWidth()  + container.getFramePadding());
			frameTop = container.getFrameHeight();
			break;
		case RIGHT_TO_CENTER:
			frameLeft = frameNumber * (container.getFrameWidth()  + container.getFramePadding());
			frameTop = container.getFrameHeight();
			break;
		case LEFT_TO_RIGHT:
			if(frameNumber < container.getFrameNumberInRow(0)){
				frameLeft = (container.getFrameNumberInRow(0) - frameNumber) * (container.getFrameWidth() + container.getFramePadding());
				frameTop = 0;
			} else {
				frameLeft = (container.getFrameNumberInRow(0) - frameNumber) * (container.getFrameWidth() + container.getFramePadding());
				frameTop = container.getFrameHeight();
			}
			break;
		case RIGHT_TO_LEFT:
			if(frameNumber < container.getFrameNumberInRow(1)){
				frameLeft = ( -container.getFrameNumberInRow(0) + frameNumber) * (container.getFrameWidth() + container.getFramePadding());
				frameTop = container.getFrameHeight();
			} else {
				frameLeft = (frameNumber - container.getFrameNumberInRow(1)) * (container.getFrameWidth() + container.getFramePadding());
				frameTop = 0;
			}
			break;
			//TODO: throw exception on default
		}
		tempRect.set(frameLeft, frameTop, container.getFrameWidth(), container.getFrameHeight());
		return tempRect;
	}
	public void drawAnimationFrame(Canvas canvas, float left, float top, Paint paint, BalanceAnimationType type, int frameNumber){
		destinationRect.set(left, top, container.getFrameWidth(), container.getFrameHeight());
		sourceRect = animationFrameRectangle(type, frameNumber);
		canvas.drawBitmap(container.getSpriteSheet(), sourceRect, destinationRect, paint);
	}

	public SpriteContainer getSpriteContainer() {
		return container;
	}
	
	
}
