package com.sutorei.canvasbalance.domain;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TaskData {

	private int taskNumber, lessonNumber;
	private TaskType taskType;
	private String taskText;
	private List<String> balanceText;
	private List<Integer> correctAnswers;
	private List<BalanceData> balanceData;
	private List<WeightedObject> questions; // TODO RENAME

	private static String KEY_TASK_TYPE = "task_type";
	private static String KEY_TASK_TEXT = "task_text";
	private static String KEY_BALANCE_DATA = "balance_data";
	private static String KEY_AVAILABLE_OBJECTS = "available_objects";
	private static String KEY_CORRECT_ANSWER = "correct_answer";
	private static String KEY_BALANCE_TEXT = "balance_text";

	public static TaskData fromJsonFile(File jsonFile, File taskFolder)
			throws ParseException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = mapper.readTree(jsonFile);
		} catch (JsonProcessingException e) {
			Log.e("Parser(File file)", "JsonProcessingException while parsing "
					+ jsonFile.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("Parser(File file)", "IOException while parsing file "
					+ jsonFile.getAbsolutePath());
			e.printStackTrace();
		}

		return fromJsonNode(rootNode, taskFolder);
	}

	protected static TaskData fromJsonNode(JsonNode rootNode, File taskFolder)
			throws ParseException {
		if (!rootNode.has(KEY_TASK_TYPE)) {
			throw new ParseException(KEY_TASK_TYPE + " not found in json", 0);
		}
		if (!rootNode.has(KEY_TASK_TEXT)) {
			throw new ParseException(KEY_TASK_TEXT + " not found in json", 0);
		}

		TaskData result = new TaskData();

		result.setTaskType(TaskType.valueOf(rootNode.path(KEY_TASK_TYPE)
				.asText()));
		result.setTaskText(rootNode.path(KEY_TASK_TEXT).asText());
		
		if (rootNode.has(KEY_BALANCE_DATA)) {
			JsonNode balanceDataListNode = rootNode.path(KEY_BALANCE_DATA);
			ArrayList<BalanceData> balanceDatas = new ArrayList<BalanceData>(
					balanceDataListNode.size());
			ArrayList<String> balanceText = new ArrayList<String>(
					balanceDataListNode.size());
			ArrayList<Integer> correctAnswers = new ArrayList<Integer>(
					balanceDataListNode.size());

			for (JsonNode balanceDataNode : balanceDataListNode) {
				if (balanceDataNode.has(KEY_CORRECT_ANSWER)) {
					correctAnswers.add(balanceDataNode.path(KEY_CORRECT_ANSWER)
							.asInt(0));
				} else {
					correctAnswers.add(null);
				}
				
				if (balanceDataNode.has(KEY_BALANCE_TEXT)) {
					balanceText.add(balanceDataNode.path(KEY_BALANCE_TEXT)
							.asText());
				} else {
					balanceText.add("");
				}
				
				balanceDatas.add(BalanceData.fromJsonNode(balanceDataNode,
						taskFolder + File.separator));
			}

			result.setBalanceData(balanceDatas);
			result.setBalanceText(balanceText);
			result.setCorrectAnswers(correctAnswers);
		}
		if (rootNode.has(KEY_AVAILABLE_OBJECTS)) {
			JsonNode balanceAvailableObjectListNode = rootNode
					.path(KEY_AVAILABLE_OBJECTS);
			ArrayList<WeightedObject> balanceAvailableObjects = new ArrayList<WeightedObject>(
					balanceAvailableObjectListNode.size());

			for (JsonNode balanceAvailableObjectNode : balanceAvailableObjectListNode) {
				balanceAvailableObjects.add(WeightedObject
						.fromJsonNode(balanceAvailableObjectNode, taskFolder
								+ File.separator));
			}

			result.setQuestions(balanceAvailableObjects);
		}

		return result;
	}

	public List<WeightedObject> getQuestions() {
		return questions;
	}

	public void setQuestions(List<WeightedObject> questions) {
		this.questions = questions;
	}

	public int getTaskNumber() {
		return taskNumber;
	}

	public void setTaskNumber(int taskNumber) {
		this.taskNumber = taskNumber;
	}

	public int getLessonNumber() {
		return lessonNumber;
	}

	public void setLessonNumber(int lessonNumber) {
		this.lessonNumber = lessonNumber;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public List<BalanceData> getBalanceData() {
		return balanceData;
	}

	public void setBalanceData(List<BalanceData> balanceData) {
		this.balanceData = balanceData;
	}

	public String getTaskText() {
		return taskText;
	}

	public void setTaskText(String taskText) {
		this.taskText = taskText;
	}

	public List<String> getBalanceText() {
		return balanceText;
	}

	public void setBalanceText(List<String> balanceText) {
		this.balanceText = balanceText;
	}

	public List<Integer> getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(List<Integer> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

}