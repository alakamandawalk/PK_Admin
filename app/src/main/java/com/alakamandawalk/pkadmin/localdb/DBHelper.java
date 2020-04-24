package com.alakamandawalk.pkadmin.localdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.alakamandawalk.pkadmin.localdb.LocalDBContract.*;

import androidx.annotation.Nullable;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FavStoryDB.db";
    public static final int DATABASE_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE_FAV_STORY = "CREATE TABLE " +
                LocalDBEntry.TABLE_NAME + "("+
                LocalDBEntry.KEY_ID + " TEXT PRIMARY KEY," +
                LocalDBEntry.KEY_NAME + " TEXT," +
                LocalDBEntry.KEY_STORY + " TEXT," +
                LocalDBEntry.KEY_DATE + " TEXT," +
                LocalDBEntry.KEY_IMAGE + " BLOB);";

        db.execSQL(CREATE_TABLE_FAV_STORY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + LocalDBEntry.TABLE_NAME);
        onCreate(db);

    }

    public boolean insertStory( String id, String name, String story, String date, byte[] image) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(LocalDBEntry.KEY_ID, id);
        cv.put(LocalDBEntry.KEY_NAME, name);
        cv.put(LocalDBEntry.KEY_STORY, story);
        cv.put(LocalDBEntry.KEY_DATE, date);
        cv.put(LocalDBEntry.KEY_IMAGE, image);
        database.insert(LocalDBEntry.TABLE_NAME, null, cv);

        return true;
    }

    public Cursor getStory(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM "+ LocalDBEntry.TABLE_NAME +" WHERE "+ LocalDBEntry.KEY_ID +"="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, LocalDBEntry.TABLE_NAME);
        return numRows;
    }

    public boolean updateStory (String id, String name, String story, String date, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBEntry.KEY_NAME, name);
        cv.put(LocalDBEntry.KEY_STORY, story);
        cv.put(LocalDBEntry.KEY_IMAGE, image);
        db.update(LocalDBEntry.TABLE_NAME, cv, ""+ LocalDBEntry.KEY_ID+" = ? ", new String[] { id } );
        return true;
    }

    public Integer deleteStory (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(LocalDBEntry.TABLE_NAME,
                ""+ LocalDBEntry.KEY_ID+" = ? ",
                new String[] { id });
    }

    public Cursor getAllStories(){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.query(
                LocalDBEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

}
