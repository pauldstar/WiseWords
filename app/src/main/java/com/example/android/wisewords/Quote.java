package com.example.android.wisewords;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by po482951 on 12/08/2016.
 */
public class Quote {

  private String quoteText, author;

  public Quote(String nQuoteText, String nAuthor) {
    quoteText = nQuoteText;
    author = nAuthor;
  }

  public String getQuoteAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getQuoteText() {
    return quoteText;
  }

  public void setQuote(String quote) {
    this.quoteText = quote;
  }

  public static ArrayList<Quote> getQuoteList(Context context, String htmlFilename) {
    String htmlContentAsString = "";
    // get the html file from the Assets folder
    try {
      AssetManager manager = context.getAssets();
      InputStream input = manager.open(htmlFilename, AssetManager.ACCESS_BUFFER);
      htmlContentAsString = HTMLParser.StreamToString(input);
      input.close();
    }
    catch (IOException e) {e.printStackTrace();}

    if (htmlContentAsString.equals("")) {
      Toast toast = Toast.makeText(context, "There is no HTML file to parse", Toast.LENGTH_LONG);
      toast.show();
      return null;
    }
    // get list of .quote and .author class nodes from the html document
    Document htmlDocument = Jsoup.parse(htmlContentAsString);
    Element htmlBodyElement = htmlDocument.body();
    Elements quoteTextList = htmlBodyElement.select("dt.quote > a");
    Elements quoteAuthorList = htmlBodyElement.select("dd.author > b > a");
    Element htmlQuoteText = quoteTextList.first();
    Element htmlQuoteAuthor =  quoteAuthorList.first();
    // iterate through the quotes and authors
    ArrayList<Quote> quoteSet = new ArrayList<>();
    String quoteText, quoteAuthor;
    Quote quote;
    do {
      quoteText = htmlQuoteText.ownText();
      quoteAuthor = htmlQuoteAuthor.ownText();

      quote = new Quote(quoteText, quoteAuthor);
      quoteSet.add(quote);

      htmlQuoteText = htmlQuoteText.nextElementSibling();
      htmlQuoteAuthor = htmlQuoteAuthor.nextElementSibling();
    }
    while (htmlQuoteText != null);

    /*ArrayList<String> quoteTexts = new ArrayList<>();
    ArrayList<String> quoteAuthors = new ArrayList<>();*/
    /*for (int index = 0; index < 20; index++) {*/
    /*while (hasMoreQuotes(htmlQuotes))
      quoteSet.add(getNextQuoteAndAuthor(htmlQuotes, htmlAuthors));*/
    /*for (int index = 0; index < htmlQuotes)
    for (Element htmlQuoteText : htmlQuotes)
      quoteTexts.add(htmlQuoteText.ownText());
    for (Element htmlAuthor : htmlAuthors)
      quoteTexts.add(htmlAuthor.ownText());*/

    return quoteSet;
  }

}
