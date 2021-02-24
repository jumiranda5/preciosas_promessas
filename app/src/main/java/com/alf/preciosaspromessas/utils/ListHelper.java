package com.alf.preciosaspromessas.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ListHelper extends SQLiteOpenHelper {

    private static final String TABLE_VERSES = "verses";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_VERSE = "verse";
    private static final String DB_NAME = "Verse.db";
    private static final int DB_VERSION = 4;
    private static final String DB_CREATE =
            "CREATE TABLE " + TABLE_VERSES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_VERSE + " TEXT" + ")";


    public ListHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        // create books table
        db.execSQL(DB_CREATE);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VERSES);
        // Create tables again
        onCreate(db);
    }

    // Adding new Verse
    public void addVerse(Verse verse) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VERSE, verse.getVerse());
        // Inserting Row
        db.insert(TABLE_VERSES, null, values);
        db.close(); // Closing database connection
    }

    // Getting single verse
    public Verse getVerse(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VERSES, new String[] { COLUMN_ID,
                        COLUMN_VERSE}, COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null) cursor.moveToFirst();

        assert cursor != null;
        int _id = Integer.parseInt(cursor.getString(0));
        String v = cursor.getString(1);

        cursor.close();
        db.close();

        return new Verse(_id, v);
    }

    // Getting single psalm
    public Verse getVerseByString(String text) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VERSES, new String[] { COLUMN_ID,
                        COLUMN_VERSE}, COLUMN_VERSE + "=?",
                new String[] { text }, null, null, null, null);

        Verse verse = null;

        if (cursor.moveToFirst()) {
            verse = new Verse(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1));
        }
        cursor.close();
        db.close();

        return verse;

    }

    // Getting all saved verses
    public List<Verse> getAllVerses() {
        List<Verse> favList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_VERSES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Verse verse = new Verse();
                verse.setId(Integer.parseInt(cursor.getString(0)));
                verse.setVerse(cursor.getString(1));
                favList.add(verse);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return favList;
    }

    // Deleting single psalm
    public void deleteVerse(Verse verse) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VERSES, COLUMN_ID + " = ?",
                new String[] { String.valueOf(verse.getId()) });
        db.close();
    }

    // Getting psalms Count
    public int getVersesCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_VERSES;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

}
