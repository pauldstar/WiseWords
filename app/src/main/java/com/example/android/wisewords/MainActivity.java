package com.example.android.wisewords;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

  private TextView quoteTextTView, quoteAuthorTView;
  private int quoteIndex = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    quoteTextTView = (TextView)findViewById(R.id.quote_text);
    quoteAuthorTView = (TextView)findViewById(R.id.quote_author);
    // retrieve a list of quotes from the html document in assets
    final Context context = getBaseContext();
    final ArrayList<Quote> quotes = Quote.getQuoteList(context, "quotes.html");
    // set the first quote to appear on screen
    quoteTextTView.setText(quoteIndex + ". " + quotes.get(quoteIndex).getQuoteText());
    quoteAuthorTView.setText(quotes.get(quoteIndex).getQuoteAuthor());
    // set listener on next_icon so a click will change to next quote on list
    ImageView image = (ImageView) findViewById(R.id.next_quote_icon);
    image.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        quoteIndex = (quoteIndex + 1) % quotes.size();
        Toast toast = Toast.makeText(context, quoteIndex, Toast.LENGTH_SHORT);
        toast.show();

        quoteTextTView.setText(quoteIndex + ". " + quotes.get(quoteIndex).getQuoteText());
        quoteAuthorTView.setText(quotes.get(quoteIndex).getQuoteAuthor());

      }
    });


  }
}
