/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package alltests;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import linnottograph.ILinnotParser;
import linnottograph.LinnotParserRED;
import proteingraphs.SSEGraph;

/**
 *
 * @author spirit
 */
public class TestLinnotParserADJ extends TestCase {
    
    private String linnot;
    private String graphType;
    ILinnotParser lnp;
    
    /**
     * Sets up the test environment and object.
     */
    @Override @org.junit.Before public void setUp() {
        linnot = "[h,-1mh,3pe]";
        graphType = SSEGraph.GRAPHTYPE_ALBELIG;
        lnp = new LinnotParserRED(linnot, graphType);
    }
    
    
    @org.junit.Test public void testNumSSEs() {     
        
        int numSSEs = lnp.getNumParsedSSEs();        
        assertEquals(numSSEs, 3);
    }
    
    @org.junit.Test public void testNumEdges() {     
        int numEdges = lnp.getNumParsedEdges();        
        assertEquals(numEdges, 2);
    }
    
    
    @org.junit.Test public void testSSETypes() {     
        List<String> types = lnp.getSSETypesList();
        
        List<String> expected = new ArrayList<>();
        expected.add(SSEGraph.notationLabelHelix);
        expected.add(SSEGraph.notationLabelHelix);
        expected.add(SSEGraph.notationLabelStrand);
        
        assertEquals(expected, types);
    }
    
    
    @org.junit.Test public void testContactTypes() {     
        List<String> types = lnp.getContactTypesList();
        
        List<String> expected = new ArrayList<>();
        expected.add("m");
        expected.add("p");
        
        assertEquals(expected, types);
    }
    
    
    @org.junit.Test public void testRelDists() {     
        List<Integer> dists = lnp.getRelDistList();
        
        List<Integer> expected = new ArrayList<>();
        expected.add(-1);
        expected.add(3);
        
        assertEquals(expected, dists);
    }
    
    @org.junit.Test public void testDistancesMakeSense() {
        assertTrue(lnp.distancesMakeSense());  // fails atm: they do not make sense for ADJ yet, we need to add the missing vertices from the PG for them to make sense
    }
}
