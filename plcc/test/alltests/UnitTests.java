package alltests;

/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
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
public class UnitTests extends TestCase {

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
		
		return suite;
	}
}

