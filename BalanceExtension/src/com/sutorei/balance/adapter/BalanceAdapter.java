package com.sutorei.balance.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import java.util.List;
import com.sutorei.balance.view.BalanceView;

public class BalanceAdapter extends PagerAdapter {
	
	List<BalanceView> pages;
	
	public BalanceAdapter(List<BalanceView> pages){
		this.pages = pages;
	}

	@Override
    public Object instantiateItem(View collection, int position){
        View v = pages.get(position);
        ((ViewPager)collection).addView(v, 0);
        return v;
    }
    
    @Override
    public void destroyItem(View collection, int position, Object view){
    	((ViewPager) collection).removeView((View) view);
    }
	
	@Override
	public int getCount() {
		return pages.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0.equals(arg1);
	}

}
