package com.sutorei.canvasbalance.view;

import java.util.ArrayList;

import com.sutorei.canvasbalance.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class LevelPreviewAdapter extends BaseAdapter {

	private ArrayList<Bitmap> previews;
	private ArrayList<Integer> progress;
	private Context mContext;

	public LevelPreviewAdapter(Context context, String folder,
			ArrayList<String> filenames, ArrayList<Integer> progress) {
		this.mContext = context;
		this.previews = new ArrayList<Bitmap>();
		this.progress = progress;

		for (String filename : filenames) {
			previews.add(BitmapFactory.decodeFile(folder + "/" + filename));
		}
	}

	@Override
	public int getCount() {
		return previews.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	private LayerDrawable createBmp(Bitmap preview, int progress) {
		BitmapDrawable drawablePreview = new BitmapDrawable(
				mContext.getResources(), preview);
		BitmapDrawable drawableFace;
		switch (progress) {
		case 0:
			drawableFace = (BitmapDrawable) mContext.getResources()
					.getDrawable(R.drawable.icon_smileblue);
			break;
		case 1:
			drawableFace = (BitmapDrawable) mContext.getResources()
					.getDrawable(R.drawable.icon_smilegreen);
			break;
		case -1:
			drawableFace = (BitmapDrawable) mContext.getResources()
					.getDrawable(R.drawable.icon_smilered);
			break;
		default:
			throw new IllegalArgumentException("Incorrect state of progress");

		}
		drawableFace.setGravity(Gravity.LEFT | Gravity.TOP);
		Drawable drawableArray[] = new Drawable[] { drawablePreview,
				drawableFace };
		LayerDrawable layerDraw = new LayerDrawable(drawableArray);
		return layerDraw;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			// imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setPadding(8, 8, 8, 8); // TODO remove constants
		} else {
			imageView = (ImageView) convertView;
		}

		imageView.setImageDrawable(createBmp(previews.get(position),
				progress.get(position)));
		return imageView;
	}

}
