package com.xyxl.tianyingn3.ui.customview;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xyxl.tianyingn3.ui.fragments.BaseFragment;

/** 
 * @author Roc Goo 
 * @version 创建时间：2017-9-5 上午10:09:26 
 * 类说明 
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> list;

	public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this.list = list;
	}

	// 返回了当前要滑动的View的个数
	@Override
	public int getCount() {
		return list.size();
	}

	// 在getItem(int arg0)中，根据传来的参数arg0，来返回当前要显示的fragment
	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}
}
