package com.example.android.wisewords.data;

import android.provider.BaseColumns;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by po482951 on 18/08/2016.
 */
public class QuoteContract {
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
    public static final String TABLE_NAME = "quote";
    // Todo: possibly separate the table into 2; have a quote, and author table
    // set the names of the table columns as constants
    // Not setting primary key; BaseColumns class has _ID constant to implement this
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_DATE = "date";
  }
}