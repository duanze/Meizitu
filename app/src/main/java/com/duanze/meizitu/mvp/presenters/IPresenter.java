package com.duanze.meizitu.mvp.presenters;

import android.os.Bundle;

/**
 * Created by Duanze on 2016/1/3.
 */
public interface IPresenter {
    void onCreate(Bundle savedInstanceState);

    void onResume();

    void onStart();

    void onPause();

    void onStop();

    void onDestroy();
}
