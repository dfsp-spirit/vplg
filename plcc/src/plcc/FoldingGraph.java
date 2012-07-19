/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package plcc;

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
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author ts
 */
public class FoldingGraph extends SSEGraph {
    
    
    
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
     * @param notation the notation to use. valid notation strings are 'KEY', 'ADJ', 'RED' and 'SEQ'.
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
            System.err.println("WARNING: drawFoldingGraph(): This graph is connected but it is not declared to be a folding graph. Still trying to draw.");
        }

        // Check minimum size
        if(this.numVertices() < Settings.getInteger("plcc_I_min_fgraph_size_draw")) {
            System.out.println("INFO: drawFoldingGraph(): Ignoring folding graph of size " + this.numVertices() + ", minimum size is " + Settings.getInteger("plcc_I_min_fgraph_size_draw") + ".");
            return(false);
        }

        // OK, let's go.
        if(notation.equals("KEY")) {
            return(this.drawFoldingGraphKEY(filePath));
        }
        else if(notation.equals("ADJ")) {
            return(this.drawFoldingGraphADJ(filePath));
        }
        else if(notation.equals("RED")) {
            return(this.drawFoldingGraphRED(filePath));
        }
        else if(notation.equals("SEQ")) {
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
        //System.err.println("WARNING: Folding graph notation 'ADJ' not implemented yet, drawing RED instead.");
        //return(this.drawFoldingGraphRED(filePath));
        return(false);
    }
    
    
    /**
     * Draws the SEQ notation image of this graph, writing the image in PNG format to the file 'filePath' (which should maybe end in ".png").
     * @param filePath the output path of the image
     * @return true if it worked out, false otherwise 
     */
    private Boolean drawFoldingGraphSEQ(String filePath) {
        //System.err.println("WARNING: Folding graph notation 'SEQ' not implemented yet.");
        return(false);
    }

    
    
    /**
     * Draws the KEY notation image of this graph, writing the image in PNG format to the file 'filePath' (which should maybe end in ".png").
     * @param filePath the output path of the image
     * @return true if it worked out, false otherwise
     */
    private Boolean drawFoldingGraphKEY(String filePath) {
        
        if(this.isBifurcated()) {
            if(Settings.getInteger("plcc_I_debug_level") > 0) {
                System.err.println("NOTE: ProtGraph.drawFoldingGraphKEY(): Called for bifurcated graph but KEY notation not supported for such graphs, skipping.");
            }
            return(false);
        }
        
        // Prepare the vertex order
        ArrayList<Integer> spatOrder = this.getSpatialOrderingOfVertexIndices();
        if(spatOrder.size() != this.size) {
            if(Settings.getInteger("plcc_I_debug_level") > 0) {
                System.err.println("NOTE: ProtGraph.drawFoldingGraphKEY(): Could not determine a valid spatial vertex ordering (" + spatOrder.size() + "/" + this.size + "), graph bifurcated?");
            }
            return(false);
        }

        // All these values are in pixels
        Integer numVert = this.numVertices();
        final Integer NONE = 0;     // no direction (because this SSE-pair does not appear in the list)
        final Integer UP = 1;       // direction: upwards (for the SSE symbols, e.g. arrows). Note that the direction represents the spatial relation between this SSE and its predecessor (i.e., an SSE pair), it is NOT a property of a single SSE.
        final Integer DOWN = 2;     // direction: downwards

        // page setup
        Integer marginLeft = 40;
        Integer marginRight = 40;
        Integer marginTop = 40;
        Integer marginBottom = 40;
        Integer minImgHeight = 80;

        // The header that contains the text describing of the graph.
        Integer headerHeight = 40;

        // The footer contains the vertex numbering.
        Integer footerHeight = 40;

        // Where to start drawing
        Integer headerStartX = marginLeft;
        Integer headerStartY = marginTop;
        Integer imgStartX = marginLeft;
        Integer imgStartY = marginTop + headerHeight;
        Integer footerStartX = marginLeft;


        // drawing of objects
        Integer vertDist = 50;                  // distance between (centers of) vertices in the drawing
        Integer vertRadius = 10;                // the width of a drawn vertex (radius around its center)
        Integer vertHeight = 80;                // height of vertex element graphics (arrow height)
        Integer vertWidth = 40;                 // height of vertex graphics (arrow width)
        Integer vertDiameter = 2 * vertRadius;

        // Determine the maximal arc height. The height of an arc is half of its width (see the part where the arcs
        //  are drawn below), but the arcHeight really describes the height of the full circle this half-circle is
        //  a part of. So we gotta divide by 2 again; the maximal height is thus a quarter of the maximal distance between
        //  vertices in the image.
        Integer maxVertDist = this.numVertices() * vertDist;
        Integer maxArcHeight = maxVertDist / 2;

        // the image area: the part where the vertices and arcs are drawn
        Integer imgWidth = numVert * vertDist + 2 * vertRadius;
        Integer imgHeight = maxArcHeight + vertRadius;
        if(imgHeight < minImgHeight) { imgHeight = minImgHeight; }

        // The text drawn in the proteinHeader is about 300px wide with the current text. We assume a maximum of
        //  500 here and make this the minimum width of the content frame.
        Integer headerTextWidth = 500;
        if(imgWidth < headerTextWidth) {
            imgWidth = headerTextWidth;
        }

        // where to start drawing the vertices
        Integer vertStartX = imgStartX;
        Integer vertStartY = imgStartY + maxArcHeight;
        Integer footerStartY = vertStartY + vertRadius + 40;

        // putting it all together
        Integer pageWidth = marginLeft + imgWidth + marginRight;
        Integer pageHeight = marginTop + headerHeight + imgHeight + footerHeight + marginBottom;               

        try {

            // ------------------------- Prepare stuff -------------------------
            // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
            BufferedImage bi = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ig2 = bi.createGraphics();
            ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // make background white
            ig2.setPaint(Color.WHITE);
            ig2.fillRect(0, 0, pageWidth, pageHeight);
            ig2.setPaint(Color.BLACK);


            // prepare font
            Font font = new Font("TimesRoman", Font.PLAIN, 18);
            ig2.setFont(font);
            FontMetrics fontMetrics = ig2.getFontMetrics();

            // ------------------------- Draw header -------------------------

            // check width of header string
            String proteinHeader = "The " + this.graphType + " graph of PDB entry " + this.pdbid + ", chain " + this.chainid + " in KEY notation.";
            Integer stringWidth = fontMetrics.stringWidth(proteinHeader);       // Should be around 300px for the text above
            Integer stringHeight = fontMetrics.getAscent();
            String sseNumber;

            if(Settings.getBoolean("plcc_B_graphimg_header")) {
                ig2.drawString(proteinHeader, headerStartX, headerStartY);
            }

            // ------------------------- Draw the graph -------------------------

            // Draw the edges as arcs
            java.awt.Shape shape;
            Arc2D.Double arc;
            Integer edgeType, connCenterX, connCenterY, leftVert, rightVert, leftVertSpat, rightVertSpat, leftVertPosX, leftVertPosY, rightVertPosX, rightVertPosY, connWidth, connHeight, connTopLeftX, connTopLeftY, spacerX, spacerY;
            
            // determine the headings of the vertices in the image. if two adjacent SSEs are parallel, they point in the same direction in the image. if they are antiparallel, they point into different directions.
            Integer[] headingsSpatOrder = new Integer[this.size];    // the heading of the vertex in the image (UP or DOWN). This is in the order of spatOrder variable, not the the original vertex list in the SSEList variable.
            Integer[] headingsSeqOrder = new Integer[this.size];
            headingsSpatOrder[0] = UP;  // heading of the 1st vertex is up by definition (it has no predecessor)
            
            if(this.size > 1) {
                
                for(Integer i = 1; i < this.size; i++) {
                    Integer sseIndex = spatOrder.get(i);
                    Integer lastsseIndex = spatOrder.get(i - 1);
                    Integer spatRel = this.getContactType(sseIndex, lastsseIndex);
                    
                    if(spatRel == SpatRel.PARALLEL || spatRel == SpatRel.LIGAND) {
                        // keep orientation
                        headingsSpatOrder[i] = headingsSpatOrder[i-1];
                    }
                    else if(spatRel == SpatRel.NONE) {
                        // should never happen
                        headingsSpatOrder[i] = headingsSpatOrder[i-1];    // whatever
                        System.err.println("ERROR: Vertices without contact are considered neighbors in the graph.");
                        System.exit(1);
                    }
                    else {
                        // all other spatial relations, e.g. SpatRel.ANTIPARALLEL: invert orientation
                        headingsSpatOrder[i] = (headingsSpatOrder[i-1] == UP ? DOWN : UP);
                    }                
                }
                
                // copy from spatOrder to seqOrder array
                Integer seqIndex;
                for(Integer i = 0; i < this.size; i++) {
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
                            edgeType = this.getContactType(i, j);
                            if(edgeType.equals(SpatRel.PARALLEL)) { ig2.setPaint(Color.RED); }
                            else if(edgeType.equals(SpatRel.ANTIPARALLEL)) { ig2.setPaint(Color.BLUE); }
                            else if(edgeType.equals(SpatRel.MIXED)) { ig2.setPaint(Color.GREEN); }
                            else if(edgeType.equals(SpatRel.LIGAND)) { ig2.setPaint(Color.MAGENTA); }
                            else { ig2.setPaint(Color.LIGHT_GRAY); }

                            // determine the center of the arc and the width of its rectangle bounding box
                            iSpatIndex = spatOrder.get(i);
                            jSpatIndex = spatOrder.get(j);
                            
                            // determine who is left and who is right
                            if(iSpatIndex < jSpatIndex) { leftVertSpat = iSpatIndex; leftVert = i; rightVertSpat = jSpatIndex; rightVert = j;}
                            else { leftVertSpat = jSpatIndex; leftVert = j; rightVertSpat = iSpatIndex; rightVert = i;}
                            
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
                            
                            System.out.print("Contact " + i + "," + j + ": ");
                            
                            if(headingsSeqOrder[leftVert] == UP) {
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
                            
                            if(headingsSeqOrder[rightVert] == UP) {
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

            
            
            for(Integer i = 0; i < this.sseList.size(); i++) {
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
                
                if(headingsSpatOrder[i] == DOWN) { 
                    //ig2.rotate(Math.toRadians(180));                                         
                    ig2.setTransform(newXform);
                    //System.out.println("Rotating canvas before drawing SSE #" + i + " of the list.");
                }
                ig2.draw(shape);
                if(headingsSpatOrder[i] == DOWN) { 
                    ig2.setTransform(origXform);
                    //ig2.rotate(Math.toRadians(-180)); 
                    //System.out.println("Rotating canvas back to normal after drawing SSE #" + i + " of the list.");
                }
                
                
                // draw the N or C terminus label under it if applicable
                if( (this.closestToCTerminus() == i)  || (this.closestToNTerminus() == i) ) {
                    if(this.closestToCTerminus() == i) {
                        System.out.println("    SSE # " + i + " is closest to C terminus.");
                        ig2.drawString("C", currentVertX, (currentVertY + 20));
                    }
                    
                    if(this.closestToNTerminus() == i) { 
                        System.out.println("    SSE # " + i + " is closest to N terminus.");
                        ig2.drawString("N", currentVertX, (currentVertY + 20));
                    }      
                    
                }
                
                
                
            }
            
            //Line2D lineTop = new Line2D.Float(vertStartX, (vertStartY - vertHeight), (vertStartX + ((this.sseList.size() - 1) * vertDist)), (vertStartY - vertHeight));
            //ig2.draw(lineTop);
            
            

            // Draw the vertex numbering into the footer
            font = new Font("TimesRoman", Font.PLAIN, 18);
            ig2.setFont(font);
            fontMetrics = ig2.getFontMetrics();
            ig2.setPaint(Color.BLACK);

            // Determine the dist between vertices that will have their vertex number printed below them in the footer field
            Integer printNth = 1;
            if(this.sseList.size() > 9) { printNth = 1; }
            if(this.sseList.size() > 99) { printNth = 2; }
            if(this.sseList.size() > 999) { printNth = 3; }

            for(Integer i = 0; i < this.sseList.size(); i++) {
                // Draw label for every 2nd vertex
                if((i + 1) % printNth == 0) {
                    sseNumber = "" + (spatOrder.get(i) + 1);
                    stringWidth = fontMetrics.stringWidth(sseNumber);
                    stringHeight = fontMetrics.getAscent();
                    ig2.drawString(sseNumber, footerStartX + (i * vertDist), footerStartY + (stringHeight / 4));
                }
            }



            // all done, write the image to disk
            ImageIO.write(bi, "PNG", new File(filePath));

        } catch (Exception e) {
            System.err.println("WARNING: Could not write image file for graph to file '" + filePath + "': " + e.getMessage() + ".");
            return(false);
        }

        return(true);
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
     * Draws this folding graph in reduced description (only SSEs of this FG count), writing the image file to 'filePath'.
     * @param filePath the output path of the image
     * @return true if it worked out, false otherwise
     */
    private Boolean drawFoldingGraphRED(String filePath) {
        //return(this.drawProteinGraph(filePath, false));
        return(false);
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

    
}
