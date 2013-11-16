package com.sutorei.canvasbalance.domain;

import java.text.ParseException;
import java.util.Comparator;

import com.fasterxml.jackson.databind.JsonNode;
import com.sutorei.canvasbalance.util.BalanceObjectBitmapCache;

public class WeightedObject extends BalanceViewObject {
	protected float weight;
	protected float scalingRatio;

	private static String KEY_BALANCE_OBJECT_IMAGE = "image";
	private static String KEY_BALANCE_OBJECT_WEIGHT = "weight";

	protected static WeightedObject fromJsonNode(JsonNode rootNode,
			String imageFolder) throws ParseException {
		if (!rootNode.has(KEY_BALANCE_OBJECT_IMAGE)) {
			throw new ParseException(KEY_BALANCE_OBJECT_IMAGE
					+ " not found in json", 0);
		}
		if (!rootNode.has(KEY_BALANCE_OBJECT_WEIGHT)) {
			throw new ParseException(KEY_BALANCE_OBJECT_WEIGHT
					+ " not found in json", 0);
		}

		return new WeightedObject(imageFolder
				+ rootNode.path(KEY_BALANCE_OBJECT_IMAGE).asText(), rootNode
				.path(KEY_BALANCE_OBJECT_WEIGHT).asInt());
	}

	public WeightedObject() {
		weight = 0f;
		sprite = null;
		x = 0;
		y = 0;
		scalingRatio = 1;
	}

	public WeightedObject(String imageLink, float weight) {
		this();
		
		this.weight = weight;
		sprite = BalanceObjectBitmapCache.getBitmap(imageLink);
	}

	// TODO Object.clone() ?
	public WeightedObject copy() {
		WeightedObject copy = new WeightedObject();
		copy.x = this.x;
		copy.y = this.y;
		copy.sprite = this.sprite;
		copy.weight = this.weight;
		copy.scalingRatio = this.scalingRatio;
		return copy;
	}

	public float getWeight() {
		return weight;
	}

	public float getScalingRatio() {
		return scalingRatio;
	}

	public void setScalingRatio(float scalingRatio) {
		this.scalingRatio = scalingRatio;
	}

	public float getHeight() {
		return sprite.getHeight() * scalingRatio;
	}

	public float getWidth() {
		return sprite.getWidth() * scalingRatio;
	}

	@Override
	public boolean isTouchedWithoutOpacity(float touchX, float touchY) {
		if (touchX - x >= 0 && touchX - x <= sprite.getWidth() * scalingRatio
				&& touchY - y >= 0
				&& touchY - y <= sprite.getHeight() * scalingRatio) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isTouched(float touchX, float touchY) {
		if (touchX - x >= 0 && touchX - x <= sprite.getWidth() * scalingRatio
				&& touchY - y >= 0
				&& touchY - y <= sprite.getHeight() * scalingRatio) {
			int color = sprite.getPixel((int) Math.ceil(touchX - x),
					(int) Math.ceil(touchY - y));
			return !((color & 0xff000000) == 0x0);
		}
		return false;
	}

	public static class WeightComparator implements Comparator<WeightedObject> {

		@Override
		public int compare(WeightedObject arg0, WeightedObject arg1) {
			return Math.round(arg0.getWeight() - arg1.getWeight());
		}

	}

	public static class HeightComparator implements Comparator<WeightedObject> {

		@Override
		public int compare(WeightedObject lhs, WeightedObject rhs) {
			return rhs.sprite.getHeight() - lhs.sprite.getHeight();
		}

	}

}
