/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package alltests;


import graphdrawing.IDrawableGraph;
import org.junit.Assert;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import parsers.ParserTools;
import parsers.SPGFGraphParser;


/**
 *
 * @author spirit
 */
public class TestSPGFGraphParser extends TestCase {
    
    private String spgf;
    private SPGFGraphParser p;
    
    @Override @org.junit.Before public void setUp() {
        
        spgf = "h,e,e,h,h,l;0p1,2a3,3j5";        
        p = new SPGFGraphParser(spgf);        
    }
    
    @org.junit.Test public void testNumEdgesAndVerts() {                     
        assertEquals(6, p.getVertices().size());
        assertEquals(3, p.getEdges().size());        
    }
    
    @org.junit.Test public void testGraphNumEdgesAndVerts() {                     
        IDrawableGraph g = p.getDrawableGraph();
        assertEquals(6, g.getDrawableVertices().size());
        assertEquals(3, g.getDrawableEdges().size());
        
    }
    
    
    
}
