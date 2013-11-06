package com.sutorei.canvasbalance;

import java.io.File;
import java.text.ParseException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.RelativeLayout;

import com.hexonxons.extension.ExtensionListener;
import com.sutorei.canvasbalance.domain.TaskData;
import com.sutorei.canvasbalance.view.game.CheckoutBalanceMode;
import com.sutorei.canvasbalance.view.game.EstablishBalanceMode;
import com.sutorei.canvasbalance.view.game.FindMassInteractiveMode;
import com.sutorei.canvasbalance.view.game.FindMassMode;
import com.sutorei.canvasbalance.view.game.GameMode;

@SuppressLint("ViewConstructor")
public class MainView extends RelativeLayout {
	private ExtensionListener mExtensionListener = null;
	private GameMode mGameMode = null;

	public MainView(Context context, File taskMarkup, File taskFolder,
			File extensionStyleFolder) throws ParseException {
		super(context);

		setWillNotDraw(false);

		// TODO: fabric
		TaskData taskData = TaskData.fromJsonFile(taskMarkup, taskFolder);
		switch (taskData.getTaskType()) {
		case ESTABLISH_BALANCE:
			mGameMode = new EstablishBalanceMode(context, this, taskData,
					extensionStyleFolder);
			break;
		case CHECKOUT_BALANCE:
			mGameMode = new CheckoutBalanceMode(context, this, taskData,
					extensionStyleFolder);
			break;
		case FIND_MASS:
			mGameMode = new FindMassMode(context, this, taskData,
					extensionStyleFolder);
			break;
		case FIND_MASS_USING_EQUATION:
			break;
		case FIND_MASS_INTERACTIVE:
			mGameMode = new FindMassInteractiveMode(context, this, taskData,
					extensionStyleFolder);
			break;
		case FIND_MASS_USING_EQUATION_INTERACTIVE:
			break;
		case LINE_UP_OBJECTS:
			break;
		case FIND_THE_DIFFERENCE:
			break;
		}
	}

	public void setExtensionListener(ExtensionListener l) {
		mExtensionListener = l;
	}

	public void check() {
		mExtensionListener.onCheck(mGameMode.check());
	}

	public void restart() {
		mGameMode.restart();
		mExtensionListener.onRestart();
	}
}
