package com.alakamandawalk.pkadmin.localdb;

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
        public static final String KEY_CATEGORY_ID = "storyCategoryId";
        public static final String KEY_PLAYLIST_ID = "storyPlaylistId";
        public static final String KEY_SEARCH_TAG = "storySearchTag";
        public static final String KEY_AUTHOR_ID = "authorId";
        public static final String KEY_AUTHOR_NAME = "authorName";
    }
}
