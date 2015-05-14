/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import plcc.ILinnotParser;
import plcc.LinnotParser;
import plcc.SSEGraph;

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
        linnot = "[h,-2h,3e]";
        graphType = SSEGraph.GRAPHTYPE_ALBELIG;
    }
    
    
    @org.junit.Test public void test7timNumSSEs() {     
        ILinnotParser lnp = new LinnotParser(linnot, graphType);
        int numSSEs = lnp.getNumSSEs();
        
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
        
        assertEquals(types, expected);
    }
    
    
    @org.junit.Test public void test7timContactTypes() {     
        ILinnotParser lnp = new LinnotParser(linnot, graphType);
        List<String> types = lnp.getContactTypesList();
        
        List<String> expected = new ArrayList<>();
        expected.add("?");
        expected.add("?");
        expected.add("?");
        
        assertEquals(types, expected);
    }
    
    
    @org.junit.Test public void test7timRelDists() {     
        ILinnotParser lnp = new LinnotParser(linnot, graphType);
        List<Integer> dists = lnp.getRelDistList();
        
        List<Integer> expected = new ArrayList<>();
        expected.add(-2);
        expected.add(3);
        
        assertEquals(dists, expected);
    }
    
}
