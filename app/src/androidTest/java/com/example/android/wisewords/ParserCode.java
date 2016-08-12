package inducesmile.com.androidjsouphtmlparser;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.wisewords.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class ParserCode extends ActionBarActivity {

  private Document htmlDocument;
  private String htmlContentInStringFormat;
  private TextView parsedHtmlNode;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    parsedHtmlNode = (TextView)findViewById(R.id.html_content);

    String htmlFilename = "filename.html";
    AssetManager mgr = getBaseContext().getAssets();
    try {
      InputStream in = mgr.open(htmlFilename, AssetManager.ACCESS_BUFFER);
      htmlContentInStringFormat = StreamToString(in);
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // output the title of the html document on button click
    Button htmlTitleButton = (Button)findViewById(R.id.button);
    htmlTitleButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(htmlContentInStringFormat.equals("")){
          Toast.makeText(inducesmile.com.androidjsouphtmlparser.ParserCode.this, "There is no HTML file to parse", Toast.LENGTH_LONG).show();
          return;
        }
        else{
          htmlDocument = Jsoup.parse(htmlContentInStringFormat);
          String pageTitle = htmlDocument.title();
          if(pageTitle != null){
            parsedHtmlNode.setText(pageTitle);
          }
        }
      }
    });
  }

  public static String StreamToString(InputStream in) throws IOException {
    if(in == null) {
      return "";
    }
    Writer writer = new StringWriter();
    char[] buffer = new char[1024];
    try {
      Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
      int n;
      while ((n = reader.read(buffer)) != -1) {
        writer.write(buffer, 0, n);
      }
    } finally {
    }
    return writer.toString();
  }
}