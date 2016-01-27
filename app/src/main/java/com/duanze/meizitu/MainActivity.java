package com.duanze.meizitu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.duanze.meizitu.fragments.FeedsFragment;
import com.duanze.meizitu.mvp.presenters.IPresenter;
import com.duanze.meizitu.mvp.presenters.impl.MainPresenter;
import com.duanze.meizitu.mvp.views.exte.MainView;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainView{
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.spinner) Spinner spinner;
    @Bind(R.id.fab) FloatingActionButton fab;

    private MainPresenter mMainPresenter;
    private Fragment curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mMainPresenter =new MainPresenter(this);
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup spinner
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                getResources().getStringArray(R.array.spinner)));

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.

                mMainPresenter.onItemSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void refresh() {
        if (null != curFragment) {
            if (curFragment instanceof FeedsFragment) {
                ((FeedsFragment) curFragment).scrollTopAndRefresh();
            }
        }
    }

    @Override
    public void setCurFragment(Fragment fragment) {
        this.curFragment = fragment;
    }

    @Override
    public FragmentManager getFM() {
        return getSupportFragmentManager();
    }

    @OnClick(R.id.fab)
    public void onFABClicked(View view){
        mMainPresenter.showAboutDialog();
    }

    @Override
    protected IPresenter getIPresenter() {
        return mMainPresenter;
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_main2;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        if (mMainPresenter.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public View getCoordinator() {
        return null;
    }


    private static class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, android.R.id.text1, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView text = (TextView) view.findViewById(android.R.id.text1);
            // Hack. Use BuildVersion 23 for a better approach.
            text.setTextColor(Color.BLACK);
            text.setBackgroundColor(Color.WHITE);
            return view;
        }
    }

}
