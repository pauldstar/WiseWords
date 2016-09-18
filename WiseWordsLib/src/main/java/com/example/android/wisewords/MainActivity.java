package com.example.android.wisewords;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.wisewords.data.QuoteContract;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity {

  private static TextView quoteTextTView, quoteAuthorTView;
  private static int quoteIndex;
  public static boolean indexHasGoneFullCycle, appJustStarted;
  public static final String htmlUrlString = "http://www.quotationspage.com/random.php3";
  private static ArrayList<Quote> quoteList;
  // nextQuoteList contains the next set of quotes to conceal latency due to loading web-page
  public static ArrayList<Quote> nextQuoteList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // check if page just restarted after a screen orientation change
    if (savedInstanceState != null) {
      quoteIndex = savedInstanceState.getInt("quoteIndex");
      nextQuoteList = savedInstanceState.getParcelableArrayList("nextQuoteList");
      appJustStarted = false;
    } else appJustStarted = true;
    quoteTextTView = (TextView) findViewById(R.id.quote_text);
    quoteAuthorTView = (TextView) findViewById(R.id.quote_author);
    // set the quote to appear on screen
    // first retrieve quoteList from SplashActivity Bundle
    Intent intent = getIntent();
    Bundle intentBundle = intent.getExtras();
    quoteList = intentBundle.getParcelableArrayList("quoteList");
    indexHasGoneFullCycle = false;
    getNextQuote();
    // set listener on next_icon so a click will change to next quote on list
    ImageView nextIconImageView = (ImageView) findViewById(R.id.next_quote_icon);
    nextIconImageView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        getNextQuote();
      }
    });
    // set listener on saved_quotes_icon so a click will change to list of saved quotes
    ImageView savedQuotesImageView = (ImageView) findViewById(R.id.saved_quotes_icon);
    final Context context = this;
    savedQuotesImageView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Intent intent = new Intent(context, SavedQuotes.class);
        startActivity(intent);
      }
    });
    // set listener on save_quote_icon so a click save quote in database
    ImageView saveQuoteImageView = (ImageView) findViewById(R.id.save_quote_icon);
    saveQuoteImageView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        saveQuote();
      }
    });
  }

  @Override
  protected void onSaveInstanceState(Bundle instanceStateToSave) {
    super.onSaveInstanceState(instanceStateToSave);
    // save the previous quoteIndex because it gets incremented when getNextQuote is called
    // decrement using modulo
    int previousIndex = (quoteIndex - 1 + quoteList.size()) % quoteList.size();
    instanceStateToSave.putInt("quoteIndex", previousIndex);
    instanceStateToSave.putParcelableArrayList("nextQuoteList", nextQuoteList);
  }

  /**
   * executed when the refresh_icon is clicked
   */
  public void getNextQuote() {
    // set the index to point to the next item on the list
    quoteIndex = appJustStarted ? 0 : (quoteIndex + 1) % quoteList.size();
    // update the quoteList if necessary
    if (quoteIndex == 0) {
      if (indexHasGoneFullCycle) {
        //transfer all quotes from nextQuoteList to current quoteList
        quoteList.clear();
        quoteList.addAll(nextQuoteList);
      }
      // only access the internet if we've exhausted the current quoteList
      // update nextQuoteList in the background
      QuoteAsyncTask quoteAsyncTask = new QuoteAsyncTask();
      quoteAsyncTask.execute(1);
    } // when it comes back to zero, then we reload some more quotes
    else if (quoteIndex == 19) indexHasGoneFullCycle = true;
    // bind quote text and author to respective text views
    quoteTextTView.setText(quoteList.get(quoteIndex).getQuoteText());
    quoteAuthorTView.setText(quoteList.get(quoteIndex).getQuoteAuthor());
    Log.d("NextQuote", "QuotesSize: " + quoteList.size() + " QuoteIndex: " + quoteIndex);
  }

  /**
   * save a quote from main activity
   */
  public void saveQuote() {
    String quoteText = quoteList.get(quoteIndex).getQuoteText();
    String quoteAuthor = quoteList.get(quoteIndex).getQuoteAuthor();
    // first check if quote already exists in database to prevent duplication
    Uri quoteUriWithTextAndAuthor = QuoteContract.QuoteEntry.
            buildQuoteUriWithTextAndAuthor(quoteText, quoteAuthor);
    Cursor cursor = getContentResolver().query(quoteUriWithTextAndAuthor, null, null, null, null);
    if (cursor.getCount() != 0) {
      Toast toast = Toast.makeText(this, "Quote already saved!", Toast.LENGTH_SHORT);
      toast.show();
      return;
    } // quote doesn't already exist, so save quote.
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
}