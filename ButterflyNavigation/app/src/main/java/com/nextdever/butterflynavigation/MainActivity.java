package com.nextdever.butterflynavigation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPager vBoxContainer;
    private ButterflyBottomNavigation vBottomBar;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vBoxContainer = (ViewPager) findViewById(R.id.main_container);
        vBottomBar = (ButterflyBottomNavigation) findViewById(R.id.main_bottomBar);

        //初始化ViewPager
        mFragmentManager = getSupportFragmentManager();
        ArrayList<Fragment> boxs = new ArrayList<>();
        boxs.add(new NoteFragment());
        boxs.add(new NoteFragment());
        boxs.add(new NoteFragment());
        boxs.add(new NoteFragment());
        boxs.add(new NoteFragment());
        ArrayList<String> titles = new ArrayList<>();
        titles.add(getResources().getString(R.string.navigation_button_name_note));
        titles.add(getResources().getString(R.string.navigation_button_name_yesterday));
        titles.add(getResources().getString(R.string.navigation_button_name_today));
        titles.add(getResources().getString(R.string.navigation_button_name_tomorrow));
        titles.add(getResources().getString(R.string.navigation_button_name_overview));
        vBoxContainer.setAdapter(new BoxPagerAdapter(mFragmentManager, boxs, titles));
        vBottomBar.bindViewPager(vBoxContainer);
        vBottomBar.setButterfly(R.mipmap.img_butterfly_left, R.mipmap.img_butterfly_right);
        vBottomBar.setDefaultPager(2);
    }

}
