package com.example.android.wisewords;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by po482951 on 10/08/2016.
 */
public class SplashActivity extends AppCompatActivity {

  private static ArrayList<String> quoteTextList = new ArrayList<>();
  private static ArrayList<String> quoteAuthorList = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // retrieve the quote text and authors list using url from MainActivity
    try {
      SplashAsyncTask splashAsyncTask = new SplashAsyncTask();
      splashAsyncTask.execute().get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    // add all string ArrayLists to a bundle
    Bundle quoteTextAuthorBundle = new Bundle();
    quoteTextAuthorBundle.putStringArrayList("quoteTextList", quoteTextList);
    quoteTextAuthorBundle.putStringArrayList("quoteAuthorList", quoteAuthorList);
    // we launch the main activity; while the activity loads the splash screen would be visible.
    Intent intent = new Intent(this, MainActivity.class).
            putExtras(quoteTextAuthorBundle);
    startActivity(intent);
    finish();
  }

  private class SplashAsyncTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
      // below line for debugging; allows breakpoints to be set for the background method
      android.os.Debug.waitForDebugger();
      try {
        Document htmlDocument = Jsoup.connect(MainActivity.htmlUrlString).get();
        Element htmlBodyElement = htmlDocument.body();
        Elements quoteTextElements = htmlBodyElement.select("dt.quote > a");
        Elements quoteAuthorElements = htmlBodyElement.select("dd.author > b");
        String quoteText, quoteAuthor;
        int listSize = quoteTextElements.size();
        for (int index = 0; index < listSize; index++) {
          quoteText = quoteTextElements.get(index).ownText();
          quoteAuthor = quoteAuthorElements.get(index).text();
          quoteTextList.add(quoteText);
          quoteAuthorList.add(quoteAuthor);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {

    }
  }
}