package com.example.android.wisewords;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.wisewords.data.QuoteContract;

public class SavedQuoteDetail extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

  private Cursor savedQuotesCursor;
  private static final int QUOTE_LIST_LOADER_ID = 0;
  private static int position, maxCursorIndex;
  private static String quoteId, quoteText, quoteAuthor;
  private static long saveDate;
  private static TextView quoteTextTV, dateTextTV, quoteAuthorTV;
  // Indices tied to the projection QUOTE_COLUMNS. If QUOTE_COLUMNS changes, these must change.
  public static final int COL_QUOTE_ID = 0;
  public static final int COL_QUOTE_TEXT = 1;
  public static final int COL_QUOTE_AUTHOR = 2;
  public static final int COL_QUOTE_DATE = 3;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_saved_quote_detail);
    // collect position of quote in cursor from intent
    Intent intent = getIntent();
    Bundle quoteBundle = intent.getExtras();
    position = quoteBundle.getInt("position");
    // get the text views for this activity
    quoteTextTV = (TextView) findViewById(R.id.detail_quote_text);
    dateTextTV = (TextView) findViewById(R.id.detail_date_text);
    quoteAuthorTV = (TextView) findViewById(R.id.detail_quote_author);
    // initialise cursor loaders. either re-connect with an existing one, or start a new one.
    getLoaderManager().initLoader(QUOTE_LIST_LOADER_ID, null, this);
    // set onClickListener for delete icon to remove quote from database
    ImageView saveQuoteImageView = (ImageView) findViewById(R.id.delete_saved_quote);
    saveQuoteImageView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        deleteQuote();
      }
    });
    // set onClickListener for down arrow icon to display earlier quote
    ImageView downArrowImageView = (ImageView) findViewById(R.id.earlier_quote);
    downArrowImageView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        displayEarlierQuote();
      }
    });
    // set onClickListener for up arrow icon to display later quote
    ImageView upArrowImageView = (ImageView) findViewById(R.id.later_quote);
    upArrowImageView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        displayLaterQuote();
      }
    });
    // set listener on share_quote_icon so a click shares quote
    ImageView shareQuoteImageView = (ImageView) findViewById(R.id.share_quote_icon);
    shareQuoteImageView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        shareQuote();
      }
    });
  }

  private void displayLaterQuote() {
    // TODO: 19/09/2016 perhaps disable to later_quote button
    if (position == 0) {
      Toast toast = Toast.makeText(this, "No later quote!", Toast.LENGTH_SHORT);
      toast.show();
      return;
    }
    position--;
    displayQuote();
  }

  private void displayEarlierQuote() {
    // TODO: 19/09/2016 perhaps disable to earlier_quote button
    if (position == maxCursorIndex) {
      Toast toast = Toast.makeText(this, "No earlier quote!", Toast.LENGTH_SHORT);
      toast.show();
      return;
    }
    position++;
    displayQuote();
  }

  private void displayQuote() {
    savedQuotesCursor.moveToPosition(position);
    quoteId = savedQuotesCursor.getString(COL_QUOTE_ID);
    quoteText = savedQuotesCursor.getString(COL_QUOTE_TEXT);
    quoteAuthor = savedQuotesCursor.getString(COL_QUOTE_AUTHOR);
    saveDate = savedQuotesCursor.getLong(COL_QUOTE_DATE);
    // set the corresponding text for the text views
    quoteTextTV.setText(quoteText);
    dateTextTV.setText(Utility.formatDate(saveDate));
    quoteAuthorTV.setText(quoteAuthor);
  }

  private void deleteQuote() {
    // destroy cursor loader; for some reason every call to database uses loader unless destroyed
    getLoaderManager().destroyLoader(QUOTE_LIST_LOADER_ID);
    String[] selectionArgs = new String[]{quoteId};
    // this command won't work unless loader destroyed
    getContentResolver().delete(QuoteContract.QuoteEntry.CONTENT_URI, null, selectionArgs);
    finish();
  }

  private void shareQuote() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    // this flag returns us to this application
    // when we close the application that handles this share intent
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    shareIntent.setType("text/plain");
    String sharedText = quoteText + "\n[" + quoteAuthor + "]\n#PaulsWiseWords";
    shareIntent.putExtra(Intent.EXTRA_TEXT, sharedText);
    startActivity(Intent.createChooser(shareIntent, "Share Quote!"));
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
    // retrieve quote list cursor with descending sort order by saveDate
    String sortOrder = QuoteContract.QuoteEntry.COLUMN_DATE + " DESC";
    Uri quoteListUri = QuoteContract.QuoteEntry.buildQuoteListUri();
    return new CursorLoader(this, quoteListUri, null, null, null, sortOrder);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    savedQuotesCursor = cursor;
    maxCursorIndex = cursor.getCount() - 1;
    displayQuote();
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {

  }
}