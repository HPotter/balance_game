package com.sutorei.canvasbalance.view.game;

import android.content.Context;
import android.view.ViewGroup;

import com.sutorei.canvasbalance.domain.TaskData;

public class FindMassInteractiveMode extends GameMode {

	public FindMassInteractiveMode(Context context, ViewGroup parentView,
			TaskData taskData) {
		super(context, parentView, taskData);
	}

	@Override
	public boolean check() {
		return super.checkBalances() && super.checkAnswers();
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
		return true;
	}

}
