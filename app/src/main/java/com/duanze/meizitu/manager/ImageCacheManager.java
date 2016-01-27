package com.duanze.meizitu.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.duanze.meizitu.network.RequestManager;


public class ImageCacheManager {
    // 取运行内存阈值的1/8作为图片缓存
    private static int MEM_CACHE_SIZE;

    private ImageLoader mImageLoader;

    private static ImageCacheManager sImageCacheManager;

    private ImageCacheManager(Context context) {
        MEM_CACHE_SIZE = 1024 * 1024
                * ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() / 8;

        mImageLoader = new ImageLoader(
                RequestManager.getInstance(context).getRequestQueue()
                , new BitmapLruCache(MEM_CACHE_SIZE));
    }

    public static ImageCacheManager getInstance(Context context) {
        if (null == sImageCacheManager) {
            sImageCacheManager = new ImageCacheManager(context);
        }
        return sImageCacheManager;
    }

    public ImageLoader.ImageContainer loadImage(String requestUrl,
                                                ImageLoader.ImageListener imageListener) {
        return loadImage(requestUrl, imageListener, 0, 0);
    }

    public ImageLoader.ImageContainer loadImage(String requestUrl,
                                                ImageLoader.ImageListener imageListener, int maxWidth, int maxHeight) {
        return mImageLoader.get(requestUrl, imageListener, maxWidth, maxHeight);
    }

    public static ImageLoader.ImageListener getImageListener(
            final Resources r, final ImageView iv, final Drawable defDrawable, final Drawable errorDrawable) {

        return new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorDrawable != null) {
                    iv.setImageDrawable(errorDrawable);
                }
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    if (!isImmediate && defDrawable != null) {
                        TransitionDrawable transitionDrawable = new TransitionDrawable(
                                new Drawable[]{
                                        defDrawable,
                                        new BitmapDrawable(r, response.getBitmap())
                                }
                        );
                        transitionDrawable.setCrossFadeEnabled(true);
                        iv.setImageDrawable(transitionDrawable);
                        transitionDrawable.startTransition(100);
                    } else {
                        iv.setImageBitmap(response.getBitmap());
                    }
                } else if (defDrawable != null) {
                    iv.setImageDrawable(defDrawable);
                }
            }
        };
    }
}
