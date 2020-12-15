/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package graphdrawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import proteingraphs.Position2D;
import plccSettings.Settings;

/**
 * A page layout that holds all non-SSE-specific information about the drawing process, e.g. the
 * position of header, footer, etc. An instance of this class is passed to a DrawSSE so it can determine
 * its own position.
 * 
 * This class uses various settings from the config file and computes the rest based on the number of 
 * vertices in the graph which is to be drawn on this page.
 * 
 * @author ts
 */
public class PageLayout {
    
    public Position2D headerStart;
    public Integer headerHeight;
    public Integer footerHeight;
    public Integer footerWidth;
    public Position2D imgStart;
    
    public Integer marginLeft;
    public Integer marginTop;
    public Integer marginRight;
    public Integer marginBottom; 
    public Integer minImgHeight;
    
    public Integer vertDist;
    public Integer vertRadius;
    
    public Integer minPageWidth;
    public Integer minPageHeight;
    public Integer textLineHeight;
    
    // must not be changed after constructor
    private Integer numVerts;
    public Boolean isForKEY;
    
    /**
     * Constructor. The margins are no set with this constructor, unless you set them later, the defaults from
     * the config file are used.
     * 
     * @param numVerts the number of vertices in the graph, this is required to determine the width and height of the image area and thus the entire image.
     */
    public PageLayout(Integer numVerts) {
        init(numVerts);
    }
    
    public PageLayout(Integer numVerts, ArrayList<String> vertLabels) {
        init(numVerts);
        ArrayList<Integer> x_y_values = getFooterOutline(vertLabels);
        this.footerHeight = x_y_values.get(1);
        this.footerWidth = x_y_values.get(0);
        // TODO preprocessing 
    }
    
    private void init(Integer numVerts) {
        this.numVerts = numVerts;
        this.minImgHeight = Settings.getInteger("plcc_I_img_min_img_height");
        
        this.marginLeft = Settings.getInteger("plcc_I_img_margin_left");
        this.marginTop = Settings.getInteger("plcc_I_img_margin_top");
        this.marginRight = Settings.getInteger("plcc_I_img_margin_right");
        this.marginBottom = Settings.getInteger("plcc_I_img_margin_bottom");
        
        this.headerStart = new Position2D(marginLeft,  marginTop);        
        this.headerHeight = Settings.getInteger("plcc_I_img_header_height");
        this.footerHeight = Settings.getInteger("plcc_I_img_footer_height");
        this.footerWidth = 0;
        
        this.textLineHeight = Settings.getInteger("plcc_I_img_text_line_height");
        
        this.imgStart = new Position2D(headerStart.x,  headerStart.y + headerHeight);
        
        
        this.minPageWidth = Settings.getInteger("plcc_I_img_minPageWidth");
        this.minPageHeight = Settings.getInteger("plcc_I_img_minPageHeight");                
        
        this.vertDist = Settings.getInteger("plcc_I_img_vert_dist");
        this.vertRadius = Settings.getInteger("plcc_I_img_vert_radius");
        this.isForKEY = false;
    }
    
    public Integer getVertDiameter() {
        return vertRadius * 2;
    }
    
    
    public void setVertRadius(Integer r) {
        this.vertRadius = r;
    }
    
    
    /**
     * Returns the number of vertices this page layout is made for.
     * @return the number of vertices
     */
    public Integer getNumVerts() {
        return(this.numVerts);
    }
        
    
    /**
     * Determines the coordinates of the footer start in the image.
     * @return the coordinates as a Position2D
     */
    public Position2D getFooterStart() {
        return(new Position2D(imgStart.x, marginTop + headerHeight + this.getImageAreaHeight() + 40));
    }
    
    /**
     * Determines the maximum distance that can occur between a vertex pair of the image, i.e., the distance
     * from the first (left-most) to the last (right-most) vertex on the canvas. 
     * @return the theoretical maximum distance in pixels
     */
    public Integer getMaxVertDist() {
        return(this.numVerts * this.vertDist);    
    }
    
