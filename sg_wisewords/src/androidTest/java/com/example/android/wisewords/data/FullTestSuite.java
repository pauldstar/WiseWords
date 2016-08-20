package com.example.android.wisewords.data;
/**
 * Created by po482951 on 18/08/2016.
 * Contains code to include all of the java test classes inside this package to a suite of tests
 * that JUnit will run. This allows us to add more tests whenever we want.
 */
import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FullTestSuite extends TestSuite {
  public static Test suite() {
    return new TestSuiteBuilder(FullTestSuite.class).includeAllPackagesUnderHere().build();
  }

  public FullTestSuite() {
    super();
  }
}
