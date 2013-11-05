package com.sutorei.canvasbalance.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sutorei.canvasbalance.domain.BalanceData;
import com.sutorei.canvasbalance.view.graphics.SpriteContainer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class SpritedBalanceViewFactory {
	
	final SpriteContainer container;
	
	public SpritedBalanceViewFactory(File sheetFile, File sheetMarkupFile){
		Bitmap sheet = BitmapFactory.decodeFile(sheetFile.getAbsolutePath());
		Log.d("READ", Boolean.toString(sheet != null));
		//TODO: write honest reading of sheet markup
		List<Integer> framesInRow = new ArrayList<Integer>();
		framesInRow.add(24); framesInRow.add(24);
		container = new SpriteContainer(370, 200, framesInRow, sheet, 10);
	}
	
	public SpritedBalanceView generateSpritedBalanceView(Context context, BalanceData balanceData){
		return new SpritedBalanceView(context, balanceData, container);
	}
}
