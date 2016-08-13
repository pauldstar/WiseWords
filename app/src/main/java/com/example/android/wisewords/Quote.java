package com.example.android.wisewords;

/**
 * Created by po482951 on 12/08/2016.
 */
public class Quote {

  private String quoteText, author;
  private static final String LOG_TAG = Quote.class.getSimpleName();

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

  public void setQuoteText(String quote) {
    this.quoteText = quote;
  }

}
