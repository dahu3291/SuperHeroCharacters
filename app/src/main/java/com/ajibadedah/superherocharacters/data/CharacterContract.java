package com.ajibadedah.superherocharacters.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ajibade on 5/22/17
 */

public class CharacterContract {

    public static final String CONTENT_AUTHORITY = "com.ajibadedah.superherocharacters";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class CharacterEntry implements BaseColumns {

        static final String TABLE_NAME = "character";

        public static final String COLUMN_CHARACTER_ID = "character_id";
        public static final String COLUMN_CHARACTER_NAME = "name";
        public static final String COLUMN_CHARACTER_BIO = "bio";
        public static final String COLUMN_CHARACTER_THUMBNAIL = "thumbnail";
        public static final String COLUMN_CHARACTER_RESOURECE = "resource";
        public static final String COLUMN_CHARACTER_COMICS = "comics";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME)
                .build();

    }


}
