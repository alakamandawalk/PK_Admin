package com.alakamandawalk.pkadmin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.alakamandawalk.pkadmin.FavStoryContract.*;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FavStoryDB.db";
    public static final int DATABASE_VERSION = 1;

    private HashMap hp;


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE_FAV_STORY = "CREATE TABLE " +
                FavStoryEntry.TABLE_NAME + "("+
                FavStoryEntry.KEY_ID + " TEXT PRIMARY KEY," +
                FavStoryEntry.KEY_NAME + " TEXT," +
                FavStoryEntry.KEY_STORY + " TEXT," +
                FavStoryEntry.KEY_DATE + " TEXT," +
                FavStoryEntry.KEY_IMAGE + " BLOB);";

        db.execSQL(CREATE_TABLE_FAV_STORY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + FavStoryEntry.TABLE_NAME);
        onCreate(db);

    }

    public boolean insertStory( String id, String name, String story, String date, byte[] image) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(FavStoryEntry.KEY_ID, id);
        cv.put(FavStoryEntry.KEY_NAME, name);
        cv.put(FavStoryEntry.KEY_STORY, story);
        cv.put(FavStoryEntry.KEY_DATE, date);
        cv.put(FavStoryEntry.KEY_IMAGE, image);
        database.insert(FavStoryEntry.TABLE_NAME, null, cv);

        return true;
    }

    public Cursor getStory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM "+ FavStoryEntry.TABLE_NAME +" WHERE "+ FavStoryEntry.KEY_ID +"="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, FavStoryEntry.TABLE_NAME);
        return numRows;
    }

    public boolean updateStory (String id, String name, String story, String date, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FavStoryEntry.KEY_NAME, name);
        cv.put(FavStoryEntry.KEY_STORY, story);
        cv.put(FavStoryEntry.KEY_IMAGE, image);
        db.update(FavStoryEntry.TABLE_NAME, cv, ""+FavStoryEntry.KEY_ID+" = ? ", new String[] { id } );
        return true;
    }

    public Integer deleteStory (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(FavStoryEntry.TABLE_NAME,
                ""+FavStoryEntry.KEY_ID+" = ? ",
                new String[] { id });
    }

    public Cursor getAllStories(){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.query(
                FavStoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

}
