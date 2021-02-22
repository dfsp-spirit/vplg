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
 * Unit tests for PLCC.
 *
 * @author ts
 */
public class AllUnitTests extends TestCase {

	/**
	 * Creates a test suite containing all unit tests
	 * for this component.
	 *
	 * @return A test suite containing all unit tests.
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		// Add tests here:
		suite.addTestSuite(SSETest.class);
                suite.addTestSuite(TestGMLGraphParser.class);
                suite.addTestSuite(TestGraphCreator.class);
                suite.addTestSuite(TestLinnotParserRED.class);
                suite.addTestSuite(TestLinnotREDToGraph.class);
                suite.addTestSuite(TestPTGLNotations.class);
		
		return suite;
	}
}

