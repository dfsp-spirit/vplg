/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

import java.awt.Color;
import java.awt.Shape;


/**
 * This class implements a drawable SSE. It includes a reference to an SSE and information on where
 * and how to draw it.
 * 
 */
public class DrawSSE {
    
    private SSE sse;
    private Position2D incomingConnectorPos;
    private Position2D outgoingConnectorPos;
    private Shape shape;
    private PageLayout pl;
    private Integer drawStyle;
    
    
    /**
     * The sequential position of this SSE in the drawn graph, i.e., the list of DrawSSEs. This may only be a subset of the list of all SSEs of this graph.
     */
    private Integer indexInDrawnGraph;

    
    /**
     * Constructor.
     * @param sse the SSE object represented by this drawSSE
     * @param indexInDrawGraph the index of the SSE in the SSE list of the current graph
     * @param pl the page layout that is used for drawing. This allows this drawSSE to determine its position etc.
     * @param drawType the style in which to draw the SSE. See the documentation of the setDrawType() function for details.
     */
    DrawSSE(SSE sse, Integer indexInDrawGraph, PageLayout pl, Integer drawType) {
        this.sse = sse;
        this.indexInDrawnGraph = indexInDrawGraph;                
        this.pl = pl;
        this.drawStyle = drawType;
        
        // TODO: determine the positions here
    }
    
    /**
     * Allows the user to choose in which style this SSE should be drawn. Currently, the supported values
     * are "CIRCLE" for classic protein graph notation (vertices as circles) and "ARROW" for the notation 
     * used for the KEY description.
     * @param drawType the draw style, use one of the DrawSSEGraph.SSEGRAPHSTYLE_* constants.
     */
    public void setDrawStyle(Integer drawType) {       
        this.drawStyle = drawType;
    }
    
    
    /**
     * Determines the drawing position where the incoming connector should end.
     */         
    public Position2D getIncConnectorPos() {
        return(this.incomingConnectorPos);
    }
    
    
    /**
     * Determines the drawing position where the outgoing connector should start.
     */         
    public Position2D getOutConnectorPos() {
        return(this.outgoingConnectorPos);
    }
    
    
    /**
     * Returns a java.awt.Color depending on the SSE type. This color should be used to draw this SSE.
     * @return the Color for this SSE
     */
    public Color getColor() {
        
        if(this.sse.isHelix()) {
            return(Color.RED);
        }
        else if(this.sse.isBetaStrand()) {
            return(Color.BLACK);
        }
        else if(this.sse.isLigandSSE()) {
            return(Color.BLACK);
        }
        else if(this.sse.isOtherSSE()) {
            return(Color.GRAY);
        }
        else{
            return(Color.GRAY);
        }
        
    }
    
    
    /**
     * Determines the sequential position of this SSE in the whole AA sequence of the protein chain.
     * @return The sequential position of this SSE in the whole AA sequence of the protein chain.
     */
    public Integer getSeqPosInSequence() {
        return(this.sse.getSSESeqChainNum());
    }
    
    
    
    
    
    
}
