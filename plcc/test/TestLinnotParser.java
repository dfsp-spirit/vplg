/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.List;
import junit.framework.TestCase;
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
    
    /**
     * Tests whether the SSE is of the correct type.
     */
    @org.junit.Test public void test7timNumSSEs() {     
        String[] tokens = LinnotParser.getTokensFromLinnot(linnot);
        List<String> sseTypes = LinnotParser.getSSETypesFromTokenList(tokens, graphType);
        List<String> contactTypes = LinnotParser.getContactTypesFromTokenList(tokens);
        List<Integer> relDists = LinnotParser.getRelDistsFromTokenList(tokens, graphType);
        
        int numSSEs = tokens.length;
        assertEquals(numSSEs, 3);
    }
    
}
