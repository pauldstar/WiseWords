package com.example.android.wisewords;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SavedQuotes extends AppCompatActivity {

  private static SimpleAdapter savedQuotesAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_saved_quotes);
    // retrieve quotes array list from intent, and use as dummy data to populate saved quotes
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    ArrayList<String> quoteTextList = extras.getStringArrayList("quoteTextList");
    ArrayList<String> quoteAuthorList = extras.getStringArrayList("quoteAuthorList");
    ArrayList<HashMap<String, String>> quoteMapList = new ArrayList<>();
    // create and populate hash map to hold the quotes (key) and their save dates (value): today
    HashMap<String, String> quoteMap = new HashMap<>();
    String[] from = {"trimmedQuoteText", "saveDate", "fullQuoteText", "quoteAuthor"}; // column names (map keys)
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
    int[] to = {R.id.trimmed_saved_quote_text, R.id.saved_quote_date,
            R.id.hidden_full_saved_quote_text, R.id.hidden_saved_quote_author};
    // initialise adapter and bind to list view
    savedQuotesAdapter = new SimpleAdapter(
            this, quoteMapList, R.layout.list_item_saved_quote, from, to);
    // bind the adaptor to the saved quotes list view to display
    ListView listView = (ListView) findViewById(R.id.listview_saved_quotes);
    listView.setAdapter(savedQuotesAdapter);
    // cause a click on an item in the list to launch the detail activity for saved quotes
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
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
    });
  }
}