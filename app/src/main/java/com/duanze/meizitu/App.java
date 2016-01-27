package com.duanze.meizitu;

import android.app.Application;
import android.content.Context;

import com.duanze.meizitu.utils.MeizituPrefs;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by Duanze on 2016/1/3.
 */
public class App extends Application {
    private static Context sMe;

    public static Context getInstance() {
        return sMe;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        sMe = this;
//        initImageLoader(this);
        MeizituPrefs.initFromXml(this);

//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
//        ImageLoader.getInstance().init(configuration);
    }

    // 初始化ImageLoader
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }
}
