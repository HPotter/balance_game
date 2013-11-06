package com.sutorei.canvasbalance.view.game;

import java.io.File;

import android.content.Context;
import android.view.ViewGroup;

import com.sutorei.canvasbalance.domain.TaskData;

public class FindMassMode extends GameMode {

	public FindMassMode(Context context, ViewGroup parentView,
			TaskData taskData, File extensionStyleFolder) {
		super(context, parentView, taskData, extensionStyleFolder);
	}

	@Override
	public boolean check() {
		return super.checkAnswers();
	}

	@Override
	public void restart() {
		inflate();
	}

	@Override
	public boolean isModeWithNumericAnswers() {
		return true;
	}

	@Override
	public boolean isBalanceViewFixed() {
		return false;
	}

	@Override
	public boolean isBalanceViewInteractive() {
		return false;
	}

}
