package com.sutorei.canvasbalance.view.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.sutorei.canvasbalance.adapter.BalanceAdapter;
import com.sutorei.canvasbalance.domain.BalanceData;
import com.sutorei.canvasbalance.domain.TaskData;
import com.sutorei.canvasbalance.view.BalanceView;

public class CheckoutBalanceMode extends GameMode {

	private TextView taskText;
	private List<BalanceView> balanceViews;
	private ViewPager balancePager;

	public CheckoutBalanceMode(Context context, ViewGroup parentView,
			TaskData taskData, File extensionStyleFolder) {
		super(context, parentView, taskData, extensionStyleFolder);

		inflate();
	}

	@Override
	public boolean check() {
		boolean result = true;
		for (int i = 0; i < balanceViews.size(); ++i) {
			if (balanceViews.get(i).getCurrentState() != getTaskData()
					.getBalanceData().get(i).getBalanceState()
					|| !balanceViews.get(i).getAvaliableObjects().isEmpty()) {
				result = false;
				break;
			}
		}

		return result;
	}

	@Override
	public void restart() {
		 inflate();
	}

	private void inflate() {
		// create
		taskText = new TextView(getContext());
		balancePager = new ViewPager(getContext());
		balanceViews = new ArrayList<BalanceView>();

		// init
		taskText.setText(getTaskData().getTaskText());

		LayoutParams balanceViewLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		balanceViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		for (BalanceData balanceData : getTaskData().getBalanceData()) {
			BalanceView balanceView = new BalanceView(getContext(),
					getExtensionStyleFolder(), balanceData);
			balanceView.setLayoutParams(balanceViewLayoutParams);
			balanceViews.add(balanceView);
		}
		balancePager.setAdapter(new BalanceAdapter(balanceViews));

		// inflate
		LayoutParams layoutParams;

		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		taskText.setId(0xaabbdd); // FIXME: constants are bad
		getParentView().addView(taskText, layoutParams);

		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		layoutParams.addRule(RelativeLayout.BELOW, taskText.getId());
		balancePager.setId(0xaabbde); // FIXME: constants are bad
		getParentView().addView(balancePager, layoutParams);

//		if (getTaskData().getBalanceData().size() > 1) {
//			PagerTitleStrip balancePagerTitleStrip = new PagerTitleStrip(
//					getContext());
//			ViewPager.LayoutParams balancePagerLayoutParams = new ViewPager.LayoutParams();
//			balancePagerLayoutParams.height = ViewPager.LayoutParams.WRAP_CONTENT;
//			balancePagerLayoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
//			balancePagerLayoutParams.gravity = Gravity.BOTTOM;
//			balancePager.addView(balancePagerTitleStrip,
//					balancePagerLayoutParams);
//		}
	}

}
