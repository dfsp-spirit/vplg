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
import java.awt.Graphics2D;

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
    
    /**
     * Constructor. The margins are no set with this constructor, unless you set them later, the defaults from
     * the config file are used.
     * 
     * @param numVerts the number of vertices in the graph, this is required to determine the width and height of the image area and thus the entire image.
     */
    PageLayout(Integer numVerts) {
        
        this.numVerts = numVerts;
        this.minImgHeight = Settings.getInteger("plcc_I_img_min_img_height");
        
        this.marginLeft = Settings.getInteger("plcc_I_img_margin_left");
        this.marginTop = Settings.getInteger("plcc_I_img_margin_top");
        this.marginRight = Settings.getInteger("plcc_I_img_margin_right");
        this.marginBottom = Settings.getInteger("plcc_I_img_margin_bottom");
        
        this.headerStart = new Position2D(marginLeft,  marginTop);        
        this.headerHeight = Settings.getInteger("plcc_I_img_header_height");
        this.footerHeight = Settings.getInteger("plcc_I_img_footer_height");
        
        this.textLineHeight = Settings.getInteger("plcc_I_img_text_line_height");
        
        this.imgStart = new Position2D(headerStart.x,  headerStart.y + headerHeight);
        
        
        this.minPageWidth = Settings.getInteger("plcc_I_img_minPageWidth");
        this.minPageHeight = Settings.getInteger("plcc_I_img_minPageHeight");                
        
        this.vertDist = Settings.getInteger("plcc_I_img_vert_dist");
        this.vertRadius = Settings.getInteger("plcc_I_img_vert_radius");
        
        
        
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
        return(getImageAreaWidth());
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
        Integer min = Settings.getInteger("plcc_I_img_min_arc_height");
        return(h < min ? min : h);    
    }
    
    
    /**
     * Determines the total page width (page = header + image + footer).
     * @return the width in pixels
     */
    public Integer getPageWidth() {
        Integer w = marginLeft + this.getImageAreaWidth() + marginRight;
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
     * Returns the standard font which can be set in the config file.
     * @return the font as java.awt.Font.
     */
    public Font getStandardFont() {
        return(new Font(Settings.get("plcc_S_img_default_font"), Font.PLAIN, Settings.getInteger("plcc_I_img_default_font_size")));
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
    
}
