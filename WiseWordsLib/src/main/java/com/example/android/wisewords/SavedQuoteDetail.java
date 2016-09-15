package com.example.android.wisewords;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.wisewords.data.QuoteContract;

public class SavedQuoteDetail extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_saved_quote_detail);
    // collect data from intent
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    final String quoteId = extras.getString("quoteId");
    String fullQuoteText = extras.getString("fullQuoteText");
    String dateText = extras.getString("dateText");
    String quoteAuthor = extras.getString("quoteAuthor");
    // get the text views for this activity
    TextView quoteTextTV = (TextView) findViewById(R.id.detail_quote_text);
    TextView dateTextTV = (TextView) findViewById(R.id.detail_date_text);
    TextView quoteAuthorTV = (TextView) findViewById(R.id.detail_quote_author);
    // set the corresponding text for the text views
    quoteTextTV.setText(fullQuoteText);
    dateTextTV.setText(dateText);
    quoteAuthorTV.setText(quoteAuthor);
    // set onClickListener for delete icon to remove quote from database
    ImageView saveQuoteImageView = (ImageView) findViewById(R.id.delete_saved_quote);
    saveQuoteImageView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) { deleteQuote(quoteId); }
    });
  }

  private void deleteQuote(String quoteId) {
    String[] selectionArgs = new String[] {quoteId};
    getContentResolver().delete(QuoteContract.QuoteEntry.CONTENT_URI, null, selectionArgs);
    finish();
  }
}
