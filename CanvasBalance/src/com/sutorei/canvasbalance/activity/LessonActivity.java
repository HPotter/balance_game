package com.sutorei.canvasbalance.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.sutorei.canvasbalance.R;
import com.sutorei.canvasbalance.storage.PathConstants;
import com.sutorei.canvasbalance.storage.SQLiteDbConnection;
import com.sutorei.canvasbalance.view.LevelPreviewAdapter;

public class LessonActivity extends Activity {

	public static final String LESSON_NUMBER_EXTRA = "lessonNumber";
	public static final String LESSON_ID_EXTRA = "lesson";


	private int lessonId;
	private ArrayList<Integer> levelIds;
	private ArrayList<Integer> progressState;
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	private boolean needsRefresh;
	private GridView levels;
	
	
/*	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		super.onWindowFocusChanged(hasFocus);
		levels.setAdapter(new LevelPreviewAdapter(
				this,
				PathConstants.LOCAL_IMAGES_RESOURCE_PATH
						+ PathConstants.LOCAL_LEVEL_PREVIEW_IMAGES_RELATIVE_RESOURCE_PATH,
				SQLiteDbConnection.getInstance()
						.getLevelPreviewImages(lessonId), progressState));
		levels.invalidate();
	} */

	
	@Override
	protected void onPause(){
		super.onPause();
		needsRefresh = true;
		
	}
	@Override
	protected void onResume(){
		super.onResume();
		if (needsRefresh){
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);


		needsRefresh = false;
		if (!getIntent().hasExtra(LESSON_NUMBER_EXTRA)) {
			throw new IllegalArgumentException("No lesson number provided");
		} else {
			lessonId = getIntent().getIntExtra(LESSON_NUMBER_EXTRA, 0);
		}
		
		progressState = new ArrayList<Integer>();
		levelIds = SQLiteDbConnection.getInstance().getLevels(lessonId);
		settings = getSharedPreferences(PathConstants.SHARED_PROGRESS_NAME, Context.MODE_PRIVATE);
		editor = settings.edit();
		
		for (int i = 0; i < levelIds.size(); ++i){
			progressState.add(settings.getInt(levelIds.get(i).toString(), 0));
			if (progressState.get(i) == 0){
				editor.putInt(levelIds.get(i).toString(), 0);
			}
		}
		editor.commit();
		setContentView(R.layout.activity_lesson);

		((TextView) findViewById(R.id.lesson_title)).setText("Lesson number "
				+ lessonId); // TODO remove string const

		levels = (GridView) findViewById(R.id.level_list);
		levels.setAdapter(new LevelPreviewAdapter(
				this,
				PathConstants.LOCAL_IMAGES_RESOURCE_PATH
						+ PathConstants.LOCAL_LEVEL_PREVIEW_IMAGES_RELATIVE_RESOURCE_PATH,
				SQLiteDbConnection.getInstance()
						.getLevelPreviewImages(lessonId), progressState));
		levels.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int levelId = levelIds.get(position);

				Intent intent = new Intent(LessonActivity.this,
						LevelActivity.class);
				intent.putExtra(LevelActivity.LEVEL_ID_EXTRA, levelId);
				intent.putExtra(LevelActivity.LEVEL_NUMBER_EXTRA, position);
				startActivity(intent);
			}
		});
	}
	

}