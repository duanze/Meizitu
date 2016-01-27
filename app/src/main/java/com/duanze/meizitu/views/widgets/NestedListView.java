package com.duanze.meizitu.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Duanze on 2016/1/24.
 */
public class NestedListView extends ListView implements AbsListView.OnScrollListener, View.OnTouchListener {
    private int listViewTouchAction;
    private static final int MAXIMUM_LIST_ITEMS_VIEWABLE = 99;

    private ViewGroup.LayoutParams mLayoutParams;

    public NestedListView(Context context) {
        super(context);
        initialize();
    }

    public NestedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public NestedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        listViewTouchAction = -1;
        mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        setOnScrollListener(this);
        setOnTouchListener(this);
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (getAdapter() != null
                && getAdapter().getCount() > MAXIMUM_LIST_ITEMS_VIEWABLE) {
            if (listViewTouchAction == MotionEvent.ACTION_MOVE) {
                scrollBy(0, -1);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int newHeight = 0;
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            ListAdapter listAdapter = getAdapter();
            if (listAdapter != null && !listAdapter.isEmpty()) {
                int listPosition = 0;
                for (listPosition = 0; listPosition < listAdapter.getCount()
                        && listPosition < MAXIMUM_LIST_ITEMS_VIEWABLE; listPosition++) {
                    View listItem = listAdapter.getView(listPosition, null,
                            this);
                    // now it will not throw a NPE if listItem is a ViewGroup
                    // instance
                    if (listItem instanceof ViewGroup) {
                        listItem.setLayoutParams(mLayoutParams);
                    }
                    listItem.measure(widthMeasureSpec, heightMeasureSpec);
                    newHeight += listItem.getMeasuredHeight();
                }
                newHeight += getDividerHeight() * listPosition;
            }
            if ((heightMode == MeasureSpec.AT_MOST) && (newHeight > heightSize)) {
                if (newHeight > heightSize) {
                    newHeight = heightSize;
                }
            }
        } else {
            newHeight = getMeasuredHeight();
        }
        setMeasuredDimension(getMeasuredWidth(), newHeight);
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (getAdapter() != null
                && getAdapter().getCount() > MAXIMUM_LIST_ITEMS_VIEWABLE) {
            if (listViewTouchAction == MotionEvent.ACTION_MOVE) {
                scrollBy(0, 1);
            }
        }
        return false;
    }
}
