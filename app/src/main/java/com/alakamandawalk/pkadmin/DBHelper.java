package com.alakamandawalk.pkadmin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FavStoryDB.db";
    public static final String FAV_STORY_TABLE_NAME = "fav_story";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_STORY = "story";
    public static final String KEY_DATE = "date";
    public static final String KEY_IMAGE = "image";

    private static final String CREATE_TABLE_FAV_STORY = "CREATE TABLE " + FAV_STORY_TABLE_NAME + "("+
            KEY_ID + " TEXT PRIMARY KEY," +
            KEY_NAME + " TEXT," +
            KEY_STORY + " TEXT," +
            KEY_DATE + " TEXT," +
            KEY_IMAGE + " BLOB);";

    private HashMap hp;


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_FAV_STORY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + FAV_STORY_TABLE_NAME);

        // create new table
        onCreate(db);

    }

    public boolean insertStory( String id, String name, String story, String date, byte[] image) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_ID, id);
        cv.put(KEY_NAME, name);
        cv.put(KEY_STORY, story);
        cv.put(KEY_DATE, date);
        cv.put(KEY_IMAGE, image);
        database.insert( FAV_STORY_TABLE_NAME, null, cv );

        return true;
    }

    public Cursor getStory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM "+ FAV_STORY_TABLE_NAME +" WHERE "+ KEY_ID +"="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, FAV_STORY_TABLE_NAME);
        return numRows;
    }

    public boolean updateStory (String id, String name, String story, String date, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_STORY, story);
        cv.put(KEY_IMAGE, image);
        db.update(FAV_STORY_TABLE_NAME, cv, ""+KEY_ID+" = ? ", new String[] { id } );
        return true;
    }

    public Integer deleteStory (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(FAV_STORY_TABLE_NAME,
                ""+KEY_ID+" = ? ",
                new String[] { id });
    }

    public ArrayList<String> getAllStories() {

        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM "+FAV_STORY_TABLE_NAME+"", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(KEY_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

}
