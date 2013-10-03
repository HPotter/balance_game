package com.sutorei.canvasbalance.domain;

import java.util.List;

public class TaskData {

	private int taskNumber, lessonNumber;
	private TaskType taskType;
	private List<BalanceData> balanceData;
	private List<WeightedObject> questions;

	/**
	 * @return the questions
	 */
	public List<WeightedObject> getQuestions() {
		return questions;
	}

	/**
	 * @param questions the questions to set
	 */
	public void setQuestions(List<WeightedObject> questions) {
		this.questions = questions;
	}

	private String taskText;

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

}