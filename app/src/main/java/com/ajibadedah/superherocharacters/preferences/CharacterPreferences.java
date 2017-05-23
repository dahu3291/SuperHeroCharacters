package com.ajibadedah.superherocharacters.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ajibade on 5/22/17
 */

public class CharacterPreferences {

    public static void setCharacterName(Context context, String superheroName) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("character", superheroName);
        editor.apply();
    }

    public static String getCharacterName(Context context, String superheroName) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("character", "spider-man");
    }
}
