package com.alakamandawalk.pkadmin.download;

import android.provider.BaseColumns;

public class LocalDBContract {

    public static final class LocalDBEntry implements BaseColumns{

        private LocalDBEntry(){}

        public static final String TABLE_NAME = "fav_story";
        public static final String KEY_ID = "storyId";
        public static final String KEY_NAME = "storyName";
        public static final String KEY_STORY = "story";
        public static final String KEY_DATE = "storyDate";
        public static final String KEY_IMAGE = "storyImage";
    }
}
