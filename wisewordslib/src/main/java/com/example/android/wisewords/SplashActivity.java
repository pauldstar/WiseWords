package com.example.android.wisewords;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SplashActivity extends AppCompatActivity {

  public static ArrayList<Quote> quoteList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    quoteList = new ArrayList<>();
    try { // retrieve quoteList in background using URL from MainActivity
      QuoteAsyncTask splashAsyncTask = new QuoteAsyncTask();
      // use get() to make splash screen wait while it loads quoteList
      splashAsyncTask.execute(0).get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    } // add quoteList to a bundle
    Bundle quoteBundle = new Bundle();
    quoteBundle.putParcelableArrayList("quoteList", quoteList);
    // we launch the main activity; while the activity loads the splash screen would be visible.
    Intent intent = new Intent(this, MainActivity.class).putExtras(quoteBundle);
    startActivity(intent);
    finish();
  }
}