package alltests;

/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Contains all test cases. You can run this from the command line with the 'ant test' command.
 *
 * @author ts
 */
public class AllTests extends TestCase {

    public static Test suite() {
        final TestSuite suite = new TestSuite();
        
        //unit tests
        suite.addTest(AllUnitTests.suite());
        
        // other test may follow here (e.g., acceptance tests)
        
        
        return suite;
    }

}
