/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package proteingraphs;

import datastructures.SparseGraph;
import graphdrawing.IDrawableEdge;
import proteinstructure.SSE;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import tools.DP;

/**
 * A folding graph is a connected component of a protein graph.
 * @author ts
 */
public class FoldingGraph extends SSEGraph {
    
    
    public static final Integer ORIENTATION_NONE = 0;     // no direction (because this SSE-pair does not appear in the list)
    public static final Integer ORIENTATION_UPWARDS = 1;       // direction: upwards (for the SSE symbols, e.g. arrows). Note that the direction represents the spatial relation between this SSE and its predecessor (i.e., an SSE pair), it is NOT a property of a single SSE.
    public static final Integer ORIENTATION_DOWNWARDS = 2;     // direction: downwards
    
    public static final String FG_NOTATION_KEY = "KEY";
    public static final String FG_NOTATION_ADJ = "ADJ";
    public static final String FG_NOTATION_RED = "RED";
    public static final String FG_NOTATION_SEQ = "SEQ";
    public static final String FG_NOTATION_DEF = "DEF";
    
    /**
     * Translate orientation int to string
     * @param o the orientation, use FoldingGraph.ORIENTATION_*
     * @return the string, like "UP" or "DOWN"
     */
    public static String getOrientationString(Integer o) {
        if(o.equals(FoldingGraph.ORIENTATION_UPWARDS)) {
            return "UP";
        }
        else if(o.equals(FoldingGraph.ORIENTATION_DOWNWARDS)){
            return "DOWN";
        }
        else {
            return "NONE";
        }
    }

    /** Names for the folds (folding graphs) by index of the CC. The first CC is called 'A', the second 'B', and so on. */
    public static final String foldNames = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    
    public static String getFoldNameOfFoldNumber(int fn) {
        if(fn < foldNames.length()) {
            return "" + foldNames.charAt(fn);
        }
        else {
            DP.getInstance().w("FoldingGraph", "getFoldNameOfFoldNumber(): No unique name available for fold #" + fn + ".");
            return "0"; // the last fold name
        }
    }
    
    //private FoldingGraph sisterFG = null;
   
 
    public SSEGraph getParent() {
        return this.parent;
    }
       
    private Integer foldingGraphNumber = null;
    /** Mapping of vertex indices in this folding graph to vertex indices in the parent PG. At position n in the list, the position of the vertex at index n in this FG in the parent PG is given. */
    private ArrayList<Integer> vertexIndicesInParentGraph = null;
    private ArrayList<Integer> vertexIndicesInADJandSEQfgs = null;
    
    
    
    /**
     * Constructor. Requires a list of SSEs that will be represented by the vertices of the graph.
     * @param sses a list of SSEs which make up this folding graph. The contacts have to be added later (or there will be none).
     */
    FoldingGraph(List<SSE> sses) {
        super(sses);
        this.isProteinGraph = false;
    }
         
    
    /**
     * Returns all vertices of this graph.
     * @return all vertices of this graph
     */
    public List<SSE> getVertexList() {
        return this.sseList;
    }
    
    
    /**
     * Returns a mapping of vertex indices in this folding graph to vertex indices in the parent PG. At position n in
     * the list, the position of the vertex at index n in this FG in the parent PG is given.
     * @return a mapping of vertex indices in this folding graph to vertex indices in the parent PG
     */
    public List<Integer> getVertexIndexListInParentGraph() {
        return this.vertexIndicesInParentGraph;
    }
    
    
    /**
     * Returns the minimum of a mapping of vertex indices in this folding graph to vertex indices in the parent PG.
     * (See the function getVertexIndexListInParentGraph() for details.) This is used to create an ordering of the connected components of a protein graph.
     * @return the minimum of a mapping of vertex indices in this folding graph to vertex indices in the parent PG.
     */
    public Integer getMinimalVertexIndexInParentGraph() {
        return Collections.min(this.vertexIndicesInParentGraph);
    }
    
