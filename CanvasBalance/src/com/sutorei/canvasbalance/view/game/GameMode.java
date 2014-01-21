package com.sutorei.canvasbalance.view.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.hexonxons.extension.AbstractKeyboard;
import com.sutorei.canvasbalance.adapter.CustomViewPagerAdapter;
import com.sutorei.canvasbalance.domain.BalanceData;
import com.sutorei.canvasbalance.domain.TaskData;
import com.sutorei.canvasbalance.util.FontContainer;
import com.sutorei.canvasbalance.view.BalanceView;

public abstract class GameMode {
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	private TaskData mTaskData = null;
	private Context mContext = null;
	private ViewGroup mParentView = null;

	private List<BalanceView> mBalanceViews;
	private List<EditText> mAnswerFields;
	private ArrayList<RelativeLayout> mRelativeLayouts;
	private ViewPager mBalancePager;
	
	public GameMode(Context context, ViewGroup parentView, TaskData taskData) {
		mTaskData = taskData;
		mContext = context;
		mParentView = parentView;

		init();
		inflate();
	}

	public final TaskData getTaskData() {
		return mTaskData;
	}

	public final Context getContext() {
		return mContext;
	}

	public final ViewGroup getParentView() {
		return mParentView;
	}

	public void init() {
		// create
		mBalancePager = new ViewPager(getContext());
		mBalanceViews = new ArrayList<BalanceView>();
		mAnswerFields = new ArrayList<EditText>();

		// init
		RelativeLayout.LayoutParams containerLayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		containerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		mRelativeLayouts = new ArrayList<RelativeLayout>();
		Iterator<BalanceData> balanceDataIterator = getTaskData()
				.getBalanceData().iterator();
		Iterator<String> balanceTextIterator = getTaskData().getBalanceText()
				.iterator();
		while (balanceDataIterator.hasNext() && balanceTextIterator.hasNext()) {
			BalanceData balanceData = balanceDataIterator.next();

			// init type-dependent params
			balanceData.setFixed(isBalanceViewFixed());
			balanceData.setInteractive(isBalanceViewInteractive());

			RelativeLayout relativeLayoutContainer = new RelativeLayout(
					mContext);
			relativeLayoutContainer.setLayoutParams(containerLayoutParams);

			// init views
			TextView textView = null;
			BalanceView balanceView = null;
			EditText editText = null;
			textView = new TextView(mContext);
			textView.setId(generateViewId());
			textView.setText(balanceTextIterator.next());
			if (FontContainer.isTypefaceSet()) {
				textView.setTypeface(FontContainer.getTypeface());
			}
			balanceView = new BalanceView(getContext(), balanceData);
			balanceView.setId(generateViewId());
			mBalanceViews.add(balanceView);
			if (isModeWithNumericAnswers()) {
				editText = new EditText(mContext);
				editText.setId(generateViewId());
				editText.setInputType(InputType.TYPE_CLASS_NUMBER);
				editText.setEms(3);
				mAnswerFields.add(editText);
			}

			// init layoutParams
			LayoutParams textViewLayoutParams = null;
			LayoutParams balanceViewLayoutParams = null;
			LayoutParams editTextLayoutParams = null;
			textViewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			textViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			textViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			balanceViewLayoutParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			balanceViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			balanceViewLayoutParams.addRule(RelativeLayout.BELOW,
					textView.getId());
			if (isModeWithNumericAnswers()) {
				balanceViewLayoutParams.addRule(RelativeLayout.ABOVE,
						editText.getId());
				editTextLayoutParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				editTextLayoutParams
						.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				editTextLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			} else {
				balanceViewLayoutParams
						.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			}

			// inflate relativeLayoutContainer
			relativeLayoutContainer.addView(textView, textViewLayoutParams);
			relativeLayoutContainer.addView(balanceView,
					balanceViewLayoutParams);
			if (isModeWithNumericAnswers()) {
				relativeLayoutContainer.addView(editText, editTextLayoutParams);
			}

			mRelativeLayouts.add(relativeLayoutContainer);
		}
		mBalancePager.setAdapter(new CustomViewPagerAdapter(mRelativeLayouts, 0.8f));
	}

	public void inflate() {
		LayoutParams layoutParams;

		layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mBalancePager.setId(generateViewId());

		RelativeLayout wrapper = new RelativeLayout(getContext());
		if (mBalanceViews.size() == 1) {
			wrapper.addView(mRelativeLayouts.get(0), new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			wrapper.addView(mBalancePager, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		getParentView().addView(wrapper, layoutParams);
	}

	// TODO: move correctness display to separate method
	public final boolean checkBalances() {
		boolean result = true;

		for (int i = 0; i < mBalanceViews.size(); ++i) {
			if (mBalanceViews.get(i).getCurrentState() != getTaskData()
					.getBalanceData().get(i).getBalanceState()
					|| !mBalanceViews.get(i).getAvaliableObjects().isEmpty()) {
				mBalanceViews.get(i).setCorrectnessPopup(false);
				result = false;
			} else {
				mBalanceViews.get(i).setCorrectnessPopup(true);
			}
		}

		return result;
	}

	// TODO: move correctness display to separate method
	public final boolean checkAnswers() {
		boolean result = true;

		Iterator<Integer> correctAnswerIterator = mTaskData.getCorrectAnswers()
				.iterator();
		Iterator<EditText> answerFieldIterator = mAnswerFields.iterator();
		while (correctAnswerIterator.hasNext() && answerFieldIterator.hasNext()) {
			EditText currentAnswerField = answerFieldIterator.next();
			int answer = -1;
			try {
				answer = Integer
						.decode(currentAnswerField.getText().toString());
			} catch (NumberFormatException e) {
				// field is empty
			}
			int correctAnswer = correctAnswerIterator.next();

			if (answer != correctAnswer) {
				result = false;
				currentAnswerField.getBackground().setColorFilter(Color.RED,
						PorterDuff.Mode.MULTIPLY);
			} else {
				currentAnswerField.getBackground().setColorFilter(Color.GREEN,
						PorterDuff.Mode.MULTIPLY);
			}
		}

		return result;
	}

	public final void setKeyboard(AbstractKeyboard keyboard) {
		for (EditText editText : mAnswerFields) {
			keyboard.registerEditText(editText);
		}
	}

	public abstract boolean check();

	public void restart() {
		mParentView.removeAllViews();
		
		mBalanceViews.clear();
		mBalancePager.removeAllViews();
		mAnswerFields.clear();
		
		init();
		inflate();
	}

	public abstract boolean isModeWithNumericAnswers();

	public abstract boolean isBalanceViewFixed();

	public abstract boolean isBalanceViewInteractive();

	/**
	 * Generate a value suitable for use in {@link #setId(int)}. This value will
	 * not collide with ID values generated at build time by aapt for R.id.
	 * 
	 * @return a generated ID value
	 */
	public static final int generateViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range
			// under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF)
				newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}
}
