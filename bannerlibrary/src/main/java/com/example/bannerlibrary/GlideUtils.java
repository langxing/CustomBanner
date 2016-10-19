package com.example.bannerlibrary;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

/**
 * Created by jack on 2016/9/30.
 * 图片加载帮助类,后期可根据需要进行扩展
 */

public class GlideUtils {

    private static GlideUtils instance;
    private static Context mContext;

    public static GlideUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (GlideUtils.class) {
                if (instance == null) {
                    instance = new GlideUtils();
                    mContext = context;
                }
            }
        }
        return instance;
    }

    /**
     *
     * @param url
     * @param imageView
     */
    public void loadImageByUrl(String url, ImageView imageView) {
        Glide.with(mContext).
                load(url).
                asBitmap().
                fitCenter().
                centerCrop().
                placeholder(R.drawable.img_def).
                error(R.drawable.img_def)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    /**
     * 加载本地资源图片
     * @param id
     * @param imageview
     */
    public void loadImageByRes(int id, ImageView imageview) {
        Glide.with(mContext).
                load(id).
                asBitmap().
                fitCenter().
                centerCrop().
                placeholder(R.drawable.img_def).
                error(R.drawable.img_def)
                .into(imageview);
    }

    /**
     * 加载文件
     * @param path
     * @param imageview
     */
    public void loadImageByPath(String path, ImageView imageview) {
            Glide.with(mContext).
                    load(new File(path)).
                    asBitmap().
                    fitCenter().
                    centerCrop()
                    .placeholder(R.drawable.img_def).
                    error(R.drawable.img_def)
                    .into(imageview);

    }

}
