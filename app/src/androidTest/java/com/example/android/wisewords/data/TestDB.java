package com.example.android.wisewords.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDB extends AndroidTestCase {

  public static final String LOG_TAG = TestDB.class.getSimpleName();

  // Since we want each test to start with a clean slate
  public void deleteTheDatabase() {
    mContext.deleteDatabase(QuoteDBHelper.DATABASE_NAME);
  }

  /* This function gets called before each test is executed to delete the database.  This makes
      sure that we always have a clean test. */
  public void setUp() {
    deleteTheDatabase();
  }

  public void testCreateDb() throws Throwable {
    // build a HashSet of all of the table names we wish to look for
    // Note that there will be another table in the DB that stores the
    // Android metadata (db version information)
    final HashSet<String> tableNameHashSet = new HashSet<>();
    tableNameHashSet.add(QuoteContract.QuoteEntry.TABLE_NAME);
    // delete any existing table, and recreate the database
    mContext.deleteDatabase(QuoteDBHelper.DATABASE_NAME);
    SQLiteDatabase db = new QuoteDBHelper(this.mContext).getWritableDatabase();
    // check that database is created and open
    assertEquals(true, db.isOpen());
    // have we created the tables we want? get cursor (contains query results)
    Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
    // check that the their exist a first row of the cursor; else test fails
    assertTrue("Error: The database has not been created correctly", cursor.moveToFirst());

    /** this next bit of code should only be used if we have multiple tables and we want to
     * verify that the tables have been created
    do {
      tableNameHashSet.remove(cursor.getString(0));
    } while (cursor.moveToNext());
    // if this fails, it means that your database doesn't contain both the location entry
    // and weather entry tables
    assertTrue("Error: Your database was created without the quote entry table",
            tableNameHashSet.isEmpty()); */

    // now, do our tables contain the correct columns?
    cursor = db.rawQuery("PRAGMA table_info(" + QuoteContract.QuoteEntry.TABLE_NAME + ")", null);
    assertTrue("Error: Unable to query the database for table information.", cursor.moveToFirst());
    // Build a HashSet of all of the column names we want to look for
    final HashSet<String> quoteColumnHashSet = new HashSet<>();
    quoteColumnHashSet.add(QuoteContract.QuoteEntry._ID);
    quoteColumnHashSet.add(QuoteContract.QuoteEntry.COLUMN_TEXT);
    quoteColumnHashSet.add(QuoteContract.QuoteEntry.COLUMN_AUTHOR);
    quoteColumnHashSet.add(QuoteContract.QuoteEntry.COLUMN_DATE);
    // next line gets the index of the column holding the row (DB column) names
    int columnNameIndex = cursor.getColumnIndex("name");
    do {
      // gets the string associated with the name of column
      String columnName = cursor.getString(columnNameIndex);
      quoteColumnHashSet.remove(columnName);
    } while (cursor.moveToNext());
    // if this fails, it means that your database doesn't contain all of the required columns
    assertTrue("Error: Database doesn't have all required columns", quoteColumnHashSet.isEmpty());
    db.close();
  }

  /** Code to test that we can insert and query the quote database.*/
  public void testQuoteTable() {
    // First step: Get reference to writable database
    QuoteDBHelper dbHelper = new QuoteDBHelper(mContext);
    SQLiteDatabase database = dbHelper.getWritableDatabase();
    // Insert ContentValues into database and get a row ID back; assert true
    ContentValues testValues = TestUtilities.createTestQuoteValues();
    testInsertQuote(); // also calls the createTestQuoteValues function
    // Query the database and receive a Cursor back
    Cursor quoteCursor = database.query(
            QuoteContract.QuoteEntry.TABLE_NAME,  // Table to Query
            null, // leaving "columns" null just returns all the columns.
            null, // cols for "where" clause
            null, // values for "where" clause
            null, // columns to group by
            null, // columns to filter by row groups
            null  // sort order
    );
    // Move the cursor to a valid database row
    assertTrue("Error: No Records returned from quote query", quoteCursor.moveToFirst() );
    // Validate data in resulting Cursor with the original ContentValues
    TestUtilities.validateCurrentRecord("testInsertReadDb quoteEntry failed to validate",
            quoteCursor, testValues);
    // Move the cursor to demonstrate that there is only one record in the database
    assertFalse("Error: More than one record returned from query", quoteCursor.moveToNext());
    // Finally, close the cursor and database
    quoteCursor.close();
    dbHelper.close();
  }

  /** Helper method to test the insertion of data into the database */
  public void testInsertQuote() {
    // the insertTestQuoteValues function also calls teh createTestQuoteValues function
    Long rowID = TestUtilities.insertTestQuoteValues(mContext);
    assertTrue(rowID != -1);
  }
}