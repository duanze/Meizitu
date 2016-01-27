package com.duanze.meizitu.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

/**
 * Created by Duanze on 2016/1/9.
 */
public class RateAppUtil {
    private static final String DEFAULT = "Unknown";
    private static final String KEY_CHANNEL = "UMENG_CHANNEL";

    public static void goToMarket(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Couldn't launch the market!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static String getVersionName(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }

    public static void feedback(Activity activity) {
        // 必须明确使用mailto前缀来修饰邮件地址
        Uri uri = Uri.parse("mailto:端泽<blue3434@qq.com>");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        // intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
        intent.putExtra(Intent.EXTRA_SUBJECT, "Meizitu用户反馈" + " Version:" + RateAppUtil.getVersionName(activity));
        // 主题
        intent.putExtra(Intent.EXTRA_TEXT, "Manufacturer:" + Build.MANUFACTURER +
                " - Device name: " + Build.MODEL + " - SDK Version: " + Build.VERSION.SDK_INT + "  "); // 正文
        activity.startActivity(Intent.createChooser(intent, "Select email client"));
    }

    /**
     * 获取渠道
     *
     * @return
     */
    public static String getChannel(Context context) {
        if (context == null) {
            return DEFAULT;
        }
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String channel =
                    appInfo != null && appInfo.metaData != null ? appInfo.metaData.getString(KEY_CHANNEL) : null;
            if (channel == null) {
                return DEFAULT;
            }
            return channel;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return DEFAULT;
    }
}
