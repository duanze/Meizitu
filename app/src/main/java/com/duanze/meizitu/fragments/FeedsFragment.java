package com.duanze.meizitu.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.duanze.meizitu.App;
import com.duanze.meizitu.MeiziActivity;
import com.duanze.meizitu.R;
import com.duanze.meizitu.data.FeedsDataHelper;
import com.duanze.meizitu.models.Feed;
import com.duanze.meizitu.mvp.presenters.IPresenter;
import com.duanze.meizitu.network.GsonRequest;
import com.duanze.meizitu.network.TaskUtils;
import com.duanze.meizitu.utils.ListViewUtils;
import com.duanze.meizitu.views.adapters.FeedsAdapter;
import com.duanze.meizitu.views.widgets.PageListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Duanze on 2016/1/12.
 */
public class FeedsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_SECTION_NUMBER = "section_number";

    @Bind(R.id.swipe_container) SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.listView) PageListView mListView;

    //    private FeedsDataHelper mDataHelper;
    private FeedsDataHelper mDataHelper;
    private FeedsAdapter mAdapter;
    private int mMaxId = 0;
    private int mSinceId = 0;
    private String mString = "http://www.ourhfuu.com/meizitu.php";

    @Override
    protected IPresenter getIPresenter() {
        return null;
    }

    public static FeedsFragment newInstance(int sectionNumber) {
        FeedsFragment fragment = new FeedsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        ((MainActivity) activity).onSectionAttached(
//                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this,contentView);

        init();

        return contentView;
    }

    private void init() {
        mDataHelper = new FeedsDataHelper(App.getInstance());
        mAdapter = new FeedsAdapter(getActivity(), mListView);
        getLoaderManager().initLoader(0, null, this);
//        View header = new View(getActivity());
//        mListView.addHeaderView(header);
//        AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
//        animationAdapter.setAbsListView(mListView);
//        mListView.setAdapter(animationAdapter);

        mListView.setAdapter(mAdapter);
        mListView.setLoadNextListener(new PageListView.OnLoadNextListener() {
            @Override
            public void onLoadNext() {
                loadNextData();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int actualPosition = position - mListView.getHeaderViewsCount();
                if (actualPosition < 0) {
                    return;
                }

//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity());

//                Intent intent = new Intent(getActivity(), ImageViewActivity.class);
//                Feed feed = mAdapter.getItem(position - mListView.getHeaderViewsCount());
//                if (feed == null) {
//                    return;
//                }
//                intent.putExtra(ImageViewActivity.IMAGE_NAME, feed.getName());
//                intent.putStringArrayListExtra(ImageViewActivity.IMAGE_URL, feed.getImgs());
//                intent.putExtra(ImageViewActivity.IMAGE_ID, feed.getId());
//                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

                MeiziActivity.actionStart(getActivity()
                        , mAdapter.getItem(position - mListView.getHeaderViewsCount()));
            }
        });

        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setOnRefreshListener(this);
    }

    private String getRefreshUrl() {
        return mString + "?max_id=" + mMaxId;
    }

    private String getNextUrl() {
        return mString + "?since_id=" + mSinceId;
    }

    private void loadNextData() {
        if (mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(false);
        }
        executeRequest(new GsonRequest(getNextUrl()
                , Feed.FeedRequestData.class
                , responseListener()
                , errorListener()));
    }

    private void refreshData() {
        if (!mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(true);
        }
        executeRequest(new GsonRequest(getRefreshUrl()
                , Feed.FeedRequestData.class
                , responseListener()
                , errorListener()));
    }

    private Response.Listener<Feed.FeedRequestData> responseListener() {
        return new Response.Listener<Feed.FeedRequestData>() {

            @Override
            public void onResponse(final Feed.FeedRequestData response) {
                TaskUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        ArrayList<Feed> feeds = response.data;
                        if (feeds != null && feeds.size() > 0) {
                            mDataHelper.bulkInsert(feeds);
                            int num1 = feeds.get(0).getId();
                            int num2 = feeds.get(feeds.size() - 1).getId();
                            if (num1 > mMaxId) {
                                mMaxId = num1;
                            }
                            if (mSinceId == 0 || num1 < mSinceId) {
                                mSinceId = num1;
                            }
                            if (num2 > mMaxId) {
                                mMaxId = num2;
                            }
                            if (mSinceId == 0 || num2 < mSinceId) {
                                mSinceId = num2;
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        mSwipeLayout.setRefreshing(false);
                        mListView.setState(PageListView.State.Idle, 3000);
                    }
                });
            }
        };
    }

    public void scrollTopAndRefresh() {
        if (mListView != null) {
            ListViewUtils.smoothScrollListViewToTop(mListView);
            refreshData();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mDataHelper.getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        if (data != null && data.getCount() == 0) {
            refreshData();
        } else {
            int num1 = mAdapter.getItem(mAdapter.getCount() -1 ).getId();
            int num2 = mAdapter.getItem(0).getId();
            if(num1 > num2) {
                mMaxId = num1;
                mSinceId = num2;
            } else {
                mMaxId = num2;
                mSinceId = num1;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public void onRefresh() {
        refreshData();
    }
}
