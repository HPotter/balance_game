package com.sutorei.canvasbalance.domain;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class BalanceData {
	private List<WeightedObject> objectsOnLeft;
	private List<WeightedObject> objectsOnRight;
	private List<WeightedObject> avaliableObjects;
	private BalanceState balanceState;
	private boolean interactive;
	private boolean fixed;

	private static String KEY_BALANCE_STATE = "state";
	private static String KEY_BALANCE_LEFT_OBJECTS = "left_objects";
	private static String KEY_BALANCE_RIGHT_OBJECTS = "right_objects";
	private static String KEY_BALANCE_AVAILABLE_OBJECTS = "available_objects";

	protected static BalanceData fromJsonNode(JsonNode rootNode,
			String taskFolder) throws ParseException {
		BalanceData result = new BalanceData();

		// balance state
		result.setBalanceState(BalanceState.fromInt(rootNode.path(
				KEY_BALANCE_STATE).asInt(0)));

		// left objects
		ArrayList<WeightedObject> leftObjects = new ArrayList<WeightedObject>();
		if (rootNode.has(KEY_BALANCE_LEFT_OBJECTS)) {
			JsonNode leftObjectListNode = rootNode
					.path(KEY_BALANCE_LEFT_OBJECTS);
			leftObjects.ensureCapacity(leftObjectListNode.size());

			for (JsonNode weightedObjectNode : leftObjectListNode) {
				leftObjects.add(WeightedObject.fromJsonNode(weightedObjectNode,
						taskFolder + File.separator));
			}
		}
		result.setObjectsOnLeft(leftObjects);

		// right objects
		ArrayList<WeightedObject> rightObjects = new ArrayList<WeightedObject>();
		if (rootNode.has(KEY_BALANCE_RIGHT_OBJECTS)) {
			JsonNode rightObjectListNode = rootNode
					.path(KEY_BALANCE_RIGHT_OBJECTS);
			rightObjects.ensureCapacity(rightObjectListNode.size());

			for (JsonNode weightedObjectNode : rightObjectListNode) {
				rightObjects.add(WeightedObject.fromJsonNode(
						weightedObjectNode, taskFolder + File.separator));
			}
		}
		result.setObjectsOnRight(rightObjects);

		// available objects
		ArrayList<WeightedObject> availableObjects = new ArrayList<WeightedObject>();
		if (rootNode.has(KEY_BALANCE_AVAILABLE_OBJECTS)) {
			JsonNode weightedObjectListNode = rootNode
					.path(KEY_BALANCE_AVAILABLE_OBJECTS);
			availableObjects.ensureCapacity(weightedObjectListNode.size());

			for (JsonNode weightedObjectNode : weightedObjectListNode) {
				availableObjects.add(WeightedObject.fromJsonNode(
						weightedObjectNode, taskFolder + File.separator));
			}
		}
		result.setAvaliableObjects(availableObjects);

		return result;
	}

	public BalanceData() {
	}

	public BalanceData(BalanceData other) {
		this.objectsOnLeft = new ArrayList<WeightedObject>(other.objectsOnLeft);
		this.objectsOnRight = new ArrayList<WeightedObject>(
				other.objectsOnRight);
		this.avaliableObjects = new ArrayList<WeightedObject>(
				other.avaliableObjects);
		this.balanceState = other.balanceState;
		this.interactive = other.interactive;
		this.fixed = other.fixed;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

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
}
