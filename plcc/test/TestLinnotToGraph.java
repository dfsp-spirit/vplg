
import graphdrawing.IDrawableEdge;
import graphdrawing.IDrawableGraph;
import graphdrawing.IDrawableVertex;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import linnottograph.LinnotParser;
import plcc.SSEGraph;
import org.junit.Assert;
import junit.framework.TestCase;
import linnottograph.ILinnotToGraph;
import linnottograph.LinnotToGraph;


/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

/**
 *
 * @author spirit
 */
public class TestLinnotToGraph {
    
    private String linnot;
    private String graphType;
    private ILinnotToGraph ltg;
    
    /**
     * Sets up the test environment and object.
     */
     @org.junit.Before public void setUp() {
        linnot = "[h,-1mh,2pe]";
        graphType = SSEGraph.GRAPHTYPE_ALBELIG;
        ltg = new LinnotToGraph(linnot, graphType);
    }
    
     
    @org.junit.Test public void test7timEdges() {             
        List<IDrawableEdge> edges = ltg.getEdges();
        assertEquals(2, edges.size());
    }
    
    @org.junit.Test public void test7timVertices() {             
        List<IDrawableVertex> verts = ltg.getVertices();
        assertEquals(3, verts.size());
    }
    
    @org.junit.Test public void test7timGraph() {             
        IDrawableGraph dg = ltg.getGraph();
    }
    
}
