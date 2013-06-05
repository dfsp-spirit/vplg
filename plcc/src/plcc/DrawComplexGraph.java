/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plcc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author marcus
 */
public class DrawComplexGraph{
        /**
     * Draws the protein graph image of this graph, writing the image in PNG format to the file 'filePath' (which should maybe end in ".png").
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     * 
     * @param filePath the file system path where to write the graph image (without file extension and the dot before it)
     * @param nonProteinGraph whether the graph is a non-protein graph and thus does NOT contain information on the relative SSE orientation in the expected way. If so, it is drawn in gray scale because the color code become useless (true => gray scale, false => color).
     * @return whether the graph could be drawn and written to the file filePath
     */

    public Boolean drawComplexGraph(String filePath, String graphType, ComplexGraph compGraph) {

        
        Integer numVerts = compGraph.getNumVertices();
        Integer SIZE = numVerts * 20;
        Integer radius = SIZE * 4 / 5;
        Integer smallRadius = SIZE / (2 * numVerts);
        
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

            //pl.drawAreaOutlines(ig2);
            // prepare font
            Font font = pl.getStandardFont();
            ig2.setFont(font);
            FontMetrics fontMetrics = ig2.getFontMetrics();

            // ------------------------- Draw header -------------------------

            // check width of header string
            String proteinHeader = "The " + graphType + " complex graph of PDB entry " + compGraph.getPDBID();
            //Integer stringWidth = fontMetrics.stringWidth(proteinHeader);       // Should be around 300px for the text above
            Integer stringHeight = fontMetrics.getAscent();
            String sseNumberSeq;    // the SSE number in the primary structure, N to C terminus
            String sseNumberGraph;  // the SSE number in this graph, 1..(this.size)

            if(Settings.getBoolean("plcc_B_graphimg_header")) {
                ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
            }

            // ------------------------- Draw the graph -------------------------
            
            
            // Draw the vertices as circles on a larger circle
            
            ig2.setColor(Color.blue);
            for (int i = 0; i < numVerts; i++) {
                double t = 2 * Math.PI * i / numVerts;
                int x = (int) Math.round(SIZE + radius * Math.cos(t));
                int y = (int) Math.round(SIZE + radius * Math.sin(t));
                ig2.fillOval(x - smallRadius, y - smallRadius, 2 * smallRadius, 2 * smallRadius);
                ig2.drawString("A", x, y);
            }
            
            // Draw the edges as arcs
/**            java.awt.Shape shape;
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
            */
                        
            // ************************************* footer **************************************
            
            if(Settings.getBoolean("plcc_B_graphimg_footer")) {

                // line markers: S for sequence order, G for graph order
                Integer lineHeight = pl.textLineHeight;            
                if(compGraph.getNumEdges() > 0) {                                            
                    ig2.drawString("Adjacencies  HH  HS  HO  SS  SO  OO", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                    //ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                }
                else {
                    ig2.drawString("(Graph has no edges.)", pl.getFooterStart().x, pl.getFooterStart().y);
                }

                for(Integer i = 0; i < compGraph.getNumEdges(); i++) {

                    ig2.drawString("a", pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight * (i + 1)) + (stringHeight / 4));
                    //ig2.drawString(sseNumberSeq, pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                    //compGraph.numAllInteractionsMap.get(compGraph.getEdges().toArray()[i])
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
            System.err.println("WARNING: Could not write image file for protein graph to file '" + filePath + "':" + e.getMessage() + "'.");
            return(false);
        }

        return(true);
    }

    
/**    
    private static final int SIZE = 256;
    private int a = SIZE / 2;
    private int b = a;
    private int r = 4 * SIZE / 5;
    private int n;

    /** @param n  the desired number of circles. */
/**    public DrawComplexGraph(int n) {
        super(true);
        this.setPreferredSize(new Dimension(SIZE, SIZE));
        this.n = n;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.black);
        a = getWidth() / 2;
        b = getHeight() / 2;
        int m = Math.min(a, b);
        r = 4 * m / 5;
        int r2 = Math.abs(m - r) / 2;
        g2d.drawOval(a - r, b - r, 2 * r, 2 * r);
        g2d.setColor(Color.blue);
        for (int i = 0; i < n; i++) {
            double t = 2 * Math.PI * i / n;
            int x = (int) Math.round(a + r * Math.cos(t));
            int y = (int) Math.round(b + r * Math.sin(t));
            g2d.fillOval(x - r2, y - r2, 2 * r2, 2 * r2);
        }
    }

    static void create() {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new DrawComplexGraph(9));
        f.pack();
        f.setVisible(true);
    }*/
    public Integer drawLegend(SVGGraphics2D ig2, Position2D startPos, PageLayout pl) {
        return 0;
    }

}