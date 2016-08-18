package com.example.android.wisewords.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for quote data.
 */
public class QuoteDBHelper extends SQLiteOpenHelper {

  // If you change the database schema, you must increment the database version.
  private static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "quote.db";

  public QuoteDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    final String SQL_CREATE_WEATHER_TABLE =
            "CREATE TABLE " + QuoteContract.QuoteEntry.TABLE_NAME + " (" +
            // _ID key from BaseColumns used here
            QuoteContract.QuoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            QuoteContract.QuoteEntry.COLUMN_TEXT + " TEXT NOT NULL, " +
            QuoteContract.QuoteEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
            QuoteContract.QuoteEntry.COLUMN_DATE + " INTEGER NOT NULL);";
    // execute the SQL command to create the database
    sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    // This database is only a cache for online data, so its upgrade policy is
    // to simply to discard the data and start over
    // Note that this only fires if you change the version number for your database.
    // It does NOT depend on the version number for your application.
    // If you want to update the schema without wiping data, commenting out the next 2 lines
    // should be your top priority before modifying this method.
    /**
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuoteContract.QuoteEntry.TABLE_NAME);
    onCreate(sqLiteDatabase);
    */
  }
}