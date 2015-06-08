/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package alltests;

import datastructures.SimpleGraphDrawer;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import plcc.PTGLNotationFoldResult;
import plcc.PTGLNotations;
import plcc.ProtGraph;
import plcc.ProtGraphs;

/**
 *
 * @author spirit
 */
public class TestPTGLNotations extends TestCase {
    
    ProtGraph g;
    PTGLNotations p;
    List<PTGLNotationFoldResult> results;
    
    @Before @Override public void setUp() {
        g = ProtGraphs.generate_7tim_A_albe();
        p = new PTGLNotations(g);
        results = p.getResults();
    }
    
    @Test public void testResultSize() {
        assertEquals(10, results.size());
        
    }
    
}
