package com.example.android.wisewords;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class QuoteAsyncTask extends AsyncTask<Integer, Void, Void> {

  private static final int SPLASH_ACTIVITY = 0;
  private static final int MAIN_ACTIVITY = 1;

  @Override
  protected Void doInBackground(Integer... params) {
    // below line for debugging; allows breakpoints to be set for the background method
      /*android.os.Debug.waitForDebugger();*/
    // implement differently depending on caller activity
    int callingActivity = params[0];
    // only access the internet when we've exhausted the current quoteList
    switch (callingActivity) {
      case SPLASH_ACTIVITY:
        SplashActivity.quoteList = getQuoteList();
        break;
      case MAIN_ACTIVITY:
        if (MainActivity.appJustStarted || MainActivity.indexHasGoneFullCycle) {
          MainActivity.appJustStarted = false;
          MainActivity.indexHasGoneFullCycle = false;
          MainActivity.nextQuoteList = getQuoteList();
        }
        break;
      default:
        throw new IllegalArgumentException();
    }
    return null;
  }

  private ArrayList<Quote> getQuoteList() {
    ArrayList<Quote> quoteList = new ArrayList<>();
    String htmlUrlString = MainActivity.htmlUrlString;
    try {
      Document htmlDocument = Jsoup.connect(htmlUrlString).get();
      Element htmlBodyElement = htmlDocument.body();
      Elements quoteTextElements = htmlBodyElement.select("dt.quote > a");
      Elements quoteAuthorElements = htmlBodyElement.select("dd.author > b");
      // iterate through the quotes and authors
      String quoteText, quoteAuthor;
      Quote quote;
      int listSize = quoteTextElements.size();
      for (int index = 0; index < listSize; index++) {
        quoteText = quoteTextElements.get(index).ownText();
        quoteAuthor = quoteAuthorElements.get(index).text();
        quote = new Quote(quoteText, quoteAuthor);
        quoteList.add(quote);
      }
      return quoteList;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}