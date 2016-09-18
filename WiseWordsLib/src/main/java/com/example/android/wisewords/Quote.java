package com.example.android.wisewords;

import android.os.Parcel;
import android.os.Parcelable;

public class Quote implements Parcelable {

  private String quoteText, author;

  public Quote(String nQuoteText, String nAuthor) {
    quoteText = nQuoteText;
    author = nAuthor;
  }

  public String getQuoteAuthor() {
    return author;
  }

  public String getQuoteText() {
    return quoteText;
  }

  protected Quote(Parcel in) {
    quoteText = in.readString();
    author = in.readString();
  }

  public static final Creator<Quote> CREATOR = new Creator<Quote>() {
    @Override
    public Quote createFromParcel(Parcel in) {
      return new Quote(in);
    }

    @Override
    public Quote[] newArray(int size) {
      return new Quote[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(quoteText);
    parcel.writeString(author);
  }
}