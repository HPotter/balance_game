package com.sutorei.canvasbalance.storage;

import java.util.ArrayList;

import com.sutorei.canvasbalance.domain.BalanceData;
import com.sutorei.canvasbalance.domain.TaskData;
import com.sutorei.canvasbalance.domain.TaskType;
import com.sutorei.canvasbalance.domain.WeightedObject;
import com.sutorei.canvasbalance.util.BalanceState;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDbConnection {
	private static class DBHelper extends SQLiteOpenHelper {

		private static final int DB_VERSION = 1;
		private static final String DB_NAME = "levels";

		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// nothing TODO here
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// nothing TODO here
		}

	};

	private static DBHelper dbHelper;
	private static SQLiteDbConnection instance = null;

	public static SQLiteDbConnection getInstance() {
		return instance;
	}

	public static String getDbName() {
		return DBHelper.DB_NAME;
	}

	public SQLiteDbConnection(Context context) {
		dbHelper = new DBHelper(context);
		instance = this;
	}

	public static void init(Context context) {
		dbHelper = new DBHelper(context);
		instance = new SQLiteDbConnection(context);
	}

	private SQLiteDatabase getDb() {
		return dbHelper.getReadableDatabase();
	}

	public ArrayList<String> getItemImages() {
		ArrayList<String> result = new ArrayList<String>();

		Cursor cursor = getDb().query("item", new String[] { "filename" },
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			result.add(cursor.getString(0));
			cursor.moveToNext();
		}

		cursor.close();

		return result;
	}

	public ArrayList<String> getLevelPreviewImages() {
		ArrayList<String> result = new ArrayList<String>();

		Cursor cursor = getDb().query("level_preview", new String[] { "filename" },
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			result.add(cursor.getString(0));
			cursor.moveToNext();
		}

		cursor.close();

		return result;
	}

	public ArrayList<String> getLevelPreviewImages(int lessonId) {
		ArrayList<String> result = new ArrayList<String>();

		Cursor cursor = getDb().rawQuery("SELECT filename FROM level, level_preview WHERE level.preview_id = level_preview._id AND level.lesson_id = '" + lessonId + "' ", null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			result.add(cursor.getString(0));
			cursor.moveToNext();
		}

		cursor.close();

		return result;
	}

	public ArrayList<Integer> getLevels(int lessonId) {
		ArrayList<Integer> result = new ArrayList<Integer>();

		Cursor cursor = getDb().query("level", new String[] { "_id" },
				"lesson_id = ?", new String[] { String.valueOf(lessonId) }, null,
				null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			result.add(cursor.getInt(0));
			cursor.moveToNext();
		}

		cursor.close();

		return result;
	}

	public ArrayList<Integer> getLessons() {
		ArrayList<Integer> result = new ArrayList<Integer>();

		Cursor cursor = getDb().query("lesson", new String[] { "_id" }, null,
				null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			result.add(cursor.getInt(0));
			cursor.moveToNext();
		}

		cursor.close();

		return result;
	}

	public TaskType getTaskType(int levelId) {
		TaskType result = null;

		Cursor cursor = getDb().query("level", new String[] { "task_type_id" },
				"_id = ?", new String[] { String.valueOf(levelId) }, null,
				null, null);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			result = TaskType.values()[cursor.getInt(0)];
			cursor.moveToNext();
		}

		cursor.close();

		return result;
	}

	public TaskData getLevelData(int levelId) {
		TaskData result = new TaskData();
		{
			Cursor cursor = getDb().query("level",
					new String[] { "task_type_id", "lesson_id", "desc" }, "_id = ?",
					new String[] { String.valueOf(levelId) }, null, null, null);

			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				result.setLessonNumber(cursor.getInt(1));
				result.setTaskNumber(levelId);
				result.setTaskText(cursor.getString(2));
				result.setTaskType(TaskType.values()[cursor.getInt(0)]);
				cursor.moveToNext();
			}

			cursor.close();
		}

		ArrayList<BalanceData> balanceDataList = new ArrayList<BalanceData>();

		{
			Cursor cursor = getDb().query("balance",
					new String[] { "interactive", "state", "fixed", "_id" },
					"level_id = ?", new String[] { String.valueOf(levelId) }, null,
					null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				BalanceData balanceData = new BalanceData();

				balanceData.setInteractivity(cursor.getInt(0) > 0);
				balanceData.setBalanceState(BalanceState.values()[cursor
						.getInt(1)]);
				balanceData.setFixed(cursor.getInt(2) > 0);

				ArrayList<WeightedObject> avaliableObjects = new ArrayList<WeightedObject>();
				ArrayList<WeightedObject> objectsOnLeft = new ArrayList<WeightedObject>();
				ArrayList<WeightedObject> objectsOnRight = new ArrayList<WeightedObject>();
				
				String balance = String.valueOf(cursor.getInt(3));

				{
					Cursor cursor2 = getDb().rawQuery("SELECT filename, weight, position FROM balance_item, item WHERE balance_item.item_id = item._id AND balance_item.balance_id = '" + balance + "' ", null);
					cursor2.moveToFirst();

					while (!cursor2.isAfterLast()) {
						WeightedObject weightedObject = new WeightedObject(PathConstants.LOCAL_IMAGES_RESOURCE_PATH + PathConstants.LOCAL_ITEM_IMAGES_RELATIVE_RESOURCE_PATH + "/" + cursor2.getString(0), cursor2.getInt(1));
						
						switch(cursor2.getInt(2)) {
						case 0:
							objectsOnLeft.add(weightedObject);
							break;
						case 1:
							avaliableObjects.add(weightedObject);
							break;
						case 2:
							objectsOnRight.add(weightedObject);
							break;
						default:
							break;
						}
						
						cursor2.moveToNext();
					}
					
					cursor2.close();
				}

				balanceData.setAvaliableObjects(avaliableObjects);
				balanceData.setObjectsOnLeft(objectsOnLeft);
				balanceData.setObjectsOnRight(objectsOnRight);

				balanceDataList.add(balanceData);
				cursor.moveToNext();
			}

			cursor.close();
		}

		result.setBalanceData(balanceDataList);
		
		ArrayList<WeightedObject> questions = new ArrayList<WeightedObject>();
		
		{
			Cursor cursor = getDb().rawQuery("SELECT filename, weight FROM level_item, item WHERE level_item.item_id = item._id AND level_item.level_id = '" + levelId + "' ", null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				questions.add(new WeightedObject(PathConstants.LOCAL_IMAGES_RESOURCE_PATH + PathConstants.LOCAL_ITEM_IMAGES_RELATIVE_RESOURCE_PATH + "/" + cursor.getString(0), cursor.getInt(1)));
				
				cursor.moveToNext();
			}

			cursor.close();
		}
		
		result.setQuestions(questions);

		return result;
	}
}
