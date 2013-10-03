package com.sutorei.canvasbalance.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sutorei.canvasbalance.R;
import com.sutorei.canvasbalance.storage.SQLiteDbConnection;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		final ListView lessonsList = (ListView) findViewById(R.id.lessons);

		final ArrayList<Integer> lessons = SQLiteDbConnection.getInstance()
				.getLessons();
		ArrayList<String> lessonsTitles = new ArrayList<String>();
		for (Integer lesson : lessons) {
			lessonsTitles.add("Lesson " + lesson);
		}

		lessonsList.setAdapter(new ArrayAdapter<String>(MainActivity.this,
				android.R.layout.simple_list_item_1, lessonsTitles));
		lessonsList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						int lessonId = lessons.get(position);

						Intent intent = new Intent(MainActivity.this,
								LessonActivity.class);
						intent.putExtra(LessonActivity.LESSON_NUMBER_EXTRA,
								lessonId);
						startActivity(intent);
					}
				});

	}

}