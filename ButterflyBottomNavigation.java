package com.nextdever.butterflynavigation;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HaoKing on 2016/5/27.
 * QQ交流群：Geek Chat 344386592
 */
public class ButterflyBottomNavigation extends RelativeLayout implements ViewPager.OnPageChangeListener, View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {

    private ArrayList<TextView> mNavNameViews;
    private ArrayList<LinearLayout> mNavButtons;
    private int mNavButtonTextDefaultColor, mNavButtonTextSelectedColor;
    private int mNavButtonCount;
    private ViewPager vBindViewPager;
    private int mDefaultPager;

    //蝴蝶
    private ImageView vButterfly;
    //蝴蝶向前（右）飞行图片，蝴蝶向后（左）飞行的图片
    private Drawable mButterflyDrawableForward, mButterflyDrawableBackward;
    //蝴蝶的大小，单位px
    private int mButterflySize;
    //蝴蝶位置偏差微调，使蝴蝶降落到文字上
    private final int mLandingFineTuningX = 14, mLandingFineTuningY = 4;
    //蝴蝶可降落的地点
    private int mLandingSiteArray[];
    private int mCurrentLandingSite, mNextLandingSite;
    private float mPrePositionOffset;
    private boolean mCurrentDirection;


    public ButterflyBottomNavigation(Context context) {
        super(context);
    }

    public ButterflyBottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ButterflyBottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setNavigationButtonNames(ArrayList<String> navNames) {
        String[] navNameArray = new String[navNames.size()];
        navNames.toArray(navNameArray);
        setNavigationButtonNames(navNameArray);
    }

    /**
     * 绑定ViewPager
     */
    public void bindViewPager(ViewPager viewPager) {
        //设置底部导航栏的文字
        vBindViewPager = viewPager;
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        int navCount = pagerAdapter.getCount();
        String[] navNames = new String[navCount];
        for (int i = 0; i < navCount; i++) {
            navNames[i] = pagerAdapter.getPageTitle(i).toString();
        }
        //设置ViewPager缓存全部的pager
        vBindViewPager.setOffscreenPageLimit(navCount);
        setNavigationButtonNames(navNames);
    }

    public void setDefaultPager(int position) {
        if (null == vBindViewPager)
            return;
        mDefaultPager = position;
    }

    public void setNavigationButtonNames(String[] navNames) {
        mNavNameViews = new ArrayList<>();
        int navNameArrayLength = navNames.length;
        for (int i = 0; i < navNameArrayLength; i++) {
            TextView navNameView = new TextView(getContext());
            navNameView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            navNameView.setText(navNames[i]);
            mNavNameViews.add(navNameView);
        }
        initButterflyBottomNavigation();
    }

    private void initButterflyBottomNavigation() {
        if (null == mNavNameViews || mNavNameViews.size() == 0)
            return;
        mNavButtons = new ArrayList<>();
        LinearLayout navContainer = new LinearLayout(getContext());
        LinearLayout.LayoutParams navContainerParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        navContainer.setLayoutParams(navContainerParams);
        navContainer.setOrientation(LinearLayout.HORIZONTAL);
        for (View navNameView : mNavNameViews) {
            LinearLayout navButton = new LinearLayout(getContext());
            LinearLayout.LayoutParams navParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            navParams.weight = 1;
            navButton.setLayoutParams(navParams);
            navButton.setGravity(Gravity.CENTER);
            navButton.setOrientation(LinearLayout.VERTICAL);
            navButton.setOnClickListener(this);
            navButton.addView(navNameView);
            mNavButtons.add(navButton);
            navContainer.addView(navButton);
        }
        addView(navContainer);
        mNavButtonCount = mNavButtons.size();
    }

    /**
     * 设置导航栏按钮的文字颜色
     *
     * @param color
     */
    public void setNavigationButtonTextColor(int color) {
        for (TextView navNameView : mNavNameViews) {
            navNameView.setTextColor(color);
        }
    }

