package com.example.android.wisewords.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.List;

/**
 * Created by po482951 on 19/08/2016.
 */
public class QuoteProvider extends ContentProvider {

  // The URI Matcher used by this content provider.
  private static final UriMatcher uriMatcher = buildUriMatcher();
  private QuoteDBHelper openHelper;
  // URI matcher constants, which are matched with the incoming content URI
  static final int QUOTE = 100;
  static final int QUOTE_WITH_ID = 101;
  static final int QUOTE_LIST = 102;
  static final int QUOTE_WITH_TEXT_AUTHOR = 103;
  // variable used to build queries from database tables (possibly JOINed)
  private static final SQLiteQueryBuilder quoteQueryBuilder = initialiseQueryBuilder();
  // selection: _ID = ?
  private static final String quoteIDSelection = QuoteContract.QuoteEntry._ID + " = ? ";
  // selection: text = ? AND author = ?
  private static final String quoteTextAuthorSelection = QuoteContract.QuoteEntry.COLUMN_TEXT +
          " = ? AND " + QuoteContract.QuoteEntry.COLUMN_AUTHOR + " = ? ";

  @Override
  public boolean onCreate() {
    openHelper = new QuoteDBHelper(getContext());
    return true;
  }

  @Override
  public String getType(Uri uri) {
    // Use the Uri Matcher to determine what kind of URI this is.
    final int matchCode = uriMatcher.match(uri);
    switch (matchCode) {
      case QUOTE_WITH_ID:
        return QuoteContract.QuoteEntry.CONTENT_ITEM_TYPE;
      case QUOTE_LIST:
        return QuoteContract.QuoteEntry.CONTENT_DIR_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
  }

  @Override
  public Uri insert(Uri uri, ContentValues contentValues) {
    final SQLiteDatabase db = openHelper.getWritableDatabase();
    Uri returnUri;
    normaliseContentValuesDate(contentValues);
    long _id = db.insert(QuoteContract.QuoteEntry.TABLE_NAME, null, contentValues);
    if (_id > 0) returnUri = QuoteContract.QuoteEntry.buildQuoteUriWithID(_id);
    else throw new android.database.SQLException("Failed to insert row into " + uri);
    getContext().getContentResolver().notifyChange(uri, null);
    return returnUri;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = openHelper.getWritableDatabase();
    final int match = uriMatcher.match(uri);
    int rowsDeleted;
    // this makes delete all rows return the number of rows deleted
    if (selection == null) selection = "1";
    switch (match) {
      case QUOTE_WITH_ID:
        rowsDeleted = db.delete(
                QuoteContract.QuoteEntry.TABLE_NAME, selection, selectionArgs);
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    // Because a null deletes all rows
    if (rowsDeleted != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowsDeleted;
  }

  @Override
  public int update(Uri uri,
                    ContentValues contentValues, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = openHelper.getWritableDatabase();
    final int match = uriMatcher.match(uri);
    int rowsUpdated;
    switch (match) {
      case QUOTE_WITH_ID:
        normaliseContentValuesDate(contentValues);
        rowsUpdated = db.update(QuoteContract.QuoteEntry.TABLE_NAME, contentValues, selection,
                selectionArgs);
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    if (rowsUpdated != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowsUpdated;
  }

  /**
   * Contains switch statement that, given a URI, will determine what kind of request it is,
   * and query the database accordingly.
   */
  @Override
  public Cursor query(Uri uri, String[] projection,
                      String selection, String[] selectionArgs, String sortOrder) {
    Cursor retCursor;
    int matchCode = uriMatcher.match(uri);
    switch (matchCode) {
      // either case of quotes with ID or text-author should implement the same way
      case QUOTE_WITH_ID: {
        selection = quoteIDSelection;
        selectionArgs = new String[]{uri.getLastPathSegment()};
        retCursor = openHelper.getReadableDatabase().query(QuoteContract.QuoteEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, sortOrder);
        break;
      }
      // either case of quotes with ID or text-author should implement the same way
      case QUOTE_WITH_TEXT_AUTHOR: {
        selection = quoteTextAuthorSelection;
        // TODO: 15/09/2016 collect uri queries instead of path segments
        selectionArgs = new String[2];
        List<String> selectionArgsList = uri.getPathSegments();
        for (int index = 1; index < selectionArgsList.size(); index++)
          selectionArgs[index - 1] = selectionArgsList.get(index);
        retCursor = openHelper.getReadableDatabase().query(QuoteContract.QuoteEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, sortOrder);
        break;
      }
      case QUOTE_LIST: {
        retCursor = getQuoteListCursor(projection, sortOrder);
        break;
      }
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    // cause cursor to register content observer to watch for changes to this URI
    // or any of it's descendants. This allows the content provider to easily tell the UI
    // when the cursor changes; due to INSERTs, DELETEs or UPDATEs.
    retCursor.setNotificationUri(getContext().getContentResolver(), uri);
    return retCursor;
  }

  /**@Override public int bulkInsert(Uri uri, ContentValues[] values) {
  final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
  final int match = sUriMatcher.match(uri);
  switch (match) {
  case WEATHER:
  db.beginTransaction();
  int returnCount = 0;
  try {
  for (ContentValues value : values) {
  normalizeDate(value);
  long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
  if (_id != -1) {
  returnCount++;
  }
  }
  db.setTransactionSuccessful();
  } finally {
  db.endTransaction();
  }
  getContext().getContentResolver().notifyChange(uri, null);
  return returnCount;
  default:
  return super.bulkInsert(uri, values);
  }
  }*/

  /**
   * Initialise SQLiteQueryBuilder
   */
  private static SQLiteQueryBuilder initialiseQueryBuilder() {
    SQLiteQueryBuilder quoteQueryBuilder = new SQLiteQueryBuilder();
    // Set the list of tables to query (using JOIN); in this case we're only using one table
    quoteQueryBuilder.setTables(QuoteContract.QuoteEntry.TABLE_NAME);
    return quoteQueryBuilder;
  }

  /**
   * This matches each URI to the QUOTE_WITH_ID and QUOTE_LIST integer constants defined above.
   */
  private static UriMatcher buildUriMatcher() {
    // All paths added to the UriMatcher have a corresponding code to return when a match is
    // found.  The code passed into the constructor represents the code to return for the root
    // URI.  It's common to use NO_MATCH as the code for this case.
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = QuoteContract.CONTENT_AUTHORITY;
    // For each type of URI you want to add, create a corresponding code
    // begin with the shortest paths first
    matcher.addURI(authority, QuoteContract.PATH_QUOTE, QUOTE);
    matcher.addURI(authority, QuoteContract.PATH_QUOTE + "/#", QUOTE_WITH_ID);
    matcher.addURI(authority, QuoteContract.PATH_QUOTE + "/" + QuoteContract.PATH_LIST, QUOTE_LIST);
    matcher.addURI(authority, QuoteContract.PATH_QUOTE + "/*/*", QUOTE_WITH_TEXT_AUTHOR);
    return matcher;
  }

  /**
   * returns a cursor containing a list of quotes
   */
  public Cursor getQuoteListCursor(String[] projection, String sortOrder) {
    // TODO: 08/09/2016  try this style of database retrieval for a single quote on line 72
    return quoteQueryBuilder.query(openHelper.getReadableDatabase(), projection, null, null,
            null, null, sortOrder);
  }

  /**
   * To make it easy to query for the exact date, we normalize all dates that go into
   * the database to the start of the the Julian day at UTC.
   */
  public static void normaliseContentValuesDate(ContentValues values) {
    if (values.containsKey(QuoteContract.QuoteEntry.COLUMN_DATE)) {
      long dateValue = values.getAsLong(QuoteContract.QuoteEntry.COLUMN_DATE);
      values.put(QuoteContract.QuoteEntry.COLUMN_DATE, QuoteContract.normaliseDate(dateValue));
    }
  }
}
