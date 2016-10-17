package com.example.bannerlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

/**
 * Created by jack on 2016/10/17.
 */

public class Banner extends RelativeLayout implements ViewPager.OnPageChangeListener  {

    private ViewPager mViewPager;
    private Context context;
    private Banner.CustomAdapter adapter;
    private LinearLayout linearLayout;
    private LinkedList<ImageView> mCaches = new LinkedList<>();
    private List<? extends Object> ids = new ArrayList<>();
    private int interval; //间隔时间
    private int dotWid; //小圆点宽
    private int dotHei; //小圆点高
    private int dotMar; //小圆点间隔
    private int resId; //小圆点选择器
    private Banner.CustomRunnable myRunnable;
    private int oldSel = 0;

    public void setIds(List<? extends Object> ids) {
        this.ids = ids;
        if(ids == null || ids.size() == 0) {
            return;
        }
        int count = ids.size();
        for (int i = 0; i < count; i++) {
            View view = new View(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotWid, dotHei);
            params.leftMargin = dotMar;
            params.rightMargin = dotMar;
            view.setLayoutParams(params);
            view.setBackgroundResource(resId);
            linearLayout.addView(view);
        }
        adapter = new Banner.CustomAdapter();
        linearLayout.getChildAt(0).setSelected(true);
        mViewPager.setAdapter(adapter);
        start();
    }

    public Banner(Context context) {
        this(context, null);
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_banner, this, true);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        this.context = context;
        myRunnable = new Banner.CustomRunnable();
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(0);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet set) {
        TypedArray array = context.obtainStyledAttributes(set, R.styleable.Banner);
        interval = array.getInt(R.styleable.Banner_delayMillis, 1000);
        dotHei = (int) array.getDimension(R.styleable.Banner_dotHei, 15);
        dotWid = (int) array.getDimension(R.styleable.Banner_dotWid, 15);
        dotMar = (int) array.getDimension(R.styleable.Banner_dotMar, 15);
        resId = array.getResourceId(R.styleable.Banner_dotScr, R.drawable.dot_selector);
        array.recycle();
    }

    public void start() {
        stop();
        if (ids.size() > 1) {
            mViewPager.postDelayed(myRunnable, interval);
        }
    }

    public void stop() {
        mViewPager.removeCallbacks(myRunnable);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int curSel = position % ids.size();
        linearLayout.getChildAt(oldSel).setSelected(false);
        linearLayout.getChildAt(curSel).setSelected(true);
        oldSel = curSel;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE)
            start();
        else if (state == SCROLL_STATE_DRAGGING)
            stop();
    }

    private class CustomAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return ids.size() == 1 ? 1 : Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            if(object != null) {
                mCaches.clear();
                mCaches.add((ImageView) object);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = null;
            if (mCaches.size() == 0) {
                imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new ViewPager.LayoutParams());
            } else {
                imageView = mCaches.removeFirst();
            }
            Object obj = ids.get(position % ids.size());
            if(obj instanceof String) {
                GlideUtils.getInstance(context).loadImageByUrl(obj.toString(), imageView);
            }else {
                GlideUtils.getInstance(context).loadImageByRes((Integer) obj, imageView);
            }
            container.addView(imageView);
            return imageView;
        }
    }

    private class CustomRunnable implements Runnable {

        @Override
        public void run() {
            int curSel = mViewPager.getCurrentItem();
            int newSel = 0;
            if (curSel < Integer.MAX_VALUE) {
                newSel = curSel + 1 % Integer.MAX_VALUE;
            } else {
                newSel = 0;
            }
            start();
            mViewPager.setCurrentItem(newSel);
        }
    }
}
