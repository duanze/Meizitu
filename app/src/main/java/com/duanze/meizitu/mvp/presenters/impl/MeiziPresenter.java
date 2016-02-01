package com.duanze.meizitu.mvp.presenters.impl;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.duanze.meizitu.R;
import com.duanze.meizitu.mvp.presenters.IPresenter;
import com.duanze.meizitu.mvp.views.exte.MeiziView;

/**
 * Created by duanze on 16-2-1.
 */
public class MeiziPresenter implements IPresenter,ViewPager.OnPageChangeListener {
    private MeiziView mMeiziView;
    private Activity mActivity;

    private MenuItem favorite;
    private MenuItem save;

    public MeiziPresenter(MeiziView meiziView) {
        mMeiziView = meiziView;
        mActivity = meiziView.getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        mMeiziView = null;
        mActivity = null;
    }

    public void onCreateOptionsMenu(Menu menu) {
        favorite = menu.findItem(R.id.action_favorite);
        save = menu.findItem(R.id.action_save);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mMeiziView.finish();
                return true;
            case R.id.action_favorite:
                favoriteOrNot();
                return true;
            case R.id.action_save:
//                savePicture();
                return true;
        }
        return false;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        if (mMeiziView.isFavorite()) {
            favorite.setIcon(R.drawable.ic_favorite_white);
        } else {
            favorite.setIcon(R.drawable.ic_favorite_outline);
        }
    }

    private void favoriteOrNot() {
        mMeiziView.favoriteOrNot();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mMeiziView.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mMeiziView.onPageScrollStateChanged(state);
    }
}
