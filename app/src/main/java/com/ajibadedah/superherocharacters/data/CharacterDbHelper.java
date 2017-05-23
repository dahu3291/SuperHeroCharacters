package com.ajibadedah.superherocharacters.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.ajibadedah.superherocharacters.data.CharacterContract.*;

/**
 * Created by ajibade on 5/22/17
 */

public class CharacterDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "character.db";

    private static final int DATABASE_VERSION = 1;

   CharacterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_CHARACTER_TABLE = "CREATE TABLE " + CharacterEntry.TABLE_NAME +
                " (" + CharacterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CharacterEntry.COLUMN_CHARACTER_ID + " STRING NOT NULL, " +
                CharacterEntry.COLUMN_CHARACTER_NAME + " STRING NOT NULL, " +
                CharacterEntry.COLUMN_CHARACTER_BIO + " STRING NOT NULL, " +
                CharacterEntry.COLUMN_CHARACTER_THUMBNAIL + " STRING NOT NULL, " +
                CharacterEntry.COLUMN_CHARACTER_RESOURECE + " STRING NOT NULL, " +
                CharacterEntry.COLUMN_CHARACTER_COMICS + " STRING NOT NULL);";

        db.execSQL(SQL_CREATE_CHARACTER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CharacterEntry.TABLE_NAME);
        onCreate(db);
    }
}
