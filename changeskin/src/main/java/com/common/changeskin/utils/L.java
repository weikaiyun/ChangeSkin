package com.common.changeskin.utils;

import android.util.Log;

public class L {
    private static final String TAG = "Skin";
    static boolean debug = true;

    public static void e(String msg) {
        if (debug)
            Log.e(TAG, msg);
    }
}