    /**
     * 设置蝴蝶的样式，必须在 bindViewPager() 以及 setNavigationButtonNames() 方法之后调用
     */
    public void setButterfly(int forwardDrawableId, int backwardDrawableId) {
        setButterfly(forwardDrawableId, backwardDrawableId, 0);
    }

    public void setButterfly(int forwardDrawableId, int backwardDrawableId, int butterflySize) {
        Drawable forwardDrawable = getContext().getResources().getDrawable(forwardDrawableId);
        Drawable backwardDrawable = getContext().getResources().getDrawable(backwardDrawableId);
        setButterfly(forwardDrawable, backwardDrawable, butterflySize);
    }

    public void setButterfly(Drawable forwardDrawable, Drawable backwardDrawable, int butterflySize) {
        mButterflySize = butterflySize;
        mButterflyDrawableForward = forwardDrawable;
        mButterflyDrawableBackward = backwardDrawable;
        vButterfly = new ImageView(getContext());
        vButterfly.setImageDrawable(forwardDrawable);
        addView(vButterfly, butterflySize, butterflySize);
        //设置了蝴蝶之后才需要视图加载完成之后保存布局的相关数据，用来计算蝴蝶的飞行
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void setNavButtonsTextColor(int defaultColor, int selectedColor) {
        mNavButtonTextDefaultColor = defaultColor;
        mNavButtonTextSelectedColor = selectedColor;
        if (null != vButterfly)
            vButterfly.setColorFilter(mNavButtonTextSelectedColor, PorterDuff.Mode.MULTIPLY);
        if (null != mNavNameViews && mNavNameViews.size() > 0)
            for (TextView navNameView : mNavNameViews) {
                navNameView.setTextColor(mNavButtonTextDefaultColor);
            }
        mNavNameViews.get(mDefaultPager).setTextColor(mNavButtonTextSelectedColor);
    }

    /**
     * 导航按钮监听事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (null == vBindViewPager)
            return;
        int position = mNavButtons.indexOf(v);
        vBindViewPager.setCurrentItem(position);
    }

    /**
     * 等待View加载完成，保存相关数据，用来计算蝴蝶的飞行
     */
    @Override
    public void onGlobalLayout() {
//        LogUtils.i("onGlobalLayout() --> getWidth:" + getWidth() + " | getHeight:" + getHeight());
        //判断butterfly的大小，如果大小为0，默认设置为导航条高度的一半
        if (0 == mButterflySize) {
            mButterflySize = getHeight() / 2;
            vButterfly.getLayoutParams().width = mButterflySize;
            vButterfly.getLayoutParams().height = mButterflySize;
            vButterfly.setY(mLandingFineTuningY);
        }
        //获取蝴蝶可以降落的点
        int landingSiteSize = mNavNameViews.size();
        mLandingSiteArray = new int[landingSiteSize * 2];
        float navX = getX();
//        LogUtils.i("navX = " + navX);
        int location[] = new int[2];
        TextView navNameView;
        for (int i = 0; i < landingSiteSize; i++) {
            navNameView = mNavNameViews.get(i);
            navNameView.getLocationOnScreen(location);
            int index = i * 2;
            mLandingSiteArray[index] = location[0];
            mLandingSiteArray[index + 1] = location[0] + navNameView.getWidth();
        }
//        LogUtils.i("mLandingSiteArray[] : " + mLandingSiteArray[0] + "," + mLandingSiteArray[1] + "," + mLandingSiteArray[2] + "," + mLandingSiteArray[3] + ","
//                + mLandingSiteArray[4] + "," + mLandingSiteArray[5] + "," + mLandingSiteArray[6] + "," + mLandingSiteArray[7]);
        //将蝴蝶降落到默认的位置
        mCurrentLandingSite = mDefaultPager * 2;
        vButterfly.setX(mLandingSiteArray[mCurrentLandingSite] - mButterflySize + mLandingFineTuningX);

        vBindViewPager.setCurrentItem(mDefaultPager, false);
        //设置ViewPager事件监听
        vBindViewPager.addOnPageChangeListener(this);
        //移除监听，只执行当前这一次
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * ViewPager的滑动事件回调
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        LogUtils.i("onPageScrolled() --> position:" + position + " | positionOffset:" + positionOffset + " | positionOffsetPixels:" + positionOffsetPixels);
        butterflySteeringGear(positionOffset);
        butterflyFlyingController(position, positionOffset);
    }

    /**
     * 蝴蝶转向控制器
     */
    private void butterflySteeringGear(float positionOffset) {
        //滑动完成时positionOffset会直接变成0，这时跳过处理,保持方向
        if (positionOffset == 0f)
            return;
        boolean nextDirection = mPrePositionOffset < positionOffset;
        boolean isNeedTurn = mCurrentDirection == nextDirection;
        mPrePositionOffset = positionOffset;
        mCurrentDirection = nextDirection;
        if (!isNeedTurn)
            return;
        setButterflyDirection(nextDirection);
    }

    private void setButterflyDirection(boolean isForword) {
        if (isForword)
            vButterfly.setImageDrawable(mButterflyDrawableForward);
        else
            vButterfly.setImageDrawable(mButterflyDrawableBackward);
    }

    /**
     * 蝴蝶飞行控制器
     *
     * @param position
     * @param positionOffset
     */
    private void butterflyFlyingController(int position, float positionOffset) {
        if (0f == positionOffset) {
            mCurrentLandingSite = mNextLandingSite;
            return;
        }
        //ViewPager向右滑动时，position为当前的position, positionOffset 0->1
        int flightPath = 0;
        if (mCurrentDirection) {//→
            if (position < mNavButtonCount - 2) {
                mNextLandingSite = (position + 1) * 2;
                flightPath = mLandingSiteArray[mNextLandingSite] - mLandingSiteArray[mCurrentLandingSite];
            } else {//滑动到最后一个时进行特殊处理,停到最后一个点
                mNextLandingSite = mLandingSiteArray.length - 1;
                flightPath = mLandingSiteArray[mNextLandingSite] - mLandingSiteArray[mCurrentLandingSite] + mButterflySize - mLandingFineTuningX * 2;
            }
            vButterfly.setX(mLandingSiteArray[mCurrentLandingSite] + flightPath * positionOffset - mButterflySize + mLandingFineTuningX);
        } else {//←
            //ViewPager向左滑动时，position为即将滑动到的（下一个）pager的position，positionOffset 1->0
            if (position > 0) {
                mNextLandingSite = position * 2 + 1;
                flightPath = mLandingSiteArray[mCurrentLandingSite] - mLandingSiteArray[mNextLandingSite];
                vButterfly.setX(mLandingSiteArray[mNextLandingSite] + flightPath * positionOffset - mLandingFineTuningX);
            } else {//返回到第一个时进行特殊处理，停到第一个点
                mNextLandingSite = 0;//position = 0
                flightPath = mLandingSiteArray[mCurrentLandingSite] - mLandingSiteArray[mNextLandingSite] + mButterflySize;
                vButterfly.setX(mLandingSiteArray[mNextLandingSite] - mButterflySize + flightPath * positionOffset + mLandingFineTuningX);
            }
        }
    }


    /**
     * ViewPager的页面切换回调
     * 注意：如果直接点击按钮切换的话会先调用该方法，然后才调用onPageScrolled()
     * 而滑动切换的时候是在释放滑动的时候调用
     */
    @Override
    public void onPageSelected(int position) {
//        LogUtils.i("onPageSelected() --> position:" + position);
    }

    /**
     * ViewPager滑动状态改变的回调
     *
     * @param state 0 正常显示 1 拖动 2 释放
     */
    @Override
    public void onPageScrollStateChanged(int state) {
//        LogUtils.i("onPageScrollStateChanged() --> state:" + state);
        switch (state) {
            case 0:
                //恢复到正常状态时，处理飞到第一个和最后一个降落点转方向
                if (mNextLandingSite == 0)
                    setButterflyDirection(true);
                else if (mNextLandingSite == mLandingSiteArray.length - 1)
                    setButterflyDirection(false);
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }
}
