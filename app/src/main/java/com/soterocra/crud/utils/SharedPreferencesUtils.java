package com.soterocra.crud.utils;

import android.content.SharedPreferences;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SharedPreferencesUtils {

    public static void write(SharedPreferences sp, SharedPreferences.Editor editor, Map<String, String> data) {

        data.entrySet().forEach(d -> editor.putString(d.getKey(), d.getValue()));

        editor.putLong("$$EXPIREDATE", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
        editor.apply();
    }

    public static SharedPreferences get(SharedPreferences sp) {
        if (!(sp.getLong("$$EXPIREDATE", -1) > System.currentTimeMillis())) {
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();
        }

        return sp;
    }

}
