package com.example.android.wisewords.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class QuoteContract {

  // The "Content authority" is a name for the entire content provider, similar to the
  // relationship between a domain name and its website.  A convenient string to use for the
  // content authority is the package name for the app, which is guaranteed to be unique on the
  // device.
  public static final String CONTENT_AUTHORITY = "com.example.android.wisewords.content_provider";
  // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
  // the content provider.
  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
  // Possible paths (appended to base content URI for possible URI's)
  // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
  // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
  // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
  public static final String PATH_QUOTE = "quote";
  public static final String PATH_LIST = "list";
  public static final String PATH_DELETE = "delete";
  /**
   * To make it easy to query for the exact date, we normalize all dates that go into
   * the database to the start of the the Julian day at UTC.
   */
  public static long normaliseDate(long startDate) {
    // get the timezone then get a default gregorian calendar
    TimeZone timeZone = TimeZone.getTimeZone("UTC");
    GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance(timeZone);
    // set date to the day we want, then set time to the start of that day; then return value
    date.setTimeInMillis(startDate);
    date.set(Calendar.HOUR_OF_DAY, 0);
    date.set(Calendar.MINUTE, 0);
    date.set(Calendar.SECOND, 0);
    date.set(Calendar.MILLISECOND, 0);
    return date.getTimeInMillis();
  }

  /** Inner class that defines the table contents of the location table */
  public static final class QuoteEntry implements BaseColumns {

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUOTE).build();
    public static final String CONTENT_DIR_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUOTE;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUOTE;
    public static final String TABLE_NAME = "quote";
    // Todo: possibly separate the table into 2; have a quote, and author table
    // set the names of the table columns as constants
    // No need to set primary key; BaseColumns class has _ID constant to implement this
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_DATE = "date";

    /** function to build URI to access a quote:
     * content/authority/quote/id */
    public static Uri buildQuoteUriWithID(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    /** function to build URI to access a quote using it's text and author:
     * content/authority/quote/text/author */
    public static Uri buildQuoteUriWithTextAndAuthor(String text, String author) {
      // TODO: 15/09/2016 build this using uri queries instead of paths
      return CONTENT_URI.buildUpon().appendPath(text).appendPath(author).build();
    }

    /** function to build URI to retrieve list of quotes:
     * content/authority/quote/list */
    public static Uri buildQuoteListUri() {
      return CONTENT_URI.buildUpon().appendPath(PATH_LIST).build();
    }

    /** return full quote projection */
    public static String[] getFullQuoteProjection() {
      // projection = "id, text, author, date"
      return new String[] {QuoteContract.QuoteEntry._ID, QuoteContract.QuoteEntry.COLUMN_TEXT,
              QuoteContract.QuoteEntry.COLUMN_AUTHOR, QuoteContract.QuoteEntry.COLUMN_DATE};
    }
  }
}