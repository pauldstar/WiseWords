package com.example.android.wisewords;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

/**
 * Created by po482951 on 10/08/2016.
 */
public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    /**
     * we launch the main activity
     * while the activity loads the splash screen would be visible
     */
    try {
      TimeUnit.SECONDS.sleep(2);
      Intent intent = new Intent(this, MainActivity.class);
      startActivity(intent);
      finish();
    }
    catch (InterruptedException e) { System.out.println(e.getMessage());}
  }
}
