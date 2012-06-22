/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

import java.io.File;
import junit.framework.TestCase;
import splitpdb.Main;

/**
 * Unit test class.
 *
 * @author ts
 */
public class MainTest extends TestCase {
    

    /**
     * Sets up the test environment and object.
     */
    @Override @org.junit.Before public void setUp() {
        cleanOutput();
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
        String outFilePath = "8icd.pdb.split";
        File outFile = new File(outFilePath);
        
        if(outFile.exists()) {
            try {
                outFile.delete();
            } catch (Exception e) {
                System.err.println("ERROR: Could not delete output file '" + outFilePath + "': '" + e.getMessage() + "'.");
            }
        }
        
    }    
    
    // ************ test cases ***************
    
    /**
     * Tests parsing of resolution from PDB comment line.
     */    
    @org.junit.Test public void testResolutionParsingSpecified() {        
        
        String pdbResLine = "REMARK   2 RESOLUTION.    2.24 ANGSTROMS.";
        
        assertTrue("Resolution not parsed correctly from PDB resolution comment line.", (Main.getResFromREMARK2Line(pdbResLine) == 2.24));
    }
    
    
    /**
     * Tests parsing of resolution from PDB comment line which does not contain a resolution entry.
     */    
    @org.junit.Test public void testResolutionParsingNotSpecified() {        
        
        String pdbResLine = "REMARK   2 RESOLUTION. NOT APPLICABLE.";
        
        Double res = Main.getResFromREMARK2Line(pdbResLine);
        
        assertTrue("Resolution set to wrong value '" + res + "' after parsing PDB resolution comment line which did not contain resolution info (NMR).", (res == -1.0));
    }
    
    
    
    
}

