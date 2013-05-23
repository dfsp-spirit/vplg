/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package plcc;

import tools.DP;
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
import javax.imageio.ImageIO;
import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author ts
 */
public class FoldingGraph extends SSEGraph {
    
    
    public final Integer ORIENTATION_NONE = 0;     // no direction (because this SSE-pair does not appear in the list)
    public final Integer ORIENTATION_UPWARDS = 1;       // direction: upwards (for the SSE symbols, e.g. arrows). Note that the direction represents the spatial relation between this SSE and its predecessor (i.e., an SSE pair), it is NOT a property of a single SSE.
    public final Integer ORIENTATION_DOWNWARDS = 2;     // direction: downwards
    
    public static final String FG_NOTATION_KEY = "KEY";
    public static final String FG_NOTATION_ADJ = "ADJ";
    public static final String FG_NOTATION_RED = "RED";
    public static final String FG_NOTATION_SEQ = "SEQ";
    
    
    
    private Integer foldingGraphNumber = null;
    
    
    
    /**
     * Constructor. Requires a list of SSEs that will be represented by the vertices of the graph.
     * @param sses a list of SSEs which make up this folding graph. The contacts have to be added later (or there will be none).
     */
    FoldingGraph(ArrayList<SSE> sses) {
        super(sses);
        this.isProteinGraph = false;
    }
    
    
    
    /**
     * Draws the current folding graph in the notation 'notation' (which is one of "KEY", "SEQ", "ADJ" or "RED") and writes the image
     * in PNG format to file 'filePath'.
     * @param filePath the output path of the image
     * @param notation the notation to use. valid notation strings are 'KEY', 'ADJ', 'RED' and 'SEQ', use the static FoldingGraph.FG_NOTATION_* strings.
     * @return true if it worked out, false otherwise 
     */
    public Boolean drawFoldingGraph(String notation, String filePath) {

        // Folding graphs are the connected components of a protein graph, they are thus connected.
        if( ! this.isConnected()) {
            System.err.println("ERROR: drawFoldingGraph(): Can't use folding graph-specific funtion on protein graph (this graph is not connected).");
            return(false);
        }

        // They should also be marked as folding graphs.
        if( ! this.isFoldingGraph()) {
            DP.getInstance().w("drawFoldingGraph(): This graph is connected but it is not declared to be a folding graph. Still trying to draw.");
        }

        // Check minimum size
        if(this.numVertices() < Settings.getInteger("plcc_I_min_fgraph_size_draw")) {
            System.out.println("INFO: drawFoldingGraph(): Ignoring folding graph of size " + this.numVertices() + ", minimum size is " + Settings.getInteger("plcc_I_min_fgraph_size_draw") + ".");
            return(false);
        }

        // OK, let's go.
        if(notation.equals(FoldingGraph.FG_NOTATION_KEY)) {
            if(this.supportsKeyNotation()) {
                return(this.drawFoldingGraphKEY(filePath));
            } else {
                return false;
            }
        }
        else if(notation.equals(FoldingGraph.FG_NOTATION_ADJ)) {
            return(this.drawFoldingGraphADJ(filePath));
        }
        else if(notation.equals(FoldingGraph.FG_NOTATION_RED)) {
            return(this.drawFoldingGraphRED(filePath));
        }
        else if(notation.equals(FoldingGraph.FG_NOTATION_SEQ)) {
            return(this.drawFoldingGraphSEQ(filePath));
        }
        else {
            System.err.println("ERROR: Folding graph notation '" + notation + "' invalid.");
            return(false);
        }
    }
    
    
    /**
     * Draws the ADJ notation image of this graph, writing the image in PNG format to the file 'filePath' (which should maybe end in ".png").
     * The ADJ notation is the same style we use for the PGs. It is similar to the RED style but includes all SSEs of the PG, not only
     * those of the current FG.
     * @param filePath the output path of the image
     * @return true if it worked out, false otherwise 
     */
    private Boolean drawFoldingGraphADJ(String filePath) {
        //DP.getInstance().w("Folding graph notation 'ADJ' not implemented yet, drawing RED instead.");
        //return(this.drawFoldingGraphRED(filePath));
        return(false);
    }
    
    /**
     * Returns the SSE at position spatPos (an index) in spatial ordering.
     * @param spatPos the index in the spatial ordering list of SSEs
     * @return the SSE at position spatPos (an index) in spatial ordering or null if this graph has no such ordering
     */
    private SSE getSSEatSpatOrderPosition(Integer spatPos) {
        
        if(this.supportsKeyNotation()) {
            return this.getSSEBySeqPosition(spatOrder.get(spatPos));            
        } else {
            return null;
        }
    }
    
