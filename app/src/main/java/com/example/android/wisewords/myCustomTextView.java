package com.example.android.wisewords;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by po482951 on 11/08/2016.
 */
public class myCustomTextView extends TextView{

  public myCustomTextView(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Blockletter.otf"));
  }
}
