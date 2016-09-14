package com.example.android.wisewords;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.wisewords.data.QuoteContract;

public class SavedQuotes extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final int QUOTE_LOADER_ID = 0;
  private QuoteAdapter savedQuotesAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_saved_quotes);
    // add "up" arrow on action bar
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    // retrieve quote list cursor with descending sort order by date
    String sortOrder = QuoteContract.QuoteEntry.COLUMN_DATE + " DESC";
    Uri quoteListUri = QuoteContract.QuoteEntry.buildQuoteListUri();
    Cursor cursor = getContentResolver().query(quoteListUri, null, null, null, sortOrder);
    /** // retrieve quotes array list from intent, and use as dummy data to populate saved quotes
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    ArrayList<String> quoteTextList = extras.getStringArrayList("quoteTextList");
    ArrayList<String> quoteAuthorList = extras.getStringArrayList("quoteAuthorList");
    ArrayList<HashMap<String, String>> quoteMapList = new ArrayList<>();*/
    savedQuotesAdapter = new QuoteAdapter(this, cursor, 0);
    /** // adapter requires a list of maps; each map having key-value pair
    HashMap<String, String> quoteMap = new HashMap<>();
    // save key names in an array for the adapter
    String[] fromKeys = {"trimmedQuoteText", "saveDate", "fullQuoteText", "quoteAuthor"};
    int listSize = quoteTextList.size();
    String quoteText, trimmedQuoteText;
    for (int index = 0; index < listSize; index++) {
      quoteText = trimmedQuoteText = quoteTextList.get(index);
      // trim the quote text to fit the text view
      if (quoteText.length() > 81) //  reduce length of string to fit text view
        trimmedQuoteText = quoteText.substring(0, 77) + "...";
      quoteMap.put("trimmedQuoteText", trimmedQuoteText);
      quoteMap.put("saveDate", "16 Aug, 16");
      quoteMap.put("fullQuoteText", quoteText);
      quoteMap.put("quoteAuthor", quoteAuthorList.get(index));
      quoteMapList.add(quoteMap);
      quoteMap = new HashMap<>();
    }
    // the list views to be populated
    int[] toViews = {R.id.trimmed_saved_quote_text, R.id.saved_quote_date,
            R.id.hidden_full_saved_quote_text, R.id.hidden_saved_quote_author};
    // initialise adapter and bind to list view
    savedQuotesAdapter = new SimpleAdapter(
            this, quoteMapList, R.layout.list_item_saved_quote, fromKeys, toViews);*/
    // bind the adaptor to the saved quotes list view to display
    ListView listView = (ListView) findViewById(R.id.listview_saved_quotes);
    listView.setAdapter(savedQuotesAdapter);
    // cause a click on an item in the list to launch the detail activity for saved quotes
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        launchSavedQuoteDetail(view);
      }
    });
  }

  private void launchSavedQuoteDetail(View view) {
    // get the needed text views associated with that list item
    TextView fullQuoteTextTV = (TextView) view.findViewById(R.id.hidden_full_saved_quote_text);
    TextView dateTextTV = (TextView) view.findViewById(R.id.saved_quote_date);
    TextView quoteAuthorTV = (TextView) view.findViewById(R.id.hidden_saved_quote_author);
    // get the strings from the text views
    String fullQuoteText = fullQuoteTextTV.getText().toString();
    String dateText = dateTextTV.getText().toString();
    String quoteAuthor = quoteAuthorTV.getText().toString();
    // add all strings to a bundle
    Bundle quoteItems = new Bundle();
    quoteItems.putString("fullQuoteText", fullQuoteText);
    quoteItems.putString("dateText", dateText);
    quoteItems.putString("quoteAuthor", quoteAuthor);
    // call intent and add bundle as extras
    Intent intent = new Intent(SavedQuotes.this, SavedQuoteDetail.class)
            .putExtras(quoteItems);
    startActivity(intent);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        // kill activity to resume previous activity on back stack
        // (without calling onResume or onCreate of the activity)
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    // initialise cursor loader
    //getLoaderManager().initLoader(QUOTE_LOADER_ID, null, this);

    return null;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {

  }
}