    /**
     * Returns the SSE at position spatPos (an index) in spatial ordering, defined by its sequential index.
     * @param spatPos the index in the spatial ordering list of SSEs
     * @return the SSE at position spatPos (an index) in spatial ordering or null if this graph has no such ordering
     */
    private Integer getSSESeqIndexatSpatOrderPosition(Integer spatPos) {
        
        if(this.supportsKeyNotation()) {
            for(Integer i = 0; i < this.getSize(); i++) {
                if(this.spatOrder.get(i) == spatPos) {
                    return i;
                }
            }
        } 
           
        return null;
        
    }
    
    
    /**
     * Returns the position (index) of the given SSE in the spatial ordering list.
     * @param seqSSEIndex the SSE, defined by its index in sequential ordering
     * @return the position of the SSE in spatOrder or null if no such SSE was found in the list / this graph has no valid spatial ordering.
     */
    private Integer getSpatOrderIndexOfSSE(Integer seqSSEIndex) {
        if(this.supportsKeyNotation()) {            
            return spatOrder.get(seqSSEIndex);                        
        }            
        return null;        
    }
    
    
    /**
     * Draws the SEQ notation image of this graph, writing the image in PNG format to the file 'filePath' (which should maybe end in ".png").
     * @param filePath the output path of the image
     * @return true if it worked out, false otherwise 
     */
    private Boolean drawFoldingGraphSEQ(String filePath) {
        //DP.getInstance().w("Folding graph notation 'SEQ' not implemented yet.");
        return(false);
    }

