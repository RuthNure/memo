package com.example.memo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MemoDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "memos.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MEMOS = "memos";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SUBJECT = "subject";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_DATE = "date";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_MEMOS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SUBJECT + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                    COLUMN_PRIORITY + " TEXT NOT NULL, " +
                    COLUMN_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP);";

    public MemoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseDebug", "Creating table with query: " + TABLE_CREATE);
        try {
            db.execSQL(TABLE_CREATE);
            Log.d("DatabaseDebug", "Table created successfully.");
        } catch (Exception e) {
            Log.e("DatabaseError", "Error creating table: ", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MemoDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + " version ");
        try {
            db.execSQL("ALTER TABLE memos ADD COLUMN invitees TEXT");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.w(MemoDatabaseHelper.class.getName(), "Database upgrade completed.");

    }

}
