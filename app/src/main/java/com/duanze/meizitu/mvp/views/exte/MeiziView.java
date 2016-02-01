package com.duanze.meizitu.mvp.views.exte;

import android.app.Activity;

import com.duanze.meizitu.mvp.views.IView;

/**
 * Created by duanze on 16-2-1.
 */
public interface MeiziView extends IView {
    void finish();
    boolean isFavorite();
    void favoriteOrNot();
    Activity getActivity();
}
