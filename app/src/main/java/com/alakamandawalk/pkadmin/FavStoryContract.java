package com.alakamandawalk.pkadmin;

import android.provider.BaseColumns;

public class FavStoryContract {

    public static final class FavStoryEntry implements BaseColumns{

        private FavStoryEntry(){}

        public static final String TABLE_NAME = "fav_story";
        public static final String KEY_ID = "storyId";
        public static final String KEY_NAME = "storyName";
        public static final String KEY_STORY = "story";
        public static final String KEY_DATE = "storyDate";
        public static final String KEY_IMAGE = "storyImage";
    }
}
