package com.duanze.meizitu.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    // 数据库名
    private static final String DB_NAME = "meizitu.db";

    private static final String TABLE_PRIMARY_KEY = "_id integer primary key autoincrement";

    private static final String CREATE_TABLE_FEEDS =
            "create table " + FeedsTable.TABLE_NAME + " ("
                    + TABLE_PRIMARY_KEY
                    + "," + FeedsTable.ID + " integer"
                    + "," + FeedsTable.NAME + " text"
                    + "," + FeedsTable.IMGS + " text"
                    + "," + FeedsTable.TAGS + " text"
                    + ")";

    // 数据库版本
    // 1-->2 add likes table
    private static final int VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FEEDS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
            case 2:
            default:
        }
    }

}