    /**
     * Determines whether this graph supports KEY notation and fills in the spatial ordering (this.spatOrder) if it does.
     * @return true if this graph does support KEY notation, false otherwise
     */
    public Boolean supportsKeyNotation() {
        if(this.isBifurcated()) {
            return(false);
        }
        
        this.spatOrder = this.getSpatialOrderingOfVertexIndices();
        if(this.spatOrder.size() != this.getSize()) {
            return(false);
        }
        
        return true;        
    }
    
    
    /**
     * Draws the KEY notation image of this graph, writing the image in PNG format to the file 'filePath' (which should maybe end in ".png").
     * @param filePath the output path of the image
     * @return true if it worked out, false otherwise
     */
    private Boolean drawFoldingGraphKEY(String filePath) {
        
        //TODO: remove this when function is ready
        // Currently this is under development and broken, so disable it
        //--------------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------------
        if(Settings.getInteger("plcc_I_debug_level") < 1) {
            System.out.println("         -Not drawing KEY notation: WIP.");
            return false;
        }
        //--------------------------------------------------------------------------------------------------
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
            
            /*
             *              
            Integer curSSE, spatPredecessorSSE;
            if(this.size > 1) {
                for(Integer i = 0; i < (this.getSize() - 1); i++) {
                    curSSE = i;
                }
            }
            */
            
            
           
            if(this.size > 1) {
                
                for(Integer spatPos = 1; spatPos < this.size; spatPos++) {
                    Integer sseSeqIndex = this.getSSESeqIndexatSpatOrderPosition(spatPos);
                    Integer lastsseSeqIndex = this.getSSESeqIndexatSpatOrderPosition(spatPos - 1);
                    Integer spatRel = this.getContactType(sseSeqIndex, lastsseSeqIndex);
                    
                    if(spatRel == SpatRel.PARALLEL) {
                        // keep orientation
                        headingsSpatOrder[spatPos] = headingsSpatOrder[spatPos-1];
                    }
                    else if(spatRel == SpatRel.NONE) {
                        // should never happen
                        headingsSpatOrder[spatPos] = headingsSpatOrder[spatPos-1];    // whatever
                        System.err.println("ERROR: Vertices without contact are considered neighbors in the graph.");
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
                
                // DEBUG output
                System.out.println("DEBUG: " + this.toShortString() );
                System.out.println("DEBUG: SpatOrder: " + IO.intArrayListToString(this.spatOrder) );
                System.out.println("DEBUG: SpatHeadg: " + IO.intArrayToString(headingsSpatOrder) );
                System.out.println("DEBUG: SeqHeadng: " + IO.intArrayToString(headingsSeqOrder) );                
                String neighbors = "DEBUG: SeqDegree: ";
                for(Integer i = 0; i < this.getSize(); i++) {
                    neighbors += this.degreeOfVertex(i) + " ";
                }
                System.out.println(neighbors);
                
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
                            edgeType = this.getContactType(i, j);
                            if(edgeType.equals(SpatRel.PARALLEL)) { ig2.setPaint(Color.RED); }
                            else if(edgeType.equals(SpatRel.ANTIPARALLEL)) { ig2.setPaint(Color.BLUE); }
                            else if(edgeType.equals(SpatRel.MIXED)) { ig2.setPaint(Color.GREEN); }
                            else if(edgeType.equals(SpatRel.LIGAND)) { ig2.setPaint(Color.MAGENTA); }
                            else if(edgeType.equals(SpatRel.BACKBONE)) { ig2.setPaint(Color.ORANGE); }
                            else { ig2.setPaint(Color.LIGHT_GRAY); }

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
                            
                            System.out.print("Contact " + i + "," + j + " (left is " + leftVertSeq + "[spat:" + this.getSpatOrderIndexOfSSE(leftVertSeq) + "], right is " + rightVertSeq + "[spat:" + this.getSpatOrderIndexOfSSE(rightVertSeq) + "]): ");
                            
                            if(headingsSeqOrder[leftVertSeq] == ORIENTATION_UPWARDS) {
                                // the left vertex points upwards, so the arc should start at its top
                                leftVertPosY = vertStartY - vertHeight;
                                startUpwards = true;
                                System.out.print("leftVert starts upwards, ");
                            }
                            else {
                                // the left vertex points downwards, so the arc should start at its bottom
                                leftVertPosY = vertStartY;
                                startUpwards = false;
                                System.out.print("leftVert starts downwards, ");
                            }
                            
                            if(headingsSeqOrder[rightVertSeq] == ORIENTATION_UPWARDS) {
                                // the right vertex points upwards, so the arc should end at its bottom
                                rightVertPosY = vertStartY;
                                System.out.print("rightVert starts upwards. ");
                            }
                            else {
                                // the right vertex points downwards, so the arc should end at its top
                                rightVertPosY = vertStartY - vertHeight;
                                System.out.print("rightVert starts downpwards. ");
                            }
                            
                            
                            // draw it        
                            System.out.print("Getting arc from " + leftVertPosX + "," + leftVertPosY + " to " + rightVertPosX + "," + rightVertPosY + ".\n");
                            ArrayList<Shape> connShapes = this.getArcConnector(leftVertPosX, leftVertPosY, rightVertPosX, rightVertPosY, ig2.getStroke(), startUpwards);
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
                
                if(headingsSeqOrder[i] == ORIENTATION_DOWNWARDS) { 
                    //ig2.rotate(Math.toRadians(180));                                         
                    ig2.setTransform(newXform);
                    //System.out.println("Rotating canvas before drawing SSE #" + i + " of the list.");
                }
                ig2.draw(shape);
                if(headingsSeqOrder[i] == ORIENTATION_DOWNWARDS) { 
                    ig2.setTransform(origXform);
                    //ig2.rotate(Math.toRadians(-180)); 
                    //System.out.println("Rotating canvas back to normal after drawing SSE #" + i + " of the list.");
                }
                
                
                // draw the N or C terminus label under it if applicable
                if( (this.closestToCTerminus() == i)  || (this.closestToNTerminus() == i) ) {
                    if(this.closestToCTerminus() == i) {
                        System.out.println("    SSE # " + i + " is closest to C terminus.");
                        //ig2.drawString("C", currentVertX, (currentVertY + 20));
                    }
                    
                    if(this.closestToNTerminus() == i) { 
                        System.out.println("    SSE # " + i + " is closest to N terminus.");
                        //ig2.drawString("N", currentVertX, (currentVertY + 20));
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
            if(this.sseList.size() > 9) { printNth = 1; }
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
     * Draws this folding graph in reduced description (only SSEs of this FG count), writing the image file to 'filePath' (which should maybe end in ".png").
     * 
     * @param filePath the file system path where to write the graph image (without file extension and the dot before it)
     * @return whether the graph could be drawn and written to the file filePath
     */
    public Boolean drawFoldingGraphRED(String filePath) {

        
        Integer numVerts = this.numVertices();

        Boolean bw = false;                                                  
        
        // All these values are in pixels
        // page setup
        PageLayout pl = new PageLayout(numVerts);        
        Position2D vertStart = pl.getVertStart();
        
        try {

            // ------------------------- Prepare stuff -------------------------
            // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
            BufferedImage bi = new BufferedImage(pl.getPageWidth(), pl.getPageHeight(), BufferedImage.TYPE_INT_ARGB);
            
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


            // prepare font
            Font font = pl.getStandardFont();
            ig2.setFont(font);
            FontMetrics fontMetrics = ig2.getFontMetrics();

            // ------------------------- Draw header -------------------------

            // check width of header string
            String proteinHeader = "The " + this.graphType + " folding graph # " + this.getFoldingGraphNumber() + " of PDB entry " + this.pdbid + ", chain " + this.chainid + ", RED notation [V=" + this.numVertices() + ", E=" + this.numSSEContacts() + "].";
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
            Integer edgeType, leftVert, rightVert, leftVertPosX, rightVertPosX, arcWidth, arcHeight, arcTopLeftX, arcTopLeftY, spacerX, spacerY;
            for(Integer i = 0; i < this.sseList.size(); i++) {
                for(Integer j = i + 1; j < this.sseList.size(); j++) {

                    // If there is a contact...
                    if(this.containsEdge(i, j)) {

                        // determine edge type and the resulting color
                        edgeType = this.getContactType(i, j);
                        if(edgeType.equals(SpatRel.PARALLEL)) { ig2.setPaint(Color.RED); }
                        else if(edgeType.equals(SpatRel.ANTIPARALLEL)) { ig2.setPaint(Color.BLUE); }
                        else if(edgeType.equals(SpatRel.MIXED)) { ig2.setPaint(Color.GREEN); }
                        else if(edgeType.equals(SpatRel.LIGAND)) { ig2.setPaint(Color.MAGENTA); }
                        else if(edgeType.equals(SpatRel.BACKBONE)) { ig2.setPaint(Color.ORANGE); }
                        else { ig2.setPaint(Color.LIGHT_GRAY); }

                        if(bw) { ig2.setPaint(Color.LIGHT_GRAY); }      // for non-protein graphs

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
            for(Integer i = 0; i < this.sseList.size(); i++) {
                
                // pick color depending on SSE type
                if(this.sseList.get(i).isHelix()) { ig2.setPaint(Color.RED); }
                else if(this.sseList.get(i).isBetaStrand()) { ig2.setPaint(Color.BLACK); }
                else if(this.sseList.get(i).isLigandSSE()) { ig2.setPaint(Color.MAGENTA); }
                else if(this.sseList.get(i).isOtherSSE()) { ig2.setPaint(Color.GRAY); }
                else { ig2.setPaint(Color.LIGHT_GRAY); }

                if(bw) { ig2.setPaint(Color.GRAY); }      // for non-protein graphs
                
                // draw a shape based on SSE type
                if(this.sseList.get(i).isBetaStrand()) {
                    // beta strands are black, filled squares
                    rect = new Rectangle2D.Double(vertStart.x + (i * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                    ig2.fill(rect);
                    
                }
                else if(this.sseList.get(i).isLigandSSE()) {
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
                if(this.sseList.size() > 0) {                    
                    ig2.drawString("N", vertStart.x - pl.vertDist, vertStart.y + 20);    // N terminus label
                    ig2.drawString("C", vertStart.x + this.sseList.size() * pl.vertDist, vertStart.y + 20);  // C terminus label
                }
            }
                        
            // ************************************* footer **************************************
            
            if(Settings.getBoolean("plcc_B_graphimg_footer")) {
            
                // Draw the vertex numbering into the footer
                // Determine the dist between vertices that will have their vertex number printed below them in the footer field
                Integer printNth = 1;
                if(this.sseList.size() > 9) { printNth = 1; }
                if(this.sseList.size() > 99) { printNth = 2; }
                if(this.sseList.size() > 999) { printNth = 3; }

                // line markers: S for sequence order, G for graph order
                Integer lineHeight = pl.textLineHeight;            
                if(this.sseList.size() > 0) {                                            
                    ig2.drawString("FG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                }
                else {
                    ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
                }

                for(Integer i = 0; i < this.sseList.size(); i++) {
                    // Draw label for every nth vertex
                    if((i + 1) % printNth == 0) {
                        sseNumberGraph = "" + (i + 1);
                        sseNumberSeq = "" + (this.sseList.get(i).getSSESeqChainNum());
                        //stringWidth = fontMetrics.stringWidth(sseNumberSeq);
                        stringHeight = fontMetrics.getAscent();                                        

                        ig2.drawString(sseNumberGraph, pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                        ig2.drawString(sseNumberSeq, pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));                    
                    }
                }

                if(Settings.getBoolean("plcc_B_graphimg_legend")) {
                    drawLegend(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + lineHeight * 2 + (stringHeight / 4)), pl);
                }
            
            }
            
            // all done, write the image to disk
            //if(Settings.get("plcc_S_img_output_format").equals("SVG")) {
            
            //boolean useCSS = true;
            //FileOutputStream fos = new FileOutputStream(new File("/tmp/mySVG.svg"));
            //Writer out = new OutputStreamWriter(fos, "UTF-8");
            //ig2.stream(out, useCSS); 
            
            Rectangle2D aoi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
            
            //System.out.println("===============pageWidth = " + pl.getPageWidth() + ", pageHeight=" + pl.getPageHeight() + ".");
            
            String svgFilePath;
            
            // hax!
            Integer fileExtLength = Settings.get("plcc_S_img_output_fileext").length();     // already includes the dot
            Integer pathLength = filePath.length();
            svgFilePath = filePath.substring(0, pathLength - fileExtLength + 1) + "svg";
            ig2.stream(new FileWriter(svgFilePath), false);                
            
            SVGConverter svgConverter = new SVGConverter();
            svgConverter.setArea(aoi);
            svgConverter.setWidth(pl.getPageWidth());
            svgConverter.setHeight(pl.getPageHeight());
            if(Settings.get("plcc_S_img_output_format").equals("PNG")) {                
                svgConverter.setDestinationType(DestinationType.PNG);                
            } else if(Settings.get("plcc_S_img_output_format").equals("JPG")) {
                svgConverter.setDestinationType(DestinationType.JPEG);
            } else {
                svgConverter.setDestinationType(DestinationType.TIFF);
            }
            
            svgConverter.setSources(new String[]{svgFilePath});
            svgConverter.setDst(new File(filePath));
            svgConverter.execute();                                    
            

        } catch (Exception e) {
            DP.getInstance().w("Could not write image file for protein graph to file '" + filePath + "':" + e.getMessage() + "'.");
            return(false);
        }

        return(true);
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
    
    public void setFoldingGraphNumber(Integer i) { this.foldingGraphNumber = i; }
    public Integer getFoldingGraphNumber() { return(this.foldingGraphNumber); }
    
    /**
     * Returns the distance of the SSE pair (a, b) in the primary structure, i.e., the length of a path 
     * between these two SSEs. Note that this number considers the direction and may thus be negative.
     * @param a the first SSE
     * @param b the second SSE
     * @return the path length between the SSEs, considers the direction and may thus be negative. For example, the
     * distance between the SSEs with indices (3, 5) is 2 and the distance of the pair (5, 3) is -2.
     * (Think of this distance in the graph-theoretic sense, you need the 2 edges [3=>4] and [4=>5] to get
     * from 3 to 5.)
     */
    public Integer getPrimarySeqSSEPairDistanceBySSEsInParentGraph(SSE a, SSE b) {
        if(a.sameSSEas(b)) { return(0); }        
        
        if(this.isProteinGraph || this.parent == null) {
            System.err.println("ERROR: getPrimarySeqSSEPairDistanceBySSEsInParentGraph(): This graph has no valid parent or it is a protein graph.");
            System.exit(1);
        }
                
        return(parent.getSeqGraphSSEPairDistanceBySSEs(a, b));
    }
    
    
    /**
     * Returns the position of an SSE in the vertex list of the parent graph.
     * @return The index of the SSE if it was found in the parent graph, -1 otherwise.
     */
    public Integer getSequentialPositionInParentGraph(SSE s) {
        
        if(this.isProteinGraph || this.parent == null) {
            System.err.println("ERROR: getSequentialPositionInParentGraph(): This graph has no valid parent or it is a protein graph.");
            System.exit(1);
        }
        
        return(parent.getSSEIndex(s));
        
    }
    
    
    /**
     * Returns a very short string description of this graph. 
     * @return a string in format 'PDBID-CHAIN-GRAPHTYPE-FGNUM'.
     */
    @Override public String toShortString() {
        return(this.pdbid + "-" + this.chainid + "-" + this.graphType + "-" + "FG" + this.getFoldingGraphNumber() + "[" + this.numVertices() + "," + this.numSSEContacts() + "]");
    }
    
    
    /**
     * Sets the info fields of this graph, defining the PDB ID as 'pdbid', the chain id as 'chainid' and the graph type as 'graphType'. Also sets the meta data.
     * @param pdbid the PDB identifier, e.g., "8icd"
     * @param chainid the PDB chain ID, e.g., "A"
     * @param graphType the graph type, e.g., "albe"
     */
    @Override public void setInfo(String pdbid, String chainid, String graphType) {
        super.setInfo(pdbid, chainid, graphType);
        this.metadata.put("graphclass", "folding graph");     
        this.metadata.put("foldinggraphnumber", this.foldingGraphNumber + "");     
    }

    
}
