/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package alltests;


import graphdrawing.IDrawableGraph;
import org.junit.Assert;
import junit.framework.TestCase;
import linnottograph.LinnotParserRED;
import parsers.GMLGraphParser;
import parsers.IGraphParser;
import parsers.ParserTools;
import proteingraphs.SSEGraph;


/**
 *
 * @author spirit
 */
public class TestGMLGraphParser extends TestCase {
    
    private String gml;
    private GMLGraphParser p;
    
    @Override @org.junit.Before public void setUp() {
        
        gml = "graph [\n";
        gml += "label \"VPLG Protein Graph 1o1d-F-albe[10,5]\"\n";
        gml += "id 0\n";
        gml += "  node [\n";
        gml += "    id 0\n";
        gml += "    label \"v0\"\n";
        gml += "  ]\n";
        gml += "  node [\n";
        gml += "    id 1\n";
        gml += "    label \"v1\"\n";
        gml += "  ]\n";
        gml += "  node [\n";
        gml += "    id 2\n";
        gml += "    label \"v2\"\n";
        gml += "  ]\n";
        gml += "  edge [\n";
        gml += "    source 0\n";
        gml += "    target 1\n";
        gml += "    label \"e(0, 1)\"\n";
        gml += "  ]\n";
        gml += "  edge [\n";
        gml += "    source 0\n";
        gml += "    target 2\n";
        gml += "    label \"e(0, 2)\"\n";
        gml += "  ]\n";
        gml += "  edge [\n";
        gml += "    source 1\n";
        gml += "    target 2\n";
        gml += "    label \"e(1, 2)\"\n";
        gml += "  ]\n";
        gml += "]\n";
        
        p = new GMLGraphParser(gml);        
    }
    
    @org.junit.Test public void testNumEdgesAndVerts() {                     
        assertEquals(3, p.getEdges().size());
        assertEquals(3, p.getVertices().size());
    }
    
    @org.junit.Test public void testGraphNumEdgesAndVerts() {                     
        IDrawableGraph g = p.getDrawableGraph();
        System.out.println("Received graph with " + g.getDrawableVertices().size() + " vertices and " + g.getDrawableEdges().size() + " edges.");
        assertEquals(3, g.getDrawableEdges().size());
        assertEquals(3, g.getDrawableVertices().size());
    }
    
    
    
    
    
}
