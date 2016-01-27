package com.duanze.meizitu.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.duanze.meizitu.models.Feed;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class FeedsDataHelper extends BaseDataHelper {
    private DBHelper mDBHelper;

    public FeedsDataHelper(Context context) {
        super(context);
        mDBHelper = new DBHelper(context);
    }

    @Override
    protected Uri getContentUri() {
        return MeiziProvider.FEEDS_CONTENT_URI;
    }

    private ContentValues getContentValues(Feed feed) {
        ContentValues values = new ContentValues();
        values.put(FeedsTable.ID, feed.getId());
        values.put(FeedsTable.NAME, feed.getName());
        values.put(FeedsTable.TAGS, new Gson().toJson(feed.getTags()));
        values.put(FeedsTable.IMGS, new Gson().toJson(feed.getImgs()));
        return values;
    }

    public Feed query(int id) {
        Feed feed = null;
        Cursor cursor = query(null
                , FeedsTable.ID + "= ?"
                , new String[]{String.valueOf(id)}
                , null);
        if (cursor.moveToFirst()) {
            feed = Feed.fromCursor(cursor);
        }
        cursor.close();
        return feed;
    }

    public void bulkInsert(List<Feed> feeds) {
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        for (Feed feed : feeds) {
            ContentValues values = getContentValues(feed);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        super.bulkInsert(contentValues.toArray(valueArray));
    }

    public int deleteAll() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        return db.delete(FeedsTable.TABLE_NAME, null, null);
    }

    public CursorLoader getCursorLoader() {
        return new CursorLoader(getContext(), getContentUri(), null, null, null, FeedsTable.ID + " DESC");
    }
}
