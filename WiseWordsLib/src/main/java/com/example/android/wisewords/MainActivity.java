package com.example.android.wisewords;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.wisewords.data.QuoteContract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity {

  private static final String MAIN_TAG = MainActivity.class.getSimpleName();
  private static TextView quoteTextTView, quoteAuthorTView;
  private static int quoteIndex = 0;
  private static boolean indexHasGoneFullCycle = true;
  private static final String htmlUrlString = "http://www.quotationspage.com/random.php3";
  private static ArrayList<Quote> quoteList = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    quoteTextTView = (TextView) findViewById(R.id.quote_text);
    quoteAuthorTView = (TextView) findViewById(R.id.quote_author);
    android.util.Log.d("Gesture","Phone module");
    // set the first quote to appear on screen
    getNextQuote();
    // set listener on next_icon so a click will change to next quote on list
    ImageView nextIconImageView = (ImageView) findViewById(R.id.next_quote_icon);
    nextIconImageView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) { getNextQuote(); }
    });
    // set listener on saved_quotes_icon so a click will change to list of saved quotes
    ImageView savedQuotesImageView = (ImageView) findViewById(R.id.saved_quotes_icon);
    savedQuotesImageView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) { showSavedQuotes(); }
    });
    // set listener on save_quote_icon so a click save quote in database
    ImageView saveQuoteImageView = (ImageView) findViewById(R.id.save_quote_icon);
    saveQuoteImageView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) { saveQuote(); }
    });
  }

  // executed when the right_arrow_icon is clicked
  public void getNextQuote() {
    QuoteAsyncTask asyncTask = new QuoteAsyncTask();
    asyncTask.execute(htmlUrlString);
  }

  // executed when the right_arrow_icon is clicked
  public void showSavedQuotes() {
    // collect all the quote texts from the quote list for the dummy data
    ArrayList<String> quoteTextList = new ArrayList<>();
    ArrayList<String> quoteAuthorList = new ArrayList<>();
    String quoteText, quoteAuthor;
    for (Quote quote : quoteList) {
      quoteText = quote.getQuoteText();
      quoteAuthor = quote.getQuoteAuthor();
      quoteTextList.add(quoteText);
      quoteAuthorList.add(quoteAuthor);
    }
    // call intent and add bundle containing the quoteText and quoteAuthor lists
    Intent intent = new Intent(this, SavedQuotes.class);
    Bundle extras = new Bundle();
    extras.putStringArrayList("quoteTextList", quoteTextList);
    extras.putStringArrayList("quoteAuthorList", quoteAuthorList);
    intent.putExtras(extras);
    startActivity(intent);
  }

  // save a quote from main activity
  public void saveQuote() {
    // quoteIndex has already been incremented, so using the previous index
    // decrementing using modulo; so we save the quote currently on view on MainActivity
    int previousIndex = (quoteIndex - 1 + quoteList.size()) % quoteList.size();
    String quoteText = quoteList.get(previousIndex).getQuoteText();
    String quoteAuthor = quoteList.get(previousIndex).getQuoteAuthor();
    // first check if quote already exists in database to prevent duplication
    Uri quoteUriWithTextAndAuthor = QuoteContract.QuoteEntry.
            buildQuoteUriWithTextAndAuthor(quoteText, quoteAuthor);
    Cursor cursor = getContentResolver().query(quoteUriWithTextAndAuthor, null, null, null, null);
    if (cursor.getCount() != 0) {
      Toast toast = Toast.makeText(this, "Quote already saved!", Toast.LENGTH_SHORT);
      toast.show();
      return;
    }
    // quote doesn't already exist, so save quote.
    ContentValues quoteValues = new ContentValues();
    quoteValues.put(QuoteContract.QuoteEntry.COLUMN_TEXT, quoteText);
    quoteValues.put(QuoteContract.QuoteEntry.COLUMN_AUTHOR, quoteAuthor);
    long date = Calendar.getInstance().getTimeInMillis(); // gets the datetime for right now
    quoteValues.put(QuoteContract.QuoteEntry.COLUMN_DATE, date);
    // insert into db
    getContentResolver().insert(QuoteContract.QuoteEntry.CONTENT_URI, quoteValues);
    Toast toast = Toast.makeText(this, "Quote saved!", Toast.LENGTH_SHORT);
    toast.show();
  }

  public static void setQuoteTextTView(String text) {

  }

  private class QuoteAsyncTask extends AsyncTask<String, Void, Void> {

    private final String ASYNC_TAG = QuoteAsyncTask.class.getSimpleName();

    @Override
    protected Void doInBackground(String... params) {
      // below line for debugging; allows breakpoints to be set for the background method
      /*android.os.Debug.waitForDebugger();*/
      String htmlPageUrl = params[0];
      // only access the internet when we've exhausted the current quoteList
      if (quoteIndex == 0 && indexHasGoneFullCycle) {
        try {
          indexHasGoneFullCycle = false;
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
      if (quoteIndex == 19) indexHasGoneFullCycle = true;
      quoteIndex = (quoteIndex + 1) % quoteList.size();
    }
  }
}