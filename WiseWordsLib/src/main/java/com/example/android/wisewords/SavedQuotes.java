package com.example.android.wisewords;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
    // initialise cursor loader. either re-connect with an existing one, or start a new one.
    getLoaderManager().initLoader(QUOTE_LOADER_ID, null, this);
    // create adapter and bind the adaptor to the saved quotes list view to display
    savedQuotesAdapter = new QuoteAdapter(this, null, 0);
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
    TextView quoteIdTV = (TextView) view.findViewById(R.id.hidden_saved_quote_id);
    TextView fullQuoteTextTV = (TextView) view.findViewById(R.id.hidden_full_saved_quote_text);
    TextView dateTextTV = (TextView) view.findViewById(R.id.saved_quote_date);
    TextView quoteAuthorTV = (TextView) view.findViewById(R.id.hidden_saved_quote_author);
    // get the strings from the text views
    String quoteId = quoteIdTV.getText().toString();
    String fullQuoteText = fullQuoteTextTV.getText().toString();
    String dateText = dateTextTV.getText().toString();
    String quoteAuthor = quoteAuthorTV.getText().toString();
    // add all strings to a bundle
    Bundle quoteItems = new Bundle();
    quoteItems.putString("quoteId", quoteId);
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
    // retrieve quote list cursor with descending sort order by date
    String sortOrder = QuoteContract.QuoteEntry.COLUMN_DATE + " DESC";
    Uri quoteListUri = QuoteContract.QuoteEntry.buildQuoteListUri();
    return new CursorLoader(this, quoteListUri, null, null, null, sortOrder);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    savedQuotesAdapter.swapCursor(cursor);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    savedQuotesAdapter.swapCursor(null);
  }
}