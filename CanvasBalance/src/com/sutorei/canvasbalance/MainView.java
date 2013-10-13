package com.sutorei.canvasbalance;

import java.io.File;
import java.text.ParseException;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hexonxons.extension.ExtensionListener;
import com.sutorei.canvasbalance.domain.TaskData;

public class MainView extends RelativeLayout {
	private ExtensionListener mExtensionListener = null;

	public MainView(Context context, File taskMarkup, File taskFolder,
			File extensionStyleFolder) throws ParseException {
		super(context);

		setWillNotDraw(false);

		TaskData taskData = TaskData.fromJsonFile(taskMarkup, taskFolder);
		
        TextView textView = new TextView(context);
        textView.setText(taskData.toString());
        textView.setId(0xaabbdd);
        
        LayoutParams params;
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.BELOW, 0xaabbcc);
        addView(textView, params);
	}

	public void setExtensionListener(ExtensionListener l) {
		mExtensionListener = l;
	}

	public void check() {
		mExtensionListener.onCheck(true);
		Toast.makeText(getContext(), "Check button pressed", Toast.LENGTH_SHORT)
				.show();
	}

	public void restart() {
		mExtensionListener.onRestart();
		Toast.makeText(getContext(), "Restart button pressed",
				Toast.LENGTH_SHORT).show();
	}
}
