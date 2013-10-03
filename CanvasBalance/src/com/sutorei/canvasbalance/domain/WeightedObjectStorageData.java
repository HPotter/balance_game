package com.sutorei.canvasbalance.domain;

public class WeightedObjectStorageData {
	private int weight;
	private String imageName; //link from 
	
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageRelativeLink(String imageName) {
		this.imageName = imageName;
	}
}
