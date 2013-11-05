package com.sutorei.canvasbalance.view.graphics;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class SpriteContainer {
	private final int frameWidth, frameHeight;
	private final List<Integer> framesInRow;
	private final Bitmap spriteSheet;
	private final int spritePadding;

	public SpriteContainer(int frameWidth, int frameHeight, List<Integer> framesInRow, Bitmap spriteSheet, int spritePadding) {
		super();
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.framesInRow = framesInRow;
		this.spriteSheet = spriteSheet;
		this.spritePadding = spritePadding;
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	public int getFrameNumberInRow(int rowNumber) {
		return framesInRow.get(rowNumber);
	}

	public Bitmap getSpriteSheet() {
		return spriteSheet;
	}
	
	public int getFramePadding(){
		return spritePadding;
	}
}