package com.sutorei.canvasbalance;

import java.io.File;
import java.text.ParseException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.RelativeLayout;

import com.hexonxons.extension.AbstractKeyboard;
import com.hexonxons.extension.ExtensionListener;
import com.hexonxons.extension.ExtensionViewImpl;
import com.sutorei.canvasbalance.domain.TaskData;
import com.sutorei.canvasbalance.util.BalanceBitmapContainer;
import com.sutorei.canvasbalance.util.BalanceObjectBitmapCache;
import com.sutorei.canvasbalance.view.game.CheckoutBalanceMode;
import com.sutorei.canvasbalance.view.game.EstablishBalanceMode;
import com.sutorei.canvasbalance.view.game.FindMassInteractiveMode;
import com.sutorei.canvasbalance.view.game.FindMassMode;
import com.sutorei.canvasbalance.view.game.GameMode;

//TODO fonts
@SuppressLint("ViewConstructor")
public class MainView extends RelativeLayout implements ExtensionViewImpl {
	private ExtensionListener mExtensionListener = null;
	private GameMode mGameMode = null;
	private TaskData mTaskData = null;

	public MainView(Context context, File taskMarkup, File taskFolder,
			File extensionStyleFolder) throws ParseException {
		super(context);

		setWillNotDraw(false);
		
		BalanceObjectBitmapCache.purge();
		BalanceObjectBitmapCache.preloadAll(taskFolder);
		
		BalanceBitmapContainer.loadBitmaps(extensionStyleFolder);

		// TODO: fabric
		mTaskData = TaskData.fromJsonFile(taskMarkup, taskFolder);
		switch (mTaskData.getTaskType()) {
		case ESTABLISH_BALANCE:
			mGameMode = new EstablishBalanceMode(context, this, mTaskData);
			break;
		case CHECKOUT_BALANCE:
			mGameMode = new CheckoutBalanceMode(context, this, mTaskData);
			break;
		case FIND_MASS:
			mGameMode = new FindMassMode(context, this, mTaskData);
			break;
		case FIND_MASS_USING_EQUATION:
			break;
		case FIND_MASS_INTERACTIVE:
			mGameMode = new FindMassInteractiveMode(context, this, mTaskData);
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

	@Override
	public boolean doLoad(boolean orientationChange) {
		return false;
	}

	@Override
	public void onPreLoad() {
		// do nothing
	}

	@Override
	public void load(int width, int height) {
		// do nothing
	}

	@Override
	public void onPostLoad() {
		// do nothing
	}

	@Override
	public void release() {
		// do nothing
	}

	@Override
	public void setKeyboard(AbstractKeyboard keyboard) {
		mGameMode.setKeyboard(keyboard);
	}

	@Override
	public int fixOrientation() {
		return ORIENTATION_FIX_LANDSCAPE;
	}

	@Override
	public String getTaskDescription() {
		return mTaskData.getTaskText();
	}
}
