package com.example.android.wisewords;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    /*code to change the font of text
    TextView textView = (TextView) findViewById(R.id.date_text_today);
    Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/Blockletter.otf");
    textView.setTypeface(myCustomFont);*/
  }
}
