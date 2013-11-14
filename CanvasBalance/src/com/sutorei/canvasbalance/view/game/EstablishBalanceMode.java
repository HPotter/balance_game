package com.sutorei.canvasbalance.view.game;

import android.content.Context;
import android.view.ViewGroup;

import com.sutorei.canvasbalance.domain.TaskData;

public class EstablishBalanceMode extends GameMode {

	public EstablishBalanceMode(Context context, ViewGroup parentView,
			TaskData taskData) {
		super(context, parentView, taskData);
	}

	@Override
	public boolean check() {
		return super.checkBalances();
	}

	@Override
	public void restart() {
		inflate();
	}

	@Override
	public boolean isModeWithNumericAnswers() {
		return false;
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
