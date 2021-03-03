package alltests;

import junit.framework.TestCase;
import proteingraphs.GraphCreator;
import proteingraphs.SSEGraph;
import org.junit.Test;
import proteingraphs.GraphCreator;
import proteingraphs.ProtGraph;


/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

/**
 *
 * @author ts
 */
public class TestGraphCreator extends TestCase {
    

    int numVertices;
    double edgeProb;
    SSEGraph g;
        
        
    @Override @org.junit.Before public void setUp() {
        numVertices = 30;
        edgeProb = 0.05;
        g = GraphCreator.createRandom(numVertices, edgeProb);    
    }
    
    @org.junit.Test public void testNumVerts() { 
        Integer expected = 30;
        assertEquals(expected, g.getSize());
    }
    

    @Test public void testGenRandom5V() {
        ProtGraph pg = GraphCreator.createRandom(5, 0.5);
        assertEquals(5, pg.getVertices().size());        
    }

}
