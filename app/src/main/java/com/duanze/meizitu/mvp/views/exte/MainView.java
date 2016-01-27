package com.duanze.meizitu.mvp.views.exte;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.duanze.meizitu.MainActivity;
import com.duanze.meizitu.mvp.views.IView;

/**
 * Created by Duanze on 2016/1/3.
 */
public interface MainView extends IView {
    Activity getActivity();
    View getCoordinator();
    void refresh();
    void setCurFragment(Fragment fragment);
    FragmentManager getFM();
}
