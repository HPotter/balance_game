package com.sutorei.canvasbalance.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.sutorei.canvasbalance.storage.SQLiteDbConnection;

public class LevelActivity extends Activity {
	public static final String LEVEL_NUMBER_EXTRA = "levelNumber";
	public static final String LEVEL_ID_EXTRA = "levelId";

	private int levelId, levelNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (!getIntent().hasExtra(LEVEL_ID_EXTRA)) {
			throw new IllegalArgumentException("No level number provided");
		} else {
			levelId = getIntent().getIntExtra(LEVEL_ID_EXTRA, 0);
			levelNumber = getIntent().getIntExtra(LEVEL_NUMBER_EXTRA, 0);
		}

		Intent intent;
		switch (SQLiteDbConnection.getInstance().getTaskType(levelId)) {
		case ESTABLISH_BALANCE:
			intent = new Intent(LevelActivity.this, EqualizeBalanceActivity.class);
			intent.putExtra(LEVEL_ID_EXTRA, levelId);
			intent.putExtra(LEVEL_NUMBER_EXTRA, levelNumber);
			startActivity(intent);
			break;
		case CHECKOUT_BALANCE:
			intent = new Intent(LevelActivity.this, CheckoutBalanceActivity.class);
			intent.putExtra(LEVEL_ID_EXTRA, levelId);
			intent.putExtra(LEVEL_NUMBER_EXTRA, levelNumber);
			startActivity(intent);
			break;
		default:
			break;
		}
		
		finish();
	}

}