    /**
     * Determines the width of the image area (page = header + image + footer).
     * @return the width in pixels
     */
    public Integer getImageAreaWidth() {
        return(numVerts * vertDist + (2 * vertRadius) );
    }
    
    public Integer getHeaderWidth() {
        return(getImageAreaWidth());
    }
    
    public Integer getFooterWidth() {
        return Math.max(this.footerWidth, getImageAreaWidth());
    }
    
    /**
     * Determines the height of the image area (page = header + image + footer).
     * @return the height in pixels
     */
    public Integer getImageAreaHeight() {
        Integer h = this.getMaxArcHeight() + (2 * vertRadius);
        Integer min = Settings.getInteger("plcc_I_img_min_img_height");        
        return(h < min ? min : h);
    }
    
    
    
    
    /**
     * Determines the maximum arc height that can occur in the image, i.e., the distance
     * from the first (left-most) to the last (right-most) vertex on the canvas divided by the width/height factor of an arc. 
     * @return the maximum arc height in pixels
     */
    public Integer getMaxArcHeight() {
        Integer h = this.getMaxVertDist() / 4;
        
        if(this.isForKEY) {
            h = (this.getMaxVertDist() / 4) + 100;
        }
        
        Integer min = Settings.getInteger("plcc_I_img_min_arc_height");
        return(h < min ? min : h);    
    }
    
    
    /**
     * Determines the total page width (page = header + image + footer).
     * @return the width in pixels
     */
    public Integer getPageWidth() {
        Integer w = Math.max( (marginLeft + this.getImageAreaWidth() + marginRight), (marginLeft + footerWidth + marginRight));
        return(w < minPageWidth ? minPageWidth : w);
    }
    
    
    public Position2D getMarginBottomStart() {
        return(new Position2D(this.getPageStart().x, this.getPageHeight() - this.marginBottom));
    }
    
    
    /**
     * Determines the total page height (page = header + image + footer).
     * @return the height in pixels
     */
    public Integer getPageHeight() {
        Integer h = marginTop + headerHeight + this.getImageAreaHeight() + footerHeight + marginBottom;
        return(h < minPageHeight ? minPageHeight : h);
    }
    
            
    /**
     * Returns the standard font which can be set in the config file, in plain type.
     * @return the font as java.awt.Font.
     */
    public Font getStandardFont() {
        return(new Font(Settings.get("plcc_S_img_default_font"), Font.PLAIN, Settings.getInteger("plcc_I_img_default_font_size")));
    }
    
    /**
     * Returns the standard font which can be set in the config file, in bold type.
     * @return the font as java.awt.Font.
     */
    public Font getStandardFontBold() {
        return(new Font(Settings.get("plcc_S_img_default_font"), Font.BOLD, Settings.getInteger("plcc_I_img_default_font_size")));
    }
    
    
    public Position2D getHeaderStart() {
        return headerStart;
    }
    
    
    public Position2D getImgStart() {
        return imgStart;
    }
    
    public Position2D getPageStart() {
        return( new Position2D(0,0));
    }
    
    /**
     * Returns the smaller font used in the image legend.
     * @return the font as java.awt.Font.
     */
    public Font getLegendFont() {
        return(new Font(Settings.get("plcc_S_img_default_font"), Font.PLAIN, Settings.getInteger("plcc_I_img_legend_font_size")));
    }
    
        
    /**
     * Sets the page margins of this PageLayout in pixels. Since they are public, you can also set them manually and separately.
     * @param left the left margin, in pixels
     * @param top the top margin, in pixels
     * @param right the right margin, in pixels
     * @param bottom the bottom margin, in pixels
     */ 
    public void setMargins(Integer left, Integer top, Integer right, Integer bottom) {
        this.marginLeft = left;
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
    }    
    
    
    /**
     * Sets the page margins of this PageLayout in pixels. The given value is used for all 4 margins (left, top, right and bottom).
     * @param margin the margin, in pixels
     */
    public void setMargins(Integer margin) {
        this.marginLeft = margin;
        this.marginTop = margin;
        this.marginRight = margin;
        this.marginBottom = margin;
    }
    
    
    
