package com.sutorei.canvasbalance.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.sutorei.canvasbalance.R;
import com.sutorei.canvasbalance.storage.SQLiteDbConnection;
import com.sutorei.canvasbalance.storage.Updater;

public class LoadingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		final ProgressDialog progressDialog = new ProgressDialog(
				LoadingActivity.this);
		progressDialog.setMessage("Checking for updates");
		progressDialog.show();

		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Updater u = new Updater(LoadingActivity.this);
				if (u.isUpdateServerAvailable()) {
					u.updateDb();
					SQLiteDbConnection.init(LoadingActivity.this);
					u.updateItemImages();
				} else {
					if (!u.existsLocalDb()) {
						throw new UnsupportedOperationException("No local db");
					} else {
						SQLiteDbConnection.init(LoadingActivity.this);
					}
				}

				progressDialog.dismiss();

				Intent intent = new Intent(LoadingActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();

			}
		}.start();
	}

}