    /**
     * Returns an identifier string of this graph for debug purposes, the string gives a unique identification of this FG (pdbid, chain, graphtype, fold number).
     * @return a short string which is a unique identification of this FG (pdbid, chain, graphtype, fold number)
     */
    public String getQuickIDString() {
        return this.pdbid + "_" + this.chainid + "_" + this.graphType + "_#" + this.foldingGraphNumber + "";
    }
    
    /**
     * Returns the maximum of a mapping of vertex indices in this folding graph to vertex indices in the parent PG.
     * (See the function getVertexIndexListInParentGraph() for details.) This is used to create an ordering of the connected components of a protein graph.
     * @return the maximum of a mapping of vertex indices in this folding graph to vertex indices in the parent PG.
     */
    public Integer getMaximalVertexIndexInParentGraph() {
        return Collections.max(this.vertexIndicesInParentGraph);
    }
    
    /**
     * Specifies the index of each vertex in the parent graph.
     * @param order the vertex order. At position n in the list, the position of the vertex at index n in this FG in the parent PG is given.
     */
    public void setVertexIndicesInParentGraph(ArrayList<Integer> order) {
        if(order.size() != this.size) {
            DP.getInstance().e("FoldingGraph", "setVertexIndicesInParentGraph: Given vertex order in parent graph has wrong length, not setting it.");
            return;
        }
        this.vertexIndicesInParentGraph = order;
        //System.out.println("[FG] vertex order in parent set to: " + IO.intListToString(this.vertexIndicesInParentGraph) + ".");
    }
    
    /**
     * Specifies the index of each vertex in the ADJ and SEQ folding graphs (these graphs include more vertices than just the CC vertices, so indices differ).
     * @param order the vertex order. At position n in the list, the position of the vertex at index n in this FG in the ADJ or SEQ FG is given. Note that, if this is a ADJ or SEQ folding graph,
     * this list is rather useless / trivial.
     */
    public void setVertexIndicesInADJandSEQfoldingGraphs(ArrayList<Integer> order) {
        if(order.size() != this.size) {
            DP.getInstance().e("FoldingGraph", "setVertexIndicesInADJandSEQfoldingGraphs: Given vertex order in ADJ and SEQ folding graphs has wrong length, not setting it.");
            return;
        }        
        this.vertexIndicesInADJandSEQfgs = order;
    }
           
                   
    
    /**
     * Returns the SSE at position spatPos (an index) in spatial ordering, defined by its sequential index.
     * @param spatPos the index in the spatial ordering list of SSEs
     * @return the SSE at position spatPos (an index) in spatial ordering or null if this graph has no such ordering
     */
    protected Integer getSSESeqIndexatSpatOrderPosition(Integer spatPos) {
        
        if(this.supportsKeyNotation()) {
            for(Integer i = 0; i < this.getSize(); i++) {
                if(Objects.equals(this.getSpatOrder().get(i), spatPos)) {
                    return i;
                }
            }
        } else {
            DP.getInstance().w("FoldingGraph", "getSSESeqIndexatSpatOrderPosition: this FG does not support KEY notation, returning null.");
            return null;
        }
           
        DP.getInstance().w("FoldingGraph", "getSSESeqIndexatSpatOrderPosition: did not find spatPos " + spatPos + " in list, returning null.");
        DP.getInstance().flush();
        return null;
        
    }
    
    
    /**
     * Returns the position (index) of the given SSE in the spatial ordering list.
     * @param seqSSEIndex the SSE, defined by its index in sequential ordering
     * @return the position of the SSE in spatOrder or null if no such SSE was found in the list / this graph has no valid spatial ordering.
     */
    protected Integer getSpatOrderIndexOfSSE(Integer seqSSEIndex) {
        if(this.supportsKeyNotation()) {            
            return getSpatOrder().get(seqSSEIndex);                        
        }            
        return null;        
    }
    
    

