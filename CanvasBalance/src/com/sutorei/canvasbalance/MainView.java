package com.sutorei.canvasbalance;

import java.io.File;
import java.text.ParseException;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hexonxons.extension.ExtensionListener;
import com.sutorei.canvasbalance.domain.BalanceData;
import com.sutorei.canvasbalance.domain.TaskData;
import com.sutorei.canvasbalance.view.SpritedBalanceView;
import com.sutorei.canvasbalance.view.SpritedBalanceViewFactory;
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
		
		Log.d("READ", extensionStyleFolder.getAbsolutePath() + File.separator + "balance_sheet.png");
		SpritedBalanceViewFactory factory = 
				new SpritedBalanceViewFactory(new File(extensionStyleFolder.getAbsolutePath() + File.separator + "balance_sheet.png"), 
						null);
		SpritedBalanceView v = factory.generateSpritedBalanceView(getContext(),
				taskData.getBalanceData().get(0));
		this.addView(v);
/*		switch (taskData.getTaskType()) {
		case ESTABLISH_BALANCE:
			break;
		case CHECKOUT_BALANCE:
			mGameMode = new CheckoutBalanceMode(context, this, taskData, extensionStyleFolder);
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
		} */
	}

	public void setExtensionListener(ExtensionListener l) {
		mExtensionListener = l;
	}

	public void check() {
		mExtensionListener.onCheck(true);
	}

	public void restart() {
		mExtensionListener.onRestart();
	}
}
