package com.example.android.wisewords;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.wisewords.data.QuoteContract;

/**
 * Created by po482951 on 13/09/2016.
 * Class collects saved quote data from cursor and binds to list view item
 */
public class QuoteAdapter extends CursorAdapter {

  // this is a PROJECTION which Specifies the columns we need for the saved quote list item view
  private static final String[] QUOTE_COLUMNS = QuoteContract.QuoteEntry.getFullQuoteProjection();

  // Indices tied to the projection QUOTE_COLUMNS. If QUOTE_COLUMNS changes, these must change.
  /**public static final int COL_QUOTE_ID = 0;*/
  public static final int COL_QUOTE_TEXT = 1;
  /**public static final int COL_QUOTE_AUTHOR = 2;*/
  public static final int COL_QUOTE_DATE = 3;

  public QuoteAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
    View view = LayoutInflater.from(context).inflate(
            R.layout.list_item_saved_quote, viewGroup, false);
    // add viewHolder to view
    ViewHolder viewHolder = new ViewHolder(view);
    view.setTag(viewHolder);
    return view;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    // the View passed into this function is the View returned from newView method.
    ViewHolder viewHolder = (ViewHolder) view.getTag();
    // bind ID
    /**long _ID = cursor.getLong(COL_QUOTE_ID);
    viewHolder.idView.setText(String.valueOf(_ID));*/
    // bind full and trimmed quote texts
    String quoteText = cursor.getString(COL_QUOTE_TEXT);
    viewHolder.trimmedQuoteTextView.setText(Utility.trimText(quoteText, 81));
    /**viewHolder.hiddenFullQuoteTextView.setText(quoteText);
    // bind author
    viewHolder.hiddenAuthorView.setText(cursor.getString(COL_QUOTE_AUTHOR));*/
    // bind date
    long date = cursor.getLong(COL_QUOTE_DATE);
    viewHolder.dateView.setText(Utility.formatDate(date));
  }

  /**
   * Static nested class which acts as a cache of the children views for a quote list item.
   */
  public static class ViewHolder {

    /**public final TextView idView;*/
    public final TextView trimmedQuoteTextView;
    public final TextView dateView;
    /**public final TextView hiddenFullQuoteTextView;
    public final TextView hiddenAuthorView;*/

    public ViewHolder(View view) {
      /**idView = (TextView) view.findViewById(R.id.hidden_saved_quote_id);*/
      trimmedQuoteTextView = (TextView) view.findViewById(R.id.trimmed_saved_quote_text);
      dateView = (TextView) view.findViewById(R.id.saved_quote_date);
      /**hiddenFullQuoteTextView = (TextView) view.findViewById(R.id.hidden_full_saved_quote_text);
      hiddenAuthorView = (TextView) view.findViewById(R.id.hidden_saved_quote_author);*/
    }
  }
}
