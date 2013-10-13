package com.sutorei.canvasbalance.view.game;

import java.io.File;

import android.content.Context;
import android.view.ViewGroup;

import com.sutorei.canvasbalance.domain.TaskData;

public abstract class GameMode {
	private TaskData mTaskData = null;
	private Context mContext = null;
	private ViewGroup mParentView = null;
	private File mExtensionStyleFolder = null;

	public GameMode(Context context, ViewGroup parentView, TaskData taskData, File extensionStyleFolder) {
		mTaskData = taskData;
		mContext = context;
		mParentView = parentView;
		mExtensionStyleFolder = extensionStyleFolder;
	}

	public TaskData getTaskData() {
		return mTaskData;
	}

	public Context getContext() {
		return mContext;
	}
	
	public ViewGroup getParentView() {
		return mParentView;
	}
	
	public File getExtensionStyleFolder() {
		return mExtensionStyleFolder;
	}

	public abstract boolean check();

	public abstract void restart();
}
