package com.duanze.meizitu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.duanze.meizitu.models.Feed;
import com.duanze.meizitu.mvp.presenters.IPresenter;
import com.duanze.meizitu.mvp.presenters.impl.MeiziPresenter;
import com.duanze.meizitu.mvp.views.exte.MeiziView;
import com.duanze.meizitu.views.widgets.ProgressWheel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by Sam on 14-4-15.
 */
public class MeiziActivity extends BaseActivity implements MeiziView,ViewPager.OnPageChangeListener {
    public static final String IMAGE_URL = "image_url";
    public static final String IMAGE_NAME = "image_name";
    public static final String IMAGE_ID = "image_id";


    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.textView) TextView tv;
    @Bind(R.id.viewpager) ViewPager pager;

    private ArrayList<PhotoViewAttacher> attachers;
    private ArrayList<PhotoView> photoViews;
    private ArrayList<ProgressWheel> progressWheels;
    private ArrayList<String> urls;
    private ArrayList<View> views;
    private String mName;
    private int mId;
    private boolean isFavorite;
    //    private LikesDataHelper mLikeHelper;
//    private RequestQueue mQueue;
    private DisplayImageOptions options;
    private MeiziPresenter mMeiziPresenter;

    public static void actionStart(Context context, Feed feed) {
        Intent intent = new Intent(context, MeiziActivity.class);
        if (feed == null) {
            return;
        }
        intent.putExtra(MeiziActivity.IMAGE_NAME, feed.getName());
        intent.putStringArrayListExtra(MeiziActivity.IMAGE_URL, feed.getImgs());
        intent.putExtra(MeiziActivity.IMAGE_ID, feed.getId());
        context.startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        mMeiziPresenter = new MeiziPresenter(this);
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!parseIntent()) return;

//        mLikeHelper = new LikesDataHelper(this);
//        isFavorite = mLikeHelper.query(mId) != null;
        setTitle(mName);
        views = new ArrayList<View>();
        tv.setText(getString(R.string.activity_meizi_bottom_order, 1, urls.size()));
        photoViews = new ArrayList<PhotoView>();
        attachers = new ArrayList<PhotoViewAttacher>();
        progressWheels = new ArrayList<ProgressWheel>();

//        mQueue = Volley.newRequestQueue(this);
        options = new DisplayImageOptions.Builder().cacheOnDisc(true).considerExifParams(true).build();

        for (int i = 0; i < urls.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.imageviewer, null);

            PhotoView pv = (PhotoView) view.findViewById(R.id.photoView);
            final ProgressWheel progress = (ProgressWheel) view.findViewById(R.id.progressWheel);
            final PhotoViewAttacher attacher = new PhotoViewAttacher(pv);
            photoViews.add(pv);
            attachers.add(attacher);
            progressWheels.add(progress);
//            attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//                @Override
//                public void onPhotoTap(View view, float x, float y) {
//                    finish();
//                }
//            });

            // Just load the first image in the beginning
            if (0 == i) {
                loadImage(i);
            }
            views.add(view);
        }

        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager) container).removeView(views.get(position));
            }

            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager) container).addView(views.get(position));
                return views.get(position);
            }

        };
        pager.setAdapter(mPagerAdapter);
        pager.addOnPageChangeListener(this);
    }

    private void loadImage(final int i) {

        ImageLoader.getInstance().displayImage(urls.get(i), photoViews.get(i), options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressWheels.get(i).setVisibility(View.GONE);
                attachers.get(i).update();
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                progressWheels.get(i).setProgress(360 * current / total);
            }
        });

        // / Volley don't support publish progress
//            ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
//            ImageLoader.ImageListener listener = ImageLoader.getImageListener(pv, 0, 0);
//            imageLoader.get(urls.get(i), listener);
    }

    private boolean parseIntent() {
        urls = getIntent().getStringArrayListExtra(IMAGE_URL);
        mName = getIntent().getStringExtra(IMAGE_NAME);
        mId = getIntent().getIntExtra(IMAGE_ID, -1);
        if (mId <= 0) {
            finish();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meizi, menu);
        mMeiziPresenter.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMeiziPresenter.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMeiziPresenter.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (attachers == null) {
            return;
        }
        for (int i = 0; i < attachers.size(); i++) {
            if (attachers.get(i) != null) {
                attachers.get(i).cleanup();
            }
        }

    }

    @Override
    protected IPresenter getIPresenter() {
        return mMeiziPresenter;
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_meizi;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tv.setText(getString(R.string.activity_meizi_bottom_order, position + 1, urls.size()));
        // If the image had loaded once, skip this step
        if (View.VISIBLE == progressWheels.get(position).getVisibility()) {
            loadImage(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public boolean isFavorite() {
        return isFavorite;
    }

    @Override
    public void favoriteOrNot() {
        if (isFavorite) {
//            mLikeHelper.delete(mId);
            Toast.makeText(this, getString(R.string.menu_meizi_favorite_false), Toast.LENGTH_SHORT).show();
        } else {
            Feed feed = new Feed();
            feed.setImgs(urls);
            feed.setName(mName);
            feed.setId(mId);
            Toast.makeText(this, getString(R.string.menu_meizi_favorite_true), Toast.LENGTH_SHORT).show();

//            mLikeHelper.insert(feed);
        }
        isFavorite = !isFavorite;
        invalidateOptionsMenu();
    }

    @Override
    public Activity getActivity() {
        return this;
    }


    private void savePicture() {
        if (pager == null || photoViews == null) {
            return;
        }

        int now = pager.getCurrentItem();
        //photoViews.get(now).getDrawingCache();
        ImageLoader.getInstance().loadImage(urls.get(now), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                writeImgToFile(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

    }

    public void writeImgToFile(final Bitmap bit) {
        String externalStorageState = Environment.getExternalStorageState();
        if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sdcard = Environment.getExternalStorageDirectory().getPath();
                String fileDir = sdcard + "/meizitu/";
                File dir = new File(fileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                final String imgFileName = fileDir + "meizi_" + SystemClock.elapsedRealtime() + ".jpg";
                File imgFile = new File(imgFileName);
                try {
                    if (imgFile.exists()) {
                        imgFile.delete();
                    }
                    imgFile.createNewFile();
                    BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(imgFile));
                    bit.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                    try {
                        bos.flush();
                    } catch (IOException e) {

                    }

                    try {
                        bos.close();
                    } catch (IOException e) {

                    }
                    MeiziActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            CToast.showToast(ImageViewActivity.this, "妹子已经成功为你保存到" + imgFileName);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
