package com.example.android.wisewords;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by po482951 on 13/09/2016.
 */
public class QuoteAdapter extends CursorAdapter {
  public QuoteAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
    View view = LayoutInflater.from(context).inflate(
            R.layout.list_item_saved_quote, viewGroup, false);
    return view;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {

  }
}
