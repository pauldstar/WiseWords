package com.example.android.wisewords;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {

  private static final String MAIN_TAG = MainActivity.class.getSimpleName();
  private TextView quoteTextTView, quoteAuthorTView;
  private static int quoteIndex = 0;
  private final String htmlUrlString = "http://www.quotationspage.com/random.php3";
  private ArrayList<Quote> quoteList = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    quoteTextTView = (TextView) findViewById(R.id.quote_text);
    quoteAuthorTView = (TextView) findViewById(R.id.quote_author);
    // set the first quote to appear on screen
    QuoteAsyncTask asyncTask = new QuoteAsyncTask();
    asyncTask.execute(htmlUrlString);
    // set listener on next_icon so a click will change to next quote on list
    ImageView image = (ImageView) findViewById(R.id.next_quote_icon);
    image.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // re-execute the task
        QuoteAsyncTask asyncTask = new QuoteAsyncTask();
        asyncTask.execute(htmlUrlString);
      }
    });
  }

  private class QuoteAsyncTask extends AsyncTask<String, Void, Void> {

    private final String ASYNC_TAG = QuoteAsyncTask.class.getSimpleName();

    @Override
    protected Void doInBackground(String... params) {
      // below line for debugging; allows breakpoints to be set for the background method
      /*android.os.Debug.waitForDebugger();*/

      String htmlPageUrl = params[0];
      if (quoteIndex == 0) {
        try {
          // get list of .quote and .author class nodes from the html document
          Document htmlDocument = Jsoup.connect(htmlPageUrl).get();
          Element htmlBodyElement = htmlDocument.body();
          Elements quoteTextList = htmlBodyElement.select("dt.quote > a");
          Elements quoteAuthorList = htmlBodyElement.select("dd.author > b");
          // iterate through the quotes and authors
          if (!quoteList.isEmpty()) quoteList.clear();
          String quoteText, quoteAuthor;
          Quote quote;
          int listSize = quoteTextList.size();
          for (int index = 0; index < listSize; index++) {
            quoteText = quoteTextList.get(index).ownText();
            quoteAuthor = quoteAuthorList.get(index).text();
            quote = new Quote(quoteText, quoteAuthor);
            quoteList.add(quote);
          }
        }
        catch (IOException e) { e.printStackTrace(); }
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      Log.d(ASYNC_TAG, "QuotesSize: " + quoteList.size() + " QuoteIndex: " + quoteIndex);
      quoteTextTView.setText(quoteList.get(quoteIndex).getQuoteText());
      quoteAuthorTView.setText(quoteList.get(quoteIndex).getQuoteAuthor());
      // set the index to point to the next item on the list
      // when it comes back to zero, then we reload some more quotes
      quoteIndex = (quoteIndex + 1) % quoteList.size();
    }
  }
}