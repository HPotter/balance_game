package com.sutorei.canvasbalance;

import java.io.File;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.hexonxons.extension.ExtensionListener;

public class MainView extends RelativeLayout{
	private ExtensionListener mExtensionListener = null;
	private EditText answerField;

	public MainView(Context context, File taskMarkup, File taskFolder,
			File extensionStyleFolder) throws ParseException {
		super(context);

		setWillNotDraw(false);
		
/*		taskText = new TextView(context);
		taskText.setText("2 чашки и 2 кувшина весят столько же, сколько 14 блюдец. " +
		"Один кувшин весит столько, сколько одна чашка и одно блюдце. " + 
		"Сколько блюдец уравновесят один кувшин?");
		RelativeLayout.LayoutParams layoutParams = 
				new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		taskText.setLayoutParams(layoutParams);
		this.addView(taskText);
		
		balanceUpper = new ImageView(context);
		layoutParams = 
				new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.BELOW,  */
		
		this.setBackgroundDrawable(Drawable.createFromPath(taskFolder + File.separator + "planted.png"));
		answerField = new EditText(context);
		RelativeLayout.LayoutParams layoutParams = 
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		this.setLayoutParams(layoutParams);
		layoutParams = new RelativeLayout.LayoutParams(50, 50);
		layoutParams.addRule(ALIGN_PARENT_BOTTOM);
		layoutParams.addRule(CENTER_HORIZONTAL, this.getId());
		answerField.setInputType(InputType.TYPE_CLASS_NUMBER);
		answerField.setEms(2);
		answerField.setBackgroundColor(Color.WHITE);
		answerField.setGravity(Gravity.CENTER);
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter.LengthFilter(1); //Filter to 10 characters
		answerField.setFilters(filters);
		this.addView(answerField, layoutParams);
		
		
	}

	public void setExtensionListener(ExtensionListener l) {
		mExtensionListener = l;
	}

	public void check() {
		Log.d("WHAT", answerField.getText().toString());
		if (answerField.getText().toString().equals("4")){
			mExtensionListener.onCheck(true);
		} else {
			mExtensionListener.onCheck(false);
		}
	}

	public void restart() {
		answerField.setText("");
		mExtensionListener.onRestart();
	}

}
