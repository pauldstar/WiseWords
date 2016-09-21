package com.example.android.wisewords;

import java.text.DateFormat;
import java.util.Date;

public class Utility {

  public static String formatDate(long dateInMilliseconds) {
    Date date = new Date(dateInMilliseconds);
    return DateFormat.getDateInstance().format(date);
  }

  public static String trimText(String text, int maxLength) {
    // trim the quote text to fit the text view
    if (text.length() > maxLength) //  reduce length of string to fit text view
      text = text.substring(0, 77) + "...";
    return text;
  }
}
