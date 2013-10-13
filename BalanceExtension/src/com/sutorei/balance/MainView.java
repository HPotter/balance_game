package com.sutorei.balance;

import com.sutorei.balance.adapter.BalanceAdapter;
import com.sutorei.balance.view.BalanceView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.widget.RelativeLayout;

public class MainView extends RelativeLayout {
	private ViewPager balancePager;

	public MainView(Context context) {
		super(context);
	}

	public MainView(Context context, File taskMarkup, File taskFolder,
			File extensionStyleFolder) {
		super(context);
		balancePager = new ViewPager(context);
		List<BalanceView> balances = new ArrayList<BalanceView>();
		balancePager.setAdapter(new BalanceAdapter(balances));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}