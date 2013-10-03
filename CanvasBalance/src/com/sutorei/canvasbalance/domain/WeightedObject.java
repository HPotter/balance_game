package com.sutorei.canvasbalance.domain;

import java.util.Comparator;

import android.content.Context;
import android.graphics.BitmapFactory;

public class WeightedObject extends BalanceViewObject{
	protected float weight;
	
	public WeightedObject(){
		weight = 0f;
		sprite = null;
		x = 0;
		y = 0;
	}
	public WeightedObject(int resourceId, Context context, float _weight){
		super(resourceId, context);
		weight = _weight;
	}
	
	public WeightedObject(String imageLink, float _weight){
		x = 0; y = 0;
		weight = _weight;
		sprite = BitmapFactory.decodeFile(imageLink);
	}
	
	public WeightedObject copy(){
		WeightedObject copy = new WeightedObject();
		copy.x = this.x;
		copy.y = this.y;
		copy.sprite = this.sprite;
		copy.weight = this.weight;
		return copy;
	}
	
	public float getWeight(){
		return weight;
	}
	
	public static class WeightComparator implements Comparator<WeightedObject>{

		@Override
		public int compare(WeightedObject arg0, WeightedObject arg1) {
			return (int)Math.round(arg0.getWeight() - arg1.getWeight());
		}
		
	}
	
	public static class HeightComparator implements Comparator<WeightedObject>{

		@Override
		public int compare(WeightedObject lhs, WeightedObject rhs) {
			return rhs.sprite.getHeight() - lhs.sprite.getHeight();
		}
		
	}

}