    /**
     * Determines the position where the vertices should be drawn. This is within the image area, but of course NOT in the upper left corner. ;)
     * @return the position where to draw the left-most vertex
     */
    public Position2D getVertStart() {
        return(new Position2D(imgStart.x, imgStart.y + this.getMaxArcHeight() + vertRadius));
    }
    
    
    /**
     * Draws the outlines of the different ares (like header, image area, borders, ...) of this page layout
     * into the given Graphics2D object. This is mainly a debug function.
     * Note that this function sets the stroke and color of the G2D object, so you may need to reset them after
     * calling it.
     * @param g2d the Graphics2D object to draw
     */
    public void drawAreaOutlines(Graphics2D g2d) {
        
        //System.out.println("      Drawing page layout outlines.");
        
        g2d.setPaint(Color.LIGHT_GRAY);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(1));

        // horizontal line at upper margin, where header starts
        g2d.drawLine(getPageStart().x, getHeaderStart().y, (getPageStart().x + getPageWidth()), getHeaderStart().y);
        
        // horizontal line where image area starts
        g2d.drawLine(getImgStart().x, getImgStart().y, (getImgStart().x + getImageAreaWidth()), getImgStart().y);
        
        // horizontal line where footer starts
        g2d.drawLine(getFooterStart().x, getFooterStart().y, (getFooterStart().x + getFooterWidth()), getFooterStart().y);
        
        // horizontal line where footer ends and lower margin starts
        g2d.drawLine(getMarginBottomStart().x, getMarginBottomStart().y, (getMarginBottomStart().x + getPageWidth()), getMarginBottomStart().y);
        
        // vertical line for left margin
        g2d.drawLine(marginLeft, getPageStart().y, marginLeft, (getPageStart().y + getPageHeight()));
        
        // vertical line for right margin
        g2d.drawLine((getPageWidth() - marginRight), getPageStart().y, (getPageWidth() - marginRight), (getPageStart().y + getPageHeight()));
    }
    
    
     /**
     * Calculates the Width and Height of the Footer Area considering the Molecule Names.
     */
    public ArrayList getFooterOutline(ArrayList<String> vertLabels){
        ArrayList<Integer> x_y_values = new ArrayList<Integer>();
        x_y_values.add(0);
        x_y_values.add(0);

        
        SVGGraphics2D graphic;
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        // Create an instance of the SVG Generator.
        graphic = new SVGGraphics2D(document);
                
        Integer stringHeight = graphic.getFontMetrics().getAscent();
        Integer start_y = getFooterStart().y + (textLineHeight * 3) + (stringHeight / 4);
        
        AffineTransform rotate_MN = new AffineTransform();
        rotate_MN.rotate(0.785d,0,0); // rotation around center of vertex
        Font rotatedFont = getStandardFont().deriveFont(rotate_MN);
        graphic.setFont(rotatedFont);
        
        for(Integer i = 0; i < vertLabels.size(); i++){
            Integer start_x = getFooterStart().x + (i * vertDist) + vertRadius / 2;
            final Rectangle2D string_measures= graphic.getFontMetrics().getStringBounds(vertLabels.get(i), graphic);
            //Integer radius = graphic.getFontMetrics().stringWidth(vertLabels.get(i));
            
            Integer new_x = (int)(start_x + string_measures.getBounds().width * cos(0.785d));
            Integer new_y = (int)(start_y + string_measures.getBounds().width * sin(0.785d));
            
            if(x_y_values.get(0) < new_x){
                x_y_values.set(0,new_x);
            }
            if(x_y_values.get(1) < new_y){
                x_y_values.set(1,new_y);
            }
        }
        x_y_values.set(0,(x_y_values.get(0) - marginRight));
        //getFooterStart().y is 40 Pixel bigger than the actual height
        // +50 for legend box size and 10px space
        x_y_values.set(1,(x_y_values.get(1) - (marginTop + headerHeight + this.getImageAreaHeight()) + 50 )); 
        
        return x_y_values;
    }
    
}
