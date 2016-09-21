package com.example.android.wisewords;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.wisewords.data.QuoteContract;

public class SavedQuotes extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final int QUOTE_LOADER_ID = 0;
  private QuoteAdapter savedQuotesAdapter;
  private ActionBar actionBar;
  private Cursor savedQuotesCursor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_saved_quotes);
    // initialise cursor loader. either re-connect with an existing one, or start a new one.
    getLoaderManager().initLoader(QUOTE_LOADER_ID, null, this);
    // add "up" arrow to action bar
    actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    // create adapter and bind the adaptor to the saved quotes list view to display
    savedQuotesAdapter = new QuoteAdapter(this, null, 0);
    ListView listView = (ListView) findViewById(R.id.listview_saved_quotes);
    listView.setAdapter(savedQuotesAdapter);
    // cause a click on an item in the list to launch the detail activity for saved quotes
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        launchSavedQuoteDetail(position);
      }
    });
  }

  private void launchSavedQuoteDetail(int position) {
    // call intent and add bundle as extras
    Intent intent = new Intent(this, SavedQuoteDetail.class).putExtra("position", position);
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
    savedQuotesCursor = cursor;
    savedQuotesAdapter.swapCursor(savedQuotesCursor);
    int quoteCount = savedQuotesAdapter.getCount();
    String activityTitle = getResources().getString(R.string.title_activity_saved_quotes)
            + " (" + quoteCount + ")";
    actionBar.setTitle(activityTitle);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    savedQuotesAdapter.swapCursor(null);
  }
}