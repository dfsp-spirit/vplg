/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This class implements an SSE graph drawer. It exists to draw a certain SSEGraph in a specific style and order.
 * It is created from an SSEGraph and consists of a list of DrawSSE objects and information on the edges between them.
 * 
 * All draw functions which are currently part of the SSEGraph class will be moved to this class in the future.
 * 
 * @author ts
 */
public class DrawSSEGraph {
    
    public static final Integer SSEGRAPHSTYLE_SIMPLE = 1;
    public static final Integer SSEGRAPHSTYLE_BIOLOGY = 2;
    
    public static final Integer SSEGRAPHORDER_AASEQUENCE = 1;
    public static final Integer SSEGRAPHORDER_SPATIAL = 2;
    
    
    private PageLayout pl;
    private SSEGraph g;
    private Integer style;
    private Integer order;
    private ArrayList<DrawSSE> drawSSEs;
    

    /**
     * Creates a new DrawSSEGraph and sets up the DrawSSE list in the requested order. Also sets the DrawSSE styles properly.
     * @param g the graph which should be drawn
     * @param sseGraphStyle the drawing style to use. Use one of the DrawSSEGraph.SSEGRAPHSTYLE_* constants.
     * @param sseGraphOrder the vertex ordering to use. Use one of the DrawSSEGraph.SSEGRAPHORDER_* constants.
     * @throws IllegalArgumentException if the requested order is not supported for this graph (e.g., the graph is bifurcated but spatial ordering was requested)
     */
    public DrawSSEGraph(SSEGraph g, Integer sseGraphStyle, Integer sseGraphOrder) throws IllegalArgumentException {
        this.g = g;
        this.pl = new PageLayout(g.size);
        
        if(this.settingsValidForThisGraph(sseGraphStyle, sseGraphOrder)) {
            this.style = sseGraphStyle;
            this.order = sseGraphOrder;
        } else {
            // requested settings invalid, so fall back to save defaults which are supported for all graphs
            this.order = DrawSSEGraph.SSEGRAPHORDER_AASEQUENCE;
            this.style = DrawSSEGraph.SSEGRAPHSTYLE_SIMPLE;
            
            throw new IllegalArgumentException("Requested style and order not possible for this SSE graph, set to defaults.");
        }
        
        
        this.drawSSEs = new ArrayList<DrawSSE>();
        
        if(this.order == DrawSSEGraph.SSEGRAPHORDER_AASEQUENCE) {
            SSE sse;
            for(int i = 0; i < this.g.sseList.size(); i++) {                        
                sse = this.g.sseList.get(i);
                this.drawSSEs.add(new DrawSSE(sse, i, this.pl, this.style));
            }            
            
        } else if(this.order == DrawSSEGraph.SSEGRAPHORDER_SPATIAL) {
            
            Integer seqIndex;
            if(g.hasSpatialOrdering()) {
                for(Integer i = 0; i < g.spatOrder.size(); i++) {
                    seqIndex = g.spatOrder.get(i);
                    this.drawSSEs.add(new DrawSSE(g.getSSEBySeqPosition(seqIndex), i, this.pl, this.style));
                }
            } else {
                //System.err.println("ERROR: DrawSSEGraph(): Graph has no spatial order, which should have been checked before.");
                throw new IllegalArgumentException("Graph has no spatial order.");
            }
            
        } else {
            // should never happen 
            //System.err.println("ERROR: DrawSSEGraph(): Order '" + sseGraphOrder + "' unknown, not adding any SSEs.");
            throw new IllegalArgumentException("Order '" + sseGraphOrder + "' unknown, not adding any SSEs.");
        }
                        
    }
    
    
    /**
     * Checks whether the given settings are possible for the SSEGraph.
     * @param sseGraphStyle the drawing style to use. Use one of the DrawSSEGraph.SSEGRAPHSTYLE_* constants.
     * @param sseGraphOrder the vertex ordering to use. Use one of the DrawSSEGraph.SSEGRAPHORDER_* constants.
     * @return true if the settings are OK, false otherwise
     */
    private Boolean settingsValidForThisGraph(Integer sseGraphStyle, Integer sseGraphOrder) {
        if(sseGraphStyle < 1 || sseGraphStyle > 2) {
            return(false);
        }
        
        if(sseGraphOrder < 1 || sseGraphOrder > 2) {
            return(false);
        }
        
        if(Objects.equals(sseGraphOrder, DrawSSEGraph.SSEGRAPHORDER_SPATIAL)) {
            if( ! this.g.hasSpatialOrdering()) {
                return(false);
            }
        }
        
        return(true);        
    }    
}

