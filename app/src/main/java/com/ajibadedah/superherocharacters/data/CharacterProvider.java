package com.ajibadedah.superherocharacters.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.ajibadedah.superherocharacters.data.CharacterContract.*;

/**
 * Created by ajibade on 5/22/17
 */

public class CharacterProvider extends ContentProvider {

    public static final int CODE_CHARACTER = 100;
    public static final int CODE_CHARACTER_WITH_ID = 101;
    public static final int CODE_FAVORITE = 200;
    public static final int CODE_FAVORITE_WITH_ID = 201;
    private CharacterDbHelper characterDbHelper;

    private UriMatcher matcher = buildMatcher();

    private UriMatcher buildMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //Match for Character table
        matcher.addURI(CharacterContract.CONTENT_AUTHORITY, CharacterEntry.TABLE_NAME, CODE_CHARACTER);
        matcher.addURI(CharacterContract.CONTENT_AUTHORITY,
                CharacterEntry.TABLE_NAME + "/*", CODE_CHARACTER_WITH_ID);

        return matcher;
    }
    @Override
    public boolean onCreate() {
        characterDbHelper = new CharacterDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = characterDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (matcher.match(uri)) {
            case CODE_CHARACTER:
                cursor = db.query(CharacterEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case CODE_CHARACTER_WITH_ID:
                selection = CharacterEntry.COLUMN_CHARACTER_ID + " = ? ";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                cursor = db.query(CharacterEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing this in popular movies");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = characterDbHelper.getWritableDatabase();
        Context context = getContext();
        switch (matcher.match(uri)){
            case CODE_CHARACTER:
                long i = db.insert(CharacterEntry.TABLE_NAME, null, contentValues);
                if (i > 0 && context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                    return uri;
                }
                break;

        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = characterDbHelper.getWritableDatabase();
        int num;
        if (null == selection) selection = "1";

        switch (matcher.match(uri)) {

            case CODE_CHARACTER:
                num = db.delete(CharacterEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case CODE_CHARACTER_WITH_ID:
                selection = CharacterEntry.COLUMN_CHARACTER_ID + " = ? ";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                num = db.delete(CharacterEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (num != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return num;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,String selection, String[] selectionArgs) {

        final SQLiteDatabase db = characterDbHelper.getReadableDatabase();
        int index;

        switch (matcher.match(uri)) {
            case CODE_CHARACTER_WITH_ID:
                selection = CharacterEntry.COLUMN_CHARACTER_ID + " = ? ";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                index = db.update(CharacterEntry.TABLE_NAME, contentValues,selection, selectionArgs);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (index > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return index;
    }
}