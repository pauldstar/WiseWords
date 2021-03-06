package com.example.android.sg_wisewords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.wisewords.Quote;
import com.example.android.wisewords.SavedQuotes;
import com.vuzix.hardware.GestureSensor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivitySG extends Activity {

  private static TextView quoteTextTView, quoteAuthorTView;
  private static int quoteIndex = 0;
  private static boolean indexHasGoneFullCycle = true;
  private static final String htmlUrlString = "http://www.quotationspage.com/random.php3";
  private static ArrayList<Quote> quoteList = new ArrayList<>();
  private static GestureSensor gestureSensor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    quoteTextTView = (TextView) findViewById(R.id.quote_text);
    quoteAuthorTView = (TextView) findViewById(R.id.quote_author);
    // check if gesture controls are enabled on the vuzix device
    gestureSensor = new MainGestureSensor(this);
    android.util.Log.d("Gesture","SmartGlass module");
    if (GestureSensor.isOn())
      android.util.Log.d("Gesture","GestureSensor is ON");
    else android.util.Log.d("Gesture","GestureSensor is OFF");
    if (gestureSensor.isCalibrated())
      android.util.Log.d("Gesture","GestureSensor is calibrated!");
    else android.util.Log.d("Gesture","GestureSensor is NOT calibrated!");
    if (GestureSensor.areKeyEventsOn())
      android.util.Log.d("Gesture","KeyEvents are ON");
    else {
      android.util.Log.d("Gesture","KeyEvents are OFF");
      GestureSensor.KeyEventsOn();
      String response = GestureSensor.areKeyEventsOn() ? "Yes" : "No";
      android.util.Log.d("Gesture", "KeyEvents ON now? " + response);
    }
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

  @Override
  protected void onResume() {
    super.onResume();
    if (gestureSensor != null) gestureSensor.register();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (gestureSensor != null) gestureSensor.unregister();
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (gestureSensor != null) gestureSensor.unregister();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (gestureSensor != null) gestureSensor.unregister();
  }

  // executed when the down_arrow_icon is clicked
  private void getNextQuote() {
    QuoteAsyncTask asyncTask = new QuoteAsyncTask();
    asyncTask.execute(htmlUrlString);
  }

  // executed when the down_arrow_icon is clicked
  private void showSavedQuotes() {
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
  private void saveQuote() {

  }

  private class QuoteAsyncTask extends AsyncTask<String, Void, Void> {

    private final String ASYNC_TAG = QuoteAsyncTask.class.getSimpleName();

    @Override
    protected Void doInBackground(String... params) {
      // below line for debugging; allows breakpoints to be set for the background method
      /*android.os.Debug.waitForDebugger();*/

      String htmlPageUrl = params[0];
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
      // increment index to point to the next item on the list
      // when it comes back to zero, then we reload some more quotes
      if (quoteIndex == 19) indexHasGoneFullCycle = true;
      quoteIndex = (quoteIndex + 1) % quoteList.size();
    }
  }

  // Gesture sensor class for MainActivitySG
  private class MainGestureSensor extends GestureSensor {

    public MainGestureSensor(Context context) {
      super(context);
    }

    @Override
    protected void onBackSwipe(int speed) {
      Log.d("Gesture", "BackSwipe");
      // show the next quote
      getNextQuote();
    }

    @Override
    protected void onForwardSwipe(int speed) {
      Log.d("Gesture", "ForwardSwipe");
      // go to saved quotes activity
      showSavedQuotes();
    }

    @Override
    protected void onNear() {
      Log.d("Gesture", "OnNear");
      // save a quote from main activity
      saveQuote();
    }

    @Override
    protected void onFar() {
      Log.d("Gesture", "OnFar");
      // also save a quote from main activity
      saveQuote();
    }
  }
}