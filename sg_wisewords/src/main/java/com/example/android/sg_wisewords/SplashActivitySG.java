package com.example.android.sg_wisewords;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by po482951 on 10/08/2016.
 */
public class SplashActivitySG extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    /**
     * we launch the main activity
     * while the activity loads the splash screen would be visible
     */
    Intent intent = new Intent(this, MainActivitySG.class);
    startActivity(intent);
    finish();
  }
}
