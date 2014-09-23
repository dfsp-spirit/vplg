/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package plcc;

//import com.google.gson.Gson;
import algorithms.CompatGraphComputation;
import algorithms.TreeNodeData;
import datastructures.Graph;
import datastructures.SimpleAttributedGraphAdapter;
import datastructures.SimpleGraphAdapter;
import datastructures.SimpleGraphInterface;
import datastructures.UndirectedGraph;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import jgrapht.PLGEdge;
import jgrapht.ProteinLigandGraph;
import jgrapht.VertexSSE;

import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import plcc.DrawTools.IMAGEFORMAT;
import tools.DP;


/**
 * This is an abstract SSEGraph. Both the ProtGraph and the FoldingGraph classes extend this
 * class and are non-abstract. It will be used later when PGs are separated from FGs in the code.
 * This class should hold all functions which they share.
 * 
 * 
 * @author spirit
 */
public abstract class SSEGraph extends SimpleAttributedGraphAdapter implements VPLGGraphFormat, GraphModellingLanguageFormat, TrivialGraphFormat, DOTLanguageFormat, KavoshFormat, SimpleGraphInterface {
    
    /** the list of all SSEs of this graph */
    protected ArrayList<SSE> sseList;
    
    /** Contains the number of the last SSE which is part of a certain chain*/
    protected ArrayList<Integer> chainEnd = new ArrayList<Integer>();
    
    /** Contains a list of all chains*/
    protected ArrayList<Chain> allChains = new ArrayList<Chain>();
    
    /** The size of this graph, i.e., the number of vertices in it. */
    protected Integer size = null;   
    
    /** The edge matrix defining contacts and the type of spatial relation. */
    protected Integer[ ][ ] matrix;               // contacts and spatial relations between pairs of SSEs
    
    /** The matrix holding distances of vertices in the graph (shortest paths between them). */
    protected Integer[ ][ ] distMatrix;           // distances of the vertices within this graph
    protected Boolean isProteinGraph;             // true if this is a protein graph, false if this is a folding graph (a connected component of the protein graph)    
    protected SSEGraph parent;
       
    protected Boolean connectedComponentsComputed;
    
    protected Boolean distancesCalculated;
    /** The connected components of this graph, i.e., the RED/KEY folding graphs. */
    protected ArrayList<FoldingGraph> connectedComponents;
    
    
    protected String pdbid;                               // the PDB ID this graph represents, e.g. "3kmf"
    protected String chainid;                             // the chain ID in the PDB file, e.g. "A"
    protected String graphType;                           // the graph type, e.g. "albe"
    
    protected HashMap<String, String> metadata;
    
    protected Integer numCliquesSoFar;
    protected Boolean reportCliques;
    
    /** the list of all SSEs of this graph which should be drawn. Not used yet. */
    protected ArrayList<DrawSSE> sseDrawList;
    
    /** This is a list of the vertices (defined by their sequential index) in this graph in spatial ordering. This means that spatOrder.get(i) returns the
        sequential index of the SSE which is at position i in the graph if it is drawn in spatial ordering. */
    protected ArrayList<Integer> spatOrder;
    
    protected ArrayList<ArrayList<Integer>> adjLists;
    protected ArrayList<Set<Integer>> cliques;    // for Bron-Kerbosch algorithm
    
    // labels for SSEs in the linear notation strings
    public static final String notationLabelHelix = "h";
    public static final String notationLabelStrand = "e";
    public static final String notationLabelLigand = "l";
    public static final String notationLabelOther = "o";
    
    // graph types
    public static final String GRAPHTYPE_ALPHA = "alpha";
    public static final String GRAPHTYPE_ALPHALIG = "alphalig";
    public static final String GRAPHTYPE_BETA = "beta";
    public static final String GRAPHTYPE_BETALIG = "betalig";
    public static final String GRAPHTYPE_ALBE = "albe";
    public static final String GRAPHTYPE_ALBELIG = "albelig";
        
    public static final Integer GRAPHTYPE_INT_ALPHA = 1;
    public static final Integer GRAPHTYPE_INT_BETA = 2;
    public static final Integer GRAPHTYPE_INT_ALBE = 3;
    public static final Integer GRAPHTYPE_INT_ALPHALIG = 4;    
    public static final Integer GRAPHTYPE_INT_BETALIG = 5;    
    public static final Integer GRAPHTYPE_INT_ALBELIG = 6;
    
    
    
    /**
     * Constructor. Requires a list of SSEs that will be represented by the vertices of the graph.
     * @param sses a list of SSEs which make up this folding graph. The contacts have to be added later (or there will be none).
     */
    SSEGraph(ArrayList<SSE> sses) {
        super();
        this.sseList = sses;
        this.sseDrawList = new ArrayList<DrawSSE>();
        this.size = sseList.size();
        this.matrix = new Integer[size][size];
        this.distMatrix = new Integer[size][size];      // distances in graph
        this.isProteinGraph = true;
        this.parent = null;
        this.connectedComponents = new ArrayList<FoldingGraph>();
        
        this.connectedComponentsComputed = false;
        
        
        this.distancesCalculated = false;

        adjLists = new ArrayList<ArrayList<Integer>>();
        // add one ArrayList for each SSE
        for (Integer i = 0; i < sseList.size(); i++) {
            adjLists.add(new ArrayList<Integer>());
            sseList.get(i).setSeqIndexInGraph(i);
        }

        this.metadata = new HashMap<String, String>();
        this.init();
        this.spatOrder = null;
        this.reportCliques = true;  // TODO: move to settings               
    }
    
    /**
     * Computes sequential distance (N to C) of the vertices at the given indices. Since the vertex list is ordered N to C, this is rather trivial.
     * @param i the index of vert 1
     * @param j the index of vert 2
     * @return sequential distance between the vertices, i.e., Math.abs(i-j)
     */
    public Integer computeDistanceSequentialByIndices(Integer i, Integer j) {
        return Math.abs(i - j);
    }
    
    /**
     * Checks whether the SSE at index 'sseIndex' is the SSE closest to the N terminus in this graph.
     * (This is the case if it is at sequential index 0.)
     * @param sseIndex the sequential index of the SSE
     * @return true if it is the SSE closest to the N terminus in this graph, false otherwise.
     */
    public Boolean isClosestToN(Integer sseIndex) {
        return(sseIndex == 0);
    }
    
    /**
     * Checks whether the SSE at index 'sseIndex' is the SSE closest to the C terminus in this graph.
     * (This is the case if it is at the last index.)
     * @param sseIndex the sequential index of the SSE
     * @return true if it is the SSE closest to the N terminus in this graph, false otherwise.
     */
    public Boolean isClosestToC(Integer sseIndex) {
        return(sseIndex == (this.size - 1));
    }
    
    
    
    /**
     * Determines whether this graph contains at least one SSE of type helix.
     * @return true if it does, false otherwise
     */
    public Boolean containsSSETypeHelix() {
       for(SSE s : this.sseList) {
           if(s.isHelix()) {
               return(true);
           }
       } 
       return(false);
    }
    
    
    /**
     * Determines whether this graph contains at least one SSE of type beta strand.
     * @return true if it does, false otherwise
     */
    public Boolean containsSSETypeBetaStrand() {
       for(SSE s : this.sseList) {
           if(s.isBetaStrand()) {
               return(true);
           }
       } 
       return(false);
    }
    
    
    /**
     * Determines whether this graph contains at least one SSE of type ligand.
     * @return true if it does, false otherwise
     */
    public Boolean containsSSETypeLigand() {
       for(SSE s : this.sseList) {
           if(s.isLigandSSE()) {
               return(true);
           }
       } 
       return(false);
    }
    
    
    /**
     * Determines whether this graph contains at least one SSE of type other.
     * @return true if it does, false otherwise
     */
    public Boolean containsSSETypeOther() {
       for(SSE s : this.sseList) {
           if(s.isOtherSSE()) {
               return(true);
           }
       } 
       return(false);
    }
    
    
    /**
     * Determines the number of isolated ligands in this graph. Isolated ligands are ligand SSEs which have
     * no neighbors, i.e., their degree is 0.
     * @return the number of isolated ligands in this graph
     */
    public Integer numIsolatedLigands() {        
        Integer numIsolatedLigands = 0;
        SSE s;
        for(int i = 0; i < this.sseList.size(); i++) {            
            s = this.sseList.get(i);
            if(s.isLigandSSE() && this.degreeOfVertex(i) == 0 ) {
                numIsolatedLigands++;
            }
        }
        return numIsolatedLigands;
    }
    
    
    
