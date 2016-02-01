package com.duanze.meizitu.utils;

import android.content.Context;

import com.duanze.litepreferences.rawmaterial.BaseLitePrefs;
import com.duanze.meizitu.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Duanze on 16-1-3.
 */
public class MeizituPrefs extends BaseLitePrefs {

    public static final String PREFS_NAME = "meizitu_prefs";
    public static final String LAUNCH_TIME = "launch_time";
    public static final String IS_RATED = "is_rated";
    public static final int RATE_THRESHOLD = 13;

    public static void initFromXml(Context context) {
        try {
            initFromXml(context, R.xml.prefs);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public static int getLaunchTime() {
        return getInt(LAUNCH_TIME);
    }

    public static boolean putLaunchTime(int n) {
        return putInt(LAUNCH_TIME, n);
    }

    public static boolean getIsRated() {
        return getBoolean(IS_RATED);
    }

    public static boolean putIsRated(boolean b) {
        return putBoolean(IS_RATED, b);
    }

}
