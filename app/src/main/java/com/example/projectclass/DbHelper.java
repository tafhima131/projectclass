package com.example.projectclass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "Attendance.db";

    // Class table
    public static final String CLASS_TABLE_NAME = "CLASS_TABLE";
    public static final String C_ID = "_CID";
    public static final String CLASS_NAME_KEY = "CLASS_NAME";
    public static final String SUBJECT_NAME_KEY = "SUBJECT_NAME";

    private static final String CREATE_CLASS_TABLE =
            "CREATE TABLE " + CLASS_TABLE_NAME + "(" +
                    C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    CLASS_NAME_KEY + " TEXT NOT NULL," +
                    SUBJECT_NAME_KEY + " TEXT NOT NULL," +
                    "UNIQUE(" + CLASS_NAME_KEY + "," + SUBJECT_NAME_KEY + ")" +
                    ");";
    private static final String DROP_CLASS_TABLE = "DROP TABLE IF EXISTS " + CLASS_TABLE_NAME;
    private static final String SELECT_CLASS_TABLE = "SELECT * FROM " + CLASS_TABLE_NAME;

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLASS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_CLASS_TABLE);
            onCreate(db);
        } catch (Exception e) {
            Log.e("DbHelper", "Error upgrading database", e);
        }
    }

    long addClass(String className, String subjectName) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLASS_NAME_KEY, className);
        values.put(SUBJECT_NAME_KEY, subjectName);
        return database.insert(CLASS_TABLE_NAME, null, values);
    }

    Cursor getClassTable() {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery(SELECT_CLASS_TABLE, null);
    }

    public void deleteClass(long cid) {
        SQLiteDatabase database = this.getWritableDatabase();
        String whereClause = C_ID + "=?";
        String[] whereArgs = {String.valueOf(cid)};
        database.delete(CLASS_TABLE_NAME, whereClause, whereArgs);
    }
    long updateClass( long cid,String className, String subjectName) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLASS_NAME_KEY, className);
        values.put(SUBJECT_NAME_KEY, subjectName);
        return database.update(CLASS_TABLE_NAME,values,C_ID+"=?",new String[]{String.valueOf(cid)});
    }
}