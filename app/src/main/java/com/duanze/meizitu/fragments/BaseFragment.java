package com.duanze.meizitu.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.duanze.meizitu.App;
import com.duanze.meizitu.mvp.presenters.IPresenter;
import com.duanze.meizitu.network.RequestManager;

/**
 * Created by Duanze on 2016/1/7.
 */
public abstract class BaseFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIPresenter()) {
            getIPresenter().onDestroy();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != getIPresenter()) {
            getIPresenter().onDestroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != getIPresenter()) {
            getIPresenter().onDestroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != getIPresenter()) {
            getIPresenter().onDestroy();
        }
    }

    @Override
    public void onStop() {
        RequestManager.getInstance(App.getInstance()).cancelAll(this);
        super.onStop();
        if (null != getIPresenter()) {
            getIPresenter().onDestroy();
        }
    }

    @Override
    public void onDestroy() {
        if (null != getIPresenter()) {
            getIPresenter().onDestroy();
        }
        super.onDestroy();
    }

    protected abstract IPresenter getIPresenter();

    protected void executeRequest(Request request) {
        RequestManager.getInstance(App.getInstance()).addRequest(request, this);
    }

    protected Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != getActivity()) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

}
