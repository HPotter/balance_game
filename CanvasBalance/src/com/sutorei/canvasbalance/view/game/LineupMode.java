package com.sutorei.canvasbalance.view.game;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.sutorei.canvasbalance.domain.TaskData;
import com.sutorei.canvasbalance.domain.WeightedObject;
import com.sutorei.canvasbalance.view.LineupView;

public class LineupMode extends GameMode {

	private LineupView mLineupView;

	public LineupMode(Context context, ViewGroup parentView, TaskData taskData) {
		super(context, parentView, taskData);
	}

	@Override
	public void init() {
		super.init();
		
		// deep copy
		List<WeightedObject> lineObjects = new ArrayList<WeightedObject>(getTaskData().getQuestions().size());
		for(WeightedObject weightedObject : getTaskData().getQuestions()) {
			lineObjects.add(new WeightedObject(weightedObject));
		}
		
		mLineupView = new LineupView(getContext(), lineObjects);
		mLineupView.setId(generateViewId());
	}

	@Override
	public void inflate() {
		super.inflate();
		
		View aboveView = (RelativeLayout) getParentView().getChildAt(
				getParentView().getChildCount() - 1);

		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.BELOW, aboveView.getId());
		((LayoutParams) aboveView.getLayoutParams()).addRule(
				RelativeLayout.ABOVE, mLineupView.getId()); // I had to use this hack.
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		getParentView().addView(mLineupView, layoutParams);
	}

	@Override
	public boolean check() {
		return mLineupView.check();
	}

	@Override
	public void restart() {
		super.restart();
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
