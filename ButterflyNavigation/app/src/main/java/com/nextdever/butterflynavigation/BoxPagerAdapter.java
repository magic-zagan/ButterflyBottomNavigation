package com.nextdever.butterflynavigation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by 蓝金琉璃 on 2016/5/26.
 */
public class BoxPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mBoxs;
    private ArrayList<String> mTitles;

    public BoxPagerAdapter(FragmentManager fm, ArrayList<Fragment> boxs, ArrayList<String> titles) {
        super(fm);
        this.mBoxs = boxs;
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mBoxs.get(position);
    }

    @Override
    public int getCount() {
        return mBoxs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

}
