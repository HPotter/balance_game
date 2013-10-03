package com.sutorei.canvasbalance.domain;

import java.util.List;

import com.sutorei.canvasbalance.util.BalanceState;

public class BalanceData {
	private List<WeightedObject> objectsOnLeft, objectsOnRight, avaliableObjects;
	private boolean interactive;
	private BalanceState balanceState;
	private boolean fixed;
	
	/**
	 * @return the fixed
	 */
	public boolean isFixed() {
		return fixed;
	}
	/**
	 * @param fixed the fixed to set
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	/**
	 * @param interactive the interactive to set
	 */
	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
	}
	public BalanceState getBalanceState() {
		return balanceState;
	}
	public void setBalanceState(BalanceState balanceState) {
		this.balanceState = balanceState;
	}
	public List<WeightedObject> getObjectsOnLeft() {
		return objectsOnLeft;
	}
	public void setObjectsOnLeft(List<WeightedObject> objectsOnLeft) {
		this.objectsOnLeft = objectsOnLeft;
	}
	public List<WeightedObject> getObjectsOnRight() {
		return objectsOnRight;
	}
	public void setObjectsOnRight(List<WeightedObject> objectsOnRight) {
		this.objectsOnRight = objectsOnRight;
	}
	public List<WeightedObject> getAvaliableObjects() {
		return avaliableObjects;
	}
	public void setAvaliableObjects(List<WeightedObject> avaliableObjects) {
		this.avaliableObjects = avaliableObjects;
	}
	public boolean isInteractive() {
		return interactive;
	}
	public void setInteractivity(boolean interactivity) {
		this.interactive = interactivity;
	}
	
	
}
