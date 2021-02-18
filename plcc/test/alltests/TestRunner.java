/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package alltests;


import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * A test runner which runs all tests. Needed for running tests from outside the IDE, e.g., on the CI server.
 * @author spirit
 */

    
public class TestRunner {
   public static void main(String[] args) {
      System.out.println("Running unit tests.");
      Result result = JUnitCore.runClasses(AllTests.class);
      for (Failure failure : result.getFailures()) {
         System.out.println("Test failed: '" + failure.toString() + "'.");
      }
      System.out.println("Overall result: " + result.wasSuccessful() + ".");
   }
}  	
    

