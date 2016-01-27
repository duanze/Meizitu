package com.duanze.meizitu;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.duanze.meizitu.mvp.presenters.IPresenter;
import com.duanze.meizitu.network.RequestManager;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * Created by Duanze on 2016/1/7.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        showActivityInAnim();
        super.onCreate(savedInstanceState);
        setContentView(getContentResId());
        // 经测试在代码里直接声明透明状态栏更有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            //沉浸式时，对状态栏染色
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getStatusBarColor());
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
//        // enable navigation bar tint
//        tintManager.setNavigationBarTintEnabled(true);
        }

        ButterKnife.bind(this);

        if (null != getIPresenter()) {
            getIPresenter().onCreate(savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != getIPresenter()) {
            getIPresenter().onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != getIPresenter()) {
            getIPresenter().onResume();
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != getIPresenter()) {
            getIPresenter().onPause();
        }
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        if (null != getIPresenter()) {
            getIPresenter().onStop();
        }
        RequestManager.getInstance(App.getInstance()).cancelAll(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (null != getIPresenter()) {
            getIPresenter().onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        showActivityExitAnim();
    }

    public int getStatusBarColor() {
        return getColorPrimaryDark();
    }

    public int getColorPrimaryDark() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    protected void executeRequest(Request request) {
        RequestManager.getInstance(App.getInstance()).addRequest(request, this);
    }

    protected Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BaseActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    protected abstract IPresenter getIPresenter();

    protected abstract int getContentResId();


    protected void showActivityInAnim() {

    }

    protected void showActivityExitAnim() {

    }
}
