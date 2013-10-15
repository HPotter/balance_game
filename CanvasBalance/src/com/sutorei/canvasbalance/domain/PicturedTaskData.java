package com.sutorei.canvasbalance.domain;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.graphics.Bitmap;
import android.util.Log;

public class PicturedTaskData {
	private String taskText;
	private String taskAnswer;
	private List<Bitmap> balances;
	
	public PicturedTaskData(String taskText, String taskAnswer,
			List<Bitmap> balances) {
		super();
		this.taskText = taskText;
		this.taskAnswer = taskAnswer;
		this.balances = balances;
	}
	
}
