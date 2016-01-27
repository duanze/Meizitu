package com.duanze.meizitu.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.duanze.meizitu.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PageListView extends ListView implements AbsListView.OnScrollListener{

    public interface OnLoadNextListener {
        void onLoadNext();
    }

    private LoadingFooter mLoadingFooter;

    private OnLoadNextListener mLoadNextListener;

    public PageListView(Context context) {
        super(context);
        init();
    }

    public PageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PageListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mLoadingFooter = new LoadingFooter(getContext());
        addFooterView(mLoadingFooter.getView());

        setOnScrollListener(this);
    }

    public void setLoadNextListener(OnLoadNextListener listener) {
        mLoadNextListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (mLoadingFooter.getState() == State.Loading
                || mLoadingFooter.getState() == State.TheEnd) {
            return;
        }
        if (firstVisibleItem + visibleItemCount >= totalItemCount
                && totalItemCount != 0
                && totalItemCount != getHeaderViewsCount() + getFooterViewsCount()
                && mLoadNextListener != null) {
            mLoadingFooter.setState(State.Loading);
            mLoadNextListener.onLoadNext();
        }
    }

    public void setState(State status) {
        mLoadingFooter.setState(status);
    }

    public void setState(State status, long delay) {
        mLoadingFooter.setState(status, delay);
    }


    public enum State {
        Idle, TheEnd, Loading
    }

    public class LoadingFooter {
        protected View mLoadingFooter;

        @Bind(R.id.textView) TextView mLoadingText;
        @Bind(R.id.progressBar) ProgressBar mProgress;

        protected State mState = State.Idle;

        private long mAnimationDuration;


        public LoadingFooter(Context context) {
            mLoadingFooter = LayoutInflater.from(context).inflate(R.layout.loading_footer, null);
            mLoadingFooter.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 屏蔽点击
                }
            });
            ButterKnife.bind(this, mLoadingFooter);

            mAnimationDuration = context.getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
        }

        public View getView() {
            return mLoadingFooter;
        }

        public State getState() {
            return mState;
        }

        public void setState(final State state, long delay) {
            mLoadingFooter.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            setState(state);
                        }
                    }
                    , delay);
        }

        public void setState(State status) {
            if (mState == status) {
                return;
            }
            mState = status;

            mLoadingFooter.setVisibility(View.VISIBLE);

            switch (status) {
                case Loading:
                    mLoadingText.setText(R.string.loading_footer_loading);
                    mLoadingText.setVisibility(View.VISIBLE);
                    mProgress.setVisibility(View.VISIBLE);
                    break;
                case TheEnd:
                    mLoadingText.setText(R.string.loading_footer_theEnd);
                    mLoadingText.setVisibility(View.VISIBLE);
                    mLoadingText.animate().withLayer().alpha(1).setDuration(mAnimationDuration);
                    mProgress.setVisibility(View.GONE);
                    break;
                default:
                    mLoadingFooter.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