    /**
     * Determines whether this graph supports KEY notation and fills in the spatial ordering (this.spatOrder) if it does.
     * @return true if this graph does support KEY notation, false otherwise
     */
    public Boolean supportsKeyNotation() {
        if(this.isBifurcated()) {
            return(false);
        }
        
        
        this.setSpatOrder(this.getSpatialOrderingOfVertexIndices());
        //if(this.spatOrder.size() != this.getSize()) {
        //    DP.getInstance().w("FoldingGraph", "supportsKeyNotation: spatOrder size = " + this.spatOrder.size() + ", but gaph size is " + this.getSize() + " -- KEY not supported.");
        //    return(false);
        //}
        
        
        return true;        
    }
    
    
    /**
     * Draws the KEY notation image of this graph, writing the image in PNG format to the file 'filePath' (which should maybe end in ".png").
     * @param filePath the output path of the image
     * @return true if it worked out, false otherwise
     */
    /*
    @Deprecated
    public Boolean drawFoldingGraphKEY(String filePath) {
        
       
        //--------------------------------------------------------------------------------------------------
        
        //--------------------------------------------------------------------------------------------------
       
        
        if(! this.supportsKeyNotation()) {
            System.err.println("NOTE: ProtGraph.drawFoldingGraphKEY(): Called for graph which does not support KEY notation, skipping.");
            return false;
        }
        
        
        // All these values are in pixels
        Integer numVerts = this.numVertices();
        PageLayout pl = new PageLayout(numVerts);
        

        // page setup
        Integer marginLeft = pl.marginLeft;
        Integer marginTop = pl.marginTop;
        Integer minImgHeight = pl.minImgHeight;

        // The header that contains the text describing of the graph.
        Integer headerHeight = pl.headerHeight;

        // The footer contains the vertex numbering.

        // Where to start drawing
        Integer headerStartX = pl.marginLeft;
        Integer headerStartY = pl.marginTop;
        Integer footerStartX = pl.marginLeft;


        // drawing of objects
        Integer vertDist = 50;                  // distance between (centers of) vertices in the drawing
        Integer vertRadius = 10;                // the width of a drawn vertex (radius around its center)
        Integer vertHeight = 80;                // height of vertex element graphics (arrow height)
        Integer vertWidth = 40;                 // height of vertex graphics (arrow width)
        pl.setVertRadius(40);

        // Determine the maximal arc height. The height of an arc is half of its width (see the part where the arcs
        //  are drawn below), but the arcHeight really describes the height of the full circle this half-circle is
        //  a part of. So we gotta divide by 2 again; the maximal height is thus a quarter of the maximal distance between
        //  vertices in the image.
        Integer maxVertDist = this.numVertices() * vertDist;
        Integer maxArcHeight = maxVertDist / 2;

        // the image area: the part where the vertices and arcs are drawn
        Integer imgWidth = numVerts * vertDist + 2 * vertRadius;
        Integer imgHeight = maxArcHeight + vertRadius;
        if(imgHeight < minImgHeight) { imgHeight = minImgHeight; }

        // The text drawn in the proteinHeader is about 300px wide with the current text. We assume a maximum of
        //  500 here and make this the minimum width of the content frame.
        Integer headerTextWidth = 500;
        if(imgWidth < headerTextWidth) {
            imgWidth = headerTextWidth;
        }

        // where to start drawing the vertices
        Integer vertStartX = pl.getVertStart().x;
        Integer vertStartY = pl.getVertStart().y;
        Integer footerStartY = pl.getFooterStart().y;

        // putting it all together
        Integer pageWidth = pl.getPageWidth();
        Integer pageHeight = pl.getPageHeight();

        try {

            // ------------------------- Prepare stuff -------------------------
            BufferedImage bi = new BufferedImage(pl.getPageWidth(), pl.getPageHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D ig2 = bi.createGraphics();
            ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pl.drawAreaOutlines(ig2);

            // make background white
            ig2.setPaint(Color.WHITE);
            ig2.fillRect(0, 0, pageWidth, pageHeight);
            ig2.setPaint(Color.BLACK);

            // prepare font
            Font font = pl.getStandardFont();
            ig2.setFont(font);
            FontMetrics fontMetrics = ig2.getFontMetrics();

            // ------------------------- Draw header -------------------------

            // check width of header string
            //String proteinHeader = "The " + this.graphType + " graph of PDB entry " + this.pdbid + ", chain " + this.chainid + " in KEY notation.";
            String proteinHeader = "The " + this.graphType + " folding graph # " + this.getFoldingGraphNumber() + " of PDB entry " + this.pdbid + ", chain " + this.chainid + ", KEY notation [V=" + this.numVertices() + ", E=" + this.numSSEContacts() + "].";
            Integer stringWidth = fontMetrics.stringWidth(proteinHeader);       // Should be around 300px for the text above
            Integer stringHeight = fontMetrics.getAscent();            

            if(Settings.getBoolean("plcc_B_graphimg_header")) {
                ig2.drawString(proteinHeader, headerStartX, headerStartY);
            }

            // ------------------------- Draw the graph -------------------------

            // Draw the edges as arcs
            java.awt.Shape shape;
            Arc2D.Double arc;
            Integer edgeType, connCenterX, connCenterY, leftVertSeq, rightVertSeq, leftVertSpat, rightVertSpat, leftVertPosX, leftVertPosY, rightVertPosX, rightVertPosY, connWidth, connHeight, connTopLeftX, connTopLeftY, spacerX, spacerY;
            
            // determine the headings of the vertices in the image. if two adjacent SSEs are parallel, they point in the same direction in the image. if they are antiparallel, they point into different directions.
            Integer[] headingsSpatOrder = new Integer[this.size];    // the heading of the vertex in the image (UP or DOWN). This is in the order of spatOrder variable, not the the original vertex list in the SSEList variable.
            Integer[] headingsSeqOrder = new Integer[this.size];
            headingsSpatOrder[0] = ORIENTATION_UPWARDS;  // heading of the 1st vertex is up by definition (it has no predecessor)
            
            
            
            
           
            if(this.size > 1) {
                
                for(Integer spatPos = 1; spatPos < this.size; spatPos++) {
                    Integer sseSeqIndex = this.getSSESeqIndexatSpatOrderPosition(spatPos);
                    Integer lastsseSeqIndex = this.getSSESeqIndexatSpatOrderPosition(spatPos - 1);
                    Integer spatRel = this.getContactSpatRel(sseSeqIndex, lastsseSeqIndex);
                    
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
                        headingsSpatOrder[spatPos] = (headingsSpatOrder[spatPos-1] == ORIENTATION_UPWARDS ? ORIENTATION_DOWNWARDS : ORIENTATION_UPWARDS);
                    }                
                }
                
                // copy from spatOrder to seqOrder array
                Integer seqIndex;
                for(Integer i = 0; i < this.getSize(); i++) {
                    seqIndex = spatOrder.get(i);
                    headingsSeqOrder[seqIndex] = headingsSpatOrder[i];
                }
                
                
            }                                    
            
            
            
            // now draw the connectors
            Integer iSpatIndex, jSpatIndex;
            Boolean startUpwards;
            ig2.setStroke(new BasicStroke(1));
            if(this.numEdges() > 0) {                          
                for(Integer i = 0; i < this.sseList.size(); i++) {
                    for(Integer j = i + 1; j < this.sseList.size(); j++) {

                        // If there is a contact...
                        if(this.containsEdge(i, j)) {

                            // determine edge type and the resulting color
                            edgeType = this.getContactSpatRel(i, j);
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
                            iSpatIndex = this.getSpatOrderIndexOfSSE(i);
                            jSpatIndex = this.getSpatOrderIndexOfSSE(j);
                            
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
                            
                            if(headingsSeqOrder[leftVertSeq] == ORIENTATION_UPWARDS) {
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
                            
                            if(headingsSeqOrder[rightVertSeq] == ORIENTATION_UPWARDS) {
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
                            ArrayList<Shape> connShapes = this.getArcConnector(leftVertPosX, leftVertPosY, rightVertPosX, rightVertPosY, ig2.getStroke(), startUpwards, 0);
                            for(Shape s : connShapes) {
                                ig2.draw(s);
                            }                                                        
                        }
                    }
                }
            }

            
            ig2.setStroke(new BasicStroke(1));

            // DEBUG
            //Line2D line = new Line2D.Float(vertStartX, vertStartY, (vertStartX + ((this.sseList.size() - 1) * vertDist)), vertStartY);
            //ig2.draw(line);
            //System.out.println("Drawing " + this.sseList.size() + " vertices...");
            
            // Draw the vertices as arrows or barrels (depending on the type)
            Polygon p;
            
            // prepare rotation of canvas            
            AffineTransform origXform = ig2.getTransform();
            AffineTransform newXform;
                    
            

            int rotationCenterX, rotationCenterY;
            int angle = 180;

            
            
            for(Integer s = 0; s < this.sseList.size(); s++) {
                Integer i = spatOrder.get(s);
                if(this.sseList.get(i).isHelix()) { ig2.setPaint(Color.RED); }
                else if(this.sseList.get(i).isBetaStrand()) { ig2.setPaint(Color.BLACK); }
                else if(this.sseList.get(i).isLigandSSE()) { ig2.setPaint(Color.MAGENTA); }
                else if(this.sseList.get(i).isOtherSSE()) { ig2.setPaint(Color.GRAY); }
                else { ig2.setPaint(Color.LIGHT_GRAY); }
                
                Integer currentVertX = vertStartX + (i * vertDist);
                Integer currentVertY = vertStartY;
                
                // draw it                                
                if(this.sseList.get(i).isHelix()) {
                    p = getDefaultArrowPolygon((vertStartY - vertHeight), currentVertX, currentVertY);
                    //p = getDefaultBarrelPolygon(vertStartY - vertHeight, vertStartX + (i * vertDist), vertStartY);
                    //System.out.println("SSE is helix: " + this.sseList.get(i).longStringRep() + ", drawing with base position (" + (vertStartX + (i * vertDist)) + "," + vertStartY + ").");
                    
                }
                else {
                    p = getDefaultArrowPolygon((vertStartY - vertHeight), currentVertX, currentVertY);
                    //p = getDefaultBarrelPolygon((vertStartY - vertHeight), (vertStartX + (i * vertDist)), vertStartY);
                    //System.out.println("SSE is NOT a helix: " + this.sseList.get(i).longStringRep() + ", drawing with base position (" + (vertStartX + (i * vertDist)) + "," + vertStartY + ").");
                }
                
                shape = ig2.getStroke().createStrokedShape(p);
                                
                newXform = (AffineTransform)(origXform.clone());
                // set rotation center to center of the current arrow / rectangle
                rotationCenterX = (vertStartX + (i * vertDist));
                rotationCenterY = vertStartY - (vertHeight / 2);
                
                // perform rotation
                newXform.rotate(Math.toRadians(angle), rotationCenterX, rotationCenterY);
                
                if(Objects.equals(headingsSeqOrder[i], ORIENTATION_DOWNWARDS)) { 
                    //ig2.rotate(Math.toRadians(180));                                         
                    ig2.setTransform(newXform);
                    //System.out.println("Rotating canvas before drawing SSE #" + i + " of the list.");
                }
                ig2.draw(shape);
                if(Objects.equals(headingsSeqOrder[i], ORIENTATION_DOWNWARDS)) { 
                    ig2.setTransform(origXform);
                    //ig2.rotate(Math.toRadians(-180)); 
                    //System.out.println("Rotating canvas back to normal after drawing SSE #" + i + " of the list.");
                }
                
                
                // draw the N or C terminus label under it if applicable
                //Integer compareToTerminus = s;
                Integer compareToTerminus = i;
                Integer ssePos = s;
                if( (Objects.equals(this.closestToCTerminus(), compareToTerminus))  || (Objects.equals(this.closestToNTerminus(), compareToTerminus)) ) {
                    if(Objects.equals(this.closestToCTerminus(), compareToTerminus)) {
                        //System.out.println("    SSE # " + compareToTerminus + " (pos # " + ssePos + ") is closest to C terminus.");
                        ig2.drawString("C", currentVertX, (currentVertY + 20));
                    }
                    
                    if(Objects.equals(this.closestToNTerminus(), compareToTerminus)) { 
                        //System.out.println("    SSE # " + compareToTerminus + " (pos # " + ssePos + ")  is closest to N terminus.");
                        ig2.drawString("N", currentVertX, (currentVertY + 20));
                    }      
                    
                }
                
                
                
            }
            
            //Line2D lineTop = new Line2D.Float(vertStartX, (vertStartY - vertHeight), (vertStartX + ((this.sseList.size() - 1) * vertDist)), (vertStartY - vertHeight));
            //ig2.draw(lineTop);
            
            

            // Draw the vertex numbering into the footer
            font = pl.getStandardFont();
            ig2.setFont(font);
            fontMetrics = ig2.getFontMetrics();
            ig2.setPaint(Color.BLACK);

            // Determine the dist between vertices that will have their vertex number printed below them in the footer field
            Integer printNth = 1;
            //if(this.sseList.size() > 9) { printNth = 1; }
            if(this.sseList.size() > 99) { printNth = 2; }
            if(this.sseList.size() > 999) { printNth = 3; }

            stringHeight = fontMetrics.getAscent();
            String sseNumberInGraph, sseNumberInSequence;
            ig2.drawString("FG", footerStartX - 40, footerStartY + (stringHeight / 4));
            ig2.drawString("SQ", footerStartX - 40, footerStartY + 40 + (stringHeight / 4));
            for(Integer i = 0; i < this.sseList.size(); i++) {
                // Draw label for every 2nd vertex
                if((i + 1) % printNth == 0) {
                    sseNumberInGraph = "" + (spatOrder.get(i) + 1);
                    sseNumberInSequence = "" + this.getSSEBySeqPosition(spatOrder.get(i)).getSSESeqChainNum();
                    stringWidth = fontMetrics.stringWidth(sseNumberInGraph);
                    
                    ig2.drawString(sseNumberInGraph, footerStartX + (i * vertDist), footerStartY + (stringHeight / 4));
                    ig2.drawString(sseNumberInSequence, footerStartX + (i * vertDist), footerStartY + 40 + (stringHeight / 4));
                }
            }



            // all done, write the image to disk
            ImageIO.write(bi, "PNG", new File(filePath));

        } catch (Exception e) {
            DP.getInstance().w("Could not write image file for graph to file '" + filePath + "': " + e.getMessage() + ".");
            return(false);
        }

        return(true);
    }
    */
    
