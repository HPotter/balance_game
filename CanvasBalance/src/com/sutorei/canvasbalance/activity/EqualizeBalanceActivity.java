package com.sutorei.canvasbalance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.DialogInterface;

import com.sutorei.canvasbalance.R;
import com.sutorei.canvasbalance.domain.TaskData;
import com.sutorei.canvasbalance.storage.PathConstants;
import com.sutorei.canvasbalance.storage.SQLiteDbConnection;
import com.sutorei.canvasbalance.util.BalanceState;
import com.sutorei.canvasbalance.view.BalanceView;

public class EqualizeBalanceActivity extends Activity {
	
	private BalanceView balance;
	private TextView taskText, taskTitle;
	private ImageButton buttonBack, buttonCheck, buttonRestart;
	private int levelId, levelNumber;
	private SharedPreferences shpref;
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_equalize_balance);
		
		balance = new BalanceView(this);
		setContentView(R.layout.game_layout);
		taskText = (TextView)findViewById(R.id.task_description);
		taskTitle = (TextView)findViewById(R.id.task_title);
		buttonBack = (ImageButton)findViewById(R.id.image_button_back);
		buttonCheck = (ImageButton)findViewById(R.id.image_button_check);
		buttonRestart = (ImageButton)findViewById(R.id.image_button_restart);
		((LinearLayout)findViewById(R.id.linear_balance_container)).addView(balance);
		
		shpref = getSharedPreferences(PathConstants.SHARED_PROGRESS_NAME, 0);
		editor = shpref.edit();
		buttonBack.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		buttonCheck.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(EqualizeBalanceActivity.this);
				if(balance.getCurrentState() == BalanceState.EQUAL &&
						balance.getAvaliableObjects().isEmpty()){
					builder.setIcon(R.drawable.icon_smilegreen);
				    builder.setTitle("������ ������ ���������");
				    builder.setInverseBackgroundForced(true);
					builder.setPositiveButton("������!",new DialogInterface.OnClickListener()
				    {
				        @Override
				        public void onClick(DialogInterface dialog, int which) 
				        {
				            dialog.dismiss();
				        }
				    });
					editor.putInt(String.valueOf(levelId), 1);
					
				} else {
					builder.setIcon(R.drawable.icon_smilered);
					builder.setTitle("������ ������ � �������");
				    builder.setInverseBackgroundForced(true);
					builder.setPositiveButton("����������",new DialogInterface.OnClickListener()
				    {
				        @Override
				        public void onClick(DialogInterface dialog, int which) 
				        {
				            dialog.dismiss();
				        }
				    });
					editor.putInt(String.valueOf(levelId), -1);
				}
				AlertDialog alert = builder.create();
				alert.show();
				editor.commit();
			}
		});
		if (!getIntent().hasExtra(LevelActivity.LEVEL_NUMBER_EXTRA)) {
			throw new IllegalArgumentException("No level number provided");
		} else {
			levelNumber = getIntent().getIntExtra(LevelActivity.LEVEL_NUMBER_EXTRA, 0);
		}
		
		if (!getIntent().hasExtra(LevelActivity.LEVEL_ID_EXTRA)) {
			throw new IllegalArgumentException("No level id provided");
		} else {
			levelId = getIntent().getIntExtra(LevelActivity.LEVEL_ID_EXTRA, 0);
		}
		
		final TaskData taskData = SQLiteDbConnection.getInstance().getLevelData(levelId);
		
		taskTitle.setText(R.string.excercise + String.valueOf(levelNumber + 1));
		taskText.setText(taskData.getTaskText());

		balance.loadAndStartTask(EqualizeBalanceActivity.this,
				taskData.getBalanceData().get(0).getObjectsOnLeft(),
				taskData.getBalanceData().get(0).getObjectsOnRight(),
				taskData.getBalanceData().get(0).getAvaliableObjects(), 
				taskData.getBalanceData().get(0).isInteractive(),
				taskData.getBalanceData().get(0).isFixed(),
				taskData.getBalanceData().get(0).getBalanceState());
		
		buttonRestart.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}
		});
	}

}
