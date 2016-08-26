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

public class MainActivity extends Activity {

  private static final String MAIN_TAG = MainActivity.class.getSimpleName();
  private static TextView quoteTextTView, quoteAuthorTView;
  private static int quoteIndex = 0;
  private static boolean indexHasGoneFullCycle = true;
  public static final String htmlUrlString = "http://www.quotationspage.com/random.php3";
  private static ArrayList<Quote> quoteList = new ArrayList<>();
  private static GestureSensor gestureSensor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    quoteTextTView = (TextView) findViewById(R.id.quote_text);
    quoteAuthorTView = (TextView) findViewById(R.id.quote_author);
    // check if gesture controls are enabled on the vuzix device
    GestureSensor gestureSensor;
    if (!GestureSensor.isOn()) {
      android.util.Log.i("Gesture", "GestureSensor is ON");
      gestureSensor = new MainGestureSensor(this, this);
    }
    else android.util.Log.i("Gesture","GestureSensor is OFF");
    // set the first quote to appear on screen
    QuoteAsyncTask asyncTask = new QuoteAsyncTask();
    asyncTask.execute(htmlUrlString);
  }

  @Override
  protected void onResume() {
    super.onResume();
    /**if (!GestureSensor.isOn()) gestureSensor.register();*/
    // Decrementing using modulo; make sure we view same quote on return to MainActivity
    quoteIndex = (quoteIndex - 1 + quoteList.size()) % quoteList.size();
    if (indexHasGoneFullCycle) indexHasGoneFullCycle = false;
  }

  @Override
  protected void onPause() {
    super.onPause();
    /**if (!GestureSensor.isOn()) gestureSensor.register();*/
  }

  @Override
  protected void onStop() {
    super.onStop();
    /**if (!GestureSensor.isOn()) gestureSensor.register();*/
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    /**if (!GestureSensor.isOn()) gestureSensor.register();*/
  }

  // executed when the right_arrow_icon is clicked
  public void getNextQuote(View view) {
    QuoteAsyncTask asyncTask = new QuoteAsyncTask();
    asyncTask.execute(htmlUrlString);
  }

  // executed when the right_arrow_icon is clicked
  public void showSavedQuotes(View view) {
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
      // set the index to point to the next item on the list
      // when it comes back to zero, then we reload some more quotes
      if (quoteIndex == 19) indexHasGoneFullCycle = true;
      quoteIndex = (quoteIndex + 1) % quoteList.size();
    }
  }

  // Gesture sensor class for MainActivity
  private class MainGestureSensor extends GestureSensor {

    private Activity activity;

    public MainGestureSensor(Context context, Activity activity) {
      super(context);
      this.activity = activity;
    }

    @Override
    protected void onBackSwipe() {
      // show the next quote
      ImageView imageView = (ImageView) activity.findViewById(R.id.next_quote_icon);
      getNextQuote(imageView);
    }

    @Override
    protected void onForwardSwipe() {
      // go to saved quotes activity
      ImageView imageView = (ImageView) activity.findViewById(R.id.saved_quotes_icon);
      showSavedQuotes(imageView);
    }

    @Override
    protected void onNear() {
      // save a quote on main activity
    }

    @Override
    protected void onFar() {    }
  }
}