    /**
     * Returns a static String that is the header for the plcc format.
     *
     * @return the multi-line header string, including a label with PDB ID and
     * graph type
     */
    @Override protected String getPlccFormatHeader() {
        String outString = "# This is the plcc format folding graph file for the " + this.graphType + " graph of PDB entry " + this.pdbid + ", chain " + this.chainid + ", FG# " + this.foldingGraphNumber + ".\n";
        outString += "# First character in a line indicates the line type ('#' => comment, '>' => meta data, '|' => SSE, '=' => contact).\n";
        //outString += "# Note on parsing this file: You can savely remove all whitespace from a line before splitting it.\n";

        return (outString);
    }
    
    
    /**
     * Small debug function to mark a postition on the canvas with a cross in the specified color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param ig2 the G2D canvas to draw on
     * @param c the Color
     */
    private void markPosition(Integer x, Integer y, Graphics2D ig2, Color c) {        
        Integer crossWidth = 5;
        
        Line2D lineRC1 = new Line2D.Float(x - crossWidth, y, x + crossWidth, y);
        Line2D lineRC2 = new Line2D.Float(x, y - crossWidth, x, y + crossWidth);
        ig2.draw(lineRC1);
        ig2.draw(lineRC2);
    }

        
    /**
     * Folding graphs are connected by definition, so this is easy. ;)
     * @return always true
     */
    @Override public Boolean isConnected() {
        return(true);
    }
    
    
    
