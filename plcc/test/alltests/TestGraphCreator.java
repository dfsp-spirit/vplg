package alltests;

import junit.framework.TestCase;
import org.junit.Test;
import plcc.GraphCreator;
import plcc.ProtGraph;

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
    
    @Test public void testGenRandom5V() {
        ProtGraph pg = GraphCreator.createRandom(5, 0.5);
        assertEquals(5, pg.getVertices().size());        
    }
    
}
