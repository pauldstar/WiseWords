package com.example.android.wisewords;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by po482951 on 11/08/2016.
 */
public class AcousticTextView extends TextView{

  public AcousticTextView(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Acoustic.ttf"));
  }
}
