package com.example.bannerlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

/**
 * Created by jack on 2016/10/17.
 */

public class Banner extends RelativeLayout implements ViewPager.OnPageChangeListener {

    public ViewPager getmViewPager() {
        return mViewPager;
    }

    private ViewPager mViewPager;
    private Context context;
    private Banner.CustomAdapter adapter;
    private OnPagerClickListener clickListener;
    private OnPageSelectedListener selectedListener;
    private LinearLayout linearLayout;
    private LinkedList<ImageView> mCaches = new LinkedList<>();
    private List<? extends Object> images = new ArrayList<>();
    private int interval; //间隔时间
    private int dotWid; //小圆点宽
    private int dotHei; //小圆点高
    private int dotMar; //小圆点间隔
    private int resId; //小圆点选择器
    private Banner.CustomRunnable myRunnable;
    private int oldSel = 0;

    public void setImages(List<? extends Object> images) {
        this.images = images;
        if (images == null || images.size() == 0) {
            return;
        }
        int count = images.size();
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
        if (images.size() > 1) {
            mViewPager.postDelayed(myRunnable, interval);
        }
    }

    public void stop() {
        mViewPager.removeCallbacks(myRunnable);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (selectedListener != null) {
            selectedListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (selectedListener != null) {
            selectedListener.onPageSelected(position);
        }
        int curSel = position % images.size();
        linearLayout.getChildAt(oldSel).setSelected(false);
        linearLayout.getChildAt(curSel).setSelected(true);
        oldSel = curSel;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (selectedListener != null) {
            selectedListener.onPageScrollStateChanged(state);
        }
        if (state == SCROLL_STATE_IDLE)
            start();
        else if (state == SCROLL_STATE_DRAGGING)
            stop();
    }

    private class CustomAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.size() == 1 ? 1 : Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView imageView = (ImageView) object;
            container.removeView(imageView);
            mCaches.clear();
            mCaches.add(imageView);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = null;
            if (mCaches.size() == 0) {
                imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new ViewPager.LayoutParams());
            } else {
                imageView = mCaches.removeFirst();
            }
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onClick(position % images.size(), (ImageView) v);
                    }
                }
            });
            Object obj = images.get(position % images.size());
            if (obj instanceof String) {
                GlideUtils.getInstance(context).loadImageByUrl(obj.toString(), imageView);
            } else {
                GlideUtils.getInstance(context).loadImageByRes((Integer) obj, imageView);
            }
            container.addView(imageView);
            return imageView;
        }
    }

    /**
     * 回收ImageView占用的图像内存
     *
     * @param imageView
     */
    public void releaseIV(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            Bitmap bitmap = bd.getBitmap();
            if (bitmap == null || bitmap.isRecycled()) return;
            imageView.setImageBitmap(null);
            bitmap.recycle();
            bitmap = null;
            System.gc();
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

    public void setOnPagerClickListener(OnPagerClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnPagerClickListener {
        public void onClick(int position, ImageView imageView);
    }

    public void setOnPageSelectedListener(OnPageSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    public interface OnPageSelectedListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }
}
