package com.sutorei.canvasbalance.view.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.sutorei.canvasbalance.domain.TaskData;
import com.sutorei.canvasbalance.view.BalanceView;

public class CheckoutBalanceMode extends GameMode {

	private TextView taskText;
	private List<BalanceView> balanceViews;
	private LinearLayout balanceList;
	
	public CheckoutBalanceMode(Context context, ViewGroup parentView, TaskData taskData, File extensionStyleFolder) {
		super(context, parentView, taskData, extensionStyleFolder);
		
		inflate();
	}

	@Override
	public boolean check() {
		boolean result = false;
		for (int i = 0; i < balanceViews.size(); ++i){
			if(balanceViews.get(i).getCurrentState() !=
				getTaskData().getBalanceData().get(i).getBalanceState() ||
				!balanceViews.get(i).getAvaliableObjects().isEmpty()){
				result = true;
				break;
			}
		}
		
		return result;
	}

	@Override
	public void restart() {
//		inflate();
	}

	private void inflate() {
		// create
		taskText = new TextView(getContext());
		balanceList = new LinearLayout(getContext());
		balanceViews = new ArrayList<BalanceView>();
		
		// init
		taskText.setText(getTaskData().getTaskText());

		for (int i = 0; i < getTaskData().getBalanceData().size(); ++i){
			BalanceView balanceView = new BalanceView(getContext(), getTaskData(), i, getExtensionStyleFolder());
			balanceViews.add(balanceView); // wtf is dat constructor
			balanceList.addView(balanceView);
		}
		
		// inflate
		LayoutParams params;
		
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		taskText.setId(0xaabbdd); // FIXME: constants are bad;
		getParentView().addView(taskText, params);

		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.BELOW, taskText.getId());
		getParentView().addView(balanceList, params);
	}

}
