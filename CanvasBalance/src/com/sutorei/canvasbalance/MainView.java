package com.sutorei.canvasbalance;

import java.io.File;
import java.text.ParseException;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hexonxons.extension.ExtensionListener;
import com.sutorei.canvasbalance.domain.TaskData;
import com.sutorei.canvasbalance.view.game.CheckoutBalanceMode;
import com.sutorei.canvasbalance.view.game.GameMode;

public class MainView extends RelativeLayout {
	private ExtensionListener mExtensionListener = null;
	private GameMode mGameMode = null;

	public MainView(Context context, File taskMarkup, File taskFolder,
			File extensionStyleFolder) throws ParseException {
		super(context);

		setWillNotDraw(false);

		TaskData taskData = TaskData.fromJsonFile(taskMarkup, taskFolder);
		 

		switch (taskData.getTaskType()) {
		case ESTABLISH_BALANCE:
			break;
		case CHECKOUT_BALANCE:
			mGameMode = new CheckoutBalanceMode(context, this, taskData, extensionStyleFolder);
			Log.d("a", taskData.toString());
			break;
		case FIND_MASS:
			break;
		case FIND_MASS_USING_EQUATION:
			break;
		case FIND_MASS_INTERACRTIVE:
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
		Toast.makeText(getContext(), "Check button pressed", Toast.LENGTH_SHORT)
				.show();
	}

	public void restart() {
		mGameMode.restart();
		mExtensionListener.onRestart();
		Toast.makeText(getContext(), "Restart button pressed",
				Toast.LENGTH_SHORT).show();
	}
}
