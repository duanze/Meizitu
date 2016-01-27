package com.duanze.meizitu.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestManager {
    public RequestQueue mRequestQueue;

    private static RequestManager sRequestManager;

    private RequestManager(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static RequestManager getInstance(Context context) {
        if (null == sRequestManager) {
            sRequestManager = new RequestManager(context);
        }
        return sRequestManager;
    }

    public void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    public void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
