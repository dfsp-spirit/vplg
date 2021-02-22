package alltests;

/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import junit.framework.TestCase;
import linnottograph.ILinnotParser;
import io.IO;
import linnottograph.LinnotParserRED;
import proteingraphs.SSEGraph;
import proteingraphs.SpatRel;
import proteinstructure.SSE;

/**
 *
 * @author ts
 */
public class TestLinnotParserRED extends TestCase {
    
    private String linnot;
    private String graphType;
    ILinnotParser lnp;
    
    /**
     * Sets up the test environment and object.
     */
    @Override @org.junit.Before public void setUp() {
        linnot = "[h,-1mh,2pe]";
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
        expected.add(2);
        
        assertEquals(expected, dists);
    }
    
    @org.junit.Test public void testVisitPath() {     
        List<Integer> path = lnp.getVisitPath();
        List<Integer> expected = new ArrayList<>();
        expected.add(0);
        expected.add(-1);
        expected.add(1);
        
        assertEquals(expected, path);
    }
    
    @org.junit.Test public void testVisitedVertices() {     
        List<Integer> olist = lnp.getAllVisitedVertices();
        List<Integer> expected = new ArrayList<>();
        expected.add(-1);
        expected.add(0);
        expected.add(1);
        
        assertEquals(expected, olist);
    }
    
    @org.junit.Test public void testNonZEdges() {     
        List<Integer[]> olist = lnp.getNonZEdges();
        List<Integer[]> expected = new ArrayList<>();
        expected.add(new Integer [] {0, -1, SpatRel.MIXED});
        expected.add(new Integer [] {-1, 1, SpatRel.PARALLEL});
        
        //System.out.println(IO.listOfintegerArraysToString(olist));
        
        for(int i = 0; i < expected.size(); i++) {
            Integer[] e = expected.get(i);
            Integer[] f = olist.get(i);
            Assert.assertArrayEquals(e, f);
        }
        assertEquals(expected.size(), olist.size());        
    }
    
    @org.junit.Test public void testMaxShiftLeft() { 
        Integer expected = -1;
        assertEquals(expected, lnp.getMaxShiftLeft());
    }
    
    @org.junit.Test public void testVertexTypesNtoC() { 
        List<String> expected = new ArrayList<>();
        expected.add("h");
        expected.add("h");
        expected.add("e");
        
        assertEquals(expected, lnp.getVertexTypesNtoC());
    }
    
    @org.junit.Test public void testNtoCOfVisitPath() { 
        List<Integer> expected = new ArrayList<>();
        expected.add(1);
        expected.add(0);
        expected.add(2);
        
        assertEquals(expected, lnp.getNtoCPositionsOfVisitPath());
    }            
    
    
    @org.junit.Test public void testOutEdges() { 
        List<Integer[]> edges = lnp.getOutGraphEdges();
        
        List<Integer[]> expected = new ArrayList<>();
        expected.add(new Integer [] {1, 0, SpatRel.MIXED});
        expected.add(new Integer [] {0, 2, SpatRel.PARALLEL});
        
        for(int i = 0; i < expected.size(); i++) {
            Integer[] e = expected.get(i);
            Integer[] f = edges.get(i);
            Assert.assertArrayEquals(e, f);
        }
        assertEquals(expected.size(), edges.size()); 
    }
    
    @org.junit.Test public void testDistancesMakeSense() {
        assertTrue(lnp.distancesMakeSense());
    } 

    
    
}
