package com.duanze.meizitu.mvp.presenters.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.duanze.meizitu.MainActivity;
import com.duanze.meizitu.R;
import com.duanze.meizitu.fragments.FeedsFragment;
import com.duanze.meizitu.mvp.presenters.IPresenter;
import com.duanze.meizitu.mvp.views.exte.MainView;
import com.duanze.meizitu.utils.MeizituPrefs;
import com.duanze.meizitu.utils.RateAppUtil;

/**
 * Created by Duanze on 2016/1/3.
 */
public class MainPresenter implements IPresenter {
    private MainView mMainView;
    private Activity mActivity;

    public MainPresenter(MainView mainView) {
        this.mMainView = mainView;
        this.mActivity = mainView.getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        int cnt = MeizituPrefs.getLaunchTime();
        MeizituPrefs.putLaunchTime(cnt + 1);
    }

    @Override
    public void onResume() {
        rateForApp();
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
        mActivity = null;
        mMainView = null;
    }

    public void showAboutDialog() {
        new AlertDialog.Builder(mActivity)
                .setMessage(mActivity.getString(R.string.menu_about_dialog_message))
                .setPositiveButton(R.string.menu_about_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goJianshu();
                    }
                })
                .setNegativeButton(R.string.menu_about_dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goGitHub();
                    }
                })
                .setNeutralButton(R.string.menu_about_dialog_neutral, null)
                .create().show();
    }

    private void goGitHub() {
        Uri uri = Uri.parse("https://github.com/duanze/Meizitu");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        mActivity.startActivity(it);
    }

    private void goJianshu() {
        Uri uri = Uri.parse("http://www.jianshu.com/users/aea2a95dbe49/latest_articles");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        mActivity.startActivity(it);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            mMainView.refresh();
            return true;
        }
        return false;
    }

    private void switchToPage(String str) {
        Snackbar.make(mMainView.getCoordinator(), str, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void onSceneryFABClick(View view) {
        Snackbar.make(mMainView.getCoordinator(), "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    //    一次性评分弹窗
    private void rateForApp() {
        if (!MeizituPrefs.getIsRated()) {
            int launchTime = MeizituPrefs.getLaunchTime();
            if (0 != launchTime
                    && (launchTime % MeizituPrefs.RATE_THRESHOLD == 0)) {
                new AlertDialog.Builder(mActivity).setMessage(R.string.dialog_rate_message)
                        .setPositiveButton(R.string.dialog_rate_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RateAppUtil.goToMarket(mActivity);
                                MeizituPrefs.putIsRated(true);
                            }
                        })
                        .setNegativeButton(R.string.dialog_rate_negative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RateAppUtil.feedback(mActivity);
                                MeizituPrefs.putIsRated(true);
                            }
                        })
                        .setNeutralButton(R.string.dialog_rate_neutral, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                restoreZeroLaunchTime();
                            }
                        })
                        .setCancelable(false)
                        .create().show();
            }
        }
    }


    private void restoreZeroLaunchTime() {
        MeizituPrefs.putLaunchTime(0);
    }

    public void onItemSelected(int position) {
        Fragment fragment = null;
        if (0 == position) {
            fragment = FeedsFragment.newInstance(0);
        }

        if (null != fragment) {
            mMainView.setCurFragment(fragment);
            mMainView.getFM().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }
}
