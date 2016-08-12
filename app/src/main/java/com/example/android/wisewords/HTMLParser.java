package com.example.android.wisewords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by po482951 on 12/08/2016.
 */
public class HTMLParser {

  /**
   * Converts the HTML document stream into a String
   */
  public static String StreamToString(InputStream input) throws IOException {
    if(input == null) return "";

    Writer writer = new StringWriter();
    Reader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
    char[] buffer = new char[1024];

    int n;
    while ((n = reader.read(buffer)) != -1) {
      writer.write(buffer, 0, n);
    }

    return writer.toString();
  }

}
