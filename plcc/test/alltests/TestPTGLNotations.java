/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package alltests;

import datastructures.SimpleGraphDrawer;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import resultcontainers.PTGLNotationFoldResult;
import proteingraphs.PTGLNotations;
import proteingraphs.ProtGraph;
import proteingraphs.ProtGraphs;

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