    public void declareFoldingGraphOf(SSEGraph p) { 
        // checking for cennectivity calculates the connected components and this is used in the function, so checking for
        //  connectivity here would produce an andless loop. in short, we don't check here atm.
        //if(this.isConnected()) {
            this.isProteinGraph = false;
            this.parent = p; 
        //}
        //else {
        //    System.err.println("ERROR: declareFoldingGraphOf(): Tried to declare a non-connected graph a folding graph, which contradicts the FG definition.");
        //    System.exit(1);            
        //}        
    }
    
    
    
    /**
     * Set the folding graph number based on the vertex position in the parent graph. See the FoldingGraphComparator class for details on the ordering to use.
     * @param i the number to set.
     */
    public void setFoldingGraphNumber(Integer i) { this.foldingGraphNumber = i; }
    
    /**
     * Returns the fold name (which depends on the folding graph number).
     * @return the fold name (which depends on the folding graph number)
     */
    public String getFoldingGraphFoldName() {
        return FoldingGraph.getFoldNameOfFoldNumber(this.foldingGraphNumber);
    }
    
    /**
     * Returns the folding graph number based on the vertex position in the parent graph. See the FoldingGraphComparator class for details on the ordering to use.
     * @return the folding graph number based on the vertex position in the parent graph. See the FoldingGraphComparator class for details on the ordering to use.
     */
    public Integer getFoldingGraphNumber() { return(this.foldingGraphNumber); }                   
    
    
    /**
     * Returns a very short string description of this graph. 
     * @return a string in format 'PDBID-CHAIN-GRAPHTYPE-FGNUM'.
     */
    @Override public String toShortString() {
        return(this.pdbid + "-" + this.chainid + "-" + this.graphType + "-" + "FG" + this.getFoldingGraphNumber() + "[" + this.numVertices() + "V," + this.numSSEContacts() + "E]");
    }
    
    
    /**
     * Sets the info fields of this graph, defining the PDB ID as 'pdbid', the chain id as 'chainid' and the graph type as 'graphType'. Also sets the meta data.
     * @param pdbid the PDB identifier, e.g., "8icd"
     * @param chainid the PDB chain ID, e.g., "A"
     * @param chainMolid the macromolecule ID of the chain in the PDB file, e.g., "1". Defines which chains belong to same macromoelcue.
     * @param graphType the graph type, e.g., "albe"
     */
    @Override public void setInfo(String pdbid, String chainid, String chainMolid, String graphType) {
        super.setInfo(pdbid, chainid, chainMolid, graphType);
        this.metadata.put("graphclass", "folding graph");     
        this.metadata.put("foldinggraphnumber", this.foldingGraphNumber + "");     
    }     
    
    
    /**
     * Constructs a SparseGraph from the data in this graph.
     * @return the SparseGraph
     */
    @Override
    public SparseGraph<String, String> toSparseGraph() {
        SparseGraph<String, String> g = new SparseGraph<>();
        
        // add verts
        for(int i = 0; i < this.sseList.size(); i++) {
            g.addVertex(this.getFGNotationOfVertex(i));
        }
        
        // add edges
        Integer[] e;
        for(int i = 0; i < this.getEdgeList().size(); i++) {
            e = this.getEdgeList().get(i);
            g.addEdge(e[0], e[1], SpatRel.getString(this.matrix[e[0]][e[1]]));
        }
        
        if(this.sseList.size() != g.getNumVertices()) {
            DP.getInstance().e("FoldingGraph", "toSparseGraph: vertex counts of graphs do not match.");
        }
        if(this.getEdgeList().size() != g.getNumEdges()) {
            DP.getInstance().e("FoldingGraph", "toSparseGraph: edge counts of graphs do not match.");
        }
        return g;        
    }
    
    
    
    
}