    /**
     * Computes the spatial contact matrix for this graph from the sequential contact matrix and the spatial ordering.
     * Will try to compute the spatial ordering using the computeSpatialVertexOrdering() function if it is null.
     * @return the spatial contact matrix or a matrix of length[0][0] if no spatial ordering exists that can be used to compute such a matrix.
     */
    public Integer[][] getSpatialContactMatrix() {

        Integer[][] noSuchMatrix = new Integer[0][0];
        
        if(this.spatOrder == null) {
            this.computeSpatialVertexOrdering();
        }
        
        if(spatOrder.size() != this.size) {
            return(noSuchMatrix);
        }
        
        Integer[][] spatContacts = new Integer[this.size][this.size];
        
        // init is not required because all fields are set in the next loop anyways
        /*
        for(Integer i = 0; i < this.size; i++) {
            for(Integer j = 0; j < this.size; j++) {
                spatContacts[i][j] = SpatRel.NONE;
            }
        }
         * 
         */
        
        // fill
        Integer sseASeqIndex, sseBSeqIndex;
        for(Integer i = 0; i < this.size; i++) {
            sseASeqIndex = this.spatOrder.get(i);
            for(Integer j = 0; j < this.size; j++) {
                sseBSeqIndex = this.spatOrder.get(j);
                spatContacts[i][j] = this.getContactType(sseASeqIndex, sseBSeqIndex);
            }
        }
        
        return(spatContacts);
    }
    
    
    /**
     * Determines whether this graph contains at least one edge of type parallel.
     * @return true if it does, false otherwise
     */
    public Boolean containsContactTypeParallel() {
       for(Integer i = 0; i < size; i++) {
           for(Integer j = i+1; j < size; j++) {
               if(matrix[i][j] == SpatRel.PARALLEL) {
                   return(true);
               }
           }           
       } 
       return(false);
    }
    
    
    /**
     * Determines whether this graph contains at least one edge of type antiparallel.
     * @return true if it does, false otherwise
     */
    public Boolean containsContactTypeAntiparallel() {
       for(Integer i = 0; i < size; i++) {
           for(Integer j = i+1; j < size; j++) {
               if(matrix[i][j] == SpatRel.ANTIPARALLEL) {
                   return(true);
               }
           }           
       } 
       return(false);
    }
    
    
    /**
     * Returns the number of contacts of a certain type.
     * @param type the contact type, use of the constants in SpatRel, e.g., SpatRel.ANTIPARALLEL
     * @return the number of contacts of the requested type in the graph
     */
    public Integer numContacts(Integer type) {
        
        Integer num = 0;
        
        for(Integer i = 0; i < size; i++) {
           for(Integer j = i+1; j < size; j++) {
               if(matrix[i][j] == type) {
                   num++;
               }
           }           
        }
        return(num);
    }
    
    
    /**
     * Determines whether this graph contains at least one edge of type parallel.
     * @return true if it does, false otherwise
     */
    public Boolean containsContactTypeMixed() {
       for(Integer i = 0; i < size; i++) {
           for(Integer j = i+1; j < size; j++) {
               if(matrix[i][j] == SpatRel.MIXED) {
                   return(true);
               }
           }           
       } 
       return(false);
    }
    
    
    /**
     * Determines whether this graph contains at least one edge of type parallel.
     * @return true if it does, false otherwise
     */
    public Boolean containsContactTypeLigand() {
       for(Integer i = 0; i < size; i++) {
           for(Integer j = i+1; j < size; j++) {
               if(matrix[i][j] == SpatRel.LIGAND) {
                   return(true);
               }
           }           
       } 
       return(false);
    }
    
    
    /**
     * Returns the distance of the SSE pair (a, b) in the primary structure when only SSEs included in this graph
     * are considered. This is the length of a path between these two SSEs in a graph which contains edges
     * between consecutive SSEs *only*. Note that this number considers the direction and may thus be negative.
     * 
     * @param sseIndexA the index of the first SSE in the SSE list of this graph
     * @param sseIndexB the index of the second SSE in the SSE list of this graph
     * @return the path length between the SSEs, considers the direction and may thus be negative. For example, the
     * distance between the SSEs with indices (3, 5) is 2 and the distance of the pair (5, 3) is -2.
     * (Think of this distance in the graph-theoretic sense, you need the 2 edges [3=>4] and [4=>5] to get
     * from 3 to 5.)
     */
    public Integer getSeqGraphSSEPairDistanceByIndices(Integer sseIndexA, Integer sseIndexB) {
        if(sseIndexA == sseIndexB) { return(0); }
        
        return(sseIndexB - sseIndexA);
    }

    
    /**
     * Returns the distance of the SSE pair (a, b) in the primary structure when only SSEs included in this graph
     * are considered. This is the length of a path between these two SSEs in a graph which contains edges
     * between consecutive SSEs *only*. Note that this number considers the direction and may thus be negative.
     * 
     * @param a the first SSE
     * @param b the second SSE
     * @return the path length between the SSEs, considers the direction and may thus be negative. For example, the
     * distance between the SSEs with indices (3, 5) is 2 and the distance of the pair (5, 3) is -2.
     * (Think of this distance in the graph-theoretic sense, you need the 2 edges [3=>4] and [4=>5] to get
     * from 3 to 5.)
     */
    public Integer getSeqGraphSSEPairDistanceBySSEs(SSE a, SSE b) {
        if(a.sameSSEas(b)) { return(0); }
        
        
        Integer aIndex = a.getSeqIndexInGraph();
        Integer bIndex = b.getSeqIndexInGraph();
        
        if(aIndex < 0 || bIndex < 0) {
            System.err.println("ERROR: getSeqGraphSSEPairDistanceBySSEs(): Cannot determine distance between SSE pair, SSE(s) without index info.");
            System.exit(1);
        }
        
        return(aIndex - bIndex);
    }
            
        
    /**
     * Draws the symbol for a beta strand at the given position.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position where to draw
     * @param pl the PageLayout to use (determines the width and height)
     */
    protected void drawSymbolBetaStrand(SVGGraphics2D ig2, Position2D startPos, PageLayout pl) {
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        Rectangle2D.Double rect = new Rectangle2D.Double(startPos.x, startPos.y, pl.getVertDiameter(), pl.getVertDiameter());
        ig2.fill(rect);        
    }
    
    
    /**
     * Draws the symbol for an alpha helix at the given position.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position where to draw
     * @param pl the PageLayout to use (determines the width and height)
     */
    protected void drawSymbolAlphaHelix(SVGGraphics2D ig2, Position2D startPos, PageLayout pl) {
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.RED);
        Ellipse2D.Double circle = new Ellipse2D.Double(startPos.x, startPos.y, pl.getVertDiameter(), pl.getVertDiameter());
        ig2.fill(circle);
    }
    
    
    /**
     * Draws the symbol for a ligand at the given position.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position where to draw
     * @param pl the PageLayout to use (determines the width and height)
     */
    protected void drawSymbolLigand(SVGGraphics2D ig2, Position2D startPos, PageLayout pl) {
        ig2.setStroke(new BasicStroke(3));
        ig2.setPaint(Color.MAGENTA);
        Ellipse2D.Double circle = new Ellipse2D.Double(startPos.x, startPos.y, pl.getVertDiameter(), pl.getVertDiameter());
        ig2.draw(circle);
        ig2.setStroke(new BasicStroke(2));
    }
    
    
    /**
     * Draws the symbol for SSEs of type other at the given position.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position where to draw
     * @param pl the PageLayout to use (determines the width and height)
     */
    protected void drawSymbolOtherSSE(SVGGraphics2D ig2, Position2D startPos, PageLayout pl) {
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.GRAY);
        Ellipse2D.Double circle = new Ellipse2D.Double(startPos.x, startPos.y, pl.getVertDiameter(), pl.getVertDiameter());
        ig2.fill(circle);
    }
    
    
    
    
    /**
     * Draws the legend for the graph at the given position. This legend is not suitable for SEQ folding graphs, because their edges are different.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position (x, y) where to start drawing
     * @return the x coordinate in the image where the legend ends (which is the left margin + the legend width). 
     * This can be used to determine the minimal width of the total image (it has to be at least this value).
     */
    public static Integer drawLegend(SVGGraphics2D ig2, Position2D startPos, PageLayout pl, SSEGraph g) {
        
        Boolean drawAll = Settings.getBoolean("plcc_B_graphimg_legend_always_all");
        
        // prepare stuff
        ig2.setFont(pl.getLegendFont());
        FontMetrics fontMetrics = ig2.getFontMetrics();
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        
        Integer spacer = 10;
        Integer pixelPosX = startPos.x;
        Integer vertWidth = pl.getVertDiameter();
        Integer vertOffset = pl.getVertDiameter() / 4 * 3;
        String label;
        
        // Edges label
        if(g.numEdges() > 0) {
            label = "[Edges: ";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;        
        }
        
        // now go through relative orientation texts and colors        
        if(g.containsContactTypeParallel() || drawAll) {
            label = "parallel";
            ig2.setPaint(Color.RED);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.containsContactTypeAntiparallel() || drawAll) {
            label = "antiparallel";
            ig2.setPaint(Color.BLUE);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.containsContactTypeMixed() || drawAll) {
            label = "mixed";
            ig2.setPaint(Color.GREEN);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.containsContactTypeLigand() || drawAll) {
            label = "ligand";
            ig2.setPaint(Color.MAGENTA);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.chainEnd.size()>0 || drawAll) {
            label = "interchain";
            ig2.setPaint(Color.PINK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        // End of edges label
        if(g.numEdges() > 0) {
            label = "]";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        // Vertices label
        if(g.numVertices() > 0) {
            label = " [Vertices: ";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;        
        }
        
        // ok, now draw the SSE symbols     
        if(g.containsSSETypeHelix() || drawAll) {
            g.drawSymbolAlphaHelix(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "helix";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.containsSSETypeBetaStrand() || drawAll) {
            g.drawSymbolBetaStrand(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "strand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.containsSSETypeLigand() || drawAll) {
            g.drawSymbolLigand(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "ligand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.containsSSETypeOther() || drawAll) {
            g.drawSymbolOtherSSE(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "other";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;        
        }
        
        // end label
        if(g.numVertices() > 0) {
            label = "]";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;    
        }
        
        return(pixelPosX);
    }
    
    /**
     * Draws the SEQ legend for the graph at the given position. This legend is not suitable for other folding graph notations but SEQ, because their edges are different.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position (x, y) where to start drawing
     * @return the x coordinate in the image where the legend ends (which is the left margin + the legend width). 
     * This can be used to determine the minimal width of the total image (it has to be at least this value).
     */
    public static Integer drawLegendSEQ(SVGGraphics2D ig2, Position2D startPos, PageLayout pl, SSEGraph g) {
        
        Boolean drawAll = Settings.getBoolean("plcc_B_graphimg_legend_always_all");
        
        // prepare stuff
        ig2.setFont(pl.getLegendFont());
        FontMetrics fontMetrics = ig2.getFontMetrics();
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        
        Integer spacer = 10;
        Integer pixelPosX = startPos.x;
        Integer vertWidth = pl.getVertDiameter();
        Integer vertOffset = pl.getVertDiameter() / 4 * 3;
        String label;
        
        // Edges label        
        label = "[Edges: ";
        ig2.setPaint(Color.BLACK);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;        
        
        
        label = "seq_distance";
        ig2.setPaint(Color.BLACK);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;
                                        
        
        // End of edge labels        
        label = "]";
        ig2.setPaint(Color.BLACK);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;
        
        
        // Vertices label
        if(g.numVertices() > 0) {
            label = " [Vertices: ";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;        
        }
        
        // ok, now draw the SSE symbols     
        if(g.containsSSETypeHelix() || drawAll) {
            g.drawSymbolAlphaHelix(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "helix";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.containsSSETypeBetaStrand() || drawAll) {
            g.drawSymbolBetaStrand(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "strand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.containsSSETypeLigand() || drawAll) {
            g.drawSymbolLigand(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "ligand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.containsSSETypeOther() || drawAll) {
            g.drawSymbolOtherSSE(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "other";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;        
        }
        
        // end label
        if(g.numVertices() > 0) {
            label = "]";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;    
        }
        
        return(pixelPosX);
    }
    
    
    /**
     * Draws the KEY legend for the graph at the given position. This legend is not suitable for other folding graph notations but KEY, because their edges and SSE symbols are different.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position (x, y) where to start drawing
     * @return the x coordinate in the image where the legend ends (which is the left margin + the legend width). 
     * This can be used to determine the minimal width of the total image (it has to be at least this value).
     */
    public static Integer drawLegendKEY(SVGGraphics2D ig2, Position2D startPos, PageLayout pl, SSEGraph g) {
        
        Boolean drawAll = Settings.getBoolean("plcc_B_graphimg_legend_always_all");
        
        // prepare stuff
        ig2.setFont(pl.getLegendFont());
        FontMetrics fontMetrics = ig2.getFontMetrics();
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        
        Integer spacer = 10;
        Integer pixelPosX = startPos.x;
        Integer vertWidth = pl.getVertDiameter();
        Integer vertOffset = pl.getVertDiameter() / 4 * 3;
        String label;
        
        // Edges label        
        label = "[Edges: ";
        ig2.setPaint(Color.BLACK);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;        
        
        
        label = "seq_distance";
        ig2.setPaint(Color.BLACK);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;
                                        
        
        // End of edge labels        
        label = "]";
        ig2.setPaint(Color.BLACK);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;
        
        
        // Vertices label
        if(g.numVertices() > 0) {
            label = " [Vertices: ";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;        
        }
        
        // ok, now draw the SSE symbols     
        if(g.containsSSETypeHelix() || drawAll) {
            g.drawSymbolAlphaHelix(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "helix";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.containsSSETypeBetaStrand() || drawAll) {
            g.drawSymbolBetaStrand(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "strand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.containsSSETypeLigand() || drawAll) {
            g.drawSymbolLigand(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "ligand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        
        if(g.containsSSETypeOther() || drawAll) {
            g.drawSymbolOtherSSE(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "other";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;        
        }
        
        // end label
        if(g.numVertices() > 0) {
            label = "]";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;    
        }
        
        return(pixelPosX);
    }
    
        
    
    
    /**
     * Determines the spatial vertex ordering of this folding graph. This only makes sense for Folding graphs which are not bifurcated and have at least 2 vertices of degree 1 (the first and the last). Since folding graphs are by definition connected, each vertex
     * has *at least* one neighbor.
     * 
     * In detail, this order can be described as follows: the first vertex in the list is the vertex of degree 1 which is closest to the N terminus in the amino acid sequence. Note that the N terminus itself may
     * not be part of this folding graph, which is no problem though. If no such vertex exists, the order does not exist.
     * The next vertex is the neighbor of this vertex in the folding graph (i.e., as SSE that this SSE is in contact with in 3D) and so on.
     * Note that this requires that all vertices (except for the first and last one) have exactly 2 neighbors. This cannot be the case if the graph is bifurcated (but the inverse does not hold, i.e., non-bifurcated graphs do not necessarily have a valid KEY ordering).
     * 
     * Note: To see this in action, you can use the beta graph of 1blr, chain A.
     * 
     * @return a list containing the seq. vertex indices in the requested order, or an empty list if no such order exists.
     * The list { 1, 0, 2, 3 } is valid and means that SSE #1 (the second SSE in sequential order) is the SSE with only one neighbor which
     * is closest to the N-terminus. It has one 3D contact to SSE #0, which has a 3D contact to #2, which has a 3D contact with #3. Note
     * that #1 and #3 both have 1 3D-neighbor, while #0 and #2 each have two. (The list does not start with SSE #0 because it has 2 3D-neighbors and thus cannot be the start, even though it is closer to the N-terminus than SSE#1.)
     */
    public ArrayList<Integer> getSpatialOrderingOfVertexIndices() {
        ArrayList<Integer> spatialOrder = new ArrayList<Integer>();
        
        if(this.isBifurcated()) {
            //System.err.println("WARNING: getSpatialOrderingOfVertexIndices(): Called for bipartite graph, ignoring.");
            return(spatialOrder);
        }
        
        if(this.numSSEContacts() != (this.numVertices() - 1)) {
            //System.err.println("WARNING: getSpatialOrderingOfVertexIndices(): Graph cannot be linear: number of edges != number of vertices -1.");
            return(spatialOrder);
        }
        
        //if(! this.distancesCalculated) {
        //    this.calculateDistancesWithinGraph();
        //}
        
        Integer start = this.closestToNTerminusDegree1();
        Boolean noVertexWithDegree1 = false;
        //System.out.println("Start vertex of " + this.toString() + " is " + start + ".");     
        if(start < 0) {
            //System.err.println("WARNING: getSpatialOrderingOfVertexIndices(): No vertex of degree 1 found, order not possible.");
            // No vertex found so this notation is not posible, return an empty ArrayList
            start = this.closestToNTerminus();
            if(start < 0) {
                // if no vertex with degree 1 exists, we do (by definition) use the vertex closest to the N terminus
                return(spatialOrder);
            }
            else {
                noVertexWithDegree1 = true;
            }
        } else {
            spatialOrder.add(start);
        }         

        Integer next;
        Integer last = start;
        Integer cur = ( noVertexWithDegree1 ? this.getVertexNeighborClosestToNTerminus(start) : this.getSingleNeighborOf(start) );
        //System.out.println("Single neighbor of " + start + " is " + cur + ".");
        while(cur >= 0 && cur != start) {
            spatialOrder.add(cur);            
            next = this.getVertexNeighborBut(cur, last);
            last = cur;
            cur = next;
        }

        //assert spatOrder.size() == this.size : "ERROR: ProtGraph.getSpatialOrderingOfVertexIndices(): Length of spatial order array does not match number of graph vertices." ;

        // DEBUG
        //System.out.println("Spatial ordering of size " + spatOrder.size() + " found for graph with " + this.size + " vertices.");
        

        return(spatialOrder);
    }
    
    
    /**
     * This function determines whether the vertex at index 'parentIndex' in the SSE list of the parent protein graph is part of this folding graph (which is only one of the connected components of its parent).
     * WARNING: This function only makes sense for folding graphs (not protein graphs) and calling it from a protein graph is a critical error. Check this first using the isFoldingGraph() function.
     * @param parentIndex the index of the vertex in question in the SSE list of the parent protein graph
     * @return true if the vertex occurs in this folding graph, false otherwise
     */
    public Boolean parentVertexIsPartOfThisFoldingGraph(Integer parentIndex) {
        
        if(this.isProteinGraph()) {
            System.err.println("ERROR: parentVertexIsPartOfThisFoldingGraph(): this is folding graph only function which is not supported for protein graphs.");
            System.exit(1);
        }
                      
       for(SSE s : this.sseList) {
           if(this.parent.getSSEBySeqPosition(parentIndex).sameSSEas(s)) {
               return(true);
           }
       }
              
       return(false);
    }
    
    
    /**
     * Returns the metadata of this graph.
     * @return the meta data hashmap.
     */
    public HashMap<String, String> getMetadata() {
        return(this.metadata);
    }

    
    /**
     * Determines whether this vertex is isolated, i.e., it has no neighbors and thus the degree of the vertex is 0.
     * @return true if the vertex is isolated, false otherwise
     */ 
    public Boolean isIsolatedVertex(Integer vertPosition) {
        if(this.degreeOfVertex(vertPosition) == 0) {
            return(true);
        }
        else {
            return(false);
        }
    }
    
    
        
    /**
     * Determines the distance from vertex #x to vertex #y in the graph. This is the spatial distance.
     * @param x the index of the first SSE in the SSE list
     * @param y the index of the second SSE in the SSE list
     * @return The length of the shortest path between the vertices at indices x and y.
     */
    public Integer getSpatialDistance(Integer x, Integer y) {
        Integer [] distAllToX = pathDistanceAllVerts(x);
        return(distAllToX[y]);
    }


    
    /**
     * Returns whether an edge exists between vertices x and y.
     * @param x the index if the first SSE in the SSE list
     * @param y the index if the second SSE in the SSE list
     */
    public Boolean containsEdge(Integer x, Integer y) {
        return(sseContactExistsPos(x, y));
    }

    /**
     * Returns whether an edge between vertices x and y exists.
     * @param x the index if the first SSE in the SSE list
     * @param y the index if the second SSE in the SSE list
     * @return true if a contact exists, false if not (or if the SSEs don't exist)
     */
    public Boolean sseContactExistsPos(Integer x, Integer y) {
        
        if(x < 0 || y < 0 || x > this.size - 1 || y > this.size - 1) {
            System.err.println("WARNING: sseContactExistsPos(): SSE index out of bounds in PG contact matrix of size " + matrix.length + " (x=" + x + ", y=" + y + ").");
            return(false);
        }
        
        if(matrix[x][y] > 0) {
            return(true);
        }
        else {
            return(false);
        }
    }

    /**
     * Returns the number of vertices of this graph.
     * @return the number of vertices
     */
    public Integer numVertices() {
        return(this.size);
    }
    
    
    /**
     * Returns the number of helices in this graph.
     * @return the number of helices
     */
    public Integer numHelices() {
        Integer num = 0;
        for(SSE s : this.sseList) {
            if(s.isHelix()) {
                num++;
            }
        }
        return(num);
    }
    
    /**
     * Returns the number of beta strands in this graph.
     * @return the number of beta strands
     */
    public Integer numBetaStrands() {
        Integer num = 0;
        for(SSE s : this.sseList) {
            if(s.isBetaStrand()) {
                num++;
            }
        }
        return(num);
    }


    /**
     * Serializes this graph and writes it to a (binary) file that can be read by the ProtGraphs.fromFile() method to restore the ProtGraph object.
     * @return true if it worked out, false otherwise
     */
    public Boolean toFileSerialized(String filePath) {

        //System.out.println("    Writing ProtGraph of type " + gt + " with " + pg.numVertices() + " vertices and " + pg.numEdges() + " edges to file " + file + ".");

        FileOutputStream fos = null;
        ObjectOutputStream outStream = null;
        Boolean res = false;

        try {
            fos = new FileOutputStream( filePath );

            outStream = new ObjectOutputStream( fos );

            outStream.writeObject( this );

            outStream.flush();          // Always flush, this ain't the bundeswehr!
            res = true;
        }
        catch (Exception e) {
            System.err.println("WARNING: Could not write serialized ProtGraph object to file '" + filePath + "'.");
            e.printStackTrace();
            res = false;
        }
        finally {
            try {
                if(fos != null) {
                    fos.close();
                }
                if(outStream != null) {
                    outStream.close();
                }
            } catch (Exception e) { /* go 2 hell */ }
        }

        return(res);

    }


    /**
     * Adds an edge labeled with spatialRelation between vertices x and y.
     */
    public void addContact(Integer x, Integer y, Integer spatialRelation) {
        
        if(x >= adjLists.size() || y >= adjLists.size()) {
            System.err.println("ERROR: addContact(): Cannot add contact between SSEs with indeces " + x + " and " + y + ", list size is " + adjLists.size() + ".");
            System.exit(-1);
        }
        
        adjLists.get(x).add(y);
        adjLists.get(y).add(x);
        matrix[x][y] = spatialRelation;
        matrix[y][x] = spatialRelation;
        
        this.spatOrder = null;      // spatOrder has to be re-computed!
    }

    /**
     * Removes the edge between vertices x and y.
     */
    public void removeContact(Integer x, Integer y) {
        matrix[x][y] = SpatRel.NONE;
        matrix[y][x] = SpatRel.NONE;
        adjLists.get(x).remove(y);
        adjLists.get(y).remove(x);
        
        this.spatOrder = null;      // spatOrder has to be re-computed!
    }

    
    /**
     * Returns the type of the contact between the SSEs at indices (x, y) as an Integer. See the SpatRel class for info on the contact
     * types encoded by the Integers. Atm, the following contacts are defined: 0=no contact, 1=mixed, 2=parallel, 3=antiparallel, 4=ligand.
     * You can also call SpatRel.getString(i) to get the string representation of some Integer i.
     */
    public Integer getContactType(Integer x, Integer y) {

        if(x < 0 || y < 0) {
            System.err.println("ERROR: getContactType(): Contact " + x + "/" + y + " out of range (graph has " + this.sseList.size() + " vertices), no negative values allowed.");
            //System.exit(-1);
            return SpatRel.NONE;
        }
        
        if(x >= this.sseList.size() || y >= this.sseList.size()) {
            System.err.println("ERROR: getContactType(): Contact " + x + "/" + y + " out of range (graph has " + this.sseList.size() + " vertices).");
            //System.exit(-1);
            return SpatRel.NONE;
        }

        return(matrix[x][y]);
    }

    /**
     * Returns the number of edges in this graph. Note that this should be divided
     * by 2 in order to get the number of contacts between SSEs because a contact x<->y is
     * counted twice (x->y and y->x) in this undirected graph.
     */
    public Integer numEdges() {
        Integer n = 0;

        for(Integer k = 0; k < this.size; k++) {
            for(Integer l = 0; l < this.size; l++) {
                if(this.sseContactExistsPos(k, l)) {
                    n++;
                }
            }
        }
        return(n);
    }
    
   
    
    
    /**
     * Determines the number of SSE contacts in this graph (which is numEdges() / 2).
     * @return the number of SSE contacts
     */
    public Integer numSSEContacts() {
        return(this.numEdges() / 2);        
    }


    /**
     * Returns the degree of the vertex at index x.
     */
    public Integer degreeOfVertex(Integer x) {
        
        Integer degree = 0;

        for(Integer i = 0; i < this.size; i++) {
            if(this.containsEdge(x, i)) {
                degree++;
            }
        }
        return(degree);
        // * 
        // */
        
        //return(adjLists.get(x).size());
    }

    
    

    
    

    /**
     * Determines the neighbors of a vertex.
     * @return An ArrayList containing the vertex list indices of all neighbors of the vertex at 'vertexIndex'.
     */
    public ArrayList<Integer> neighborsOf(Integer vertexIndex) {
        return(adjLists.get(vertexIndex));
    }
    
    /**
     * Returns all neighbors of a vertex which are in the given set.
     * @param vertexIndex the target vertex
     * @param retainVerts the set of allowed return values
     * @return the remaining neighbors
     */
    public ArrayList<Integer> neighborsOfFromSet(Integer vertexIndex, Collection retainVerts) {
        ArrayList<Integer> nf = new ArrayList<Integer>();
        for(Integer n : adjLists.get(vertexIndex)) {
            nf.add(n);
        }
        nf.retainAll(retainVerts);
        return nf;
    }
    
    
    /**
     * This function determines a neighbor of the vertex at index 'vertexID' which is not the vertex with index 'noThisNeighbor'.
     * So if a vertex has 2 neighbors, it gets you the other one.
     * @param vertexID the index of the vertex to consider
     * @param notThisNeighbor the index of the neighbor you do NOT want
     * @return the index of the second neighbor or -1 if no such vertex exists or if this neighbor cannot be determined uniquely (i.e., the vertex 'vertexID' does NOT have exactly 2 neighbors).
     */
    public Integer getVertexNeighborBut(Integer vertexID, Integer notThisNeighbor) {
        
        Integer err = -1;
        
        if(this.degreeOfVertex(vertexID) != 2) {
            return(err);
        }
        
        for(Integer i : this.neighborsOf(vertexID)) {
            if(i != notThisNeighbor) {
                return(i);
            }
        }
        
        return(err);
    }
    
    
    /**
     * This function determines the index of the neighbor of the vertex at index 'vertexID' which is farthest from the N-terminus in the AA sequence.
     * @param vertexID the index of the vertex to consider
     * @return the index of the neighbor of the vertex at index 'vertexID' which is farthest from the N-terminus in the AA sequence, or -1 if no such vertex exists (i.e., this vertex has no neighbors).
     */
    public Integer getVertexNeighborFarthestFromNTerminus(Integer vertexID) {
        
        Integer err = -1;
        
        SSE tmp;
        Integer minDist = Integer.MAX_VALUE;
        Integer sseIndex = err;
        
        for(Integer i : this.neighborsOf(vertexID)) {
            tmp = this.getSSEBySeqPosition(i);
            if(tmp.getSSESeqChainNum() < minDist) {
                minDist = tmp.getSSESeqChainNum();
                sseIndex = i;
            }
        }
        
        return(sseIndex);
    }
    
    
    /**
     * Returns the list of edges of this graph. Each edge is an Integer[] of length 2. The two integers
     * represent the index of the 2 vertices connected by this edge.
     * @return the list of edges
     */
    public ArrayList<Integer[]> getEdgeList() {
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        
        if(this.getSize() < 2) {
            return edges;
        }
        
        for(Integer i = 0; i < this.size; i++) {
            for(Integer j = i+1; j < this.size; j++) {
                if(this.containsEdge(i, j)) {
                    edges.add(new Integer[] {i, j});
                }            
            }            
        }
        
        return(edges);
    }
    
    
    
    /**
     * Determines the edge label of the edge e=(v1, v2).
     * @param v1 first vertex of the edge
     * @param v2 second vertex of the edge
     * @return the edge label as a string (e.g. "p" for parallel) or NULL if no such edge exists in this graph
     */
    public String getEdgeLabel(Integer v1, Integer v2) {
        if(this.containsEdge(v1, v2)) {
            return(SpatRel.getString(this.matrix[v1][v2]));
        }
        else {
            System.err.println("WARNING: SSEGraph.getEdgeLabel(): No such edge: (" + v1 + "," + v2 + ").");
            return(null);
        }
        
    }
    
    
    
    /**
     * Determines the vertex label (i.e., the SSE type string: "H" for helix, "E" for beta strand, etc.) of the
     * vertex defined by the given vertIndex.
     * @param vertIndex the index of the vertex (SSE) in the vertex list
     * @return the vertex label (the SSE type of the SSE represented by the vertex, e.g., "H" for helix) or NULL if no such vertex exists in this graph
     */
    public String getVertexLabel(Integer vertIndex) {
        if(vertIndex < 0 || vertIndex >= this.size) {
            return(null);
        }
        return(this.sseList.get(vertIndex).getSseType());        
    }
    
    
    /**
     * This function determines the index of the neighbor of the vertex at index 'vertexID' which is closest to the N-terminus in the AA sequence.
     * @param vertexID the index of the vertex to consider
     * @return the index of the neighbor of the vertex at index 'vertexID' which is closest to the N-terminus in the AA sequence, or -1 if no such vertex exists (i.e., this vertex has no neighbors).
     */
    public Integer getVertexNeighborClosestToNTerminus(Integer vertexID) {
        
        Integer err = -1;
        
        SSE tmpSSE;
        Integer maxDist = Integer.MIN_VALUE;
        Integer sseIndex = err;
        
        for(Integer i : this.neighborsOf(vertexID)) {
            tmpSSE = this.getSSEBySeqPosition(i);
            if(tmpSSE.getSSESeqChainNum() < maxDist) {
                maxDist = tmpSSE.getSSESeqChainNum();
                sseIndex = i;
            }
        }
        
        return(sseIndex);
    }



    

    /**
     * Determines the index of the SSE in the SSE list that the given DSSP residue is part of.
     * @param dsspResNum the DSSP residue number, which is unique in a chain
     * @return the index of the SSE containing dsspResNum or -1 if no SSE in the list contains this residue
     */
    public Integer getSSEPosOfDsspResidue(Integer dsspResNum) {
        Integer pos = -1;

        for(Integer i = 0; i < this.sseList.size(); i++) {
            if( (this.sseList.get(i).getStartResidue().getDsspResNum() <= dsspResNum) && (this.sseList.get(i).getEndResidue().getDsspResNum() >= dsspResNum)  ) {
                //System.out.println("   +DSSP Residue " + dsspResNum + " is part of SSE #" + i + ": " + this.sseList.get(i).shortStringRep() + ".");
                return(i);
            }
        }

        //System.out.println("   -DSSP Residue " + dsspResNum + " is NOT part of any SSE in list, returning " + pos + ".");
        return(pos);
    }

    /**
     * Returns the SSE represented by vertex position in this graph. (This is the sequential index of the SSE in the graph, N to C terminus.)
     * @param position the index in the SSE list
     * @return the SSE object at the given index
     */
    public SSE getSSEBySeqPosition(Integer position) {
        //if(position >= this.size || position < 0) {
        if(position >= this.size) {
            System.err.println("ERROR: getSSE(): Index " + position + " out of range, matrix size is " + this.size + ".");
            System.exit(1);
        }
        return(sseList.get(position));
    }


    /**
     * Returns the position of an SSE (defined by its DSSP start and end residues) in the vertex list.
     * @return The index of the SSE if it was found, -1 otherwise.
     */
    public Integer getSsePositionInList(Integer dsspStart, Integer dsspEnd) {

        for(Integer i = 0; i < this.sseList.size(); i++) {

            if(this.sseList.get(i).getStartResidue().getDsspResNum().equals(dsspStart)) {
                if(this.sseList.get(i).getEndResidue().getDsspResNum().equals(dsspEnd)) {
                    return(i);
                }
            }

        }

        //System.out.println("    No SSE with DSSP start and end residues " + dsspStart + "/" + dsspEnd + " found.");
        return(-1);
    }
    
    
    /**
     * Returns the position of an SSE  in the vertex list.
     * @return The index of the SSE if it was found, -1 otherwise.
     */
    public Integer getSSEIndex(SSE s) {
        return(this.getSsePositionInList(s.getStartDsspNum(), s.getEndDsspNum()));
    }


    /**
     * Inits the arrays, removing all edges from this graph.
     */
    protected void init() {
        for(Integer i = 0; i < size; i++) {
            for(Integer j = 0; j < size; j++) {
                matrix[i][j] = SpatRel.NONE;
                distMatrix[i][j] = Integer.MAX_VALUE;
            }
        }
    }


    /**
     * Inits the arrays, removing all edges from this graph.
     */
    public void reinit() {
        this.init();
    }

    /**
     * Prints the matrix of spatial relations between the SSEs to STDOUT.
     */
    public void print() {

        String spacer = "    ";

        System.out.println(spacer + "Spatial relations matrix of the " + this.size + " SSEs follows:");
        //System.out.print("SSEs:");
        System.out.print(spacer);
        for(Integer i = 0; i < this.size; i++) {
            System.out.printf("%2d", + sseList.get(i).getSSESeqChainNum());
        }
        System.out.print("\n");

        for(Integer i = 0; i < this.size; i++) {
            // print line i
            System.out.print(spacer);
            for(Integer j = 0; j < this.size; j++) {
                // print column j of line i
                if(i.equals(j)) {
                    System.out.print(". ");
                }
                else {
                    System.out.printf("%s ", SpatRel.getString(matrix[i][j]));
                }
            }
            System.out.print("\n");
        }
    }

    // public String get

    /**
     * Implements the Bron-Kerbosch algorithm to find all maximal (= non-extandable) cliques. Note that these are NOT
     * only the largest cliques in the graph but all maximal ones.
     * 
     * This is based on the JGraphT source code.
     */
    public ArrayList<Set<Integer>> getMaximalCliques() {

        this.cliques = new ArrayList<Set<Integer>>();        // global class var
        this.numCliquesSoFar = 0;

        List<Integer> potential_clique = new ArrayList<Integer>();
        List<Integer> candidates = new ArrayList<Integer>();
        List<Integer> already_found = new ArrayList<Integer>();

        // add all candidate vertices
        for(Integer i = 0; i < this.size; i++) {
            candidates.add(i);
        }

        int printDepth = 1;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("r");
        findCliques(potential_clique, candidates, already_found, printDepth, root);
        System.out.println("BK recursion tree: \n" + CompatGraphComputation.drawTree(root));
        return cliques;

    }

    /**
     * The recursive part of the Bron-Kerbosch algorithm, used in getMaximalCliques().
     * 
     * This is based on the JGraphT source code.
     */
    protected void findCliques(List<Integer> potential_clique, List<Integer> candidates, List<Integer> already_found, int printDepth, DefaultMutableTreeNode node) {
        
        boolean debug = true;
        
        if(debug) {
            System.out.println(IO.space(printDepth) + "findCliques called: C=" + IO.intListToString(potential_clique, "[", "]") + ", P=" + IO.intListToString(candidates, "[", "]") + ", S=" + IO.intListToString(already_found, "[", "]"));
        }
        
        List<Integer> candidates_array = new ArrayList<Integer>(candidates);
        if(debug && candidates_array.isEmpty()) {            
            System.out.println(IO.space(printDepth) + "Candidates list is empty, ending recursion");
        }
        if (!end(candidates, already_found)) {
            // for each candidate_node in candidates do
            int candidateNum = 0;   // for output explanation only
            for (Integer candidate : candidates_array) {
                
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new TreeNodeData("" + candidate));
                node.add(newNode);
                
                if(debug) {
                    System.out.println(IO.space(printDepth) + "At candidate #" + (candidateNum + 1 )+ " of " + candidates_array.size() + " total (which is " + candidates_array.get(candidateNum) + ")." + " C=" + IO.intListToString(potential_clique, "[", "]") + ", P=" + IO.intListToString(candidates, "[", "]") + ", S=" + IO.intListToString(already_found, "[", "]"));
                }
                
                List<Integer> new_candidates = new ArrayList<Integer>();
                List<Integer> new_already_found = new ArrayList<Integer>();

                // move candidate node to potential_clique
                potential_clique.add(candidate);
                candidates.remove(candidate);

                // create new_candidates by removing nodes in candidates not
                // connected to candidate node
                for (Integer new_candidate : candidates) {
                    if (this.containsEdge(candidate, new_candidate)) {
                        new_candidates.add(new_candidate);
                    } 
                }

                // create new_already_found by removing nodes in already_found
                // not connected to candidate node
                for (Integer new_found : already_found) {
                    if (this.containsEdge(candidate, new_found)) {
                        new_already_found.add(new_found);
                    } 
                }

                // if new_candidates and new_already_found are empty
                if (new_candidates.isEmpty() && new_already_found.isEmpty()) {
                    // potential_clique is maximal_clique
                    ((TreeNodeData)newNode.getUserObject()).markSpecial(true, IO.intListToString(potential_clique, "[", "]"));
                    cliques.add(new HashSet<Integer>(potential_clique));
                    if(debug) {
                        System.out.println(IO.space(printDepth) + "Found new clique: " + IO.intListToString(potential_clique, "[", "]"));
                        System.out.println(IO.space(printDepth) + "BK recursion tree: \n" + CompatGraphComputation.drawTree((DefaultMutableTreeNode)node.getRoot()));
                    }
                    this.numCliquesSoFar++;
                    
                    // DEBUG output
                    if(this.reportCliques) {
                        if(this.numCliquesSoFar % 1000 == 0) {
                        System.out.print("    Found clique #" + this.numCliquesSoFar + " of size " + potential_clique.size() + ".\n");
                        /*
                        for(Integer v : potential_clique) {
                            System.out.print(" " + v);
                        }
                        System.out.print(" ]\n");                         
                         */
                        }
                    }
                }
                else {
                    // recursive call
                    if(debug) {
                        System.out.println(IO.space(printDepth) + "No clique found, next Recursion with C=" + IO.intListToString(potential_clique, "[", "]") + ", P=" + IO.intListToString(new_candidates, "[", "]") + ", S=" + IO.intListToString(new_already_found, "[", "]"));
                    }
                    findCliques(potential_clique, new_candidates, new_already_found, printDepth + 1, newNode);
                } 

                // move candidate_node from potential_clique to already_found;
                already_found.add(candidate);
                potential_clique.remove(candidate);
                
                if(debug && (candidateNum == candidates_array.size() - 1)) {
                    System.out.println(IO.space(printDepth) + "Last candidate used (" + (candidateNum + 1 )+ " of " + candidates_array.size() + "), ending recursion");
                }
                candidateNum++;
            }
        }
        else {
            // end
            System.out.println(IO.space(printDepth) + "Recursion end met (aborting handling of same subtree once more)");
        }
    }

    /**
     * part of the Bron-Kerbosch Algorithm.
     */
    protected boolean end(List<Integer> candidates, List<Integer> already_found) {
        // if a node in already_found is connected to all nodes in candidates
        boolean end = false;
        int edgecounter;
        for (Integer found : already_found) {
            edgecounter = 0;
            for (Integer candidate : candidates) {
                if (this.containsEdge(found, candidate)) {
                    edgecounter++;
                } // of if
            } // of for
            if (edgecounter == candidates.size()) {
                end = true;
            }
        } // of for
        return end;
    }

    
    /**
     * Determines whether this graph is bifurcated (has a vertex with more than 2 neighbors).
     * @return true if it is, false otherwise 
     */
    public Boolean isBifurcated() {
        return(this.maxVertexDegree() > 2);
    }
    
    
    
    /**
     * Returns a String which represents this graph in 'Trivial Graph Format'. See http://docs.yworks.com/yfiles/doc/developers-guide/tgf.html.
     * @return the (multi-line) TGF string
     */
    @Override
    public String toTrivialGraphFormat() {

        StringBuilder tgf = new StringBuilder();

        // add vertices
        for(Integer i = 0; i < this.sseList.size(); i++) {
            tgf.append(i + 1).append(" ").append(i + 1).append("-").append(sseList.get(i).getSseType()).append("\n");
        }

        // separator to indicate that edges follow
        tgf.append("#\n");

        // edges
        for(Integer i = 0; i < this.sseList.size(); i++) {
            for(Integer j = i + 1; j < this.sseList.size(); j++) {
                if(this.containsEdge(i, j)) {
                    //tgf += (i + 1) + " " + (j + 1) + " " + SpatRel.getString(this.getContactType(i, j)) + "\n";
                    tgf.append(i + 1).append(" ").append(j + 1).append(" (").append(i + 1).append("-").append(SpatRel.getString(this.getContactType(i, j))).append("-").append(j + 1).append(")\n");
                }
            }
        }

        return(tgf.toString());
    }
    
    
    /**
     * Converts this graph to the format required by the Perl script which computes the 
     * PTGL folding graph notations on the old PTGL server. The name of the updated version
     * of the script is 'makePTGLnotationsTS.pl'. Each file is for one graph of one
     * chain. Here is an example output:

chains: 1
# protein: 1arsA
# SSEs: 10
E	3	3	3
E	3	3	3
E	3	3	3
E	3	3	3
E	3	3	3
E	3	3	3
E	3	3	3
E	3	3	3
E	3	3	3
E	3	3	3
# SSE contacts:
1 9 A EE
2 3 P EE
2 6 P EE
6 7 P EE
7 8 P EE
8 9 A EE

     * 
     * This format is for internal use at MolBI (for historical reasons) only. Do not expose
     * it.
     * 
     * @return the graph string
     */
    public String toPerlFoldingGraphNotationsScriptFormat() {

        StringBuilder pf = new StringBuilder();
        pf.append("chains: 1\n");
        pf.append("# protein: ").append(this.pdbid).append(this.chainid).append("\n");
        pf.append("# SSEs: ").append(this.size).append("\n");

        // add vertices
        SSE sse;
        for(Integer i = 0; i < this.sseList.size(); i++) {
            sse = this.getVertex(i);
            pf.append(sse.getPLCCSSELabel()).append("\t").append(sse.getAASequence().length()).append("\t").append(sse.getStartResidue().getPdbResNum()).append("\t").append(sse.getEndResidue().getPdbResNum()).append("\n");
        }

        // separator to indicate that edges follow
        pf.append("# SSE contacts:\n");

        // edges
        SSE sseA, sseB;
        for(Integer i = 0; i < this.sseList.size(); i++) {
            for(Integer j = i + 1; j < this.sseList.size(); j++) {
                if(this.containsEdge(i, j)) {
                    sseA = this.getVertex(i);
                    sseB = this.getVertex(j);
                    pf.append((i + 1) + " " + (j + 1) + " " + (SpatRel.getString(this.getContactType(i, j))).toUpperCase() + " " + sseA.getPLCCSSELabel() + sseB.getPLCCSSELabel() + "\n");
                }
            }
        }

        return(pf.toString());
    }

    /**
     * Determines the SSE/vertex which is closest to the C-terminus (has the highest DSSP end residue number).
     * @return the SSE/vertex by index in the SSE list
     */
    public Integer closestToCTerminus() {
        Integer vertIndex = -1;
        Integer maxDsspNum = Integer.MIN_VALUE;

        for(Integer i = 0; i < this.size; i++) {
            if(sseList.get(i).getEndDsspNum() > maxDsspNum) {
                maxDsspNum = sseList.get(i).getEndDsspNum();
                vertIndex = i;
            }
        }

        if(vertIndex < 0) {
            System.err.println("WARNING: closestToCTerminus(): No SSE found, returning '" + vertIndex + "'.");
            if(this.size > 0) {
                System.err.println("ERROR: closestToCTerminus(): Graph has " + this.size + " vertices, so not finding anything is a bug.");
                System.exit(1);
            }
        }
        return(vertIndex);
    }

    /**
     * Orders the vertices of this graph by their spatial distance (in the graph) to the N-terminus.
     * @return An array containing the vertex indices, ordered by their graph distance (length of the shortest path to) to the residue that is closest to the N-terminus.
     */
    public Integer[] vertexOrderSpatial() {
        Integer ntIndex = this.closestToNTerminus();
        Integer [] distN = this.pathDistanceAllVerts(ntIndex);

        System.out.print("      Printing distances of all vertices to vertex " + ntIndex + ", which is clostest to the N-terminus:\n      ");
        for(Integer i = 0; i < this.size; i++) {
            System.out.print(" " + distN[i]);
        }
        System.out.print("\n");

        Integer [] spatialOrder = new Integer[this.size];
        // Init order with sequential order
        for(Integer i = 0; i < this.size; i++) {
            spatialOrder[i] = i;
        }

        // Now sort the spatialOrder array by the distances in the distN array
        Boolean switched = true;
        Integer tmp;
        while(switched) {
            switched = false;
            for(Integer i = 1; i < this.size; i++) {
                if(distN[i] < distN[i-1]) {
                    // This pair is in wrong order, switch it.
                    tmp = spatialOrder[i-1];
                    spatialOrder[i-1] = spatialOrder[i];
                    spatialOrder[i] = tmp;

                    // Also switch the value in distN, of course
                    tmp = distN[i-1];
                    distN[i-1] = distN[i];
                    distN[i] = tmp;

                    switched = true;
                }
            }
        }

      
        return(spatialOrder);
    }

    /**
     * Calculates the distance of the shortest path to vertex x from all vertices in this graph.
     * @return An array with the distance to x for all vertices (defined by their vertex indices). If a vertex is not reachable from x (it is not in the same connected component), its distance is set to -1.
     */
    public Integer[] pathDistanceAllVerts(Integer x) {

        if(x < 0 || x >= this.size) {
            System.err.println("ERROR: pathDistanceAllVerts(): Vertex index '" + x + "' invalid.\n");
            System.exit(-1);
        }

        //System.out.println("      Calculating distance of all " + this.size + " vertices to vertex " + x + ".");

        // Now perform breadth-first search with source vertex x and fill in the computed distances. Vertices not in the
        //  connected component of x will not be found and their distance will remain at -1.
        Integer [] color = new Integer [this.size];
        Integer [] dist = new Integer [this.size];
        Integer [] predec = new Integer [this.size];
        Integer v = null;
        LinkedList<Integer> queue;


        // Init stuff
        for(Integer i = 0; i < sseList.size(); i++) {
            color [i] = 1;  // 1 = white, 2 = gray, 3 = black. White vertices have not been handled yet, gray ones
                            //  are currently being handled and black ones have already been handled.
            dist[i] = -1;    // distance of vertex i to the root of the search (x)
            predec[i] = -1;       // the predecessor of vertex i
        }

        queue = new LinkedList<Integer>();

        // Start breadth-first search in vertex x
        color[x] = 2;   // x is currently being handled
        dist[x] = 0;    // the distance of x ito itself is ... zero! ;)
        queue.addFirst(x);

        while( ! queue.isEmpty()) {
            v = queue.peekFirst();      // v is the 1st element of the FIFO

            // For all neighbors w of v
            for(Integer w : (adjLists.get(v))) {
                // If w has not been treated yet
                if(color[w].equals(1)) {
                    color[w] = 2;               // ...now it is being treated
                    dist[w] = dist[v] + 1;      // w is a successor of v (a neighbor of v that is treated after v)
                    predec[w] = v;              // so v is a predecessor of w
                    queue.addLast(w);           // add the neighbors of v to the queue so all their neighbors are checked, too.
                                                //   (Note that adding them to the end makes this find all vertices in distance n before finding
                                                //    any vertex in distance n + 1 to i.)
                }
            }
            queue.removeFirst();    // This vertex has just been handled,
            color[v] = 3;           //  so mark it as handled.
        }

        // The queue is empty, so all vertices reachable from x have been checked.
        return(dist);
    }
    
    
    
    /**
     * Returns a node type list of the graph vertices. This is pretty special and intended
     * for usage with RAGE (Dror Marcus and Yuval Shavitt. RAGE - A Rapid Graphlet Enumerator for Large Networks. Computer Networks (COMNET, Elsevier), to appear).
     * @return the string description
     */
    public String getNodeTypeList() {
        StringBuilder sb = new StringBuilder();
        
        SSE s;
        for(int i = 0; i < this.sseList.size(); i++) {
            s = this.sseList.get(i);
            sb.append(i + 1);
            sb.append(" ");
            sb.append("SSE_type ");
            sb.append(s.getPLCCSSELabel());
            sb.append("\n");
        }
        
        
        return sb.toString();
    }

    


    
    
    /**
     * Adds a constant backbone of contacts to the SSEs of this graph, i.e., connects the SSEs in sequential order
     * from the N to the C terminus with contacts of type backbone. Note that this function will replace any existing
     * contacts between consecutive SSEs with contacts of type backbone.
     */
    public void addFullBackboneContacts() {
        if(this.size <= 1) {
            return;
        } else {
            for(Integer i = 0; i < (this.size - 1); i++) {
                this.addContact(i, (i+1), SpatRel.BACKBONE);
            }
        }
        
    }
    
    
    /**
     * Determines whether the contact (x, y) in the contact matrix is a crossover contact (i.e., the SSEs are parallel).
     * @return true if the two SSEs are parallel, false otherwise
     */ 
    public Boolean isCrossoverConnection(Integer x, Integer y) {
        if(this.getContactType(x, y) == SpatRel.PARALLEL) {
            return(true);
        }
        return(false);
    }
    
    
    /**
     * Returns the number of SSEs of type 'type' in this graph. Supported types are "HELIX", "SHEET", "LIGAND" and "OTHER"
     */
    public Integer numSSE(String type) {
        Integer num = 0;

        for(SSE s : this.sseList) {
            if(type.equals("HELIX")) {
                if(s.isHelix()) {
                    num++;
                }
            }
            else if(type.equals("SHEET")) {
                if(s.isBetaStrand()) {
                    num++;
                }
            }
            else if(type.equals("LIGAND")) {
                if(s.isLigandSSE()) {
                    num++;
                }
            }
            else if(type.equals("OTHER")) {
                if(s.isOtherSSE()) {
                    num++;
                }
            }
            else {
                System.err.println("ERROR: numSSE(): SSE type '" + type + "' not supported.");
                System.exit(-1);
            }
        }

        return(num);
    }

    /**
     * Determines all SSEs of type 'type' in this graph.
     * @return An ArrayList containing the indices of the SSEs.
     */
    public ArrayList<Integer> getAllSSEsOfType(String type) {
        ArrayList<Integer> sses = new ArrayList<Integer>();

        for(Integer i = 0; i < sseList.size(); i++) {
            if(type.equals("HELIX")) {
                if( (sseList.get(i)).isHelix() ) {
                    sses.add(i);
                }
            }
            else if(type.equals("SHEET")) {
                if( (sseList.get(i)).isBetaStrand() ) {
                    sses.add(i);
                }
            }
            else if(type.equals("LIGAND")) {
                if( (sseList.get(i)).isLigandSSE() ) {
                    sses.add(i);
                }
            }
            else if(type.equals("OTHER")) {
                if( (sseList.get(i)).isOtherSSE() ) {
                    sses.add(i);
                }
            }
            else {
                System.err.println("ERROR: getAllSSEsOfType(): SSE type '" + type + "' not supported.");
                System.exit(-1);
            }
        }

        return(sses);
    }

    /**
     * Determines all SSEs of type 'type' in this graph.
     * @return An ArrayList containing the indices of the SSEs.
     */
    public ArrayList<Integer> getAllSSEsOfTypeFromList(String type, ArrayList<Integer> sseIndices) {
        ArrayList<Integer> sses = new ArrayList<Integer>();

        for(Integer i = 0; i < sseIndices.size(); i++) {
            if(type.equals("HELIX")) {
                if( (sseList.get(sseIndices.get(i))).isHelix() ) {
                    sses.add(sseIndices.get(i));
                }
            }
            else if(type.equals("SHEET")) {
                if( (sseList.get(sseIndices.get(i))).isBetaStrand() ) {
                    sses.add(sseIndices.get(i));
                }
            }
            else if(type.equals("LIGAND")) {
                if( (sseList.get(sseIndices.get(i))).isLigandSSE() ) {
                    sses.add(sseIndices.get(i));
                }
            }
            else if(type.equals("OTHER")) {
                if( (sseList.get(sseIndices.get(i))).isOtherSSE() ) {
                    sses.add(sseIndices.get(i));
                }
            }
            else {
                System.err.println("ERROR: getAllSSEsOfTypeFromList(): SSE type '" + type + "' not supported.");
                System.exit(1);
            }
        }

        return(sses);
    }


    /**
     * Checks whether this graph contains a beta barrel.
     * @return true if it does, false otherwise
     */
    public Boolean containsBetaBarrel() {

        // Currently this implementation is just a heuristic. We assume that there is a beta barrel if
        // at least 4 connected beta sheets exists, all of which have exactly 2 neighbors which are beta sheets
        // and members of the same set of at least 4 beta sheets.
        ArrayList<Integer> allSheets = this.getAllSSEsOfType("SHEET");
        
        // at least 4 sheets required
        if(allSheets.size() < 4) {
            return(false);
        }

        ArrayList<Integer> neighList = new ArrayList<Integer>();
        ArrayList<Integer> betaNeighList = new ArrayList<Integer>();

        // Check neighbor conditions for all sheets. Note that we assume that all beta strands of a graph have to
        // be part of a beta barrel if such a barrel exists, which may be too harsh.
        for(Integer vertexIndex : allSheets) {
            neighList = this.neighborsOf(vertexIndex);
           
            // Beta strands which are part of a beta barrel obviously must have at least 2 neighbors that
            //  are beta strands.
            betaNeighList = getAllSSEsOfTypeFromList("SHEET", neighList);
            if( betaNeighList.size() < 2) {
                return(false);
            }            
        }

        // None of the checks proved that this is not a beta barrel, so we assume it is.
        return(true);
    }

    /**
     * Returns a string array of size 2. The first element contains the opening bracket for this graph's serial notation,
     * and the second the closing bracket.
     */
    public String[] getNotationBrackets() {
        String[] brackets = new String[2];

        // default brackets: "[" and "]"
        brackets[0] = "[";
        brackets[1] = "]";

        // non-bifurctaed graphs that contain beta barrels use "(" and ")"
        if(this.containsBetaBarrel()) {
            brackets[0] = "(";
            brackets[1] = ")";
        }

        // all bifurcated graphs use "{" and "}"
        if(this.isBifurcated()) {
            brackets[0] = "{";
            brackets[1] = "}";
        }

        return(brackets);
    }
    
    
    /**
     * Returns a static String that is the header for the plcc format.
     * @return the multi-line header string, including a label with PDB ID and graph type
     */
    protected String getPlccFormatHeader() {
        String outString = "# This is the plcc format protein graph file for the " + this.graphType + " graph of PDB entry " + this.pdbid + ", chain " + this.chainid + ".\n";
        outString += "# First character in a line indicates the line type ('#' => comment, '>' => meta data, '|' => SSE, '=' => contact).\n";
        //outString += "# Note on parsing this file: You can savely remove all whitespace from a line before splitting it.\n";
        
        return(outString);
    }

    /**
     * Returns the contents of the SSE list text file that explains the SSEs in the image. Used for the plcc graph file format. The String includes
     * multiple lines (one for each SSE) which are organized in fields separated by the field separator "|".
     * Each line contains the following fields in this order:
     *
     * PDB ID | chain ID | graph type | sequential SSE number in chain | SSE number in image | SSE type | DSSP start residue # | DSSP end residue # | PDB end residue ID (format: chain-res#-icode) | PDB start residue ID | AA sequence
     *
     * This function is not meant to be called externally anymore, use getGraphFilePLG() instead.
     * 
     *
     */
    protected String getSSEListString() {
        String outString = "# SSEs follow in format '| PDB ID | chain ID | graph type | sequential SSE number in chain | SSE number in graph and image | SSE type | DSSP start residue # | DSSP end residue # | PDB end residue ID (format: chain-res#-icode) | PDB start residue ID | AA sequence'\n";

        SSE s = null;
        for(Integer i = 0; i < this.sseList.size(); i++) {
            s = this.sseList.get(i);
            outString += String.format("| %s | %s | %s | %d | %d | %s | %d | %d | %s | %s | %s \n", this.pdbid, this.chainid, this.graphType, s.getSSESeqChainNum(), (i + 1), s.getSseType(), s.getStartDsspNum(), s.getEndDsspNum(), s.getStartPdbResID(), s.getEndPdbResID(), s.getAASequence());
        }

        outString += "# Printed info on " + this.sseList.size() + " SSEs.\n";

        return(outString);
    }
    
    
    /**
     * Returns a multi-line String which represents this graph in our own plcc format, v2. All info required to draw the graph is included in the file format, but stuff like the atom coordinates etc is not.
     * Internally, this calls getSSEListString() and getContactListString() and returns their concatenated results (for historical reasons). See their java doc for more info on the format. It is really easy and also documented in the
     * output String itself (using comment lines) though.
     * 
     * @return a String representing this protein graph in plcc v2 format
     */
    public String toVPLGGraphFormat() {
        this.metadata.put("format_version", "2");
        return(this.getPlccFormatHeader() + this.getMetaDataString() + this.getSSEListString() + this.getContactListString());
    }
    
    
    /**
     * Returns a parse-able String representation of the meta data of this protein graph. Used for the plcc graph file format.
     * @return the meta data String (includes comment lines)
     */
    protected String getMetaDataString() {
        String outString = "# The graph meta data follows in format '> key > value'.\n";
        
        HashMap<String, String> md = this.metadata;       
        
        for(String key : md.keySet()) {
            outString += "> " + key + " > " + (String)md.get(key) + "\n";
        }
        
        return(outString);
    }
    
    
    /**
     * Returns a (multi-line) string representing the contacts between the SSEs of this graph. Used for the plcc graph file format. The string is designed in a 
     * way that makes parsing it easy: each line contains info on a contact between a pair of SSEs in the following format:
     *   SSE1 = spatial_relation = SSE2
     * e.g., '1 = p = 3' means that SSE #1 is in contact with SSE #3, and they are parallel to each other. 
     * 
     * This function is not meant to be called externally anymore, use getGraphFilePLG() instead.
     * 
     */
    protected String getContactListString() {
        String outString = "# The contacts between the SSEs follow in format '= SSE1 = contact_type = SSE2'. The SSEs are labeled by their position in this graph.\n";
        Integer numContacts = 0;
        
        for (Integer i = 0; i < this.sseList.size(); i++) {
            for (Integer j = (i + 1); j < this.sseList.size(); j++) {
                if(this.sseContactExistsPos(i, j)) {
                    outString += "= " + (i + 1) + " = " + SpatRel.getString(this.matrix[i][j]) + " = " + (j + 1) + "\n";
                    numContacts++;
                }
            }            
        }
        
        outString += "# Listed " + numContacts + " contacts. EOF.";
        
        return(outString);
    }
        
    
    /**
     * Determines the SSE/vertex which is closest to the N-terminus (has the lowest DSSP start residue number).
     * @return the index of the vertex in the SSE list
     */
    public Integer closestToNTerminus() {
        Integer vertIndex = -1;
        Integer minDsspNum = Integer.MAX_VALUE;

        for(Integer i = 0; i < this.size; i++) {
            if(sseList.get(i).getStartDsspNum() < minDsspNum) {
                minDsspNum = sseList.get(i).getStartDsspNum();
                vertIndex = i;
            }
        }

        if(vertIndex < 0) {
            System.err.println("WARNING: closestToNTerminus(): No SSE found, returning '" + vertIndex + "'.");
            if(this.size > 0) {
                System.err.println("ERROR: closestToNTerminus(): Graph has " + this.size + " vertices, so not finding anything is a bug.");
                System.exit(1);
            }
        }
        return(vertIndex);
    }


    /**
     * Determines which of the vertices in the List someVertices is closest to the N-terminus (in sequential order/ AA sequence).
     * @return the index of the vertex or -1 if no such vertex exists in this graph (i.e., this graph is empty)
     */
    public Integer closestToNTerminusOf(java.util.List<Integer> someVertices) {
        Integer vertex = -1;
        Integer minDsspNum = Integer.MAX_VALUE;

        for(Integer i = 0; i < someVertices.size(); i++) {
            if(sseList.get(someVertices.get(i)).getStartDsspNum() < minDsspNum) {
                minDsspNum = sseList.get(someVertices.get(i)).getStartDsspNum();
                vertex = someVertices.get(i);
            }
        }

        if(vertex < 0) {
            System.err.println("WARNING: closestToNTerminusOf(): No SSE found in list, returning '" + vertex + "'.");
            if(someVertices.size() > 0) {
                System.err.println("ERROR: closestToNTerminusOf(): List has " + someVertices.size() + " vertices, so not finding anything is a bug.");
                System.exit(-1);
            }
        }
        return(vertex);
    }


    /**
     * Determines the SSE/vertex which is closest to the N-terminus (has the lowest DSSP start residue number) and has a degree of 1.
     * Note that no such vertex may exist, for example if the graph consists of nothing but a beta barrel (and thus all vertices have degree 2).
     * @return the index of the vertex or -1 if no such vertex exists in this graph
     */
    public Integer closestToNTerminusDegree1() {
        Integer vertIndex = -1;
        Integer minDsspNum = Integer.MAX_VALUE;

        for(Integer i = 0; i < this.size; i++) {
            if(this.degreeOfVertex(i) == 1) {
                if(sseList.get(i).getStartDsspNum() < minDsspNum) {
                    minDsspNum = sseList.get(i).getStartDsspNum();
                    vertIndex = i;
                }                
            }            
        }

        //if(vertIndex < 0) {
        //    System.err.println("WARNING: closestToNTerminusDegree1(): No SSE found, returning '" + vertIndex + "'.");            
        //}
        
        return(vertIndex);
    }
    
    
    
    /**
     * Returns the only neighbor of vertex at index 'vertIndex' (it is assumed that this vertex only has 1 neighbor).
     * @param vertIndex the vertex
     * @return the index of the only neighbor of this vertex or -1 if no such vertex exists, i.e., the vertex does not have exactly 1 neighbor.
     */
    public Integer getSingleNeighborOf(Integer vertIndex) {
        if(this.degreeOfVertex(vertIndex) != 1) {
            return(-1);
        }
        else {
            return(this.neighborsOf(vertIndex).get(0));
        }
    }
    
    
    /**
     * Debug function, prints the distance matrix of this graph to STDOUT. Note that is not the contact matrix of the SSEs but
     * the distance matrix of the vertices in the graph, computed using Dijkstra's shortest path algorithm.
     * 
     * You have to call calculateDistancesWithinGraph() first.
     * 
     */
    public void printDistMatrix() {
        
        if(! this.distancesCalculated) {
            this.calculateDistancesWithinGraph();
        }
        
        String s = "";
        
        for( Integer i = 0; i < this.size; i++ ) {
            for(Integer j = 0; j < this.size; j++) {
                s += (distMatrix[i][j] == Integer.MAX_VALUE ? "i" : distMatrix[i][j]) + " ";
            }
            s += "\n";
        }                
        System.out.print("Distance matrix follows (i := infinite/no path between vertices):\n" + s);
    }
    
    
    
    
    
    
    /**
     * Determines the maximum vertex degree in this graph.
     * @return the maximum vertex degree
     */
    public Integer maxVertexDegree() {

        Integer max = 0;

        for(Integer i = 0; i < this.size; i++) {
            if(this.degreeOfVertex(i) > max) {
                max = this.degreeOfVertex(i);
            }
        }

        return(max);
    }
    
    





    
    
    public Boolean isProteinGraph() { return(this.isProteinGraph); }
    public Boolean isFoldingGraph() { return( ! this.isProteinGraph); }
    
    
    
    /**
     * Uses Dijkstra's algorithm to compute the distance matrix of this graph, i.e. all pairwise distances between vertices.
     * Writes the results into distMatrix[][].
     */
    public void calculateDistancesWithinGraph() {
        
        // clear or init data
        for(Integer i = 0; i < size; i++) {
            for(Integer j = 0; j < size; j++) {
                distMatrix[i][j] = Integer.MAX_VALUE;
            }
        }
        
        // compute paths for all vertices
        for(Integer i = 0; i < this.size; i++) {
            computeAllPairwiseDistancesForVertex(i);            
        }       
        
        this.distancesCalculated = true;
    }
    
    
    /**
     * A wrapper around computePaths() which only fills out the distance matrix for the vertex sourceVertex. Note that you need to
     * call this for all vertices in order to get the complete distance matrix.
     * @param sourceVertex the source vertex for which the distances to all other vertices should be computed and inserted into distMatrix.
     */
    protected void computeAllPairwiseDistancesForVertex(Integer sourceVertex) {
        computePaths(sourceVertex, sourceVertex);
    }
    
    
    /**
     * A wrapper around computePaths() which returns the path from source to target.
     * @param source the source vertex
     * @param target the target vertex
     * @return the path as an ArrayList of the vertices in the path (in correct order from source to target, of course)
     */
    protected ArrayList<Integer> getPathBetweenVertices(Integer source, Integer target) {
        return(computePaths(source, target));
    }

    
    /**
     * Determines whether this graph contains a cycle (works on connected graphs only).
     * Note that this function only works if this graph is connected.
     * @return true if a cycle exists, false otherwise. May NOT find the cycle if this graph is not connected.
     */
    public Boolean hasCycle() {
        ArrayList<Integer> allIndices = new ArrayList<Integer>();
        for(int i = 0; i < this.getSize(); i++) {
            allIndices.add(i);
        }
        return this.hasCycleInVertexSet(allIndices);
    }
    
    
    /**
     * Determines whether the vertices in the list (which are vertex indices of this graph) contains a cycle.
     * Note that this function only works if all vertices in the list are connected.
     * @param vertexPositions the vertices to consider (by index in this graph)
     * @return true if a cycle exists, false otherwise. May NOT find the cycle if the vertex set is not connected.
     */
    public Boolean hasCycleInVertexSet(ArrayList<Integer> vertexPositions) {
        
        Stack<Integer> stack = new Stack<Integer>();
        HashSet<Integer> visited = new HashSet<Integer>();
        Integer cur;
        int start; int end;
        
        stack.add(vertexPositions.get(0));
        while(stack.size() > 0) {
            cur = stack.remove(0);
            if( ! visited.contains(cur)) {
                visited.add(cur);
                ArrayList<Integer> curNeighbors = this.neighborsOfFromSet(cur, vertexPositions);
                start = 0;
                end = curNeighbors.size() - 1;
                while(start < end) {
                    if(visited.contains(curNeighbors.get(start))) {
                        return true;
                    }
                    else {
                        stack.add(curNeighbors.get(start));
                    }
                    start++;
                }
            }
        }
        return false;
    }
    
    
    /**
     * Tests whether the given vertex set is connected, i.e., each vertex is reachable from all other vertices.
     * @param vertexPositions the vertices, by index in the graph
     * @return true if all vertices in the list are connected, false otherwise
     */
    public Boolean isConnectedVertexSet(ArrayList<Integer> vertexPositions) {
        
        if(vertexPositions.isEmpty()) {
            throw new java.lang.IllegalArgumentException("SSEGraph.isConnectedVertexSet(): vertex list must not be empty.");
        }
        
        Stack<Integer> stack = new Stack<Integer>();
        HashSet<Integer> visited = new HashSet<Integer>();
        Integer cur;
        int start; int end;
        
        stack.add(vertexPositions.get(0));
        while(stack.size() > 0) {
            cur = stack.remove(0);
            if( ! visited.contains(cur)) {
                visited.add(cur);
                ArrayList<Integer> curNeighbors = this.neighborsOfFromSet(cur, vertexPositions);
                start = 0;
                end = curNeighbors.size() - 1;
                while(start < end) {
                    if( ! visited.contains(curNeighbors.get(start))) {
                        stack.add(curNeighbors.get(start));
                    }
                    start++;
                }
            }
        }        
        
        if(visited.size() == vertexPositions.size()) {
            return true;
        }
        return false;
    }
    
    /**
     * Tests whether this graph is connected, i.e., each vertex is reachable from all other vertices.
     * @return true if this graph is connected, false otherwise
     */
    public Boolean isConnected() {
        
        ArrayList<Integer> vertexPositions = new ArrayList<Integer>();
        for(int i = 0; i < this.getSize(); i++) {
            vertexPositions.add(i);
        }
        
        if(vertexPositions.isEmpty()) {
            throw new java.lang.IllegalArgumentException("SSEGraph.isConnected(): vertex list must not be empty.");
        }
        
        Stack<Integer> stack = new Stack<Integer>();
        HashSet<Integer> visited = new HashSet<Integer>();
        Integer cur;
        int start; int end;
        
        stack.add(vertexPositions.get(0));
        while(stack.size() > 0) {
            cur = stack.remove(0);
            if( ! visited.contains(cur)) {
                visited.add(cur);
                ArrayList<Integer> curNeighbors = this.neighborsOf(cur);
                start = 0;
                end = curNeighbors.size() - 1;
                while(start < end) {
                    if( ! visited.contains(curNeighbors.get(start))) {
                        stack.add(curNeighbors.get(start));
                    }
                    start++;
                }
            }
        }        
        
        if(visited.size() == vertexPositions.size()) {
            return true;
        }
        return false;
    }
    
    /**
     * Sets the metadata for this graph.
     * @param md the meta data Hashmap to assign to this graph.
     */
    public void setMetaData(HashMap<String, String> md) {
        this.metadata = md;
    }
    
    public void setComplexData(ArrayList<Integer> chainEnd, ArrayList<Chain> allChains){
        this.allChains = allChains;
        this.chainEnd = chainEnd;
    }
    
    
    /**
     * Part of Dijkstra's algorithm, calculates the distances to all other vertices for a single source vertex.
     * Called by calculateDistancesWithinGraph() for each vertex.
     * This function can also return the shortest path to a target vertex along the way. If you do not need this
     * and only want the distance matrix to be filled, you can make 'targetVertex' an arbitrary value (e.g., the same as
     * sourceVertex) and ignore the returned ArrayList.
     * @param sourceVertex the source vertex. The distances to all the other vertices will be computed for this vertex.
     * @param targetVertex the target vertex. A path to this vertex will be in the returned ArrayList. If you don't need this, just set targetVertex to the 
     * same value as sourceVertex and ignore the resulting (empty) ArrayList.
     * @return an ArrayList containing the shortest path from sourceVertex to targetVertex, described by the indices of all vertices of the path.
     */    
    protected ArrayList<Integer> computePaths(Integer sourceVertex, Integer targetVertex) {
        
        //System.out.println("DEBUG: Computing distances from vertex " + sourceVertex + " to all other vertices.");
        
        // init stuff
        Integer src = sourceVertex;
        Integer[] minDistances = new Integer[this.size];
        Integer[] previous = new Integer[this.size];        // This allows us to determine the path from sourceVertex to a target vertex at
                                                            // the end of this function (take the target vertex, than follow the 'previous' values
                                                            // until it is <0. Do not forget to invert this path in the end (to get source=>target instead of target=>source).
        
        ArrayList<Integer> pathToTarget = new ArrayList<Integer>();
        
        for(Integer i = 0; i < this.size; i++) { minDistances[i] = Integer.MAX_VALUE; previous[i] = -1; }
        
        // go
        minDistances[src] = 0;      // the distance of the vertex to itself is 0
        PriorityQueue<PriorityVertex> vertexQueue = new PriorityQueue<PriorityVertex>();
  	vertexQueue.add(new PriorityVertex(src, minDistances[src]));
        
        while (!vertexQueue.isEmpty()) {
            
  	    PriorityVertex u = vertexQueue.poll();
            
            assert u.minDistance == minDistances[u.index] : "ERROR: ProtGraph.computePaths(): minDistances[u.index] and u.minDistance inconsistent.";
  
            // Visit each edge exiting u...
            ArrayList<PriorityVertex> neighborsOfU = new ArrayList<PriorityVertex>();
            for(Integer index : adjLists.get(u.index)) {
               neighborsOfU.add(new PriorityVertex(index, minDistances[index])); 
            }
            
            //System.out.println("DEBUG: Vertex " + u + " has " + neighborsOfU.size() + " neighbors.");
            
            
            for (PriorityVertex neighborOfU : neighborsOfU) {
                
                // ... compute their distance ...                
                Integer distanceThroughU = minDistances[u.index] + 1;     // That vertex is a neighbor of u, so the distance to it through u is the distance to u + 1.
                                                                    // Note that during the first run, u==sourceVertex, so minDinstaces[u] == 0.
                
  		if (distanceThroughU < minDistances[neighborOfU.index]) {
                    
                    //System.out.println("DEBUG: Updated minDistance of vertex " + neighborOfU + " to source vertex " + sourceVertex + ": new value via vertex " + u + " is " + distanceThroughU + ".");
                    
  		    vertexQueue.remove(neighborOfU);
  
  		    minDistances[neighborOfU.index] = distanceThroughU;   // this is what we are after
                    neighborOfU.minDistance = distanceThroughU;             // gotta keep track of both spots
  		    previous[neighborOfU.index] = u.index;
                    
                    // ...and re-add this vertex to the end of the queue
  		    vertexQueue.add(neighborOfU);
  		}
                
                assert neighborOfU.minDistance == minDistances[neighborOfU.index] : "ERROR: ProtGraph.computePaths(): minDistances[neighborOfU.index] and neighborOfU.minDistance inconsistent.";
            }
        }
        
        // fill in the distMatrix from the minDist array
        for(Integer i = 0; i < this.size; i++) {
            distMatrix[src][i] = minDistances[i];
            distMatrix[i][src] = minDistances[i];
        }     
        
        // reconstruct the path from target to source via backtracking
        
        // gotta reverse this, of course (we want source=>target, not target=>source)
        for (Integer vertex = targetVertex; vertex >= 0; vertex = previous[vertex]) {
            pathToTarget.add(vertex);
        }
        
        assert pathToTarget.size() == distMatrix[sourceVertex][targetVertex] : "ERROR: ProtGraph.computePaths(): length of path between a vertex pair inconsistent with distMatrix entry.";
        
        Collections.reverse(pathToTarget);        
        return(pathToTarget);
    }
    
    /**
     * Draws a protein graph in all formats, returns a list of written files.
     * @param baseFilePathNoExt the base file path where to put the image (without dot and file extension)
     * @param drawBlackAndWhite whether to omit colors, only useful for non-protein graphs
     * @param formats an array of type DrawTools.IMAGEFORMAT. Do not include SVG, this will always be drawn anyways
     * @return a map of formats to the corresponding output files written to disk
     */
    public static HashMap<IMAGEFORMAT, String> drawProteinGraph(String baseFilePathNoExt, Boolean drawBlackAndWhite, IMAGEFORMAT[] formats, ProtGraph pg) {                
               
       
        DrawResult drawRes = SSEGraph.drawProteinGraphG2D(drawBlackAndWhite, pg);
        
        //System.out.println("drawProteinGraph: Basefilepath is '" + baseFilePathNoExt + "'.");
        String svgFilePath = baseFilePathNoExt + ".svg";
        HashMap<IMAGEFORMAT, String> resultFilesByFormat = new HashMap<IMAGEFORMAT, String>();
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            resultFilesByFormat.put(IMAGEFORMAT.SVG, svgFilePath);
            resultFilesByFormat.putAll(DrawTools.convertSVGFileToOtherFormats(svgFilePath, baseFilePathNoExt, drawRes, formats));
        } catch (IOException ex) {
            DP.getInstance().e("Could not write protein graph file : '" + ex.getMessage() + "'.");
        }
        
        if( ! Settings.getBoolean("plcc_B_silent")) {
            StringBuilder sb = new StringBuilder();
            sb.append("      Output protein graph files: ");
            for(IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                sb.append("(").append(format.toString()).append(" => ").append(resultFilesByFormat.get(format)).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }
    
    
    /**
     * Draws a folding graph in all formats, returns a list of written files.
     * @param baseFilePathNoExt the base file path where to put the image (without dot and file extension)
     * @param drawBlackAndWhite whether to omit colors, only useful for non-protein graphs
     * @param formats an array of type DrawTools.IMAGEFORMAT. Do not include SVG, this will always be drawn anyways
     * @param pnfr
     * @return a map of formats to the corresponding output files written to disk
     */
    public static HashMap<IMAGEFORMAT, String> drawFoldingGraphADJ(String baseFilePathNoExt, Boolean drawBlackAndWhite, IMAGEFORMAT[] formats, PTGLNotationFoldResult pnfr) {                
               
        DrawResult drawRes = SSEGraph.drawFoldingGraphADJG2D(pnfr);
        
        //System.out.println("drawProteinGraph: Basefilepath is '" + baseFilePathNoExt + "'.");
        String svgFilePath = baseFilePathNoExt + ".svg";
        HashMap<IMAGEFORMAT, String> resultFilesByFormat = new HashMap<IMAGEFORMAT, String>();
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            resultFilesByFormat.put(IMAGEFORMAT.SVG, svgFilePath);
            resultFilesByFormat.putAll(DrawTools.convertSVGFileToOtherFormats(svgFilePath, baseFilePathNoExt, drawRes, formats));
        } catch (IOException ex) {
            DP.getInstance().e("Could not write ADJ folding graph file : '" + ex.getMessage() + "'.");
        }
        
        if( ! Settings.getBoolean("plcc_B_silent")) {
            StringBuilder sb = new StringBuilder();
            sb.append("        Output ADJ folding graph files: ");
            for(IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                sb.append("(").append(format.toString()).append(" => ").append(resultFilesByFormat.get(format)).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }
    
    /**
     * Draws a RED folding graph in all formats, returns a list of written files.
     * @param baseFilePathNoExt the base file path where to put the image (without dot and file extension)
     * @param drawBlackAndWhite whether to omit colors, only useful for non-protein graphs
     * @param formats an array of type DrawTools.IMAGEFORMAT. Do not include SVG, this will always be drawn anyways
     * @param pnfr
     * @return a map of formats to the corresponding output files written to disk
     */
    public static HashMap<IMAGEFORMAT, String> drawFoldingGraphRED(String baseFilePathNoExt, Boolean drawBlackAndWhite, IMAGEFORMAT[] formats, PTGLNotationFoldResult pnfr) {                
               
        DrawResult drawRes = SSEGraph.drawFoldingGraphREDG2D(pnfr);
        
        //System.out.println("drawProteinGraph: Basefilepath is '" + baseFilePathNoExt + "'.");
        String svgFilePath = baseFilePathNoExt + ".svg";
        HashMap<IMAGEFORMAT, String> resultFilesByFormat = new HashMap<IMAGEFORMAT, String>();
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            resultFilesByFormat.put(IMAGEFORMAT.SVG, svgFilePath);
            resultFilesByFormat.putAll(DrawTools.convertSVGFileToOtherFormats(svgFilePath, baseFilePathNoExt, drawRes, formats));
        } catch (IOException ex) {
            DP.getInstance().e("Could not write RED folding graph file : '" + ex.getMessage() + "'.");
        }
        
        if( ! Settings.getBoolean("plcc_B_silent")) {
            StringBuilder sb = new StringBuilder();
            sb.append("        Output RED folding graph files: ");
            for(IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                sb.append("(").append(format.toString()).append(" => ").append(resultFilesByFormat.get(format)).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }
    
    /**
     * Draws a SEQ folding graph in all formats, returns a list of written files.
     * @param baseFilePathNoExt the base file path where to put the image (without dot and file extension)
     * @param drawBlackAndWhite whether to omit colors, only useful for non-protein graphs
     * @param formats an array of type DrawTools.IMAGEFORMAT. Do not include SVG, this will always be drawn anyways
     * @param pnfr
     * @return a map of formats to the corresponding output files written to disk
     */
    public static HashMap<IMAGEFORMAT, String> drawFoldingGraphSEQ(String baseFilePathNoExt, Boolean drawBlackAndWhite, IMAGEFORMAT[] formats, PTGLNotationFoldResult pnfr) {                
               
        DrawResult drawRes = SSEGraph.drawFoldingGraphSEQG2D(pnfr);
        
        //System.out.println("drawProteinGraph: Basefilepath is '" + baseFilePathNoExt + "'.");
        String svgFilePath = baseFilePathNoExt + ".svg";
        HashMap<IMAGEFORMAT, String> resultFilesByFormat = new HashMap<IMAGEFORMAT, String>();
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            resultFilesByFormat.put(IMAGEFORMAT.SVG, svgFilePath);
            resultFilesByFormat.putAll(DrawTools.convertSVGFileToOtherFormats(svgFilePath, baseFilePathNoExt, drawRes, formats));
        } catch (IOException ex) {
            DP.getInstance().e("Could not write SEQ folding graph file : '" + ex.getMessage() + "'.");
        }
        
        if( ! Settings.getBoolean("plcc_B_silent")) {
            StringBuilder sb = new StringBuilder();
            sb.append("        Output SEQ folding graph files: ");
            for(IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                sb.append("(").append(format.toString()).append(" => ").append(resultFilesByFormat.get(format)).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }
    
    /**
     * Draws a KEY folding graph in all formats, returns a list of written files.
     * @param baseFilePathNoExt the base file path where to put the image (without dot and file extension)
     * @param drawBlackAndWhite whether to omit colors, only useful for non-protein graphs
     * @param formats an array of type DrawTools.IMAGEFORMAT. Do not include SVG, this will always be drawn anyways
     * @param pnfr
     * @return a map of formats to the corresponding output files written to disk
     */
    public static HashMap<IMAGEFORMAT, String> drawFoldingGraphKEY(String baseFilePathNoExt, Boolean drawBlackAndWhite, IMAGEFORMAT[] formats, PTGLNotationFoldResult pnfr) {                
               
        HashMap<IMAGEFORMAT, String> resultFilesByFormat = new HashMap<IMAGEFORMAT, String>();
        
        if( pnfr.getFoldingGraph().isBifurcated()) {
            // graph is bifurcated, KEY not supported
            System.out.println("        Output KEY folding graph files: none, FG is bifurcated -- KEY not supported.");
            return resultFilesByFormat;
        }
        
        /*
        boolean debug = true;
        if(debug) {
            FoldingGraph fg = pnfr.getFoldingGraph();
            if(fg.pdbid.equals("7tim") && fg.chainid.equals("A") && fg.graphType.equals("alpha")) {
                String file = System.getProperty("user.home") + "/fg_7tim_A_alpha_" + fg.getFoldingGraphNumber() + ".png";
                System.out.println("DEBUG: Drawing KEY FG # " + fg.getFoldingGraphNumber() + " to '" + file + "'.");
                fg.drawFoldingGraphKEY(file);
            }
        }
        */
        
        DrawResult drawRes = SSEGraph.drawFoldingGraphKEYG2D(pnfr);
        
        //System.out.println("drawProteinGraph: Basefilepath is '" + baseFilePathNoExt + "'.");
        String svgFilePath = baseFilePathNoExt + ".svg";
        
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            resultFilesByFormat.put(IMAGEFORMAT.SVG, svgFilePath);
            resultFilesByFormat.putAll(DrawTools.convertSVGFileToOtherFormats(svgFilePath, baseFilePathNoExt, drawRes, formats));
        } catch (IOException ex) {
            DP.getInstance().e("Could not write KEY folding graph file : '" + ex.getMessage() + "'.");
        }
        
        if( ! Settings.getBoolean("plcc_B_silent")) {
            StringBuilder sb = new StringBuilder();
            sb.append("        Output KEY folding graph files: ");
            for(IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                sb.append("(").append(format.toString()).append(" => ").append(resultFilesByFormat.get(format)).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }
    
    
    
    
    
    /**
     * Draws the protein graph image of this graph and returns the DrawResult.
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     * 
     * @param nonProteinGraph whether the graph is a non-protein graph and thus does NOT contain information on the relative SSE orientation in the expected way. If so, it is drawn in gray scale because the color code becomes useless (true => gray scale, false => color).
     * @return the DrawResult. You can write this to a file or whatever.
     */
    private static DrawResult drawProteinGraphG2D(Boolean nonProteinGraph, ProtGraph pg) {

        
        Integer numVerts = pg.numVertices();

        Boolean bw = nonProteinGraph;                                                  
        
        // All these values are in pixels
        // page setup
        PageLayout pl = new PageLayout(numVerts);        
        Position2D vertStart = pl.getVertStart();
        
        

            // ------------------------- Prepare stuff -------------------------
            // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
            //BufferedImage bi = new BufferedImage(pl.getPageWidth(), pl.getPageHeight(), BufferedImage.TYPE_INT_ARGB);
            
            SVGGraphics2D ig2;
            
            
            //if(Settings.get("plcc_S_img_output_format").equals("SVG")) {                    
                // Apache Batik SVG library, using W3C DOM tree implementation
                // Get a DOMImplementation.
                DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
                // Create an instance of org.w3c.dom.Document.
                String svgNS = "http://www.w3.org/2000/svg";
                Document document = domImpl.createDocument(svgNS, "svg", null);
                // Create an instance of the SVG Generator.
                ig2 = new SVGGraphics2D(document);
                //ig2.getRoot(document.getDocumentElement());
           // }
            //else {
            //    ig2 = (SVGGraphics2D)bi.createGraphics();
            //}
            
            ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // make background white
            ig2.setPaint(Color.WHITE);
            ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
            ig2.setPaint(Color.BLACK);

            //pl.drawAreaOutlines(ig2);
            // prepare font
            Font font = pl.getStandardFont();
            ig2.setFont(font);
            FontMetrics fontMetrics = ig2.getFontMetrics();

            // ------------------------- Draw header -------------------------

            // check width of header string
            String proteinHeader = "The " + pg.graphType + " protein graph of PDB entry " + pg.pdbid + ", chain " + pg.chainid + " [V=" + pg.numVertices() + ", E=" + pg.numSSEContacts() + "].";
            //Integer stringWidth = fontMetrics.stringWidth(proteinHeader);       // Should be around 300px for the text above
            Integer stringHeight = fontMetrics.getAscent();
            String sseNumberSeq;    // the SSE number in the primary structure, N to C terminus
            String sseNumberGraph;  // the SSE number in this graph, 1..(this.size)

            if(Settings.getBoolean("plcc_B_graphimg_header")) {
                ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
            }

            // ------------------------- Draw the graph -------------------------
            
            // Draw the edges as arcs
            java.awt.Shape shape;
            Arc2D.Double arc;
            ig2.setStroke(new BasicStroke(2));  // thin edges
            Integer edgeType, leftVert, rightVert, leftVertPosX, rightVertPosX, arcWidth, arcHeight, arcTopLeftX, arcTopLeftY, spacerX, spacerY, iChainID, jChainID;
            for(Integer i = 0; i < pg.sseList.size(); i++) {
                for(Integer j = i + 1; j < pg.sseList.size(); j++) {

                    // If there is a contact...
                    if(pg.containsEdge(i, j)) {

                        // determine edge type and the resulting color
                        edgeType = pg.getContactType(i, j);
                        if(edgeType.equals(SpatRel.PARALLEL)) { ig2.setPaint(Color.RED); }
                        else if(edgeType.equals(SpatRel.ANTIPARALLEL)) { ig2.setPaint(Color.BLUE); }
                        else if(edgeType.equals(SpatRel.MIXED)) { ig2.setPaint(Color.GREEN); }
                        else if(edgeType.equals(SpatRel.LIGAND)) { ig2.setPaint(Color.MAGENTA); }
                        else if(edgeType.equals(SpatRel.BACKBONE)) { ig2.setPaint(Color.ORANGE); }
                        else { ig2.setPaint(Color.LIGHT_GRAY); }

                        if(bw) { ig2.setPaint(Color.LIGHT_GRAY); }      // for non-protein graphs
                        
                        // ----- complex graph specific stuff -----
                        // determine chain of SSEs
                        iChainID = -1;
                        jChainID = -1;
                        for(Integer x = 0; x < pg.chainEnd.size(); x++){
                            if(i < pg.chainEnd.get(x)) {iChainID = x; break;}
                        }
                        for(Integer x = 0; x < pg.chainEnd.size(); x++){
                            if(j < pg.chainEnd.get(x)) {jChainID = x; break;}
                        }
                        if (!Objects.equals(iChainID, jChainID)) {ig2.setPaint(Color.PINK);}
                        // ----- end complex graph specific stuff -----

                        // determine the center of the arc and the width of its rectangle bounding box
                        if(i < j) { leftVert = i; rightVert = j; }
                        else { leftVert = j; rightVert = i; }
                        leftVertPosX = pl.getVertStart().x + (leftVert * pl.vertDist);
                        rightVertPosX = pl.getVertStart().x + (rightVert * pl.vertDist);

                        arcWidth = rightVertPosX - leftVertPosX;
                        arcHeight = arcWidth / 2;

                        arcTopLeftX = leftVertPosX;
                        arcTopLeftY = pl.getVertStart().y - arcHeight / 2;

                        spacerX = pl.vertRadius;
                        spacerY = 0;

                        // draw it                                                
                        arc = new Arc2D.Double(arcTopLeftX + spacerX, arcTopLeftY + spacerY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
                        shape = ig2.getStroke().createStrokedShape(arc);
                        ig2.fill(shape);

                    }
                }
            }

            // Draw the vertices as circles
            Ellipse2D.Double circle;
            Rectangle2D.Double rect;
            ig2.setStroke(new BasicStroke(2));
            for(Integer i = 0; i < pg.sseList.size(); i++) {
                
                // pick color depending on SSE type
                if(pg.sseList.get(i).isHelix()) { ig2.setPaint(Color.RED); }
                else if(pg.sseList.get(i).isBetaStrand()) { ig2.setPaint(Color.BLACK); }
                else if(pg.sseList.get(i).isLigandSSE()) { ig2.setPaint(Color.MAGENTA); }
                else if(pg.sseList.get(i).isOtherSSE()) { ig2.setPaint(Color.GRAY); }
                else { ig2.setPaint(Color.LIGHT_GRAY); }

                if(bw) { ig2.setPaint(Color.GRAY); }      // for non-protein graphs
                
                // draw a shape based on SSE type
                if(pg.sseList.get(i).isBetaStrand()) {
                    // beta strands are black, filled squares
                    rect = new Rectangle2D.Double(vertStart.x + (i * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    ig2.fill(rect);
                    
                }
                else if(pg.sseList.get(i).isLigandSSE()) {
                    // ligands are magenta circles (non-filled)
                    circle = new Ellipse2D.Double(vertStart.x + (i * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    //ig2.fill(circle);
                    ig2.setStroke(new BasicStroke(3));  // this does NOT get filled, so give it a thicker border
                    ig2.draw(circle);
                    ig2.setStroke(new BasicStroke(2));
                }
                else {
                    // helices and all others are filled circles (helices are red circles, all others are gray circles)
                    circle = new Ellipse2D.Double(vertStart.x + (i * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    ig2.fill(circle);                    
                }
            }
            
            // Draw the markers for the N-terminus and C-terminus if there are any vertices in this graph            
            ig2.setStroke(new BasicStroke(2));
            ig2.setPaint(Color.BLACK);
            
            if( ! bw) {
                if(pg.sseList.size() > 0) {                    
                    ig2.drawString("N", vertStart.x - pl.vertDist, vertStart.y + 20);    // N terminus label
                    ig2.drawString("C", vertStart.x + pg.sseList.size() * pl.vertDist, vertStart.y + 20);  // C terminus label
                }
            }
                        
            // ************************************* footer **************************************
            
            if(Settings.getBoolean("plcc_B_graphimg_footer")) {
            
                // Draw the vertex numbering into the footer
                // Determine the dist between vertices that will have their vertex number printed below them in the footer field
                Integer printNth = 1;
                if(pg.sseList.size() > 9) { printNth = 1; }
                if(pg.sseList.size() > 99) { printNth = 2; }
                if(pg.sseList.size() > 999) { printNth = 3; }

                // line markers: S for sequence order, G for graph order
                Integer lineHeight = pl.textLineHeight;            
                if(pg.sseList.size() > 0) {                                            
                    ig2.drawString("PG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                }
                else {
                    ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
                }
                iChainID = -1;
                for(Integer i = 0; i < pg.sseList.size(); i++) {
                    // Draw label for every nth vertex
                    if((i + 1) % printNth == 0) {
                        sseNumberGraph = "" + (i + 1);
                        sseNumberSeq = "" + (pg.sseList.get(i).getSSESeqChainNum());
                        //stringWidth = fontMetrics.stringWidth(sseNumberSeq);
                        stringHeight = fontMetrics.getAscent();                                        

                        ig2.drawString(sseNumberGraph, pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                        ig2.drawString(sseNumberSeq, pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));                    
                        
                        // determine chain of SSEs
                        for(Integer x = 0; x < pg.chainEnd.size(); x++){
                            if(i < pg.chainEnd.get(x)) {iChainID = x; break;}
                        }
                        if(iChainID != -1) {ig2.drawString(pg.allChains.get(iChainID).getPdbChainID(), pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight * 2) + (stringHeight / 4));}
                    }
                }

                if(Settings.getBoolean("plcc_B_graphimg_legend")) {
                    if(iChainID != -1){
                        SSEGraph.drawLegend(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + lineHeight * 3 + (stringHeight / 4)), pl, pg);
                    }
                    else{
                        SSEGraph.drawLegend(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + lineHeight * 2 + (stringHeight / 4)), pl, pg);
                    }
                }
            
            }
            
            // all done, write the image to disk
            //if(Settings.get("plcc_S_img_output_format").equals("SVG")) {
            
            //boolean useCSS = true;
            //FileOutputStream fos = new FileOutputStream(new File("/tmp/mySVG.svg"));
            //Writer out = new OutputStreamWriter(fos, "UTF-8");
            //ig2.stream(out, useCSS); 
            
            Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
            
            DrawResult drawRes = new DrawResult(ig2, roi);
            return drawRes;                                                                         
    }
    
    
    /**
     * Creates an encapsulated postscript (EPS) format image file of the folding graph.
     * See http://xmlgraphics.apache.org/commons/postscript.html for documentation on this.
     * @param outputFile the target file
     * @throws IOException In case of an I/O error
     */
    public static void drawProteinGraphToEncPostscriptFile(File outputFile) throws IOException {
        OutputStream out = new java.io.FileOutputStream(outputFile);
        out = new java.io.BufferedOutputStream(out);
        try {
            //Instantiate the EPSDocumentGraphics2D instance
            EPSDocumentGraphics2D g2d = new EPSDocumentGraphics2D(false);
            g2d.setGraphicContext(new org.apache.xmlgraphics.java2d.GraphicContext());

            //Set up the document size
            g2d.setupDocument(out, 400, 200); //400pt x 200pt

            //Paint a bounding box
            g2d.drawRect(0, 0, 400, 200);

            //A few rectangles rotated and with different color
            Graphics2D copy = (Graphics2D)g2d.create();
            int c = 12;
            for (int i = 0; i < c; i++) {
                float f = ((i + 1) / (float)c);
                Color col = new Color(0.0f, 1 - f, 0.0f);
                copy.setColor(col);
                copy.fillRect(70, 90, 50, 50);
                copy.rotate(-2 * Math.PI / (double)c, 70, 90);
            }
            copy.dispose();

            //Some text
            g2d.rotate(-0.25);
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("sans-serif", Font.PLAIN, 36));
            g2d.drawString("Hello world!", 140, 140);
            g2d.setColor(Color.RED.darker());
            g2d.setFont(new Font("serif", Font.PLAIN, 36));
            g2d.drawString("Hello world!", 140, 180);

            //Cleanup
            g2d.finish();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
    
    

    
    /**
     * This function creates a connector between the 2D points (startX, startY) and (targetX, targetY). This connector is returned as a list of Shape
     * objects that can be painted on a Graphics2D canvas by stroking or filling them (e.g., G2Dinstance.fill(shapeInstance) or similar). The shapes
     * are created using the given Stroke (call G2Dinstance.getStroke() to use the current Stroke).
     * 
     * If the start and end points are on the same height (i.e., startY == targetY), the connector will look 
     * similar to an 'S' and will consist of a single shape (an arc forming a half circle). Otherwise it will
     * consist of 3 Shapes (two arcs and a line).
     * 
     * You can choose whether the connector should start upwards or downwards from (startX, startY) using the startUpwards parameter.
     * 
     * @param startX the x coordinate of the start point
     * @param startY the y coordinate of the start point
     * @param targetX the x coordinate of the end point
     * @param targetY the y coordinate of the end point
     * @param stroke the Stroke to use. You can get one from your Graphics2D instance.
     * @param startUpwards whether to start upwards from the 2D Point (startX, startY). If this is false, downwards is used instead.
     * @return a list of Shapes that can be painted on a G2D canvas.
     */
    public static ArrayList<Shape> getArcConnector(Integer startX, Integer startY, Integer targetX, Integer targetY, Stroke stroke, Boolean startUpwards) {

        // System.out.print("getArcConnector: startX=" + startX + ", startY=" + startY + ", targetX=" + targetX + ", targetY=" + targetY + ", startUpwards=" + startUpwards.toString() + ": ");
        
        if(startY.equals(targetY)) {
            // System.out.print("getting simple connector(" + startY + " == " + targetY + ").\n");
            return(getSimpleArcConnector(startX, startY, targetX, stroke, startUpwards));            
        }
        else {
            // System.out.print("getting crossover connector(" + startY + " != " + targetY + ").\n");
            return(getCrossoverArcConnector(startX, startY, targetX, targetY, stroke, startUpwards));
        }        
    }
    
    
    /**
     * The function that implements a simple half circle-shaped arc connector between the 2D points (startX, startY) and (targetX, targetY) in the
     * requested direction. Internal function, call the more general getArcConnector() function instead.
     * 
     * You can choose whether the connector should start upwards or downwards from (startX, startY) using the startUpwards parameter.
     * 
     *   upwards:                downwards:
     * 
     *      __                   s    t
     *     /  \                  |    |
     *    |    |                  \__/
     *    s    t                
     * 
     * @param startX the x coordinate of the start point
     * @param bothY the y coordinate of both the start point and the end point
     * @param targetX the x coordinate of the end point
     * @param stroke the Stroke to use. You can get one from your Graphics2D instance.
     * @param startUpwards whether to start upwards from the 2D Point (startX, startY). If this is false, downwards is used instead.
     * @return a list of Shapes that can be painted on a G2D canvas.
     */
    protected static ArrayList<Shape> getSimpleArcConnector(Integer startX, Integer bothY, Integer targetX, Stroke stroke, Boolean startUpwards) {
        
        ArrayList<Shape> parts = new ArrayList<Shape>();
        Integer leftVertPosX, rightVertPosX, arcWidth, arcHeight, vertStartY, arcTopLeftX, arcTopLeftY;
        
        // ensure left-right order
        if(startX < targetX) { 
            leftVertPosX = startX;
            rightVertPosX = targetX; }
        else
        { 
            leftVertPosX = targetX; 
            rightVertPosX = startX;
        }
        
        // stuff common for up/down
        vertStartY = bothY;
        arcWidth = rightVertPosX - leftVertPosX;
        arcHeight = arcWidth / 2;
        arcTopLeftX = leftVertPosX;
        arcTopLeftY = vertStartY - arcHeight / 2;
        
        Arc2D arc;
        if(startUpwards) {                                                           
            // create the Shape
            arc = new Arc2D.Double(arcTopLeftX, arcTopLeftY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
        }
        else {
            arc = new Arc2D.Double(arcTopLeftX, arcTopLeftY, arcWidth, arcHeight, 180, 180, Arc2D.OPEN);
        }
        
        Shape shape = stroke.createStrokedShape(arc);
        // add it to the parts list
        parts.add(shape);
        return(parts);
    }
    
    
    /**
     * The function that implements a more complex 'S'-shaped arc connector between the 2D points (startX, startY) and (targetX, targetY) in the
     * requested direction. Internal function, call the more general getArcConnector() function instead.
     * 
     * This connector consists of 3 Shapes: two half-circles and a line (think of the letter 'S').
     * 
     * You can choose whether the connector should start upwards or downwards from (startX, startY) using the startUpwards parameter.
     * 
     * upwards:                downwards:
     *      __                     __
     *     /  \                   /   \
     *    |    |                  |    |
     *    s    |                  |    t
     *         |   t         s    |
     *         |   |         |    |
     *         \__/           \__/
     * 
     * @param startX the x coordinate of the start point
     * @param startY the y coordinate of the start point
     * @param targetX the x coordinate of the end point
     * @param targetY the y coordinate of the end point
     * @param stroke the Stroke to use. You can get one from your Graphics2D instance.
     * @param startUpwards whether to start upwards from the 2D Point (startX, startY). If this is false, downwards is used instead.
     * @return a list of Shapes that can be painted on a G2D canvas.
     */
    protected static ArrayList<Shape> getCrossoverArcConnector(Integer startX, Integer startY, Integer targetX, Integer targetY, Stroke stroke, Boolean startUpwards) {
        
        Integer upwards = 0;
        Integer downwards = 180;
        
        ArrayList<Shape> parts = new ArrayList<Shape>();
        Integer leftVertPosX, rightVertPosX, bothArcsSumWidth, bothArcsSumHeight, vertStartY, leftArcHeight, leftArcWidth, rightArcHeight, rightArcWidth, arcWidth, arcEllipseHeight;
        Integer leftArcUpperLeftX, leftArcUpperLeftY, centerBetweenBothArcsX, centerBetweenBothArcsY, leftArcEndX, leftArcEndY, rightArcEndX, rightArcEndY;
        Integer leftArcLowerRightX, leftArcLowerRightY, leftArcUpperRightX, leftArcUpperRightY;
        Integer rightArcLowerRightX, rightArcLowerRightY, rightArcUpperRightX, rightArcUpperRightY, rightArcUpperLeftX, rightArcUpperLeftY;
        Integer lineStartX, lineStartY, lineEndX, lineEndY, lineLength;
        
        
        // ensure left-right order
        if(startX < targetX) { 
            leftVertPosX = startX;
            rightVertPosX = targetX; }
        else
        { 
            leftVertPosX = targetX; 
            rightVertPosX = startX;
        }
        
        // stuff common for up/down
        vertStartY = startY;
        lineLength = Math.abs(startY - targetY);
        
        bothArcsSumWidth = rightVertPosX - leftVertPosX;
        leftArcWidth = rightArcWidth = bothArcsSumWidth / 2;
        
        bothArcsSumHeight = bothArcsSumWidth;
        //bothArcsSumHeight = bothArcsSumWidth;
        leftArcHeight = rightArcHeight = arcEllipseHeight = bothArcsSumHeight / 2;
        
        centerBetweenBothArcsX = rightVertPosX - (bothArcsSumWidth / 2);    // this is where the upright line is created
        centerBetweenBothArcsY = vertStartY;
        
        leftArcUpperLeftX = leftVertPosX;
        leftArcUpperLeftY = vertStartY - (arcEllipseHeight / 2);
        
        leftArcLowerRightX = leftArcUpperLeftX + leftArcWidth;
        leftArcLowerRightY = leftArcUpperLeftY + leftArcHeight - (arcEllipseHeight / 2);
        
        leftArcUpperRightX = leftArcUpperLeftX + leftArcWidth;
        leftArcUpperRightY = leftArcUpperLeftY;
        
        // everything computed, now start to create the shapes based on the required arc starting angle (up or down)
        Shape shape; Arc2D arc;
        
        if(startUpwards) {                  
            
            // left arc starts in lower left corner and ends in lower right corner of its bounding rectangle (looks like an inverted 'U')
            leftArcEndX = leftArcLowerRightX;
            leftArcEndY = leftArcLowerRightY;
           
            // create the Shape for the left arc, which starts upwards in this case
            arc = new Arc2D.Double(leftArcUpperLeftX, leftArcUpperLeftY, leftArcWidth, leftArcHeight, upwards, 180, Arc2D.OPEN);
            shape = stroke.createStrokedShape(arc);
            parts.add(shape);
            
            // create the Shape for the line, which goes straight down
            lineStartX = leftArcEndX;
            lineStartY = leftArcEndY;
            lineEndX = leftArcEndX;
            lineEndY = leftArcEndY + lineLength;    // the line goes downwards, therefor the '+' in this case!
            Line2D l = new Line2D.Double(lineStartX, lineStartY, lineEndX, lineEndY);
            shape = stroke.createStrokedShape(l);
            parts.add(shape);
            
            // create the Shape for the right arc. it starts in upper left corner and ends in upper right corner of its bounding rectangle (looks like a 'U').
            rightArcUpperLeftX = lineEndX;
            rightArcUpperLeftY = lineEndY;
            arc = new Arc2D.Double(rightArcUpperLeftX, rightArcUpperLeftY - (arcEllipseHeight / 2), rightArcWidth, rightArcHeight, downwards, 180, Arc2D.OPEN);
            shape = stroke.createStrokedShape(arc);
            parts.add(shape);
        }
        else {
            // left arc ends in upper right corner of its bounding rectangle
            leftArcEndX = leftArcUpperRightX;
            leftArcEndY = leftArcUpperRightY;
            
            // create the Shape for the left arc, which starts downwards in this case
            arc = new Arc2D.Double(leftArcUpperLeftX, leftArcUpperLeftY, leftArcWidth, leftArcHeight, downwards, 180, Arc2D.OPEN);
            shape = stroke.createStrokedShape(arc);
            parts.add(shape);
            
            // create the Shape for the line, which goes straight up
            lineStartX = leftArcEndX;
            lineStartY = leftArcEndY + (arcEllipseHeight / 2);
            lineEndX = leftArcEndX;
            lineEndY = lineStartY - lineLength;    // the line goes downwards, therefor the '-' in this case!
            Line2D l = new Line2D.Double(lineStartX, lineStartY, lineEndX, lineEndY);
            shape = stroke.createStrokedShape(l);
            parts.add(shape);
            
            // create the Shape for the right arc. it starts in upper left corner and ends in upper right corner of its bounding rectangle (looks like a 'U').
            rightArcUpperLeftX = lineEndX;
            rightArcUpperLeftY = lineEndY - (arcEllipseHeight / 2);
            arc = new Arc2D.Double(rightArcUpperLeftX, rightArcUpperLeftY, rightArcWidth, rightArcHeight, upwards, 180, Arc2D.OPEN);
            shape = stroke.createStrokedShape(arc);
            parts.add(shape);

        }
        return(parts);
    }
 
    
    /**
     * Creates an outlined arrow (7 lines) on the given Graphics2D context, using the Polygon class. The arrow points straight up, thus headX is = tailX and does not have to be supplied.
     * You can transform this is you want to change its angle. It's a shape so you can stroke or fill it, too.
     * You should also check out getDefaultArrowPolygon() for an easy way to get arrows with the same width, length and head length.
     *
     * @param tailX The x location of the center of the "tail" of the arrow
     * @param tailY The y location of the center of the "tail" of the arrow
     * @param headY The y location of the "head" of the arrow
     * @param widthTail The width of the arrow at the tail
     * @param widthHead The width of the arrow at the broadest part of the head. Note that widthHead > widthTail is required if this is meant to make sense.
     */
    protected Polygon getArrowPolygon(int headY, int tailX, int tailY, int widthTail, int widthHead, int lengthHead) {
        
        int numPoints = 7;
        int[] xPoints = new int[numPoints] ;
        int[] yPoints = new int[numPoints] ;
     
        // Create all points of the arrow.
        
        //Start at the left point of the tail (the bottom left point when the arrow is pointing upwards) and add the other points counter-clockwise.
        xPoints[0] = tailX - (widthTail / 2);
        yPoints[0] = tailY;
        
        // The right point of the tail.
        xPoints[1] = tailX + (widthTail / 2);
        yPoints[1] = tailY;
        
        // The right point of the start of the head.
        xPoints[2] = tailX + (widthTail / 2);
        yPoints[2] = headY + lengthHead;
        
        // The right-most point of the head.
        xPoints[3] = tailX + (widthHead / 2);
        yPoints[3] = headY + lengthHead;
        
        // The arrow tip
        xPoints[4] = tailX;
        yPoints[4] = headY;
        
        // The left-most point of the head.
        xPoints[5] = tailX - (widthHead / 2);
        yPoints[5] = headY + lengthHead;
        
        // The left point of the start of the head.
        xPoints[6] = tailX - (widthTail / 2);
        yPoints[6] = headY + lengthHead;
        
        // The line closing the polygon (between the last we just set and the first point) is automatically inserted to close the Polygon
        return(new Polygon(xPoints, yPoints, numPoints));     
    }
    
    
    /**
     * Creates an outlined barrel (4 lines) on the given Graphics2D context, using the Polygon class. The barrel points straight up.
     * You can transform this is you want to change its angle. It's a shape so you can stroke or fill it, too.
     * You should also check out getDefaultBarrelPolygon() for an easy way to get barrels with the same width and length.
     *
     * @param tailX The x location of the center of the "tail" of the barrel
     * @param tailY The y location of the center of the "tail" of the barrel
     * @param headX The x location of the "head" of the barrel
     * @param headY The y location of the "head" of the barrel
     * @param width The width of the barrel
     */
    protected Polygon getBarrelPolygon(int headX, int headY, int tailX, int tailY, int width) {
        
        int numPoints = 4;
        
        int[] xPoints = new int[numPoints] ;
        int[] yPoints = new int[numPoints] ;
     
        // Create all points of the arrow.
        
        //Start at the left point of the tail and add the other points counter-clockwise.
        xPoints[0] = tailX - (width / 2);
        yPoints[0] = tailY;
        
        // The right point of the tail.
        xPoints[1] = tailX + (width / 2);
        yPoints[1] = tailY;
        
        // The right point of the head.
        xPoints[2] = headX + (width / 2);
        yPoints[2] = headY;
        
        // The left point of the head.
        xPoints[3] = headX - (width / 2);
        yPoints[3] = headY;
                
        // The line closing the polygon (between the last we just set and the first point) is automatically inserted to close the Polygon
        return(new Polygon(xPoints, yPoints, numPoints));     
    }
    
    
    /**
     * Just a helper function that sets default values for the width of the barrel.
     * See getBarrelPolygon() for details on the other parameters.
     */
    protected Polygon getDefaultBarrelPolygon(int headY, int bothX, int tailY) {
        
        int defaultWidth = 10;
        
        return(getBarrelPolygon(bothX, headY, bothX, tailY, defaultWidth));
    }
    

    
    /**
     * Just a helper function that sets default values for the width of the arrow. See the first 3 parameters of the drawOutlinedArrow() function for parameter explanation. 
     */
    protected Polygon getDefaultArrowPolygon(int headY, int bothX, int tailY) {
        
        int defaultWidthTail = 10;
        int defaultWidthHead = 20;
        int defaultLengthHead = 20;
        
        return(getArrowPolygon(headY, bothX, tailY, defaultWidthTail, defaultWidthHead, defaultLengthHead));
    }
    
    
    /**
     * Sets the info fields of this graph, defining the PDB ID as 'pdbid', the chain id as 'chainid' and the graph type as 'graphType'. Also sets the meta data.
     * @param pdbid the PDB identifier, e.g., "8icd"
     * @param chainid the PDB chain ID, e.g., "A"
     * @param graphType the graph type, e.g., "albe"
     */
    public void setInfo(String pdbid, String chainid, String graphType) {
        this.pdbid = pdbid;
        this.chainid = chainid;
        this.graphType = graphType;
        
        this.metadata.put("pdbid", pdbid);
        this.metadata.put("chainid", chainid);
        this.metadata.put("graphtype", graphType);
        this.metadata.put("graphclass", "protein graph");
    }
    
    /**
     * Adds the given meta data to this graph.
     * @param md the meta data
     */
    public void addMetadata(HashMap<String, String> md) {
        for(String key : md.keySet()) {
            this.metadata.put(key, md.get(key));
        }
    }
    
    
    
    /**
     * Determines whether this graph contains a vertex with degree n.
     * @return true if it does, false otherwise
     */
    public Boolean hasVertexWithDegree(Integer n) {

        Boolean hasVert = false;

        for(Integer i = 0; i < this.size; i++) {
            if(this.degreeOfVertex(i).equals(n)) {
                hasVert = true;
            }
        }

        return(hasVert);
    }
    
    
    /**
     * Determines the minimum vertex degree in this graph.
     * @return the minimum vertex degree
     */
    public Integer minVertexDegree() {

        Integer min = Integer.MAX_VALUE;

        for(Integer i = 0; i < this.size; i++) {
            if(this.degreeOfVertex(i) < min) {
                min = this.degreeOfVertex(i);
            }
        }

        if(this.size <= 1) {
            return(0);
        }
        return(min);
    }
    
    
    /**
     * Determines whether this graph has a valid spatial ordering. It computes this ordering if that
     * has not already been done.
     * 
     * @return true if the graph has such an ordering, false otherwise
     */
    public Boolean hasSpatialOrdering() {
        
        if(this.spatOrder == null) {        
            this.computeSpatialVertexOrdering();
        }
        
        if(this.spatOrder.size() == this.size) {
            return(true);
        } else {
            return(false);
        }
        
    }
    
    /**
     * Returns the SSEString of the graph in sequence order, e.g., "HHEHEHEHEHEHHEEHL".
     * @return the SSEString in AA sequence order, e.g., "HHEHEHEHEHEHHEEHL".
     */
    public String getSSEStringSequential() {
        String s = "";
        for(SSE sse : this.sseList) {
            s += sse.getSseType();
        }
        return(s);
    }
    
    
    /**
     * Generates a string representation of this graph which is similart to the Tops+ string as described in: Mallika Veeramalai and David Gilbert.
     * "A Novel Method for Comparing Topological Models of Protein Structures Enhanced with Ligand Information"
     * Bioinformatics, (2008), 24(23):2698-2705; doi:10.1093/bioinformatics/btn518
     * @return a string representation of this graph
     * 
     */
    public String getGraphPlusString() {
        String graphString = "";
        SSE sse, contactSSE;
        String contactType;
        
        for(Integer i = 0; i < this.size; i++) {
            sse = this.getSSEBySeqPosition(i);
            
            // the SSE itself, format: <SSE_type>[<dssp_start>-<length_in_residues>]
            graphString += sse.getSseType() + "[" + sse.getStartDsspNum() + "-" + sse.getLength() + "]";
            
            // contacts for this SSE, format: (<distance_between_SSEs><spatial_relation>,<other_sse_type>=<sse_or_ligand_name>)
            HashMap<Integer, String> contacts = this.getAllSpatialContactsForSSE(i);
            for(Integer otherSSEIndex : contacts.keySet()) {
                contactType = contacts.get(otherSSEIndex);
                graphString += "(" + ((otherSSEIndex + 1) - (i + 1)) + contactType + ":";
                contactSSE = this.sseList.get(otherSSEIndex);
                if(contactSSE.isLigandSSE()) {
                    graphString += "L=" + contactSSE.getLigandName3() + ")";
                } else {
                    graphString += "S=" + contactSSE.getSseType() + ")";
                }
            }
            
            if(i < (this.size - 1)) {
                graphString += ",";
            }
        }
        
        return graphString;        
    }
    
    
    /**
     * Returns all contacts for the SSE at the given index.
     * @param sseIndex the index of the SSE
     * @return the contacts as a HashMap. Keys are indices of SSEs that the SSE at 'sseIndex' is in contact with, values are their spatial relation (as a SpatRel string).
     */
    public HashMap<Integer, String> getAllSpatialContactsForSSE(Integer sseIndex) {
        HashMap<Integer, String> contacts = new HashMap<Integer, String>();
        
        for(Integer i=0; i < this.size; i++) {
            if(this.containsEdge(sseIndex, i) && sseIndex != i) {
                contacts.put(i, this.getContactTypeString(sseIndex, i));
            }
        }
        
        return(contacts);
    }
    
    
    /**
     * Returns the contact type string for the contact between the SSEs with indices i and j.
     * @param i index of the first SSE
     * @param j index of the second SSE
     * @return the spatial relation of i and j, as a SpatRel String (e.g., "m" for mixed).
     */
    public String getContactTypeString(Integer i, Integer j) {
        return(SpatRel.getString(this.matrix[i][j]));
    }
    
    /**
     * Returns the contact type character for the contact between the SSEs with indices i and j.
     * @param i index of the first SSE
     * @param j index of the second SSE
     * @return the spatial relation of i and j, as a SpatRel character (e.g., 'm' for mixed).
     */
    public Character getContactTypeCharacter(Integer i, Integer j) {
        return(SpatRel.getCharacter(this.matrix[i][j]));
    }
    
    /**
     * Returns the PTGL .graph file contact type string for the contact between the SSEs with indices i and j.
     * Note that .graph is the input format for Patrick's PTGL notation Perl script.
     * @param i index of the first SSE
     * @param j index of the second SSE
     * @return the spatial relation of i and j, as a PTGL .graph format string
     */
    public String getContactTypeStringPTGLGraph(Integer i, Integer j) {
        int r = this.matrix[i][j];
        String res;
        if(r == SpatRel.MIXED) {
            return "X";
        }
        else if(r == SpatRel.PARALLEL) {
            return "P";
        }
        else if(r == SpatRel.ANTIPARALLEL) {
            return "A";
        }
        else if(r == SpatRel.LIGAND) {
            return "L";
        }
        else {
            DP.getInstance().e("SSEGraph", "getContactTypeStringPTGLGraph(): No PTGL string known for contact type int = " + r + ".");
            return "?";
        }
    }
    
    /**
     * Returns the SSEString of the graph in spatial order if this graph has such an order, e.g., "HHEHEHEHEHEHHEEHL".
     * @return the SSEString in spatial order or the empty string "" if no such order exists for this graph
     */
    public String getSSEStringSpatial() {
        
        String spatSSEString = "";
        Integer seqIndexCurrentSSE;
        
        if(this.hasSpatialOrdering()) {
            
            for(Integer i = 0; i < this.spatOrder.size(); i++) {
                seqIndexCurrentSSE = this.spatOrder.get(i);
                spatSSEString += this.sseList.get(seqIndexCurrentSSE).getSseType();
            }
        }
        return(spatSSEString);
    }
    
    
    
    
    /**
     * Computes the spatial, linear vertex ordering for this graph and assigns it to the class variable 'this.spatOrder'.
     * Note that the ordering may be invalid because no such order exists for certain graphs. In this case, the length 
     * of the spatOrder ArrayList is != the number of vertices in this graph.
     * 
     * The SSEs of this graph also get their spatialIndex property set by this function.
     * 
     * This function has to be called after all contacts have been added to the graph! Adding or removing edges from/to it will destroy the ordering.
     * 
     */
    public void computeSpatialVertexOrdering() {
        this.spatOrder = this.getSpatialOrderingOfVertexIndices();
        
        /*
        for(Integer i = 0; i < this.size; i++) {
            this.getSSEBySeqPosition(i).setSpatialIndexInGraph(-2); // This makes no sense: it is applied to the SSE, which may be part of many different graphs!
        }
        
        for(Integer i = 0; i < spatOrder.size(); i++) {
            this.getSSEBySeqPosition(i).setSpatialIndexInGraph(spatOrder.get(i));   // This makes no sense: it is applied to the SSE, which may be part of many different graphs!
        }
        */
    }
            
    /**
     * Returns a short string description of this graph via its meta data like PDB ID, graph type, etc., without data on vertices and edges. 
     * @return a short string description of this graph
     */
    @Override public String toString() {
        String mgt = (this.isFoldingGraph() ? "FG" : "PG");
        return("[" + mgt + " " + this.graphType + " graph of PDB " + this.pdbid + " chain " + this.chainid + "]");
    }
    
    
    /**
     * Returns a very short string description of this graph. 
     * @return a string in format 'PDBID-CHAIN-GRAPHTYPE'.
     */
    public String toShortString() {
        return(this.pdbid + "-" + this.chainid + "-" + this.graphType + "[" + this.numVertices() + "," + this.numSSEContacts() + "]");
    }
    
    
    public String getPdbid() { return(this.pdbid); }
    public String getChainid() { return(this.chainid); }
    public String getGraphType() { return(this.graphType); }        
    public void setPdbid(String s) { this.pdbid = s; this.metadata.put("pdbid", s); }
    public void setChainid(String s) { this.chainid = s; this.metadata.put("chainid", s);}
    public void setGraphType(String s) { this.graphType = s; this.metadata.put("graphtype", s);}
    
    /** Returns the size of this graph, i.e., the number of vertices in it. */
    public Integer getSize() { return(this.numVertices()); }
    
    
    /**
     * Returns the meta data as a single line. Useful for exporting to graph file formats that do only support a single comment string
     * instead of arbitrary custom info fields.
     * @return the meta data (pdbid, organism, etc) as a single line
     */
    public String getOneLineMetadataString() {
        String sep = "";
        try {
            sep = Settings.get("plcc_S_graph_metadata_splitstring");
        } catch(java.lang.NullPointerException e) {
            // no settings object yet
            return "";
        }
        String mds = "";
        Integer numMD = this.metadata.keySet().size(); Integer cur = 0;
        for(String key : this.metadata.keySet()) {
            mds += key + "=" + this.metadata.get(key) + "";
            if(cur < (this.metadata.keySet().size() - 1)) {
                mds += sep;
            }
        }
        return mds;
    }
    
    
    /** Returns a Graph Modelling Language format representation of this graph.
     *  See http://www.fim.uni-passau.de/fileadmin/files/lehrstuhl/brandenburg/projekte/gml/gml-technical-report.pdf for the publication 
     * and http://en.wikipedia.org/wiki/Graph_Modelling_Language for a brief description.
     * @return the GML format graph string
     */
    @Override
    public String toGraphModellingLanguageFormat() {
        
        StringBuilder gmlf = new StringBuilder();
        
        // we put meta data in comments for GML language graphs
        try {
            if(Settings.getBoolean("plcc_B_add_metadata_comments_GML")) {
                for(String key : this.metadata.keySet()) {
                    gmlf.append("//[VPLG_METADATA] ").append(key).append("=").append(this.metadata.get(key)).append("\n");
                }
            }
        } catch(java.lang.NullPointerException e) {
            // no settings object yet, assume not to write stuff
        }
        
        // print the header
        String shortStr = this.toShortString();
        String startNode = "  node [";
        String endNode   = "  ]";
        String startEdge = "  edge [";
        String endEdge   = "  ]";
        
        gmlf.append("graph [\n");
        gmlf.append("  id ").append(1).append("\n");
        gmlf.append("  label \"" + "VPLG Protein Graph ").append(shortStr).append("\"\n");
        gmlf.append("  comment \"").append(this.getOneLineMetadataString()).append("\"\n");
        gmlf.append("  directed 0\n");
        gmlf.append("  isplanar 0\n");
        try {
            gmlf.append("  creator \"PLCC version ").append(Settings.getVersion()).append("\"\n");
        }catch(java.lang.NullPointerException e) {
            // no settings object yet, assume not to write stuff
        }
        
        gmlf.append("  pdb_id \"" + this.pdbid + "\"\n");
        gmlf.append("  chain_id \"" + this.chainid + "\"\n");
        gmlf.append("  graph_type \"" + this.graphType + "\"\n");
        gmlf.append("  is_protein_graph " + (this.isProteinGraph ? "1" : "0") + "\n");
        gmlf.append("  is_folding_graph " + (this.isProteinGraph ? "0" : "1") + "\n");
        gmlf.append("  is_SSE_graph 1\n");
        gmlf.append("  is_AA_graph 0\n");
        gmlf.append("  is_all_chains_graph " + (this.isComplexGraph() ? "1" : "0") + "\n");
        
        // print all nodes
        SSE vertex;
        for(Integer i = 0; i < this.size; i++) {
            vertex = this.sseList.get(i);
            gmlf.append(startNode).append("\n");
            gmlf.append("    id ").append(i).append("\n");
            gmlf.append("    label \"").append(i).append("-").append(vertex.getSseType()).append("\"\n");
            gmlf.append("    num_in_chain ").append(vertex.getSSESeqChainNum()).append("\n");            
            gmlf.append("    num_residues ").append(vertex.getLength()).append("\n");
            
            gmlf.append("    pdb_res_start \"").append(vertex.getStartPdbResID()).append("\"\n");
            gmlf.append("    pdb_res_end \"").append(vertex.getEndPdbResID()).append("\"\n");            
            
            gmlf.append("    dssp_res_start ").append(vertex.getStartDsspNum()).append("\n");
            gmlf.append("    dssp_res_end ").append(vertex.getEndDsspNum()).append("\n");
            
            gmlf.append("    pdb_residues_full \"").append(vertex.getAllPdbResiduesString(",")).append("\"\n");
            
            gmlf.append("    aa_sequence \"").append(vertex.getAASequence()).append("\"\n");
            
            if(this.isFoldingGraph()) {
                gmlf.append("    index_in_parent_pg \"").append(((FoldingGraph)this).getVertexIndexListInParentGraph().get(i)).append("\"\n");                
            }
            
            if(vertex.isLigandSSE()) {
                gmlf.append("    lig_name \"").append(vertex.getLigandName3()).append("\"\n");
            }
            
            gmlf.append("    sse_type \"").append(vertex.getSseType()).append("\"\n");
            
            gmlf.append(endNode).append("\n");
        }
        
        // print all edges
        Integer src, tgt;
        ArrayList<Integer[]> edges = this.getEdgeList();
        for(Integer[] edge : edges) {
            src = edge[0];
            tgt = edge[1];
            
            gmlf.append(startEdge).append("\n");
            gmlf.append("    source ").append(src).append("\n");
            gmlf.append("    target ").append(tgt).append("\n");
            
            //gmlf += "    label \"(" + src + ", " + tgt + ":" + this.getEdgeLabel(src, tgt) +  ")\"\n";
            gmlf.append("    label \"").append(this.getEdgeLabel(src, tgt)).append("\"\n");
            gmlf.append("    spatial \"").append(this.getEdgeLabel(src, tgt)).append("\"\n");
            
            gmlf.append(endEdge).append("\n");
        }
        
        // print footer (close graph)
        gmlf.append("]\n");
        
        return(gmlf.toString());
    }
    
    
    /**
     * Generates a string representation of this graph in DOT language format.
     * See http://en.wikipedia.org/wiki/DOT_language for details on the format.
     * @return the DOT language format graph string
     */    
    @Override
    public String toDOTLanguageFormat() {
        
        StringBuilder dlf = new StringBuilder();
        
        // put meta data in comments for DOT language graphs
        if(Settings.getBoolean("plcc_B_add_metadata_comments_GML")) {
            for(String key : this.metadata.keySet()) {
                dlf.append("#[VPLG_METADATA] ").append(key).append("=").append(this.metadata.get(key)).append("\n");
            }

            dlf.append("#[VPLG_METADATA] creator=PLCC version ").append(Settings.getVersion()).append("\n");
        }
        
        String graphLabel = "ProteinGraph";
        
        // start graph
        dlf.append("graph ").append(graphLabel).append(" {\n");
        
        // print the nodes
        SSE vertex; String shapeModifier, vertColor;
        for(Integer i = 0; i < this.size; i++) {
            
                        
            vertex = this.sseList.get(i);
            
            shapeModifier = "";
            vertColor = "";
            switch (vertex.getSseType()) {
                case "E":
                    shapeModifier = " shape=square";
                    vertColor = " color=black";
                    break;
                case "H":
                    shapeModifier = " shape=circle";
                    vertColor = " color=red";
                    break;
                case "L":
                    shapeModifier = " shape=triangle";
                    vertColor = " color=magenta";
                    break;
                default:
                    shapeModifier = " shape=circle";
                    vertColor = " color=gray";
                    break;
            }
            
            dlf.append("    ").append(i).append(" [label=\"").append(i).append("-").append(vertex.getSseType()).append("\"").append(shapeModifier).append(vertColor).append("];\n");
        }
        
        // print the edges        
        Integer src, tgt;
        String colorModifier, lineModifier, edgeLabel;
        ArrayList<Integer[]> edges = this.getEdgeList();
        for(Integer[] edge : edges) {
            src = edge[0];
            tgt = edge[1];
            
            colorModifier = lineModifier = "";
            edgeLabel = "label=\"" + this.getContactTypeString(src, tgt) + "\"";
            if(this.getContactType(src, tgt) == SpatRel.NONE) { continue; }
            else if(this.getContactType(src, tgt) == SpatRel.PARALLEL) { colorModifier = " color=red"; }
            else if(this.getContactType(src, tgt) == SpatRel.ANTIPARALLEL) { colorModifier = " color=blue"; }
            else if(this.getContactType(src, tgt) == SpatRel.MIXED) { colorModifier = " color=green"; }
            else if(this.getContactType(src, tgt) == SpatRel.LIGAND) { colorModifier = " color=magenta"; }
            else if(this.getContactType(src, tgt) == SpatRel.BACKBONE) { colorModifier = " color=orange"; lineModifier = " style=dotted"; }
            else { colorModifier = " color=gray"; lineModifier=""; edgeLabel=""; }
                                        
            dlf.append("    ").append(src).append(" -- ").append(tgt).append(" [").append(edgeLabel).append(colorModifier).append(lineModifier).append("]" + ";\n");
        }
        
        
        // close graph
        dlf.append("}\n");
        
        return(dlf.toString());
    }
    
    /**
     * Pseudo-method, setting this info is not implemented yet. Always returns false until that is fixed.
     * @return false
     */
    public boolean isComplexGraph() {
        return false;
    }
    
    /**
     * Generates a string representation of this graph in edge list format.
     * Each line in the string represents an edge in the graph, given by vertex indices.
     * @return the edge list format graph string
     */
    public String toEdgeList() {
        StringBuilder sb = new StringBuilder();                            

        for(Integer i = 0; i < this.getSize(); i++) {
            for(Integer j = 0 ; j < this.getSize(); j++) {
                if(this.containsEdge(i, j) && i != j) {
                    sb.append(i+1);
                    sb.append(" ");
                    sb.append(j+1);
                    sb.append("\n");                    
                }            
            }            
        }
        return sb.toString();
    }
    
    
    public SSE getVertex(int index) {
        return this.getSSEBySeqPosition(index);
    } 
    
    
    /**
     * Generates a string representation of this graph in Kavosh format. The first line contains the total
     * number of vertices in the graph. The following lines represent one edge per line, given by the vertex indices.
     * @return the kavosh format graph string
     */
    @Override
    public String toKavoshFormat() {
        StringBuilder kf = new StringBuilder();
        
        if(Settings.getBoolean("plcc_B_kavosh_format_directed")) {
            kf.append(this.numVertices()).append("\n");

            for(Integer i = 0; i < this.getSize(); i++) {
                for(Integer j = 0 ; j < this.getSize(); j++) {
                    if(this.containsEdge(i, j) && i != j) {
                        kf.append(i+1).append(" ").append(j+1).append("\n");
                    }            
                }            
            }
            
        } else {
        
            kf.append(this.numVertices()).append("\n");

            for(Integer i = 0; i < this.getSize(); i++) {
                for(Integer j = i + 1; j < this.getSize(); j++) {
                    if(this.containsEdge(i, j)) {
                        kf.append(i+1).append(" ").append(j+1).append("\n");
                    }            
                }            
            }
        }
        
        return kf.toString();
    }                  
    
    
    public ProteinLigandGraph<VertexSSE, PLGEdge> toProteinLigandGraph() {
        
        ProteinLigandGraph<VertexSSE, PLGEdge> plg = new ProteinLigandGraph<VertexSSE, PLGEdge>(PLGEdge.class);
        
        // set meta data
        plg.setPdbid(this.pdbid);
        plg.setChain(this.chainid);
        plg.setModelid(1);
        
        // add vertices
        for (int i = 0; i < this.size; i++) {            
            //TODO: continue here
        }
        
        // add edges
        return null;
        
    }
    
    
     public String[] getVertexInfoFieldNames() {
        return new String[] { "Position in graph" , "Position in chain", "SSE type", "DSSP start residue", "DSSP end residue", "PDB start residue ID", "PDB end residue", "AA sequence" };
    }
     
    public String[] getVertexInfoFieldsForSSE(int index) {
        SSE sse = this.sseList.get(index);
        String[] infoFields = new String[this.getVertexInfoFieldNames().length];
        infoFields[0] = "" + (index + 1);
        infoFields[1] = "" + sse.getSSESeqChainNum();
        infoFields[2] = "" + sse.getSseType();
        infoFields[3] = "" + sse.getStartDsspNum();
        infoFields[4] = "" + sse.getEndDsspNum();
        infoFields[5] = "" + sse.getStartPdbResID();
        infoFields[6] = "" + sse.getEndPdbResID();
        infoFields[7] = "" + sse.getAASequence();
        return infoFields;
    }
    
    
    public String[] getLigandInfoFieldsForSSE(int index) {
        SSE sse = this.sseList.get(index);
        String[] infoFields = new String[this.getLigandInfoFieldNames().length];
        infoFields[0] = "" + (index + 1);
        infoFields[1] = "" + sse.getSseType();
        infoFields[2] = "" + sse.getStartPdbResID();
        infoFields[3] = "" + sse.getLigandName3();
        return infoFields;
    }
    
    
    public String[] getEdgeInfoFieldNames() {
        return new String[] { "SSE 1 in graph", "SSE 2 in graph", "SSE 1 type", "SSE 2 type", "Spatial contact type" };
    }
    
    
    public String[] getLigandInfoFieldNames() {
        return new String[] { "Position in graph", "SSE type", "First PDB residue", "Ligand name" };
    }
    
    
    public String[] getEdgeInfoFieldsForEdge(int index1, int index2) {
        String[] infoFields = new String[this.getEdgeInfoFieldNames().length];
        infoFields[0] = "" + (index1 + 1);
        infoFields[1] = "" + (index2 + 1);
        infoFields[2] = "" + this.getSSEBySeqPosition(index1).getSseType();
        infoFields[3] = "" + this.getSSEBySeqPosition(index2).getSseType();
        infoFields[4] = "" + this.getEdgeLabel(index1, index2);
        return infoFields;
    }

   /**
    * Determines the number of ligands in this graph.
    * @return the number of ligands in this graph
    */
    public Integer numLigands() {
        Integer numIsolatedLigands = 0;
        for(SSE s : this.sseList) {
            if(s.isLigandSSE()) {
                numIsolatedLigands++;
            }
        }
        return numIsolatedLigands;
    } 

    
    @Override
    public Character getVertexLabelChar(Integer i) {
        char c;
        
        try {
            String l = this.getVertex(i).getPLCCSSELabel();
            c = l.charAt(0);
        } catch(Exception e) {
            if(i >= 0 && i <= 9) {
                c =  (char) ('0' + i);
            } else {
                c = 'v';
            }
        }
        return c;
    }
    
    @Override
    public Character getEdgeLabelChar(Integer i, Integer j) {
        char c;        
        try {
            c = this.getContactTypeCharacter(i, j);
        } catch(Exception e) {
            c =  '*';
        }
        return c;
    }
    
    
    /**
     * Determines the degree distribution of this graph. For a graph with n vertices, it counts 
     * how often each of the possible vertex degrees 0..n-1 occur in the graph.
     * @return a list of counts for each possible degree. So to get the number of 
     * vertices with degree 5, check in list[5]. The max degree you can check for is n -1 for a graph with n vertices.
     */
    public int[] getDegreeDistribution() {
        int maxDegree = this.size - 1;
        int[] degreeDistribution = new int[maxDegree + 1];
        Arrays.fill(degreeDistribution, 0);
        
        int d;
        for(int i = 0; i < this.size; i++) {
            d = this.degreeOfVertex(i);
            degreeDistribution[d]++;
        }
        
        return degreeDistribution;
    }
    
    
    /**
     * Sets the status of all vertices to 'not visited'. Used for algorithms like BFS.
     */
    public void resetVertexStates() {
        for(int i = 0; i < this.getSize(); i++) {
            this.getVertex(i).visitedState = SSEGraphVertex.STATE_NOT_VISITED;
        }
    }
    
    
    /**
     * Determines whether the set of vertices in bifurcated, i.e., whether any vertex in the set has a degree greater than 2.
     * @param vertexIndices the vertex set to check, vertices are given by their index in the vertex list of this graph
     * @return true if bifurcated, false otherwise
     */
    public boolean vertexSetIsBifurcated(Collection<Integer> vertexIndices) {
        for(int idx : vertexIndices) {
            if(this.degreeOfVertex(idx) > 2) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determines whether the set of vertices in NOT bifurcated. Function for PTGL script only.
     * @param vertexIndices the vertex set to check, vertices are given by their index in the vertex list of this graph
     * @return true if NOT bifurcated, false otherwise
     */
    public boolean vertexSetIsNotBifurcated(Collection<Integer> vertexIndices) {
        return( ! vertexSetIsBifurcated(vertexIndices));
    }
    
    
    /**
     * Returns a list of the degrees of all vertices in this graph.
     * @return a list of the degrees of all vertices in this graph, ordered by index.
     */
    public List<Integer> getAllVertexDegrees() {
        ArrayList<Integer> degrees = new ArrayList<Integer>();
        for(int i = 0; i < this.getSize(); i++) {
            degrees.add(this.degreeOfVertex(i));
        }
        return degrees;
    }
    
    /**
     * Returns a HashMap of the degrees of all vertices in this graph.
     * @return a HashMap of the degrees of all vertices in this graph, ordered by index.
     */
    public HashMap<Integer, Integer> getAllVertexDegreesMap() {
        HashMap<Integer, Integer> degrees = new HashMap<>();
        for(int i = 0; i < this.getSize(); i++) {
            degrees.put(i, this.degreeOfVertex(i));
        }
        return degrees;
    }
    
    /**
     * Computes the parent graph vertex positions in the FG back.
     * @param fgVertexPositionsInParent the positions of the FG vertices in the parent PG
     * @param numVerticesInParentGraph the size of the parent FG
     * @return The parent graph vertex positions in the FG. if a parent graph vertex is NOT contained in the FG, -1 is added at that position.
     */
    public static List<Integer> computeParentGraphVertexPositionsInFoldingGraph(List<Integer> fgVertexPositionsInParent, Integer numVerticesInParentGraph) {
        List<Integer> res = new ArrayList<Integer>();
        
        for(Integer i = 0; i < numVerticesInParentGraph; i++) {
            if(fgVertexPositionsInParent.contains(i)) {
                // the parent vertex i is contained in the FG
                res.add(fgVertexPositionsInParent.indexOf(i));
            }
            else {
                // the parent vertex i is NOT contained in the FG, so put -1 to mark this
                res.add(-1);
            }
        }
        
        return res;
    }

    
    /**
     * Draws the ADJ folding graph image of this graph and returns the DrawResult.
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     * 
     * @param pnfr a folding graph notation result

     * @return the DrawResult. You can write this to a file or whatever.
     */
    private static DrawResult drawFoldingGraphADJG2D(PTGLNotationFoldResult pnfr) {

        FoldingGraph fg = pnfr.getFoldingGraph();
        SSEGraph pg = fg.parent;
        Integer startVertexInParent = fg.getMinimalVertexIndexInParentGraph();
        Integer endVertexInParent = fg.getMaximalVertexIndexInParentGraph();
        
        Integer shiftBack = startVertexInParent;    // how many vertices we skipped on the left, we can shift the whole image to the left by this number of vetex lengths
        
        List<Integer> fgVertexPosInParent = fg.getVertexIndexListInParentGraph();
        List<Integer> parentVertexPosInFG = pg.computeParentGraphVertexPositionsInFoldingGraph(fgVertexPosInParent, pg.size);
        
        Integer numVerts = endVertexInParent - startVertexInParent + 1;

        Boolean bw = false;                                                  
        
        // All these values are in pixels
        // page setup
        PageLayout pl = new PageLayout(numVerts);        
        Position2D vertStart = pl.getVertStart();
        Integer lineHeight = pl.textLineHeight;            
        
        

            // ------------------------- Prepare stuff -------------------------
            // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
            //BufferedImage bi = new BufferedImage(pl.getPageWidth(), pl.getPageHeight(), BufferedImage.TYPE_INT_ARGB);
            
            SVGGraphics2D ig2;
            
            
            //if(Settings.get("plcc_S_img_output_format").equals("SVG")) {                    
                // Apache Batik SVG library, using W3C DOM tree implementation
                // Get a DOMImplementation.
                DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
                // Create an instance of org.w3c.dom.Document.
                String svgNS = "http://www.w3.org/2000/svg";
                Document document = domImpl.createDocument(svgNS, "svg", null);
                // Create an instance of the SVG Generator.
                ig2 = new SVGGraphics2D(document);
                //ig2.getRoot(document.getDocumentElement());
           // }
            //else {
            //    ig2 = (SVGGraphics2D)bi.createGraphics();
            //}
            
            ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // make background white
            ig2.setPaint(Color.WHITE);
            ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
            ig2.setPaint(Color.BLACK);

            //pl.drawAreaOutlines(ig2);
            // prepare font
            Font font = pl.getStandardFont();
            ig2.setFont(font);
            FontMetrics fontMetrics = ig2.getFontMetrics();

            // ------------------------- Draw header -------------------------

            // check width of header string
            String proteinHeader = "The ADJ " + pg.graphType + " folding graph " + fg.getFoldingGraphFoldName() + " (# " + fg.getFoldingGraphNumber() + ") of PDB entry " + pg.pdbid + ", chain " + pg.chainid + " [V=" + fg.numVertices() + ", E=" + fg.numSSEContacts() + "].";
            String notation = "ADJ notation: '" + pnfr.adjNotation + "' (svip=" + (pnfr.adjStart + 1) + ")";
            //Integer stringWidth = fontMetrics.stringWidth(proteinHeader);       // Should be around 300px for the text above
            Integer stringHeight = fontMetrics.getAscent();
            String sseNumberSeq;    // the SSE number in the primary structure, N to C terminus
            String sseNumberFoldingGraph;  // the SSE number in this graph, 1..(this.size)
            String sseNumberProteinGraph;  // the SSE number in the parent protein graph, 1..(this.size)

            if(Settings.getBoolean("plcc_B_graphimg_header")) {
                ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
                ig2.drawString(notation, pl.headerStart.x, pl.headerStart.y + lineHeight);                
            }

            // ------------------------- Draw the graph -------------------------
            
            // Draw the edges as arcs
            Integer k,l;
            java.awt.Shape shape;
            Arc2D.Double arc;
            ig2.setStroke(new BasicStroke(2));  // thin edges
            Integer edgeType, leftVert, rightVert, leftVertPosX, rightVertPosX, arcWidth, arcHeight, arcTopLeftX, arcTopLeftY, spacerX, spacerY;
            for(Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                for(Integer j = i + 1; j <= endVertexInParent; j++) {

                    
                    k = parentVertexPosInFG.get(i);
                    l = parentVertexPosInFG.get(j);
                    if(k < 0 || l < 0) {
                        continue;
                    }
                    
                    // If there is a contact...
                    if(fg.containsEdge(k, l)) {

                        // determine edge type and the resulting color
                        edgeType = fg.getContactType(k, l);
                        if(edgeType.equals(SpatRel.PARALLEL)) { ig2.setPaint(Color.RED); }
                        else if(edgeType.equals(SpatRel.ANTIPARALLEL)) { ig2.setPaint(Color.BLUE); }
                        else if(edgeType.equals(SpatRel.MIXED)) { ig2.setPaint(Color.GREEN); }
                        else if(edgeType.equals(SpatRel.LIGAND)) { ig2.setPaint(Color.MAGENTA); }
                        else if(edgeType.equals(SpatRel.BACKBONE)) { ig2.setPaint(Color.ORANGE); }
                        else { ig2.setPaint(Color.LIGHT_GRAY); }

                        if(bw) { ig2.setPaint(Color.LIGHT_GRAY); }      // for non-protein graphs
                                                

                        // determine the center of the arc and the width of its rectangle bounding box
                        //if(k < l) { leftVert = k; rightVert = l; }
                        //else { leftVert = l; rightVert = k; }
                        
                        if(i < j) { leftVert = i; rightVert = j; }
                        else { leftVert = j; rightVert = i; }
                        
                        leftVertPosX = pl.getVertStart().x + ((leftVert - shiftBack) * pl.vertDist);
                        rightVertPosX = pl.getVertStart().x + ((rightVert - shiftBack) * pl.vertDist);

                        arcWidth = rightVertPosX - leftVertPosX;
                        arcHeight = arcWidth / 2;

                        arcTopLeftX = leftVertPosX;
                        arcTopLeftY = pl.getVertStart().y - arcHeight / 2;

                        spacerX = pl.vertRadius;
                        spacerY = 0;

                        // draw it                                                
                        arc = new Arc2D.Double(arcTopLeftX + spacerX, arcTopLeftY + spacerY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
                        shape = ig2.getStroke().createStrokedShape(arc);
                        ig2.fill(shape);

                    }
                }
            }

            // Draw the vertices as circles
            Ellipse2D.Double circle;
            Rectangle2D.Double rect;
            ig2.setStroke(new BasicStroke(2));
            for(Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                
                // pick color depending on SSE type
                if(pg.sseList.get(i).isHelix()) { ig2.setPaint(Color.RED); }
                else if(pg.sseList.get(i).isBetaStrand()) { ig2.setPaint(Color.BLACK); }
                else if(pg.sseList.get(i).isLigandSSE()) { ig2.setPaint(Color.MAGENTA); }
                else if(pg.sseList.get(i).isOtherSSE()) { ig2.setPaint(Color.GRAY); }
                else { ig2.setPaint(Color.LIGHT_GRAY); }

                Integer parentVertexPosInFoldingGraph = parentVertexPosInFG.get(i);
                if(parentVertexPosInFoldingGraph < 0) {
                    ig2.setPaint(Color.LIGHT_GRAY);
                } 
                
                if(bw) { ig2.setPaint(Color.GRAY); }      // for non-protein graphs
                
                // draw a shape based on SSE type
                                
                
                SSE sse = pg.getVertex(i);
                if(sse.isBetaStrand()) {
                    // beta strands are black, filled squares
                    rect = new Rectangle2D.Double(vertStart.x + ((i-shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    ig2.fill(rect);
                    
                }
                else if(sse.isLigandSSE()) {
                    // ligands are magenta circles (non-filled)
                    circle = new Ellipse2D.Double(vertStart.x + ((i-shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    //ig2.fill(circle);
                    ig2.setStroke(new BasicStroke(3));  // this does NOT get filled, so give it a thicker border
                    ig2.draw(circle);
                    ig2.setStroke(new BasicStroke(2));
                }
                else {
                    // helices and all others are filled circles (helices are red circles, all others are gray circles)
                    circle = new Ellipse2D.Double(vertStart.x + ((i-shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    ig2.fill(circle);                    
                }
            }
            
            // Draw the markers for the N-terminus and C-terminus if there are any vertices in this graph            
            ig2.setStroke(new BasicStroke(2));
            ig2.setPaint(Color.BLACK);
            
            if( ! bw) {
                if(fg.getSize() > 0) {                    
                    ig2.drawString("N", vertStart.x - pl.vertDist, vertStart.y + 20);    // N terminus label
                    ig2.drawString("C", vertStart.x + numVerts * pl.vertDist, vertStart.y + 20);  // C terminus label
                }
            }
                        
            // ************************************* footer **************************************
            
            if(Settings.getBoolean("plcc_B_graphimg_footer")) {
            
                // Draw the vertex numbering into the footer
                // Determine the dist between vertices that will have their vertex number printed below them in the footer field
                Integer printNth = 1;
                //if(fg.getSize() > 9) { printNth = 1; }
                if(fg.getSize() > 99) { printNth = 2; }
                if(fg.getSize() > 999) { printNth = 3; }

                // line markers: S for sequence order, G for graph order
                
                if(fg.getSize() > 0) {                                            
                    ig2.drawString("FG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                    ig2.drawString("PG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + lineHeight + (stringHeight / 4));
                }
                else {
                    ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
                }


                for(Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                    // Draw label for every nth vertex
                    if((i + 1) % printNth == 0) {
                        sseNumberFoldingGraph = "" + (parentVertexPosInFG.get(i) >= 0 ? (parentVertexPosInFG.get(i) + 1) : "");
                        sseNumberSeq = "" + (pg.sseList.get(i).getSSESeqChainNum());
                        sseNumberProteinGraph = "" + (i + 1);
                        //stringWidth = fontMetrics.stringWidth(sseNumberSeq);
                        stringHeight = fontMetrics.getAscent();                                        

                        ig2.drawString(sseNumberFoldingGraph, pl.getFooterStart().x + ((i-shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                        ig2.drawString(sseNumberSeq, pl.getFooterStart().x + ((i-shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));                    
                        ig2.drawString(sseNumberProteinGraph, pl.getFooterStart().x + ((i-shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight *2) + (stringHeight / 4));                                                                    
                    }
                }

                if(Settings.getBoolean("plcc_B_graphimg_legend")) {
                    SSEGraph.drawLegend(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + (lineHeight * 3) + (stringHeight / 4)), pl, fg);
                }
            
            }
                      
            Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
            
            DrawResult drawRes = new DrawResult(ig2, roi);
            return drawRes;                                                                         
    }
    
    
    /**
     * Draws the RED folding graph image of this graph and returns the DrawResult.
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     * 
     * @param pnfr a folding graph notation result

     * @return the DrawResult. You can write this to a file or whatever.
     */
    private static DrawResult drawFoldingGraphREDG2D(PTGLNotationFoldResult pnfr) {

        FoldingGraph fg = pnfr.getFoldingGraph();
        SSEGraph pg = fg.parent;
        Integer startVertexInParent = fg.getMinimalVertexIndexInParentGraph();
        Integer endVertexInParent = fg.getMaximalVertexIndexInParentGraph();
                
        
        List<Integer> fgVertexPosInParent = fg.getVertexIndexListInParentGraph();
        List<Integer> parentVertexPosInFG = ProtGraph.computeParentGraphVertexPositionsInFoldingGraph(fgVertexPosInParent, pg.size);
        
        // compute the shiftBack array
        Integer[] shiftBack = new Integer[pg.size];    // how many vertices we skipped on the left at which position
        Integer shift = 0;
        for(int i = 0; i < parentVertexPosInFG.size(); i++) {
            if(parentVertexPosInFG.get(i) < 0) {
                shift++;
            }
            shiftBack[i] = shift;
        }
        
        Integer numVerts = fg.getSize();

        Boolean bw = false;                                                  
        
        // All these values are in pixels
        // page setup
        PageLayout pl = new PageLayout(numVerts);        
        Position2D vertStart = pl.getVertStart();
        Integer lineHeight = pl.textLineHeight;            
        
        

            // ------------------------- Prepare stuff -------------------------
            // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
            //BufferedImage bi = new BufferedImage(pl.getPageWidth(), pl.getPageHeight(), BufferedImage.TYPE_INT_ARGB);
            
            SVGGraphics2D ig2;
            
            
            //if(Settings.get("plcc_S_img_output_format").equals("SVG")) {                    
                // Apache Batik SVG library, using W3C DOM tree implementation
                // Get a DOMImplementation.
                DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
                // Create an instance of org.w3c.dom.Document.
                String svgNS = "http://www.w3.org/2000/svg";
                Document document = domImpl.createDocument(svgNS, "svg", null);
                // Create an instance of the SVG Generator.
                ig2 = new SVGGraphics2D(document);
                //ig2.getRoot(document.getDocumentElement());
           // }
            //else {
            //    ig2 = (SVGGraphics2D)bi.createGraphics();
            //}
            
            ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // make background white
            ig2.setPaint(Color.WHITE);
            ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
            ig2.setPaint(Color.BLACK);

            //pl.drawAreaOutlines(ig2);
            // prepare font
            Font font = pl.getStandardFont();
            ig2.setFont(font);
            FontMetrics fontMetrics = ig2.getFontMetrics();

            // ------------------------- Draw header -------------------------

            // check width of header string
            String proteinHeader = "The RED " + pg.graphType + " folding graph " + fg.getFoldingGraphFoldName() + " (# " + fg.getFoldingGraphNumber() + ") of PDB entry " + pg.pdbid + ", chain " + pg.chainid + " [V=" + fg.numVertices() + ", E=" + fg.numSSEContacts() + "].";
            String notation = "RED notation: '" + pnfr.redNotation + "' (svip=" + (pnfr.redStart + 1) + ")";
            //Integer stringWidth = fontMetrics.stringWidth(proteinHeader);       // Should be around 300px for the text above
            Integer stringHeight = fontMetrics.getAscent();
            String sseNumberSeq;    // the SSE number in the primary structure, N to C terminus
            String sseNumberFoldingGraph;  // the SSE number in this graph, 1..(this.size)
            String sseNumberProteinGraph;  // the SSE number in the parent protein graph, 1..(this.size)

            if(Settings.getBoolean("plcc_B_graphimg_header")) {
                ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
                ig2.drawString(notation, pl.headerStart.x, pl.headerStart.y + lineHeight);                
            }

            // ------------------------- Draw the graph -------------------------
            
            // Draw the edges as arcs
            Integer k,l;
            java.awt.Shape shape;
            Arc2D.Double arc;
            ig2.setStroke(new BasicStroke(2));  // thin edges
            Integer edgeType, leftVert, rightVert, leftVertPosX, rightVertPosX, arcWidth, arcHeight, arcTopLeftX, arcTopLeftY, spacerX, spacerY;
            for(Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                for(Integer j = i + 1; j <= endVertexInParent; j++) {

                    
                    k = parentVertexPosInFG.get(i);
                    l = parentVertexPosInFG.get(j);
                    if(k < 0 || l < 0) {
                        continue;
                    }
                    
                    // If there is a contact...
                    if(fg.containsEdge(k, l)) {

                        // determine edge type and the resulting color
                        edgeType = fg.getContactType(k, l);
                        if(edgeType.equals(SpatRel.PARALLEL)) { ig2.setPaint(Color.RED); }
                        else if(edgeType.equals(SpatRel.ANTIPARALLEL)) { ig2.setPaint(Color.BLUE); }
                        else if(edgeType.equals(SpatRel.MIXED)) { ig2.setPaint(Color.GREEN); }
                        else if(edgeType.equals(SpatRel.LIGAND)) { ig2.setPaint(Color.MAGENTA); }
                        else if(edgeType.equals(SpatRel.BACKBONE)) { ig2.setPaint(Color.ORANGE); }
                        else { ig2.setPaint(Color.LIGHT_GRAY); }

                        if(bw) { ig2.setPaint(Color.LIGHT_GRAY); }      // for non-protein graphs
                                                

                        // determine the center of the arc and the width of its rectangle bounding box
                        //if(k < l) { leftVert = k; rightVert = l; }
                        //else { leftVert = l; rightVert = k; }
                        
                        if(i < j) { leftVert = i; rightVert = j; }
                        else { leftVert = j; rightVert = i; }
                        
                        leftVertPosX = pl.getVertStart().x + ((leftVert - shiftBack[leftVert]) * pl.vertDist);
                        rightVertPosX = pl.getVertStart().x + ((rightVert - shiftBack[rightVert]) * pl.vertDist);

                        arcWidth = rightVertPosX - leftVertPosX;
                        arcHeight = arcWidth / 2;

                        arcTopLeftX = leftVertPosX;
                        arcTopLeftY = pl.getVertStart().y - arcHeight / 2;

                        spacerX = pl.vertRadius;
                        spacerY = 0;

                        // draw it                                                
                        arc = new Arc2D.Double(arcTopLeftX + spacerX, arcTopLeftY + spacerY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
                        shape = ig2.getStroke().createStrokedShape(arc);
                        ig2.fill(shape);

                    }
                }
            }

            // Draw the vertices as circles
            Ellipse2D.Double circle;
            Rectangle2D.Double rect;
            ig2.setStroke(new BasicStroke(2));
            for(Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                
                // pick color depending on SSE type
                if(pg.sseList.get(i).isHelix()) { ig2.setPaint(Color.RED); }
                else if(pg.sseList.get(i).isBetaStrand()) { ig2.setPaint(Color.BLACK); }
                else if(pg.sseList.get(i).isLigandSSE()) { ig2.setPaint(Color.MAGENTA); }
                else if(pg.sseList.get(i).isOtherSSE()) { ig2.setPaint(Color.GRAY); }
                else { ig2.setPaint(Color.LIGHT_GRAY); }

                Integer parentVertexPosInFoldingGraph = parentVertexPosInFG.get(i);
                if(parentVertexPosInFoldingGraph < 0) {
                    continue;
                } 
                
                if(bw) { ig2.setPaint(Color.GRAY); }      // for non-protein graphs
                
                // draw a shape based on SSE type
                                
                
                SSE sse = pg.getVertex(i);
                if(sse.isBetaStrand()) {
                    // beta strands are black, filled squares
                    rect = new Rectangle2D.Double(vertStart.x + ((i-shiftBack[i]) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    ig2.fill(rect);
                    
                }
                else if(sse.isLigandSSE()) {
                    // ligands are magenta circles (non-filled)
                    circle = new Ellipse2D.Double(vertStart.x + ((i-shiftBack[i]) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    //ig2.fill(circle);
                    ig2.setStroke(new BasicStroke(3));  // this does NOT get filled, so give it a thicker border
                    ig2.draw(circle);
                    ig2.setStroke(new BasicStroke(2));
                }
                else {
                    // helices and all others are filled circles (helices are red circles, all others are gray circles)
                    circle = new Ellipse2D.Double(vertStart.x + ((i-shiftBack[i]) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    ig2.fill(circle);                    
                }
            }
            
            // Draw the markers for the N-terminus and C-terminus if there are any vertices in this graph            
            ig2.setStroke(new BasicStroke(2));
            ig2.setPaint(Color.BLACK);
            
            if( ! bw) {
                if(fg.getSize() > 0) {                    
                    ig2.drawString("N", vertStart.x - pl.vertDist, vertStart.y + 20);    // N terminus label
                    ig2.drawString("C", vertStart.x + numVerts * pl.vertDist, vertStart.y + 20);  // C terminus label
                }
            }
                        
            // ************************************* footer **************************************
            
            if(Settings.getBoolean("plcc_B_graphimg_footer")) {
            
                // Draw the vertex numbering into the footer
                // Determine the dist between vertices that will have their vertex number printed below them in the footer field
                Integer printNth = 1;
                //if(fg.getSize() > 9) { printNth = 1; }
                if(fg.getSize() > 99) { printNth = 2; }
                if(fg.getSize() > 999) { printNth = 3; }

                // line markers: S for sequence order, G for graph order
                
                if(fg.getSize() > 0) {                                            
                    ig2.drawString("FG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                    ig2.drawString("PG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + lineHeight + (stringHeight / 4));
                }
                else {
                    ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
                }


                for(Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                    
                    Integer parentVertexPosInFoldingGraph = parentVertexPosInFG.get(i);
                    if(parentVertexPosInFoldingGraph < 0) {
                        continue;
                    }
                    // Draw label for every nth vertex
                    if((i + 1) % printNth == 0) {
                        sseNumberFoldingGraph = "" + (parentVertexPosInFG.get(i) >= 0 ? (parentVertexPosInFG.get(i) + 1) : "");
                        sseNumberSeq = "" + (pg.sseList.get(i).getSSESeqChainNum());
                        sseNumberProteinGraph = "" + (i + 1);
                        //stringWidth = fontMetrics.stringWidth(sseNumberSeq);
                        stringHeight = fontMetrics.getAscent();                                        

                        ig2.drawString(sseNumberFoldingGraph, pl.getFooterStart().x + ((i-shiftBack[i]) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                        ig2.drawString(sseNumberSeq, pl.getFooterStart().x + ((i-shiftBack[i]) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));                    
                        ig2.drawString(sseNumberProteinGraph, pl.getFooterStart().x + ((i-shiftBack[i]) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight *2) + (stringHeight / 4));                                                                    
                    }
                }

                if(Settings.getBoolean("plcc_B_graphimg_legend")) {
                    SSEGraph.drawLegend(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + (lineHeight * 3) + (stringHeight / 4)), pl, fg);
                }
            
            }
                      
            Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
            
            DrawResult drawRes = new DrawResult(ig2, roi);
            return drawRes;                                                                         
    }
    
    
    /**
     * Draws the KEY folding graph image of this graph and returns the DrawResult.
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     * 
     * See http://ptgl.uni-frankfurt.de/cgi-bin/showpict.pl?topology=a&rep=3&protlist=7timA+7timB+&nmrlist=
     * and http://ptgl.uni-frankfurt.de/ptglhelp.html#key
     * 
     * @param pnfr a folding graph notation result

     * @return the DrawResult. You can write this to a file or whatever.
     */
    private static DrawResult drawFoldingGraphKEYG2D(PTGLNotationFoldResult pnfr) {

        //System.err.println("ERROR: drawFoldingGraphKEYG2D: implement me, i draw RED atm.");
        
        FoldingGraph fg = pnfr.getFoldingGraph();
        SSEGraph pg = fg.parent;
        
        if(fg.isBifurcated()) {
            DP.getInstance().e("SSEGraph", "drawFoldingGraphKEYG2D: This FG is bifurcated, KEY notation not supported.");
        }
        
        Integer startVertexInParent = fg.getMinimalVertexIndexInParentGraph();
        Integer endVertexInParent = fg.getMaximalVertexIndexInParentGraph();
                
        
        List<Integer> fgVertexPosInParent = fg.getVertexIndexListInParentGraph();
        List<Integer> parentVertexPosInFG = ProtGraph.computeParentGraphVertexPositionsInFoldingGraph(fgVertexPosInParent, pg.size);
        
        // compute the shiftBack array
        Integer[] shiftBack = new Integer[pg.size];    // how many vertices we skipped on the left at which position
        Integer shift = 0;
        for(int i = 0; i < parentVertexPosInFG.size(); i++) {
            if(parentVertexPosInFG.get(i) < 0) {
                shift++;
            }
            shiftBack[i] = shift;
        }
        
        Integer numVerts = fg.getSize();

        Boolean bw = false;                                                  
        
        // All these values are in pixels
        // page setup
        PageLayout pl = new PageLayout(numVerts);        
        Position2D vertStart = pl.getVertStart();
        Integer lineHeight = pl.textLineHeight;      
        
        // drawing of objects
        Integer vertDist = 50;                  // distance between (centers of) vertices in the drawing
        Integer vertHeight = 80;                // height of vertex element graphics (arrow height)
        Integer vertWidth = 40;                 // height of vertex graphics (arrow width)
        Integer vertStartX = pl.getVertStart().x;
        Integer vertStartY = pl.getVertStart().y;
        
        

            // ------------------------- Prepare stuff -------------------------
            // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
            //BufferedImage bi = new BufferedImage(pl.getPageWidth(), pl.getPageHeight(), BufferedImage.TYPE_INT_ARGB);
            
            SVGGraphics2D ig2;
            
            
            //if(Settings.get("plcc_S_img_output_format").equals("SVG")) {                    
                // Apache Batik SVG library, using W3C DOM tree implementation
                // Get a DOMImplementation.
                DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
                // Create an instance of org.w3c.dom.Document.
                String svgNS = "http://www.w3.org/2000/svg";
                Document document = domImpl.createDocument(svgNS, "svg", null);
                // Create an instance of the SVG Generator.
                ig2 = new SVGGraphics2D(document);
                //ig2.getRoot(document.getDocumentElement());
           // }
            //else {
            //    ig2 = (SVGGraphics2D)bi.createGraphics();
            //}
            
            ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // make background white
            ig2.setPaint(Color.WHITE);
            ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
            ig2.setPaint(Color.BLACK);

            //pl.drawAreaOutlines(ig2);
            // prepare font
            Font font = pl.getStandardFont();
            ig2.setFont(font);
            FontMetrics fontMetrics = ig2.getFontMetrics();

            // ------------------------- Draw header -------------------------

            // check width of header string
            String proteinHeader = "The KEY " + pg.graphType + " folding graph " + fg.getFoldingGraphFoldName() + " (# " + fg.getFoldingGraphNumber() + ") of PDB entry " + pg.pdbid + ", chain " + pg.chainid + " [V=" + fg.numVertices() + ", E=" + fg.numSSEContacts() + "].";
            String notation = "KEY notation: '" + pnfr.keyNotation + "' (svip=" + (pnfr.keyStart < 0 ? "-" : pnfr.keyStart + 1) + ")";
            //Integer stringWidth = fontMetrics.stringWidth(proteinHeader);       // Should be around 300px for the text above
            Integer stringHeight = fontMetrics.getAscent();
            String sseNumberSeq;    // the SSE number in the primary structure, N to C terminus
            String sseNumberFoldingGraph;  // the SSE number in this graph, 1..(this.size)
            String sseNumberProteinGraph;  // the SSE number in the parent protein graph, 1..(this.size)

            if(Settings.getBoolean("plcc_B_graphimg_header")) {
                ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
                ig2.drawString(notation, pl.headerStart.x, pl.headerStart.y + lineHeight);                
            }

            // ------------------------- Draw the graph -------------------------
            
            
            
            
            
            // determine the headings of the vertices in the image. if two adjacent SSEs are parallel, they point in the same direction in the image. if they are antiparallel, they point into different directions.
            Integer[] headingsSpatOrder = new Integer[fg.size];    // the heading of the vertex in the image (UP or DOWN). This is in the order of spatOrder variable, not the the original vertex list in the SSEList variable.
            Integer[] headingsSeqOrder = new Integer[fg.size];
            headingsSpatOrder[0] = FoldingGraph.ORIENTATION_UPWARDS;  // heading of the 1st vertex is up by definition (it has no predecessor)
                                  
           
            if(fg.size > 1) {
                
                for(Integer spatPos = 1; spatPos < fg.size; spatPos++) {
                    Integer sseSeqIndex = fg.getSSESeqIndexatSpatOrderPosition(spatPos);
                    Integer lastsseSeqIndex = fg.getSSESeqIndexatSpatOrderPosition(spatPos - 1);
                    
                    if(sseSeqIndex == null || lastsseSeqIndex == null) {
                        System.out.println("DEBUG: spatPos=" + spatPos + ", sseSeqIndex=" + sseSeqIndex + ", lastsseSeqIndex=" + lastsseSeqIndex + ".");
                    }
                    
                    Integer spatRel = fg.getContactType(sseSeqIndex, lastsseSeqIndex);
                    
                    if(spatRel == SpatRel.PARALLEL) {
                        // keep orientation
                        headingsSpatOrder[spatPos] = headingsSpatOrder[spatPos-1];
                    }
                    else if(spatRel == SpatRel.NONE) {
                        // should never happen
                        headingsSpatOrder[spatPos] = headingsSpatOrder[spatPos-1];    // whatever
                        //System.err.println("WARNING: Vertices at indices " + sseSeqIndex + " and " + lastsseSeqIndex + " without contact are considered neighbors in the graph.");
                        //System.err.println("WARNING: SSE " + sseSeqIndex + ": " + this.getVertex(sseSeqIndex).longStringRep() + ", SSE " + lastsseSeqIndex + ": " + this.getVertex(lastsseSeqIndex).longStringRep() + ".");
                        
                        //System.exit(1);
                    }
                    else {
                        // all other spatial relations, e.g. SpatRel.ANTIPARALLEL, SpatRel.LIGAND, SpatRel.BACKBONE and SpatRel.MIXED: invert orientation
                        headingsSpatOrder[spatPos] = (headingsSpatOrder[spatPos-1] == FoldingGraph.ORIENTATION_UPWARDS ? FoldingGraph.ORIENTATION_DOWNWARDS : FoldingGraph.ORIENTATION_UPWARDS);
                    }                
                }
                
                // copy from spatOrder to seqOrder array
                Integer seqIndex;
                for(Integer i = 0; i < fg.getSize(); i++) {
                    seqIndex = fg.spatOrder.get(i);
                    headingsSeqOrder[seqIndex] = headingsSpatOrder[i];
                }
                
                // DEBUG output
                /*
                System.out.println("DEBUG: " + this.toShortString() );
                System.out.println("DEBUG: SpatOrder: " + IO.intArrayListToString(this.spatOrder) );
                System.out.println("DEBUG: SpatHeadg: " + IO.intArrayToString(headingsSpatOrder) );
                System.out.println("DEBUG: SeqHeadng: " + IO.intArrayToString(headingsSeqOrder) );                
                String neighbors = "DEBUG: SeqDegree: ";
                for(Integer i = 0; i < this.getSize(); i++) {
                    neighbors += this.degreeOfVertex(i) + " ";
                }
                System.out.println(neighbors);
                */
                
            }                                    
            
            
            
            // now draw the connectors
            Integer edgeType, connCenterX, connCenterY, leftVertSeq, rightVertSeq, leftVertSpat, rightVertSpat, leftVertPosX, leftVertPosY, rightVertPosX, rightVertPosY, connWidth, connHeight, connTopLeftX, connTopLeftY, spacerX, spacerY;
            Integer iSpatIndex, jSpatIndex;
            Boolean startUpwards;
            ig2.setStroke(new BasicStroke(1));
            if(fg.numEdges() > 0) {                          
                for(Integer i = 0; i < fg.sseList.size(); i++) {
                    for(Integer j = i + 1; j < fg.sseList.size(); j++) {

                        // If there is a contact...
                        if(fg.containsEdge(i, j)) {

                            // determine edge type and the resulting color
                            edgeType = fg.getContactType(i, j);
                            if(edgeType.equals(SpatRel.PARALLEL)) { ig2.setPaint(Color.RED); }
                            else if(edgeType.equals(SpatRel.ANTIPARALLEL)) { ig2.setPaint(Color.BLUE); }
                            else if(edgeType.equals(SpatRel.MIXED)) { ig2.setPaint(Color.GREEN); }
                            else if(edgeType.equals(SpatRel.LIGAND)) { ig2.setPaint(Color.MAGENTA); }
                            else if(edgeType.equals(SpatRel.BACKBONE)) { ig2.setPaint(Color.ORANGE); }
                            else { ig2.setPaint(Color.LIGHT_GRAY); }
                            
                            if(Settings.getBoolean("plcc_B_key_foldinggraph_arcs_allways_black")) {
                                ig2.setPaint(Color.BLACK);
                            }

                            // determine the center of the arc and the width of its rectangle bounding box
                            //iSpatIndex = spatOrder.get(i);
                            //jSpatIndex = spatOrder.get(j);
                            iSpatIndex = fg.getSpatOrderIndexOfSSE(i);
                            jSpatIndex = fg.getSpatOrderIndexOfSSE(j);
                            
                            // determine who is left and who is right
                            if(iSpatIndex < jSpatIndex) { 
                                leftVertSpat = iSpatIndex; 
                                leftVertSeq = i; 
                                rightVertSpat = jSpatIndex; 
                                rightVertSeq = j;
                            }
                            else { 
                                leftVertSpat = jSpatIndex; 
                                leftVertSeq = j;
                                rightVertSpat = iSpatIndex;
                                rightVertSeq = i;
                            }
                            
                            leftVertPosX = vertStartX + (leftVertSpat * vertDist);      // center of the left vertex object (arrow or rectangle)
                            rightVertPosX = vertStartX + (rightVertSpat * vertDist);    // center of the right...

                            connWidth = rightVertPosX - leftVertPosX;                   // total width of the connector
                            connHeight = connWidth / 2;                                 // total height...

                            connCenterX = rightVertPosX - (connWidth / 2);      // the center of the connector, here we draw the line if it is required
                            connCenterY = vertStartY - (connHeight / 2);

                            connTopLeftX = leftVertPosX;                        // the upper left point of the connector, i.e., where it is connected to the left vertex object (if that vertex has to be connected at the upper end)                                                                
                            connTopLeftY = vertStartY - (connHeight / 2);

                            spacerX = vertWidth;
                            spacerY = 0;

                            
                            // Determine the y axis positions where the connector should start (at the left vertex) and end (at the right vertex). This depends
                            //  on whether the respective vertex points upwards or downwards.
                            
                            //System.out.print("Contact " + i + "," + j + " (left is " + leftVertSeq + "[spat:" + this.getSpatOrderIndexOfSSE(leftVertSeq) + "], right is " + rightVertSeq + "[spat:" + this.getSpatOrderIndexOfSSE(rightVertSeq) + "]): ");
                            
                            if(headingsSeqOrder[leftVertSeq] == FoldingGraph.ORIENTATION_UPWARDS) {
                                // the left vertex points upwards, so the arc should start at its top
                                leftVertPosY = vertStartY - vertHeight;
                                startUpwards = true;
                                //System.out.print("leftVert starts upwards, ");
                            }
                            else {
                                // the left vertex points downwards, so the arc should start at its bottom
                                leftVertPosY = vertStartY;
                                startUpwards = false;
                                //System.out.print("leftVert starts downwards, ");
                            }
                            
                            if(headingsSeqOrder[rightVertSeq] == FoldingGraph.ORIENTATION_UPWARDS) {
                                // the right vertex points upwards, so the arc should end at its bottom
                                rightVertPosY = vertStartY;
                                //System.out.print("rightVert starts upwards. ");
                            }
                            else {
                                // the right vertex points downwards, so the arc should end at its top
                                rightVertPosY = vertStartY - vertHeight;
                                //System.out.print("rightVert starts downpwards. ");
                            }
                            
                            
                            // draw it        
                            //System.out.print("Getting arc from " + leftVertPosX + "," + leftVertPosY + " to " + rightVertPosX + "," + rightVertPosY + ".\n");
                            ArrayList<Shape> connShapes = FoldingGraph.getArcConnector(leftVertPosX, leftVertPosY, rightVertPosX, rightVertPosY, ig2.getStroke(), startUpwards);
                            for(Shape s : connShapes) {
                                ig2.draw(s);
                            }                                                        
                        }
                    }
                }
            }

            
            ig2.setStroke(new BasicStroke(1));

            
            
            
            // Draw the edges as arcs
            Integer k,l;
            java.awt.Shape shape;
            Arc2D.Double arc;
            ig2.setStroke(new BasicStroke(2));  // thin edges
            Integer leftVert, rightVert, arcWidth, arcHeight, arcTopLeftX, arcTopLeftY;
            for(Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                for(Integer j = i + 1; j <= endVertexInParent; j++) {

                    
                    k = parentVertexPosInFG.get(i);
                    l = parentVertexPosInFG.get(j);
                    if(k < 0 || l < 0) {
                        continue;
                    }
                    
                    // If there is a contact...
                    if(fg.containsEdge(k, l)) {

                        // determine edge type and the resulting color
                        edgeType = fg.getContactType(k, l);
                        if(edgeType.equals(SpatRel.PARALLEL)) { ig2.setPaint(Color.RED); }
                        else if(edgeType.equals(SpatRel.ANTIPARALLEL)) { ig2.setPaint(Color.BLUE); }
                        else if(edgeType.equals(SpatRel.MIXED)) { ig2.setPaint(Color.GREEN); }
                        else if(edgeType.equals(SpatRel.LIGAND)) { ig2.setPaint(Color.MAGENTA); }
                        else if(edgeType.equals(SpatRel.BACKBONE)) { ig2.setPaint(Color.ORANGE); }
                        else { ig2.setPaint(Color.LIGHT_GRAY); }

                        if(bw) { ig2.setPaint(Color.LIGHT_GRAY); }      // for non-protein graphs
                                                

                        // determine the center of the arc and the width of its rectangle bounding box
                        //if(k < l) { leftVert = k; rightVert = l; }
                        //else { leftVert = l; rightVert = k; }
                        
                        if(i < j) { leftVert = i; rightVert = j; }
                        else { leftVert = j; rightVert = i; }
                        
                        leftVertPosX = pl.getVertStart().x + ((leftVert - shiftBack[leftVert]) * pl.vertDist);
                        rightVertPosX = pl.getVertStart().x + ((rightVert - shiftBack[rightVert]) * pl.vertDist);

                        arcWidth = rightVertPosX - leftVertPosX;
                        arcHeight = arcWidth / 2;

                        arcTopLeftX = leftVertPosX;
                        arcTopLeftY = pl.getVertStart().y - arcHeight / 2;

                        spacerX = pl.vertRadius;
                        spacerY = 0;

                        // draw it                                                
                        arc = new Arc2D.Double(arcTopLeftX + spacerX, arcTopLeftY + spacerY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
                        shape = ig2.getStroke().createStrokedShape(arc);
                        ig2.fill(shape);

                    }
                }
            }

            // Draw the vertices as circles
            Ellipse2D.Double circle;
            Rectangle2D.Double rect;
            ig2.setStroke(new BasicStroke(2));
            for(Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                
                // pick color depending on SSE type
                if(pg.sseList.get(i).isHelix()) { ig2.setPaint(Color.RED); }
                else if(pg.sseList.get(i).isBetaStrand()) { ig2.setPaint(Color.BLACK); }
                else if(pg.sseList.get(i).isLigandSSE()) { ig2.setPaint(Color.MAGENTA); }
                else if(pg.sseList.get(i).isOtherSSE()) { ig2.setPaint(Color.GRAY); }
                else { ig2.setPaint(Color.LIGHT_GRAY); }

                Integer parentVertexPosInFoldingGraph = parentVertexPosInFG.get(i);
                if(parentVertexPosInFoldingGraph < 0) {
                    continue;
                } 
                
                if(bw) { ig2.setPaint(Color.GRAY); }      // for non-protein graphs
                
                // draw a shape based on SSE type
                                
                
                SSE sse = pg.getVertex(i);
                if(sse.isBetaStrand()) {
                    // beta strands are black, filled squares
                    rect = new Rectangle2D.Double(vertStart.x + ((i-shiftBack[i]) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    ig2.fill(rect);
                    
                }
                else if(sse.isLigandSSE()) {
                    // ligands are magenta circles (non-filled)
                    circle = new Ellipse2D.Double(vertStart.x + ((i-shiftBack[i]) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    //ig2.fill(circle);
                    ig2.setStroke(new BasicStroke(3));  // this does NOT get filled, so give it a thicker border
                    ig2.draw(circle);
                    ig2.setStroke(new BasicStroke(2));
                }
                else {
                    // helices and all others are filled circles (helices are red circles, all others are gray circles)
                    circle = new Ellipse2D.Double(vertStart.x + ((i-shiftBack[i]) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    ig2.fill(circle);                    
                }
            }
            
            // Draw the markers for the N-terminus and C-terminus if there are any vertices in this graph            
            ig2.setStroke(new BasicStroke(2));
            ig2.setPaint(Color.BLACK);
            
            if( ! bw) {
                if(fg.getSize() > 0) {                    
                    ig2.drawString("N", vertStart.x - pl.vertDist, vertStart.y + 20);    // N terminus label
                    ig2.drawString("C", vertStart.x + numVerts * pl.vertDist, vertStart.y + 20);  // C terminus label
                }
            }
                        
            // ************************************* footer **************************************
            
            if(Settings.getBoolean("plcc_B_graphimg_footer")) {
            
                // Draw the vertex numbering into the footer
                // Determine the dist between vertices that will have their vertex number printed below them in the footer field
                Integer printNth = 1;
                //if(fg.getSize() > 9) { printNth = 1; }
                if(fg.getSize() > 99) { printNth = 2; }
                if(fg.getSize() > 999) { printNth = 3; }

                // line markers: S for sequence order, G for graph order
                
                if(fg.getSize() > 0) {                                            
                    ig2.drawString("FG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                    ig2.drawString("PG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + lineHeight + (stringHeight / 4));
                }
                else {
                    ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
                }


                for(Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                    
                    Integer parentVertexPosInFoldingGraph = parentVertexPosInFG.get(i);
                    if(parentVertexPosInFoldingGraph < 0) {
                        continue;
                    }
                    // Draw label for every nth vertex
                    if((i + 1) % printNth == 0) {
                        sseNumberFoldingGraph = "" + (parentVertexPosInFG.get(i) >= 0 ? (parentVertexPosInFG.get(i) + 1) : "");
                        sseNumberSeq = "" + (pg.sseList.get(i).getSSESeqChainNum());
                        sseNumberProteinGraph = "" + (i + 1);
                        //stringWidth = fontMetrics.stringWidth(sseNumberSeq);
                        stringHeight = fontMetrics.getAscent();                                        

                        ig2.drawString(sseNumberFoldingGraph, pl.getFooterStart().x + ((i-shiftBack[i]) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                        ig2.drawString(sseNumberSeq, pl.getFooterStart().x + ((i-shiftBack[i]) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));                    
                        ig2.drawString(sseNumberProteinGraph, pl.getFooterStart().x + ((i-shiftBack[i]) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight *2) + (stringHeight / 4));                                                                    
                    }
                }

                if(Settings.getBoolean("plcc_B_graphimg_legend")) {
                    SSEGraph.drawLegendKEY(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + (lineHeight * 3) + (stringHeight / 4)), pl, fg);
                }
            
            }
                      
            Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
            
            DrawResult drawRes = new DrawResult(ig2, roi);
            return drawRes;                                                                         
    }

    
    
    /**
     * Draws the SEQ folding graph image of this graph and returns the DrawResult.
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     * 
     * @param pnfr a folding graph notation result

     * @return the DrawResult. You can write this to a file or whatever.
     */
    private static DrawResult drawFoldingGraphSEQG2D(PTGLNotationFoldResult pnfr) {

        FoldingGraph fg = pnfr.getFoldingGraph();
        SSEGraph pg = fg.parent;
        Integer startVertexInParent = fg.getMinimalVertexIndexInParentGraph();
        Integer endVertexInParent = fg.getMaximalVertexIndexInParentGraph();
        
        Integer shiftBack = startVertexInParent;    // how many vertices we skipped on the left, we can shift the whole image to the left by this number of vetex lengths
        
        List<Integer> fgVertexPosInParent = fg.getVertexIndexListInParentGraph();
        List<Integer> parentVertexPosInFG = pg.computeParentGraphVertexPositionsInFoldingGraph(fgVertexPosInParent, pg.size);
        
        Integer numVerts = endVertexInParent - startVertexInParent + 1;

        Boolean bw = false;                                                  
        
        // All these values are in pixels
        // page setup
        PageLayout pl = new PageLayout(numVerts);        
        Position2D vertStart = pl.getVertStart();
        Integer lineHeight = pl.textLineHeight;            
        
        

            // ------------------------- Prepare stuff -------------------------
            // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
            //BufferedImage bi = new BufferedImage(pl.getPageWidth(), pl.getPageHeight(), BufferedImage.TYPE_INT_ARGB);
            
            SVGGraphics2D ig2;
            
            
            //if(Settings.get("plcc_S_img_output_format").equals("SVG")) {                    
                // Apache Batik SVG library, using W3C DOM tree implementation
                // Get a DOMImplementation.
                DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
                // Create an instance of org.w3c.dom.Document.
                String svgNS = "http://www.w3.org/2000/svg";
                Document document = domImpl.createDocument(svgNS, "svg", null);
                // Create an instance of the SVG Generator.
                ig2 = new SVGGraphics2D(document);
                //ig2.getRoot(document.getDocumentElement());
           // }
            //else {
            //    ig2 = (SVGGraphics2D)bi.createGraphics();
            //}
            
            ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // make background white
            ig2.setPaint(Color.WHITE);
            ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
            ig2.setPaint(Color.BLACK);

            //pl.drawAreaOutlines(ig2);
            // prepare font
            Font font = pl.getStandardFont();
            ig2.setFont(font);
            FontMetrics fontMetrics = ig2.getFontMetrics();

            // ------------------------- Draw header -------------------------

            // check width of header string
            String proteinHeader = "The SEQ " + pg.graphType + " folding graph " + fg.getFoldingGraphFoldName() + " (# " + fg.getFoldingGraphNumber() + ") of PDB entry " + pg.pdbid + ", chain " + pg.chainid + " [V=" + fg.numVertices() + ", E=" + fg.numSSEContacts() + "].";
            String notation = "SEQ notation: '" + pnfr.seqNotation + "' (svip=" + (pnfr.seqStart + 1) + ")";
            //Integer stringWidth = fontMetrics.stringWidth(proteinHeader);       // Should be around 300px for the text above
            Integer stringHeight = fontMetrics.getAscent();
            String sseNumberSeq;    // the SSE number in the primary structure, N to C terminus
            String sseNumberFoldingGraph;  // the SSE number in this graph, 1..(this.size)
            String sseNumberProteinGraph;  // the SSE number in the parent protein graph, 1..(this.size)

            if(Settings.getBoolean("plcc_B_graphimg_header")) {
                ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
                ig2.drawString(notation, pl.headerStart.x, pl.headerStart.y + lineHeight);                
            }

            // ------------------------- Draw the graph -------------------------
            
            // Draw the edges as arcs
            Integer k,l;
            java.awt.Shape shape;
            Arc2D.Double arc;
            ig2.setStroke(new BasicStroke(2));  // thin edges
            Integer edgeType, leftVert, rightVert, leftVertPosX, rightVertPosX, arcWidth, arcHeight, arcTopLeftX, arcTopLeftY, spacerX, spacerY;
            
            List<Integer> fgVertexIndices = fg.getVertexIndexList();
            Integer j;
            for(Integer i = 0; i < fgVertexIndices.size() -1; i++) {
                j = fgVertexIndices.get(i+1);
                    
                k = fgVertexPosInParent.get(i);
                l = fgVertexPosInParent.get(j);

                // we don not even check for actual edges here, we just draw the computed distance!

                // all edges are black in SEQ, no matter the edge type
                ig2.setPaint(Color.BLACK);

                if(k < l) { leftVert = k; rightVert = l; }
                else { leftVert = l; rightVert = k; }

                leftVertPosX = pl.getVertStart().x + ((leftVert - shiftBack) * pl.vertDist);
                rightVertPosX = pl.getVertStart().x + ((rightVert - shiftBack) * pl.vertDist);

                arcWidth = rightVertPosX - leftVertPosX;
                arcHeight = arcWidth / 2;

                arcTopLeftX = leftVertPosX;
                arcTopLeftY = pl.getVertStart().y - arcHeight / 2;

                spacerX = pl.vertRadius;
                spacerY = 0;

                // draw it                                                
                arc = new Arc2D.Double(arcTopLeftX + spacerX, arcTopLeftY + spacerY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
                shape = ig2.getStroke().createStrokedShape(arc);
                ig2.fill(shape);
                
            }

            // Draw the vertices as circles
            Ellipse2D.Double circle;
            Rectangle2D.Double rect;
            ig2.setStroke(new BasicStroke(2));
            for(Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                
                // pick color depending on SSE type
                if(pg.sseList.get(i).isHelix()) { ig2.setPaint(Color.RED); }
                else if(pg.sseList.get(i).isBetaStrand()) { ig2.setPaint(Color.BLACK); }
                else if(pg.sseList.get(i).isLigandSSE()) { ig2.setPaint(Color.MAGENTA); }
                else if(pg.sseList.get(i).isOtherSSE()) { ig2.setPaint(Color.GRAY); }
                else { ig2.setPaint(Color.LIGHT_GRAY); }

                Integer parentVertexPosInFoldingGraph = parentVertexPosInFG.get(i);
                
                if(parentVertexPosInFoldingGraph < 0) {
                    // draw vertices which are NOT part of this FG in gray
                    ig2.setPaint(Color.LIGHT_GRAY);
                } 
                
                if(bw) { ig2.setPaint(Color.GRAY); }      // for non-protein graphs
                
                // draw a shape based on SSE type
                                
                
                SSE sse = pg.getVertex(i);
                if(sse.isBetaStrand()) {
                    // beta strands are black, filled squares
                    rect = new Rectangle2D.Double(vertStart.x + ((i-shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    ig2.fill(rect);
                    
                }
                else if(sse.isLigandSSE()) {
                    // ligands are magenta circles (non-filled)
                    circle = new Ellipse2D.Double(vertStart.x + ((i-shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    //ig2.fill(circle);
                    ig2.setStroke(new BasicStroke(3));  // this does NOT get filled, so give it a thicker border
                    ig2.draw(circle);
                    ig2.setStroke(new BasicStroke(2));
                }
                else {
                    // helices and all others are filled circles (helices are red circles, all others are gray circles)
                    circle = new Ellipse2D.Double(vertStart.x + ((i-shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    ig2.fill(circle);                    
                }
            }
            
            // Draw the markers for the N-terminus and C-terminus if there are any vertices in this graph            
            ig2.setStroke(new BasicStroke(2));
            ig2.setPaint(Color.BLACK);
            
            if( ! bw) {
                if(fg.getSize() > 0) {                    
                    ig2.drawString("N", vertStart.x - pl.vertDist, vertStart.y + 20);    // N terminus label
                    ig2.drawString("C", vertStart.x + numVerts * pl.vertDist, vertStart.y + 20);  // C terminus label
                }
            }
                        
            // ************************************* footer **************************************
            
            if(Settings.getBoolean("plcc_B_graphimg_footer")) {
            
                // Draw the vertex numbering into the footer
                // Determine the dist between vertices that will have their vertex number printed below them in the footer field
                Integer printNth = 1;
                //if(fg.getSize() > 9) { printNth = 1; }
                if(fg.getSize() > 99) { printNth = 2; }
                if(fg.getSize() > 999) { printNth = 3; }

                // line markers: S for sequence order, G for graph order
                
                if(fg.getSize() > 0) {                                            
                    ig2.drawString("FG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.setPaint(Color.LIGHT_GRAY);
                    ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                    ig2.drawString("PG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + lineHeight + (stringHeight / 4));
                    ig2.setPaint(Color.BLACK);
                }
                else {
                    ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
                }

                ig2.setPaint(Color.BLACK);
                for(Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                    // Draw label for every nth vertex
                    if((i + 1) % printNth == 0) {
                        sseNumberFoldingGraph = "" + (parentVertexPosInFG.get(i) >= 0 ? (parentVertexPosInFG.get(i) + 1) : "");
                        sseNumberSeq = "" + (pg.sseList.get(i).getSSESeqChainNum());
                        sseNumberProteinGraph = "" + (i + 1);
                        //stringWidth = fontMetrics.stringWidth(sseNumberSeq);
                        stringHeight = fontMetrics.getAscent();                                        

                        ig2.drawString(sseNumberFoldingGraph, pl.getFooterStart().x + ((i-shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                        ig2.setPaint(Color.LIGHT_GRAY);
                        ig2.drawString(sseNumberSeq, pl.getFooterStart().x + ((i-shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));                    
                        ig2.drawString(sseNumberProteinGraph, pl.getFooterStart().x + ((i-shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight *2) + (stringHeight / 4));                                                                    
                        ig2.setPaint(Color.BLACK);
                    }
                }

                if(Settings.getBoolean("plcc_B_graphimg_legend")) {
                    SSEGraph.drawLegendSEQ(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + (lineHeight * 3) + (stringHeight / 4)), pl, fg);
                }
            
            }
                      
            Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
            
            DrawResult drawRes = new DrawResult(ig2, roi);
            return drawRes;                                                                         
    }
    
    
    
    /**
     * Returns a list of all vertex indices in this graph, which is trivial. It is a list of integers from 0 to (this.size-1).
     * @return a list of all vertex indices in this graph, which is a list of integers from 0 to (this.size-1)
     */
    public List<Integer> getVertexIndexList() {
        List<Integer> vertIndices = new ArrayList<Integer>();
        for(int i = 0; i < this.size; i++) {
            vertIndices.add(i);
        }
        return vertIndices;
    }
                    
    

}

/**
 * A class required only for the priority queue used in the Dijkstra implementation of the ProtGraph class. 
 * Allows comparing vertices by their minDistance property.
 * 
 * @author spirit
 */
class PriorityVertex implements Comparable<PriorityVertex> {
      public Integer index;
      public Integer minDistance;
      
      
      @Override public String toString() { return("" + index); }

      /**
       * Implements comparability between PriorityVertex instances via the minDinstance property, as required by the priorityQueue.
       */ 
    @Override
      public int compareTo(PriorityVertex other) {
          return(Double.compare(this.minDistance, other.minDistance));
      }
      
      /**
       * Constructor which creates a new PriorityVertex using the position in the sequential SSE list and the minDistance property.
       * A PriorityVertex object represents an SSE in the protein graph. It is used by the Dijkstra implementation to compute distances and 
       * shortest paths with the protein graph.
       * @param index the position of the SSE in the SSE list of the protein graph
       * @param minDistance the minimal distance of this vertex to the vertex for which the single-source shortest path problem is currently solved (see ProtGraph.computePaths() function).
       */
      PriorityVertex(Integer index, Integer minDistance) {
          this.index = index;
          this.minDistance = minDistance;
      }  
  }



    
    

