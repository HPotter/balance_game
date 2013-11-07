package com.sutorei.canvasbalance.domain;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.sutorei.canvasbalance.util.BalanceState;

public class BalanceData {
	@Override
	public String toString() {
		return "BalanceData [objectsOnLeft=" + objectsOnLeft
				+ ", objectsOnRight=" + objectsOnRight + ", avaliableObjects="
				+ avaliableObjects + ", interactive=" + interactive
				+ ", balanceState=" + balanceState + ", fixed=" + fixed + "]";
	}

	private List<WeightedObject> objectsOnLeft, objectsOnRight,
			avaliableObjects;
	private boolean interactive;
	private BalanceState balanceState;
	private boolean fixed;

	private static String KEY_BALANCE_STATE = "state";
	// private static String KEY_BALANCE_FIXED = "fixed";
	// private static String KEY_BALANCE_INTERACTIVE = "interactive";
	private static String KEY_BALANCE_LEFT_OBJECTS = "left_objects";
	private static String KEY_BALANCE_RIGHT_OBJECTS = "right_objects";
	private static String KEY_BALANCE_AVAILABLE_OBJECTS = "available_objects";

	protected static BalanceData fromJsonNode(JsonNode rootNode,
			String taskFolder) throws ParseException {
		// if (!rootNode.has(KEY_BALANCE_STATE)) {
		// throw new ParseException(KEY_BALANCE_STATE + " not found in json",
		// 0);
		// }
		// if (!rootNode.has(KEY_BALANCE_FIXED)) {
		// throw new ParseException(KEY_BALANCE_FIXED + " not found in json",
		// 0);
		// }
		// if (!rootNode.has(KEY_BALANCE_INTERACTIVE)) {
		// throw new ParseException(KEY_BALANCE_INTERACTIVE
		// + " not found in json", 0);
		// }

		BalanceData result = new BalanceData();
		result.setBalanceState(BalanceState.fromInt(rootNode.path(
				KEY_BALANCE_STATE).asInt(0)));
		// result.setFixed(rootNode.path(KEY_BALANCE_FIXED).asBoolean());
		// result.setInteractive(rootNode.path(KEY_BALANCE_INTERACTIVE)
		// .asBoolean());

		if (rootNode.has(KEY_BALANCE_LEFT_OBJECTS)) {
			JsonNode weightedObjectListNode = rootNode
					.path(KEY_BALANCE_LEFT_OBJECTS);
			ArrayList<WeightedObject> weightedObjects = new ArrayList<WeightedObject>(
					weightedObjectListNode.size());

			for (JsonNode weightedObjectNode : weightedObjectListNode) {
				weightedObjects.add(WeightedObject.fromJsonNode(
						weightedObjectNode, taskFolder + File.separator));
			}

			result.setObjectsOnLeft(weightedObjects);
		} else {
			result.setObjectsOnLeft(new ArrayList<WeightedObject>());
		}
		if (rootNode.has(KEY_BALANCE_AVAILABLE_OBJECTS)) {
			JsonNode weightedObjectListNode = rootNode
					.path(KEY_BALANCE_AVAILABLE_OBJECTS);
			ArrayList<WeightedObject> weightedObjects = new ArrayList<WeightedObject>(
					weightedObjectListNode.size());

			for (JsonNode weightedObjectNode : weightedObjectListNode) {
				weightedObjects.add(WeightedObject.fromJsonNode(
						weightedObjectNode, taskFolder + File.separator));
			}

			result.setAvaliableObjects(weightedObjects);
		} else {
			result.setAvaliableObjects(new ArrayList<WeightedObject>());
		}
		if (rootNode.has(KEY_BALANCE_RIGHT_OBJECTS)) {
			JsonNode weightedObjectListNode = rootNode
					.path(KEY_BALANCE_RIGHT_OBJECTS);
			ArrayList<WeightedObject> weightedObjects = new ArrayList<WeightedObject>(
					weightedObjectListNode.size());

			for (JsonNode weightedObjectNode : weightedObjectListNode) {
				weightedObjects.add(WeightedObject.fromJsonNode(
						weightedObjectNode, taskFolder + File.separator));
			}

			result.setObjectsOnRight(weightedObjects);
		} else {
			result.setObjectsOnRight(new ArrayList<WeightedObject>());
		}

		return result;
	}

	/**
	 * @return the fixed
	 */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * @param fixed
	 *            the fixed to set
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	/**
	 * @param interactive
	 *            the interactive to set
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
}
