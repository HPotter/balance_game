package com.sutorei.canvasbalance.view.game;

import java.util.Collections;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.sutorei.canvasbalance.domain.TaskData;
import com.sutorei.canvasbalance.view.LineupView;

public class LineupMode extends GameMode {
	
	private LineupView mLineupView = null;
	
	public LineupMode(Context context, ViewGroup parentView,
			TaskData taskData) {
		super(context, parentView, taskData);
		Log.d("MSG", "Constructed. Initializing");
	}
	
	@Override
	public void init(){
		super.init();
		mLineupView = new LineupView(getContext(), getTaskData().getQuestions());
		mLineupView.setId(generateViewId());
		Log.d("MSG", "Initialized. Inflating");
	}
	
	@Override
	public void inflate(){
		super.inflate();
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
				layoutParams.addRule(RelativeLayout.BELOW,
				getParentView().getChildAt(getParentView().getChildCount() - 1)
				.getId());
				getParentView().addView(mLineupView, layoutParams);
		Log.d("MSG", "Inflated. Watching out");
	}

	@Override
	public boolean check() {
		return mLineupView.check();
	}

	@Override
	public void restart() {
		Collections.shuffle(mLineupView.getObjectsToSort());
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
