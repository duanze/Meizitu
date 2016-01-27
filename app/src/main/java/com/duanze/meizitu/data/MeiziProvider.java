package com.duanze.meizitu.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Duanze on 2016/1/18.
 */
public class MeiziProvider extends ContentProvider {
    static final String TAG = MeiziProvider.class.getSimpleName();

    public static final String AUTHORITY = "com.duanze.meizitu.provider";

    public static final String SCHEME = "content://";

    // messages
    public static final String PATH_FEEDS = "/feeds";

    public static final String PATH_LIKES = "/likes";

    public static final Uri FEEDS_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_FEEDS);

    public static final Uri LIKES_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_LIKES);

    private static final int FEEDS = 0;
    private static final int LIKES = 1;

    /*
     * MIME type definitions
     */
    public static final String FEED_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + ".feed";

    public static final String LIKE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + ".like";

    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "feeds", FEEDS);
        sUriMatcher.addURI(AUTHORITY, "likes", LIKES);
    }

    private DBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String table = matchTable(uri);
        queryBuilder.setTables(table);

        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, // The database to query
                projection, // The columns to return from the queryFromDB
                selection, // The columns for the where clause
                selectionArgs, // The values for the where clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case FEEDS:
                return FEED_CONTENT_TYPE;
            case LIKES:
                return LIKE_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        String table = matchTable(uri);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long newId = 0;
        db.beginTransaction();
        try {
            newId = db.insert(table, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
        if (newId > 0) {
            Uri returnUri = ContentUris.withAppendedId(uri, newId);
            getContext().getContentResolver().notifyChange(uri, null);
            return returnUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rowDeleted = 0;
        String table = matchTable(uri);
        db.beginTransaction();
        try {
            rowDeleted = db.delete(table, selection, selectionArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rowUpdated;
        String table = matchTable(uri);
        db.beginTransaction();
        try {
            rowUpdated = db.update(table, values, selection, selectionArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowUpdated;
    }

    private String matchTable(Uri uri) {
        String table = null;
        switch (sUriMatcher.match(uri)) {
            case FEEDS:
                table = FeedsTable.TABLE_NAME;
                break;
            case LIKES:
//                table = LikesDataHelper.FeedsDBInfo.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return table;
    }
}
