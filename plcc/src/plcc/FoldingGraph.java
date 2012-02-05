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
import java.awt.geom.Arc2D;
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
        Integer maxArcHeight = maxVertDist / 4;

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
                        headingsSpatOrder[i] = headingsSpatOrder[i-1];
                    }
                    else if(spatRel == SpatRel.NONE) {
                        headingsSpatOrder[i] = headingsSpatOrder[i-1];    // whatever
                        System.err.println("ERROR: Vertices without contact are considered neighbors in the graph.");
                        System.exit(1);
                    }
                    else {
                        // all other spatial relations, e.g. SpatRel.ANTIPARALLEL
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
                            if(iSpatIndex < jSpatIndex) { leftVertSpat = iSpatIndex; leftVert = i; rightVertSpat = jSpatIndex; rightVert = j;}
                            else { leftVertSpat = jSpatIndex; leftVert = j; rightVertSpat = iSpatIndex; rightVert = i;}
                            leftVertPosX = vertStartX + (leftVertSpat * vertDist);
                            rightVertPosX = vertStartX + (rightVertSpat * vertDist);

                            connWidth = rightVertPosX - leftVertPosX;
                            connHeight = connWidth / 2;

                            connCenterX = rightVertPosX - (connWidth / 2);
                            connCenterY = vertStartY;

                            connTopLeftX = leftVertPosX;
                            connTopLeftY = vertStartY - connHeight / 2;

                            spacerX = vertWidth;
                            spacerY = 0;

                            
                            // Determine the y axis positions where the connector should start (at the left vertex) and end (at the right vertex). This depends
                            //  on whether the respective vertex points upwards or downwards.
                            if(headingsSeqOrder[leftVert] == UP) {
                                // the left vertex points upwards, so the arc should start at its top
                                leftVertPosY = vertStartY - vertHeight;
                                startUpwards = true;
                            }
                            else {
                                // the left vertex points downwards, so the arc should start at its bottom
                                leftVertPosY = vertStartY;
                                startUpwards = false;
                            }
                            
                            if(headingsSeqOrder[rightVert] == UP) {
                                // the left vertex points upwards, so the arc should start at its top
                                rightVertPosY = vertStartY - vertHeight;
                            }
                            else {
                                // the left vertex points downwards, so the arc should start at its bottom
                                rightVertPosY = vertStartY;
                            }
                            
                            
                            // draw it
                            ig2.setStroke(new BasicStroke(2));
                            ArrayList<Shape> connShapes = this.getArcConnector(leftVertPosX, leftVertPosY, rightVertPosX, rightVertPosY, ig2.getStroke(), startUpwards);
                            for(Shape s : connShapes) {
                                ig2.draw(s);
                            }                                                        
                        }
                    }
                }
            }


            // Draw the vertices as arrows or barrels (depending on the type)
            Polygon p;
            for(Integer i = 0; i < this.sseList.size(); i++) {
                if(this.sseList.get(i).isHelix()) { ig2.setPaint(Color.RED); }
                else if(this.sseList.get(i).isBetaStrand()) { ig2.setPaint(Color.BLACK); }
                else if(this.sseList.get(i).isLigandSSE()) { ig2.setPaint(Color.MAGENTA); }
                else if(this.sseList.get(i).isOtherSSE()) { ig2.setPaint(Color.GRAY); }
                else { ig2.setPaint(Color.LIGHT_GRAY); }
                
                // draw it
                ig2.setStroke(new BasicStroke(1));
                
                if(this.sseList.get(i).isHelix()) {
                    p = getDefaultArrowPolygon(vertStartY - vertHeight, vertStartX + (i * vertDist), vertStartY);
                    
                }
                else {
                    p = getDefaultBarrelPolygon(vertStartY - vertHeight, vertStartX + (i * vertDist), vertStartY);
                }
                
                shape = ig2.getStroke().createStrokedShape(p);
                
                if(headingsSpatOrder[i] == DOWN) { ig2.rotate(180); }
                ig2.draw(shape);
                if(headingsSpatOrder[i] == DOWN) { ig2.rotate(-180); }
            }
            
            

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
                    ig2.drawString(sseNumber, footerStartX + (i * vertDist) + vertRadius / 2, footerStartY + (stringHeight / 4));
                }
            }



            // all done, write the image to disk
            ImageIO.write(bi, "PNG", new File(filePath));

        } catch (Exception e) {
            System.err.println("WARNING: Could not write image file for graph to file '" + filePath + "'.");
            return(false);
        }

        return(true);
    }


    /**
     * Draws this folding graph in reduced description (only SSEs of this FG count), writing the image file to 'filePath'.
     * @param filePath the output path of the image
     * @return true if it worked out, false otherwise
     */
    private Boolean drawFoldingGraphRED(String filePath) {
        return(this.drawProteinGraph(filePath, false));
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

    
}
