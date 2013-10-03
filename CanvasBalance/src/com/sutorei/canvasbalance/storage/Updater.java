package com.sutorei.canvasbalance.storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

public class Updater {
	private String sqliteDbFolder;
	private String sqliteDbFile;

	public Updater(Context context) {
		sqliteDbFile = SQLiteDbConnection.getDbName();
		sqliteDbFolder = context.getFilesDir().getParentFile().getPath()
				+ "/databases/";
	}

	public boolean isUpdateServerAvailable() {
		StrictMode.ThreadPolicy oldPolicy = StrictMode.getThreadPolicy(); // XXX
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		try {
			new URL(PathConstants.UPDATE_SERVER_ROOT).openConnection()
					.connect();
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			StrictMode.setThreadPolicy(oldPolicy);
		}
	}

	public boolean existsLocalDb() {
		return new File(sqliteDbFolder, sqliteDbFile).exists();
	}

	public boolean updateDb() {
		StrictMode.ThreadPolicy oldPolicy = StrictMode.getThreadPolicy(); // XXX
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		InputStream is = null;
		OutputStream os = null;
		try {
			URLConnection urlConnection = new URL(
					PathConstants.UPDATE_SERVER_ROOT
							+ PathConstants.UPDATE_SERVER_RELATIVE_DB_PATH)
					.openConnection();

			is = new BufferedInputStream(urlConnection.getInputStream());

			new File(sqliteDbFolder).mkdirs();

			File dbFile = new File(sqliteDbFolder, sqliteDbFile);
			if (!dbFile.exists()) {
				dbFile.createNewFile();
			}

			os = new FileOutputStream(dbFile);

			int r;
			byte[] b = new byte[100];
			while ((r = is.read(b)) != -1) {
				os.write(b, 0, r);
			}

			return true;
		} catch (Exception e) {
			Log.e(getClass().getName(), e.toString());
			return false;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(getClass().getName(), e.toString());
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(getClass().getName(), e.toString());
				}
			}

			StrictMode.setThreadPolicy(oldPolicy);
		}
	}

	private boolean updateImages(ArrayList<String> filenames,
			String updateServerRelativePath, String localResourcePath) {
		boolean result = true;

		StrictMode.ThreadPolicy oldPolicy = StrictMode.getThreadPolicy(); // XXX
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		try {
			for (String filename : filenames) {
				InputStream is = null;
				OutputStream os = null;

				try {
					URLConnection urlConnection = new URL(
							PathConstants.UPDATE_SERVER_ROOT
									+ updateServerRelativePath + filename)
							.openConnection();

					is = new BufferedInputStream(urlConnection.getInputStream());

					new File(localResourcePath).mkdirs();

					File imageFile = new File(localResourcePath, filename);
					if (!imageFile.exists()) {
						imageFile.createNewFile();
					}

					os = new FileOutputStream(imageFile);

					int r;
					byte[] b = new byte[100];
					while ((r = is.read(b)) != -1) {
						os.write(b, 0, r);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					result = false;
					Log.e(getClass().getName(), e.toString());
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							Log.e(getClass().getName(), e.toString());
						}
					}
					if (os != null) {
						try {
							os.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							Log.e(getClass().getName(), e.toString());
						}
					}
				}

			}

			return result;
		} finally {
			StrictMode.setThreadPolicy(oldPolicy);
		}
	}

	public boolean updateItemImages() {
		if (!existsLocalDb()) {
			throw new UnsupportedOperationException("No local db");
		}

		ArrayList<String> itemImages = SQLiteDbConnection.getInstance()
				.getItemImages();
		ArrayList<String> levelPreviewImages = SQLiteDbConnection.getInstance()
				.getLevelPreviewImages();
		
		return updateImages(itemImages,
				PathConstants.UPDATE_SERVER_RELATIVE_ITEM_IMAGES_PATH,
				PathConstants.LOCAL_IMAGES_RESOURCE_PATH + PathConstants.LOCAL_ITEM_IMAGES_RELATIVE_RESOURCE_PATH)
				&& updateImages(levelPreviewImages,
						PathConstants.UPDATE_SERVER_RELATIVE_LEVEL_PREVIEW_IMAGES_PATH,
						PathConstants.LOCAL_IMAGES_RESOURCE_PATH + PathConstants.LOCAL_LEVEL_PREVIEW_IMAGES_RELATIVE_RESOURCE_PATH);

	}
}
