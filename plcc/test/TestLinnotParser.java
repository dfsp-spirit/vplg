/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import junit.framework.TestCase;
import plcc.ILinnotParser;
import plcc.IO;
import plcc.LinnotParser;
import plcc.SSEGraph;
import plcc.SpatRel;

/**
 *
 * @author ts
 */
public class TestLinnotParser extends TestCase {
    
    private String linnot;
    private String graphType;
    
    /**
     * Sets up the test environment and object.
     */
    @Override @org.junit.Before public void setUp() {
        linnot = "[h,-1mh,2pe]";
        graphType = SSEGraph.GRAPHTYPE_ALBELIG;
    }
    
    
    @org.junit.Test public void test7timNumSSEs() {     
        ILinnotParser lnp = new LinnotParser(linnot, graphType);
        int numSSEs = lnp.getNumParsedSSEs();
        
        assertEquals(numSSEs, 3);
    }
    
    @org.junit.Test public void test7timNumEdges() {     
        ILinnotParser lnp = new LinnotParser(linnot, graphType);
        int numEdges = lnp.getNumParsedEdges();
        
        assertEquals(numEdges, 2);
    }
    
    
    @org.junit.Test public void test7timSSETypes() {     
        ILinnotParser lnp = new LinnotParser(linnot, graphType);
        List<String> types = lnp.getSSETypesList();
        
        List<String> expected = new ArrayList<>();
        expected.add(SSEGraph.notationLabelHelix);
        expected.add(SSEGraph.notationLabelHelix);
        expected.add(SSEGraph.notationLabelStrand);
        
        assertEquals(expected, types);
    }
    
    
    @org.junit.Test public void test7timContactTypes() {     
        ILinnotParser lnp = new LinnotParser(linnot, graphType);
        List<String> types = lnp.getContactTypesList();
        
        List<String> expected = new ArrayList<>();
        expected.add("m");
        expected.add("p");
        
        assertEquals(expected, types);
    }
    
    
    @org.junit.Test public void test7timRelDists() {     
        ILinnotParser lnp = new LinnotParser(linnot, graphType);
        List<Integer> dists = lnp.getRelDistList();
        
        List<Integer> expected = new ArrayList<>();
        expected.add(-1);
        expected.add(2);
        
        assertEquals(expected, dists);
    }
    
    @org.junit.Test public void test7timVisitPath() {     
        ILinnotParser lnp = new LinnotParser(linnot, graphType);
        List<Integer> path = lnp.getVisitPath();
        List<Integer> expected = new ArrayList<>();
        expected.add(0);
        expected.add(-1);
        expected.add(1);
        
        assertEquals(expected, path);
    }
    
    @org.junit.Test public void test7timVisitedVertices() {     
        ILinnotParser lnp = new LinnotParser(linnot, graphType);
        List<Integer> olist = lnp.getAllVisitedVertices();
        List<Integer> expected = new ArrayList<>();
        expected.add(-1);
        expected.add(0);
        expected.add(1);
        
        assertEquals(expected, olist);
    }
    
    @org.junit.Test public void testNonZEdges() {     
        ILinnotParser lnp = new LinnotParser(linnot, graphType);
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
    
    @org.junit.Test public void test7timMaxShiftLeft() { 
        ILinnotParser lnp = new LinnotParser(linnot, graphType);
        Integer expected = -1;
        assertEquals(expected, lnp.getMaxShiftLeft());
    }
    
    
}
