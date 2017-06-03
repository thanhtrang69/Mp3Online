package com.example.trang.mp3online.util;

import android.util.Log;

import java.util.regex.Pattern;

/**
 * Created by Trang on 5/24/2017.
 */

public class Utils {

    public static void showLog(String tag, String message) {
        Log.d(tag, message);
    }

    public static void showLog(String message) {
        showLog("thangggggggggggggggg", message);
    }

    public static String  parseString(String string, String character) {
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < stringBuilder.length(); i++) {
            if (String.valueOf(stringBuilder.charAt(i)).equals(character)) {
                stringBuilder.setCharAt(i, (char) 32);
            }
        }
        return stringBuilder.toString();
    }
}
