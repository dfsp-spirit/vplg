/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

import junit.framework.TestCase;
import proteinstructure.SSE;

/**
 * Unit test class.
 *
 * @author ts
 */
public class SSETest extends TestCase {
    
    private SSE sse_helix;

    /**
     * Sets up the test environment and object.
     */
    @Override @org.junit.Before public void setUp() {
        cleanOutput();
        sse_helix = new SSE(SSE.SSE_TYPE_ALPHA_HELIX);
    }

    /**
    * Tears down the test environment, deleting SaveAs output,
    * if it exists.
    */
    @Override @org.junit.After public void tearDown() {
        cleanOutput();
    }
    
    
    /**
     * Removes all output of test case from the file system.
     */
    private void cleanOutput() {
        
    }    
    
    // ************ test cases ***************
    
    /**
     * Tests whether the SSE is of the correct type.
     */
    @org.junit.Test public void testSSEType() {        
        assertTrue("Wrong SSE type.", sse_helix.isHelix());
    }
    
    
    /**
    * Tests passing a null residue.
    */
    @org.junit.Test public void testAddNullResidue() {       

        try {
            // This call should throw an exception
            sse_helix.addResidue(null);

            fail("Did not throw exception when passing NULL residue.");
        }
        catch (IllegalArgumentException iae) { /* expected */ }
    }
    
    
}

