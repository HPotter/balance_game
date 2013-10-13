package com.sutorei.canvasbalance.domain;

import java.text.ParseException;
import java.util.Comparator;

import com.fasterxml.jackson.databind.JsonNode;

import android.content.Context;
import android.graphics.BitmapFactory;

public class WeightedObject extends BalanceViewObject {
	@Override
	public String toString() {
		return "WeightedObject [weight=" + weight + ", filePath="
				+ filePath + "]";
	}

	protected float weight;
	private String filePath;
	protected float scalingRatio;

	private static String KEY_BALANCE_OBJECT_IMAGE = "image";
	private static String KEY_BALANCE_OBJECT_WEIGHT = "weight";

	protected static WeightedObject fromJsonNode(JsonNode rootNode, String imageFolder) throws ParseException {
		if (!rootNode.has(KEY_BALANCE_OBJECT_IMAGE)) {
			throw new ParseException(KEY_BALANCE_OBJECT_IMAGE
					+ " not found in json", 0);
		}
		if (!rootNode.has(KEY_BALANCE_OBJECT_WEIGHT)) {
			throw new ParseException(KEY_BALANCE_OBJECT_WEIGHT
					+ " not found in json", 0);
		}
		
		return new WeightedObject(imageFolder + rootNode.path(KEY_BALANCE_OBJECT_IMAGE).asText(), rootNode.path(KEY_BALANCE_OBJECT_WEIGHT).asInt());
	}

	public WeightedObject() {
		weight = 0f;
		sprite = null;
		x = 0;
		y = 0;
		scalingRatio = 1;
	}

	public WeightedObject(int resourceId, Context context, float _weight) {
		super(resourceId, context);
		weight = _weight;
		x = 0;
		y = 0;
		scalingRatio = 1;
	}

	public WeightedObject(int resourceId, Context context, float _weight,
			float _scalingRatio) {
		super(resourceId, context);
		weight = _weight;
		x = 0;
		y = 0;
		scalingRatio = _scalingRatio;
	}

	public WeightedObject(String imageLink, float _weight) {
		filePath = imageLink;
		x = 0;
		y = 0;
		weight = _weight;
		sprite = BitmapFactory.decodeFile(imageLink);
		scalingRatio = 1;
	}

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
