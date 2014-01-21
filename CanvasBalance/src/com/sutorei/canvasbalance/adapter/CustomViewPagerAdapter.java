package com.sutorei.canvasbalance.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class CustomViewPagerAdapter extends PagerAdapter {
	private List<? extends View> mPages;
	private final float mPageWidth;

	public CustomViewPagerAdapter(List<? extends View> pages, float pageWidth) {
		this.mPages = pages;
		this.mPageWidth = pageWidth;
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		View v = mPages.get(position);
		((ViewPager) collection).addView(v, 0);
		return v;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public int getCount() {
		return mPages.size();
	}

	// TODO fix argument names
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0.equals(arg1);
	}

	@Override
	public float getPageWidth(int position) {
		return mPageWidth;
	}
}
